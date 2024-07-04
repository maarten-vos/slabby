package gg.mew.slabby.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Date;

@ConfigSerializable
@Accessors(fluent = true, chain = false)
@Getter
public final class BukkitSlabbyMessages implements SlabbyMessages {

    private BukkitClient client;
    private BukkitCreate create;
    private BukkitDestroy destroy;
    private BukkitCommandBlock commandBlock;
    private BukkitLog log;
    private BukkitModify modify;
    private BukkitOwner owner;

    @ConfigSerializable
    @Accessors(fluent = true, chain = false)
    @Getter
    final static class BukkitClient implements Client {

        @ConfigSerializable
        final static class BukkitBuy implements Buy {

            private String title;
            private String price;
            private String stock;
            private String stacks;
            private String message;

            @Override
            public Component title(final Component displayName, final int quantity) {
                return MiniMessage.miniMessage().deserialize(this.title, Placeholder.component("item", displayName), Formatter.number("quantity", quantity));
            }

            @Override
            public Component price(final double price) {
                return MiniMessage.miniMessage().deserialize(this.price, Formatter.number("price", price));
            }

            @Override
            public Component stock(final int stock) {
                return MiniMessage.miniMessage().deserialize(this.stock, Formatter.number("stock", stock));
            }

            @Override
            public Component stacks(final int stacks) {
                return MiniMessage.miniMessage().deserialize(this.stacks, Formatter.number("stacks", stacks));
            }

            @Override
            public Component message(final Component displayName, final int quantity, final double buyPrice) {
                return MiniMessage.miniMessage().deserialize(this.message,
                        Placeholder.component("item", displayName),
                        Formatter.number("quantity", quantity),
                        Formatter.number("price", buyPrice));
            }
        }

        @ConfigSerializable
        final static class BukkitSell implements Sell {

            private String title;
            private String price;
            private String stock;
            private String stacks;
            private String message;

            @Override
            public Component title(final Component displayName, final int quantity) {
                return MiniMessage.miniMessage().deserialize(this.title, Placeholder.component("item", displayName), Formatter.number("quantity", quantity));
            }

            @Override
            public Component price(final double price) {
                return MiniMessage.miniMessage().deserialize(this.price, Formatter.number("price", price));
            }

            @Override
            public Component stock(final int stock) {
                return MiniMessage.miniMessage().deserialize(this.stock, Formatter.number("stock", stock));
            }

            @Override
            public Component stacks(final int stacks) {
                return MiniMessage.miniMessage().deserialize(this.stacks, Formatter.number("stacks", stacks));
            }

            @Override
            public Component message(final Component displayName, final int quantity, final double sellPrice) {
                return MiniMessage.miniMessage().deserialize(this.message,
                        Placeholder.component("item", displayName),
                        Formatter.number("quantity", quantity),
                        Formatter.number("price", sellPrice));
            }
        }

        @ConfigSerializable
        final static class BukkitFunds implements Funds {

            private String title;
            private String balance;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component balance(final double balance) {
                return MiniMessage.miniMessage().deserialize(this.balance, Formatter.number("balance", balance));
            }
        }

        @ConfigSerializable
        final static class BukkitNote implements Note {

            private String title;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }
        }

        private BukkitBuy buy;
        private BukkitSell sell;
        private BukkitFunds funds;
        private BukkitNote note;
        private String title;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }

    }

    @ConfigSerializable
    final static class BukkitCommandBlock implements CommandBlock {

        private String title;
        private String owners;
        private String selling;
        private String buyPrice;
        private String sellPrice;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }

        @Override
        public Component owners(final String[] names) {
            final var namesString = String.join(", ", names);
            return MiniMessage.miniMessage().deserialize(this.owners, Placeholder.unparsed("names", namesString));
        }

        @Override
        public Component selling(final Component displayName) {
            return MiniMessage.miniMessage().deserialize(this.selling, Placeholder.component("item", displayName));
        }

        @Override
        public Component buyPrice(int quantity, double buyPrice, double eachPrice) {
            return MiniMessage.miniMessage().deserialize(this.buyPrice,
                    Formatter.number("quantity", quantity),
                    Formatter.number("price", buyPrice),
                    Formatter.number("each", eachPrice)
            );
        }

        @Override
        public Component sellPrice(int quantity, double sellPrice, double eachPrice) {
            return MiniMessage.miniMessage().deserialize(this.sellPrice,
                    Formatter.number("quantity", quantity),
                    Formatter.number("price", sellPrice),
                    Formatter.number("each", eachPrice)
            );
        }
    }

    @ConfigSerializable
    final static class BukkitCreate implements Create {

        private String title;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }
    }

    @ConfigSerializable
    @Accessors(fluent = true, chain = false)
    @Getter
    final static class BukkitDestroy implements Destroy {

        @ConfigSerializable
        final static class BukkitConfirm implements Confirm {

            private String title;
            private String description;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component description() {
                return MiniMessage.miniMessage().deserialize(this.description);
            }

        }

        @ConfigSerializable
        final static class BukkitCancel implements Cancel {

            private String title;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }
        }

        private String title;
        private BukkitConfirm confirm;
        private BukkitCancel cancel;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }

    }

    @ConfigSerializable
    @Accessors(fluent = true, chain = false)
    @Getter
    final static class BukkitLog implements Log {

        @ConfigSerializable
        final static class BukkitBuy implements Buy {

            private String title;
            private String amount;
            private String quantity;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }

            @Override
            public Component quantity(final int quantity) {
                return MiniMessage.miniMessage().deserialize(this.quantity, Formatter.number("quantity", quantity));
            }
        }

        @ConfigSerializable
        final static class BukkitSell implements Sell {

            private String title;
            private String amount;
            private String quantity;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }

            @Override
            public Component quantity(final int quantity) {
                return MiniMessage.miniMessage().deserialize(this.quantity, Formatter.number("quantity", quantity));
            }
        }

        @ConfigSerializable
        final static class BukkitDeposit implements Deposit {

            private String title;
            private String amount;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final int amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }
        }

        @ConfigSerializable
        final static class BukkitWithdraw implements Withdraw {

            private String title;
            private String amount;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final int amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }
        }

        @ConfigSerializable
        final static class BukkitLocationChanged implements LocationChanged {

            private String title;
            private String x;
            private String y;
            private String z;
            private String world;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component x(final int x) {
                return MiniMessage.miniMessage().deserialize(this.x, Formatter.number("x", x));
            }

            @Override
            public Component y(final int y) {
                return MiniMessage.miniMessage().deserialize(this.y, Formatter.number("y", y));
            }

            @Override
            public Component z(final int z) {
                return MiniMessage.miniMessage().deserialize(this.z, Formatter.number("z", z));
            }

            @Override
            public Component world(final String world) {
                return MiniMessage.miniMessage().deserialize(this.world, Placeholder.unparsed("world", world));
            }
        }

        @ConfigSerializable
        final static class BukkitInventoryLinkChanged implements InventoryLinkChanged {

            private String title;
            private String x;
            private String y;
            private String z;
            private String world;
            private String removed;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component x(final int x) {
                return MiniMessage.miniMessage().deserialize(this.x, Formatter.number("x", x));
            }

            @Override
            public Component y(final int y) {
                return MiniMessage.miniMessage().deserialize(this.y, Formatter.number("y", y));
            }

            @Override
            public Component z(final int z) {
                return MiniMessage.miniMessage().deserialize(this.z, Formatter.number("z", z));
            }

            @Override
            public Component world(final String world) {
                return MiniMessage.miniMessage().deserialize(this.world, Placeholder.unparsed("world", world));
            }

            @Override
            public Component removed() {
                return MiniMessage.miniMessage().deserialize(this.removed);
            }
        }

        @ConfigSerializable
        final static class BukkitBuyPriceChanged implements BuyPriceChanged {

            private String title;
            private String from;
            private String to;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component from(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.from, Formatter.number("amount", amount));
            }

            @Override
            public Component to(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.to, Formatter.number("amount", amount));
            }
        }

        @ConfigSerializable
        final static class BukkitSellPriceChanged implements SellPriceChanged {

            private String title;
            private String from;
            private String to;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component from(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.from, Formatter.number("amount", amount));
            }

            @Override
            public Component to(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.to, Formatter.number("amount", amount));
            }
        }

        @ConfigSerializable
        final static class BukkitQuantityChanged implements QuantityChanged {

            private String title;
            private String from;
            private String to;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component from(final int amount) {
                return MiniMessage.miniMessage().deserialize(this.from, Formatter.number("amount", amount));
            }

            @Override
            public Component to(final int amount) {
                return MiniMessage.miniMessage().deserialize(this.to, Formatter.number("amount", amount));
            }
        }

        @ConfigSerializable
        final static class BukkitNoteChanged implements NoteChanged {

            private String title;
            private String from;
            private String to;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component from(final String note) {
                return MiniMessage.miniMessage().deserialize(this.from, Placeholder.unparsed("note", note));
            }

            @Override
            public Component to(final String note) {
                return MiniMessage.miniMessage().deserialize(this.to, Placeholder.unparsed("note", note));
            }
        }

        @ConfigSerializable
        final static class BukkitNameChanged implements NameChanged {

            private String title;
            private String from;
            private String to;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component from(final String name) {
                return MiniMessage.miniMessage().deserialize(this.from, Placeholder.unparsed("name", name));
            }

            @Override
            public Component to(final String name) {
                return MiniMessage.miniMessage().deserialize(this.to, Placeholder.unparsed("name", name));
            }
        }

        private BukkitBuy buy;
        private BukkitSell sell;
        private BukkitDeposit deposit;
        private BukkitWithdraw withdraw;
        private BukkitLocationChanged locationChanged;
        private BukkitInventoryLinkChanged inventoryLinkChanged;
        private BukkitBuyPriceChanged buyPriceChanged;
        private BukkitSellPriceChanged sellPriceChanged;
        private BukkitQuantityChanged quantityChanged;
        private BukkitNoteChanged noteChanged;
        private BukkitNameChanged nameChanged;

        private String title;
        private String player;
        private String date;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }

        @Override
        public Component player(final Component displayName) {
            return MiniMessage.miniMessage().deserialize(this.player, Placeholder.component("player", displayName));
        }

        @Override
        public Component date(final Date date) {
            return MiniMessage.miniMessage().deserialize(this.date, Formatter.date("created_on", date.toInstant()));
        }
    }

    @ConfigSerializable
    @Accessors(fluent = true, chain = false)
    @Getter
    final static class BukkitModify implements Modify {

        @ConfigSerializable
        final static class BukkitNote implements Note {

            private String title;
            private String request;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component request() {
                return MiniMessage.miniMessage().deserialize(this.request);
            }

        }

        @ConfigSerializable
        final static class BukkitBuy implements Buy {

            private String title;
            private String amount;
            private String notForSale;
            private String request;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }

            @Override
            public Component notForSale() {
                return MiniMessage.miniMessage().deserialize(this.notForSale);
            }

            @Override
            public Component request() {
                return MiniMessage.miniMessage().deserialize(this.request);
            }
        }

        @ConfigSerializable
        final static class BukkitSell implements Sell {

            private String title;
            private String amount;
            private String notBuying;
            private String request;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final double amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }

            @Override
            public Component notBuying() {
                return MiniMessage.miniMessage().deserialize(this.notBuying);
            }

            @Override
            public Component request() {
                return MiniMessage.miniMessage().deserialize(this.request);
            }
        }

        @ConfigSerializable
        final static class BukkitQuantity implements Quantity {

            private String title;
            private String amount;
            private String description;
            private String request;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final int quantity) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("quantity", quantity));
            }

            @Override
            public Component description() {
                return MiniMessage.miniMessage().deserialize(this.description);
            }

            @Override
            public Component request() {
                return MiniMessage.miniMessage().deserialize(this.request);
            }
        }

        @ConfigSerializable
        final static class BukkitConfirm implements Confirm {

            private String title;
            private String description;
            private String location;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component description() {
                return MiniMessage.miniMessage().deserialize(this.description);
            }

            @Override
            public Component location(final String world, final int x, final int y, final int z) {
                return MiniMessage.miniMessage().deserialize(this.location,
                        Placeholder.unparsed("world", world),
                        Formatter.number("x", x),
                        Formatter.number("y", y),
                        Formatter.number("z", z)
                );
            }
        }

        @ConfigSerializable
        final static class BukkitCancel implements Cancel {

            private String title;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }
        }

        private String title;
        private String clickToSet;
        private BukkitNote note;
        private BukkitBuy buy;
        private BukkitSell sell;
        private BukkitQuantity quantity;
        private BukkitConfirm confirm;
        private BukkitCancel cancel;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }

        @Override
        public Component clickToSet() {
            return MiniMessage.miniMessage().deserialize(this.clickToSet);
        }
    }

    @ConfigSerializable
    @Accessors(fluent = true, chain = false)
    @Getter
    final static class BukkitOwner implements Owner {

        @ConfigSerializable
        final static class BukkitDeposit implements Deposit {

            private String title;
            private String bulk;

            @Override
            public Component title(final Component displayName) {
                return MiniMessage.miniMessage().deserialize(this.title, Placeholder.component("item", displayName));
            }

            @Override
            public Component bulk() {
                return MiniMessage.miniMessage().deserialize(this.bulk);
            }
        }

        @ConfigSerializable
        final static class BukkitWithdraw implements Withdraw {

            private String title;
            private String bulk;

            @Override
            public Component title(final Component displayName) {
                return MiniMessage.miniMessage().deserialize(this.title, Placeholder.component("item", displayName));
            }

            @Override
            public Component bulk() {
                return MiniMessage.miniMessage().deserialize(this.bulk);
            }
        }

        @ConfigSerializable
        final static class BukkitChangeRate implements ChangeRate {

            private String title;
            private String amount;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component amount(final int amount) {
                return MiniMessage.miniMessage().deserialize(this.amount, Formatter.number("amount", amount));
            }
        }

        @ConfigSerializable
        final static class BukkitLogs implements Logs {

            private String title;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }
        }

        @ConfigSerializable
        @Accessors(fluent = true, chain = false)
        @Getter
        final static class BukkitInventoryLink implements InventoryLink {

            @ConfigSerializable
            final static class BukkitCancel implements Cancel {

                private String title;
                private String message;

                @Override
                public Component title() {
                    return MiniMessage.miniMessage().deserialize(this.title);
                }

                @Override
                public Component message() {
                    return MiniMessage.miniMessage().deserialize(this.message);
                }

            }

            private String title;
            private String description;
            private String message;
            private BukkitCancel cancel;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }

            @Override
            public Component description() {
                return MiniMessage.miniMessage().deserialize(this.description);
            }

            @Override
            public Component message() {
                return MiniMessage.miniMessage().deserialize(this.message);
            }
        }

        @ConfigSerializable
        final static class BukkitModify implements Modify {

            private String title;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }
        }

        @ConfigSerializable
        final static class BukkitCustomer implements Customer {

            private String title;

            @Override
            public Component title() {
                return MiniMessage.miniMessage().deserialize(this.title);
            }
        }

        private String title;
        private String stock;
        private String stacks;
        private BukkitDeposit deposit;
        private BukkitWithdraw withdraw;
        private BukkitChangeRate changeRate;
        private BukkitLogs logs;
        private BukkitInventoryLink inventoryLink;
        private BukkitModify modify;
        private BukkitCustomer customer;

        @Override
        public Component title() {
            return MiniMessage.miniMessage().deserialize(this.title);
        }

        @Override
        public Component stock(final int stock) {
            return MiniMessage.miniMessage().deserialize(this.stock, Formatter.number("stock", stock));
        }

        @Override
        public Component stacks(final int stacks) {
            return MiniMessage.miniMessage().deserialize(this.stacks, Formatter.number("stacks", stacks));
        }
    }

}
