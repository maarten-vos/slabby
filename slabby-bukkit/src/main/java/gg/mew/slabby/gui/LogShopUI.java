package gg.mew.slabby.gui;

import gg.mew.slabby.SlabbyAPI;
import gg.mew.slabby.audit.Auditable;
import gg.mew.slabby.shop.Shop;
import gg.mew.slabby.shop.log.LocationChanged;
import gg.mew.slabby.shop.log.Transaction;
import gg.mew.slabby.shop.log.ValueChanged;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@UtilityClass
public final class LogShopUI {

    //TODO: category menu

    public void open(final SlabbyAPI api, final Player shopOwner, final Shop shop) {
        final var items = shop.logs().stream().sorted(Comparator.comparing(Auditable::createdOn, Comparator.reverseOrder())).map(it -> {
            final var item = new ItemStack(Material.PAPER);
            final var meta = item.getItemMeta();

            meta.lore(new ArrayList<>() {{
                //TODO: use display name
                final var player = Bukkit.getOfflinePlayer(it.uniqueId());
                add(api.messages().log().player(Component.text(player.getName())));

                switch (it.action()) {
                    case BUY -> {
                        meta.displayName(api.messages().log().buy().title());

                        final var data = api.gson().fromJson(it.data(), Transaction.class);

                        add(api.messages().log().buy().amount(data.amount()));
                        add(api.messages().log().buy().quantity(data.quantity()));
                    }
                    case SELL -> {
                        meta.displayName(api.messages().log().sell().title());

                        final var data = api.gson().fromJson(it.data(), Transaction.class);

                        add(api.messages().log().sell().amount(data.amount()));
                        add(api.messages().log().sell().quantity(data.quantity()));
                    }
                    case DEPOSIT -> {
                        meta.displayName(api.messages().log().deposit().title());
                        final var data = api.gson().fromJson(it.data(), ValueChanged.Int.class);
                        final var deposited = data.to() - data.from();
                        add(api.messages().log().deposit().amount(deposited));
                    }
                    case WITHDRAW -> {
                        meta.displayName(api.messages().log().withdraw().title());
                        final var data = api.gson().fromJson(it.data(), ValueChanged.Int.class);
                        final var deposited = data.from() - data.to();
                        add(api.messages().log().withdraw().amount(deposited));
                    }
                    case INVENTORY_LINK_CHANGED -> {
                        meta.displayName(api.messages().log().inventoryLinkChanged().title());

                        final var data = api.gson().fromJson(it.data(), LocationChanged.class);

                        if (data.isRemoved()) {
                            add(api.messages().log().inventoryLinkChanged().removed());
                        } else {
                            add(api.messages().log().inventoryLinkChanged().x(data.x()));
                            add(api.messages().log().inventoryLinkChanged().y(data.y()));
                            add(api.messages().log().inventoryLinkChanged().z(data.z()));
                            add(api.messages().log().inventoryLinkChanged().world(data.world()));
                        }
                    }
                    case LOCATION_CHANGED -> {
                        meta.displayName(api.messages().log().locationChanged().title());

                        final var data = api.gson().fromJson(it.data(), LocationChanged.class);

                        add(api.messages().log().locationChanged().x(data.x()));
                        add(api.messages().log().locationChanged().y(data.y()));
                        add(api.messages().log().locationChanged().z(data.z()));
                        add(api.messages().log().locationChanged().world(data.world()));
                    }
                    case NAME_CHANGED -> {
                        meta.displayName(api.messages().log().nameChanged().title());

                        final var data = (ValueChanged.String) api.gson().fromJson(it.data(), it.action().dataClass());

                        add(api.messages().log().nameChanged().from(data.from()));
                        add(api.messages().log().nameChanged().to(data.to()));
                    }
                    case NOTE_CHANGED -> {
                        meta.displayName(api.messages().log().noteChanged().title());

                        final var data = (ValueChanged.String) api.gson().fromJson(it.data(), it.action().dataClass());

                        add(api.messages().log().noteChanged().from(data.from()));
                        add(api.messages().log().noteChanged().to(data.to()));
                    }
                    case QUANTITY_CHANGED -> {
                        meta.displayName(api.messages().log().quantityChanged().title());

                        final var data = (ValueChanged.Int) api.gson().fromJson(it.data(), it.action().dataClass());

                        add(api.messages().log().quantityChanged().from(data.from()));
                        add(api.messages().log().quantityChanged().to(data.to()));
                    }
                    case SELL_PRICE_CHANGED -> {
                        meta.displayName(api.messages().log().sellPriceChanged().title());

                        final var data = (ValueChanged.Double) api.gson().fromJson(it.data(), it.action().dataClass());

                        add(api.messages().log().sellPriceChanged().from(data.from()));
                        add(api.messages().log().sellPriceChanged().to(data.to()));
                    }
                    case BUY_PRICE_CHANGED -> {
                        meta.displayName(api.messages().log().buyPriceChanged().title());

                        final var data = (ValueChanged.Double) api.gson().fromJson(it.data(), it.action().dataClass());

                        add(api.messages().log().buyPriceChanged().from(data.from()));
                        add(api.messages().log().buyPriceChanged().to(data.to()));
                    }
                    default -> add(Component.text(it.data()));
                }

                add(api.messages().log().date(it.createdOn()));
            }});

            item.setItemMeta(meta);

            return (Item) new SimpleItem(item);
        }).toList();

        final var gui = PagedGui.items()
                .setStructure(
                        "X X X X X X X X X",
                        "X X X X X X X X X",
                        "X X X X X X X X X",
                        "X X X X X X X X X",
                        "X X X X X X X X X",
                        "# # # < # > # # #")
                .addIngredient('X', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('<', new PageItem(false) {
                    @Override
                    public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
                        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(new AdventureComponentWrapper(api.messages().general().previousPage()));
                    }
                })
                .addIngredient('>', new PageItem(true) {
                    @Override
                    public ItemProvider getItemProvider(PagedGui<?> pagedGui) {
                        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(new AdventureComponentWrapper(api.messages().general().nextPage()));
                    }
                })
                .setContent(items)
                .build();

        final var window = Window.single()
                .setViewer(shopOwner)
                .setTitle(new AdventureComponentWrapper(api.messages().log().title()))
                .setGui(gui)
                .build();

        window.open();
    }

}
