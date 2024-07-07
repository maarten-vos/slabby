package gg.mew.slabby.gui;

import gg.mew.slabby.SlabbyAPI;
import gg.mew.slabby.audit.Auditable;
import gg.mew.slabby.shop.Shop;
import gg.mew.slabby.shop.ShopWizard;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
import java.util.UUID;

@UtilityClass
public final class RestoreShopUI {

    public void open(final SlabbyAPI api, final Player viewer, final UUID uniqueId) {
        try {
            final var deletedShops = api.repository()
                    .shopsOf(uniqueId, Shop.State.DELETED)
                    .stream()
                    .sorted(Comparator.comparing(Auditable::createdOn, Comparator.reverseOrder()))
                    .map(it -> {
                        final var itemStack = Bukkit.getItemFactory().createItemStack(it.item());

                        final var owners = it.owners()
                                .stream()
                                .map(i -> Bukkit.getOfflinePlayer(i.uniqueId()).getName())
                                .toArray(String[]::new);

                        itemStack.lore(new ArrayList<>() {{
                            add(api.messages().restore().buyPrice(it.buyPrice()));
                            add(api.messages().restore().sellPrice(it.sellPrice()));
                            add(api.messages().restore().quantity(it.quantity()));
                            add(api.messages().restore().stock(it.stock()));
                            add(api.messages().restore().note(it.note()));
                            add(api.messages().restore().owners(owners));
                        }});

                        return (Item) new SimpleItem(itemStack, c -> {
                            api.operations()
                                    .wizardFrom(viewer.getUniqueId(), it)
                                    .state(Shop.State.ACTIVE)
                                    .wizardState(ShopWizard.WizardState.AWAITING_LOCATION);

                            viewer.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

                            viewer.sendMessage(api.messages().restore().message());
                        });
                    })
                    .toList();

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
                    .setContent(deletedShops)
                    .build();

            final var window = Window.single()
                    .setViewer(viewer)
                    .setTitle(new AdventureComponentWrapper(api.messages().restore().title()))
                    .setGui(gui)
                    .build();

            window.open();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
