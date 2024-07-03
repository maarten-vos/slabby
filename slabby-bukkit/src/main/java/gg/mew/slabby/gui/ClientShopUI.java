package gg.mew.slabby.gui;

import gg.mew.slabby.SlabbyAPI;
import gg.mew.slabby.shop.Shop;
import gg.mew.slabby.wrapper.sound.Sounds;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import static gg.mew.slabby.gui.GuiHelper.*;

import java.util.ArrayList;

@UtilityClass
public final class ClientShopUI {

    public void open(final SlabbyAPI api, final Player client, final Shop shop) {
        final var item = Bukkit.getItemFactory().createItemStack(shop.item());

        final var gui = Gui.empty(9, 1);

        if (shop.buyPrice() != null) {
            gui.setItem(0, 0, new SuppliedItem(itemStack(Material.GOLD_INGOT, (it, meta) -> {
                meta.displayName(api.messages().client().buy().title(item.displayName(), shop.quantity()));
                meta.lore(new ArrayList<>() {{
                    add(api.messages().client().buy().price(shop.buyPrice()));
                    add(api.messages().client().buy().stock(shop.stock()));
                    add(api.messages().client().buy().stacks(shop.stock() / item.getMaxStackSize()));
                }});
            }), c -> {
                final var result = api.operations().buy(client.getUniqueId(), shop);

                if (!result.success()) {
                    client.sendMessage(localize(result));
                    api.sound().play(client.getUniqueId(), shop, Sounds.BLOCKED);
                } else {
                    api.sound().play(client.getUniqueId(), shop, Sounds.BUY_SELL_SUCCESS);

                    client.sendMessage(api.messages().client().buy().message(item.displayName(), shop.quantity(), shop.buyPrice()));
                    //TODO: notify sellers
                }
                return true;
            }));
        }

        if (shop.sellPrice() != null) {
            gui.setItem(1, 0, new SuppliedItem(itemStack(Material.IRON_INGOT, (it, meta) -> {
                meta.displayName(api.messages().client().sell().title(item.displayName(), shop.quantity()));
                meta.lore(new ArrayList<>() {{
                    add(api.messages().client().sell().price(shop.buyPrice()));
                    add(api.messages().client().sell().stock(shop.stock()));
                    add(api.messages().client().sell().stacks(shop.stock() / item.getMaxStackSize()));
                }});
            }), c -> {
                final var result = api.operations().sell(client.getUniqueId(), shop);

                if (!result.success()) {
                    client.sendMessage(localize(result));
                    api.sound().play(client.getUniqueId(), shop, Sounds.BLOCKED);
                } else {
                    client.sendMessage(api.messages().client().sell().message(item.displayName(), shop.quantity(), shop.sellPrice()));
                    //TODO: notify sellers
                }
                return true;
            }));
        }

        gui.setItem(4, 0, new SimpleItem(item));

        gui.setItem(6, 0, new SimpleItem(itemStack(Material.NAME_TAG, (it, meta) -> {
            meta.displayName(api.messages().client().note().title());
            meta.lore(new ArrayList<>() {{
                add(Component.text(shop.note(), NamedTextColor.DARK_PURPLE));
            }});
        }).get()));

        gui.setItem(7, 0, new AutoUpdateItem(20, itemStack(Material.PAPER, (it, meta) -> {
            meta.displayName(api.messages().client().funds().title());
            meta.lore(new ArrayList<>() {{
                add(api.messages().client().funds().balance(api.economy().balance(client.getUniqueId())));
            }});
        })));

        gui.setItem(8, 0, commandBlock(api, shop, item));

        final var window = Window.single()
                .setViewer(client)
                .setTitle(new AdventureComponentWrapper(api.messages().client().title()))
                .setGui(gui)
                .build();

        window.open();
    }

}
