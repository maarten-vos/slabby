package com.valesmp.slabby.shop;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.valesmp.slabby.SlabbyAPI;
import com.valesmp.slabby.cache.ShopCache;
import com.valesmp.slabby.exception.SlabbyException;
import com.valesmp.slabby.exception.UnrecoverableException;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

public final class SQLiteShopRepository implements ShopRepository, Closeable {

    @SuppressWarnings("FieldCanBeLocal")
    private final SlabbyAPI api;

    private final ConnectionSource connectionSource;

    private final Dao<SQLiteShop, Integer> shopDao;
    private final Dao<SQLiteShopOwner, Integer> shopOwnerDao;
    private final Dao<SQLiteShopLog, Integer> shopLogDao;

    private final ShopCache shopCache;

    @Override
    public Cache shopCache() {
        return this.shopCache;
    }

    public SQLiteShopRepository(final SlabbyAPI api) throws SQLException {
        this.api = api;

        this.connectionSource = new JdbcConnectionSource(api.configuration().database().url());

        this.shopDao = DaoManager.createDao(this.connectionSource, SQLiteShop.class);
        this.shopOwnerDao = DaoManager.createDao(this.connectionSource, SQLiteShopOwner.class);
        this.shopLogDao = DaoManager.createDao(this.connectionSource, SQLiteShopLog.class);

        this.shopCache = new ShopCache(this.shopDao);
    }

    public void initialize() throws SQLException {
        TableUtils.createTableIfNotExists(this.connectionSource, SQLiteShop.class);
        TableUtils.createTableIfNotExists(this.connectionSource, SQLiteShopOwner.class);
        TableUtils.createTableIfNotExists(this.connectionSource, SQLiteShopLog.class);
    }

    @Override
    public void close() {
        try {
            this.connectionSource.close();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T builder(final Class<?> builderType) {
        if (builderType == Shop.Builder.class)
            return (T) SQLiteShop.builder();

        if (builderType == ShopOwner.Builder.class)
            return (T) SQLiteShopOwner.builder();

        if (builderType == ShopLog.Builder.class)
            return (T) SQLiteShopLog.builder();

        throw new IllegalArgumentException();
    }

    @Override
    public void createOrUpdate(final Shop shop) throws SlabbyException {
        try {
            this.shopDao.createOrUpdate((SQLiteShop) shop);
            this.shopDao.refresh((SQLiteShop) shop); //NOTE: Required because the owners collection is not eagerly loaded
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while inserting or updating shop", e);
        }

        this.shopCache.store(shop);
    }

    @Override
    public void delete(final Shop shop) throws SlabbyException {
        try {
            this.shopDao.delete((SQLiteShop) shop);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while deleting shop", e);
        }
        //TODO: Remove from cache. When shops can actually be deleted in the future, we need to check if the result from the cache is not null.
    }

    @Override
    public void delete(final ShopOwner shopOwner) throws SlabbyException {
        try {
            this.shopOwnerDao.delete((SQLiteShopOwner) shopOwner);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while deleting shop owner", e);
        }
    }

    @Override
    public void createOrUpdate(final ShopOwner shopOwner) throws SlabbyException {
        try {
            this.shopOwnerDao.createOrUpdate((SQLiteShopOwner) shopOwner);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while inserting or updating shop owner", e);
        }
    }

    @Override
    public void update(final Shop shop) throws SlabbyException {
        try {
            this.shopDao.update((SQLiteShop) shop);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while updating shop", e);
        }
        this.shopCache.store(shop);
    }

    @Override
    public void update(final ShopOwner shopOwner) throws SlabbyException {
        try {
            this.shopOwnerDao.update((SQLiteShopOwner) shopOwner);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while updating shop owner", e);
        }
    }

    @Override
    public void refresh(final Shop shop) throws SlabbyException {
        try {
            this.shopDao.refresh((SQLiteShop) shop);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while refreshing shop", e);
        }
    }

    @Override
    public void refresh(final ShopOwner shopOwner) throws SlabbyException {
        try {
            this.shopOwnerDao.refresh((SQLiteShopOwner) shopOwner);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while refreshing shop owner", e);
        }
    }

    @Override
    public void markAsDeleted(final UUID uniqueId, final Shop shop) throws SlabbyException {
        final var hasLocation = shop.hasLocation();
        final var x = shop.x();
        final var y = shop.y();
        final var z = shop.z();
        final var world = shop.world();

        final var hasInventory = shop.hasInventory();
        final var inventoryX = shop.inventoryX();
        final var inventoryY = shop.inventoryY();
        final var inventoryZ = shop.inventoryZ();
        final var inventoryWorld = shop.inventoryWorld();

        shop.state(Shop.State.DELETED);
        shop.location(null, null, null, null);

        //NOTE: We remove the inventory link because otherwise a new shop cannot be linked to this location
        //NOTE: We also cannot add the shop state to the index because then a shop cannot be restored if another shop uses that inventory location
        shop.inventory(null, null, null, null);

        this.transaction(() -> {
            this.shopDao.update((SQLiteShop) shop);

            final var log = api.repository()
                    .<ShopLog.Builder>builder(ShopLog.Builder.class)
                    .action(ShopLog.Action.SHOP_DESTROYED)
                    .uniqueId(uniqueId)
                    .build();

            shop.logs().add(log);

            return null;
        });

        if (hasLocation)
            this.shopCache.delete(x, y, z, world);

        if (hasInventory)
            this.shopCache.delete(inventoryX, inventoryY, inventoryZ, inventoryWorld);
    }

    @Override
    public Optional<Shop> shopAt(final int x, final int y, final int z, final String world) throws SlabbyException {
        final var cached = this.shopCache.get(x, y, z, world);

        if (cached != null) {
            if (cached.hasIdentity()) {
                final var shop = cached.get();

                //NOTE: Because the cache can return a shop by inventory or shop location, we need to check if this is actually the shop's location.
                if (shop.isAt(x, y, z, world))
                    return Optional.of(shop);

                if (!shop.isInventoryAt(x, y, z, world))
                    this.shopCache.delete(x, y, z, world);
            } else {
                return Optional.empty();
            }
        }

        try {
            final var result = this.shopDao.queryBuilder()
                    .where()
                    .eq(Shop.Names.STATE, Shop.State.ACTIVE)
                    .and()
                    .eq(Shop.Names.X, x)
                    .and()
                    .eq(Shop.Names.Y, y)
                    .and()
                    .eq(Shop.Names.Z, z)
                    .and()
                    .eq(Shop.Names.WORLD, world)
                    .queryForFirst();

            this.shopCache.store(x, y, z, world, result);

            return Optional.ofNullable(result);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while retrieving shop by location", e);
        }
    }

    @Override
    public Optional<Shop> shopWithInventoryAt(final int x, final int y, final int z, final String world) throws SlabbyException {
        final var cached = this.shopCache.get(x, y, z, world);

        if (cached != null) {
            if (cached.hasIdentity()) {
                final var shop = cached.get();

                //NOTE: Because the cache can return a shop by inventory or shop location, we need to check if this is actually the inventory location
                if (shop.isInventoryAt(x, y, z, world)) {
                    return Optional.of(shop);
                }

                if (!shop.isAt(x, y, z, world))
                    this.shopCache.delete(x, y, z, world);
            } else {
                return Optional.empty();
            }
        }

        try {
            final var result = this.shopDao.queryBuilder()
                    .where()
                    .eq(Shop.Names.STATE, Shop.State.ACTIVE)
                    .and()
                    .eq(Shop.Names.INVENTORY_X, x)
                    .and()
                    .eq(Shop.Names.INVENTORY_Y, y)
                    .and()
                    .eq(Shop.Names.INVENTORY_Z, z)
                    .and()
                    .eq(Shop.Names.INVENTORY_WORLD, world)
                    .queryForFirst();

            this.shopCache.store(x, y, z, world, result);

            return Optional.ofNullable(result);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while retrieving shop by inventory location", e);
        }
    }

    @Override
    public <T> Optional<Shop> shopById(final T id) throws SlabbyException {
        if (id == null)
            return Optional.empty();

        try {
            return Optional.ofNullable(this.shopDao.queryForId((int)id));
        } catch (SQLException e) {
            throw new UnrecoverableException("Error while retrieving shop by id");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Shop> shopsOf(final UUID uniqueId, final Shop.State state) throws SlabbyException {
        try {
            final var result = this.shopOwnerDao.queryBuilder()
                    .join(this.shopDao.queryBuilder().where().eq(Shop.Names.STATE, state).queryBuilder())
                    .where().eq(ShopOwner.Names.UNIQUE_ID, uniqueId)
                    .query()
                    .stream()
                    .map(SQLiteShopOwner::shop)
                    .toList();
            return (Collection<Shop>) (Collection<? extends Shop>) result;
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while retrieving shops for owner", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Shop> shopsByItem(final String item) throws SlabbyException {
        try {
            final var result = this.shopDao.queryBuilder()
                    .where()
                    .eq(Shop.Names.STATE, Shop.State.ACTIVE)
                    .and()
                    .eq(Shop.Names.ITEM, item)
                    .query();
            return (Collection<Shop>) (Collection<? extends Shop>) result;
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while retrieving shops by item", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Shop> shopsInArea(final int minX, final int minZ, final int maxX, final int maxZ, final String world) {
        try {
            final var result = this.shopDao.queryBuilder()
                    .where()
                    .eq(Shop.Names.STATE, Shop.State.ACTIVE)
                    .and()
                    .eq(Shop.Names.WORLD, world)
                    .and()
                    .between(Shop.Names.X, minX, maxX)
                    .and()
                    .between(Shop.Names.Z, minZ, maxZ)
                    .query();

            return (Collection<Shop>) (Collection<? extends Shop>) result;
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while retrieving shops by area", e);
        }
    }

    @Override
    public boolean isShopOrInventory(final int x, final int y, final int z, final String world) throws SlabbyException {
        final var cached = this.shopCache.get(x, y, z, world);

        if (cached != null) {
            if (cached.hasIdentity()) {
                final var shop = cached.get();

                return shop.isInventoryAt(x, y, z, world) || shop.isAt(x, y, z, world);
            } else {
                return false;
            }
        }

        try {
            final var where = this.shopDao.queryBuilder().where();

            final var result = where
                    .and(where.eq(Shop.Names.STATE, Shop.State.ACTIVE), where.or(
                            where.and(where.eq(Shop.Names.INVENTORY_X, x), where.eq(Shop.Names.INVENTORY_Y, y), where.eq(Shop.Names.INVENTORY_Z, z), where.eq(Shop.Names.INVENTORY_WORLD, world)),
                            where.and(where.eq(Shop.Names.X, x), where.eq(Shop.Names.Y, y), where.eq(Shop.Names.Z, z), where.eq(Shop.Names.WORLD, world))))
                    .queryForFirst();

            this.shopCache.store(x, y, z, world, result);

            return result != null;
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while checking if location is a shop or inventory", e);
        }
    }

    @Override
    public <T> T transaction(final Callable<T> transaction) throws SlabbyException {
        try {
            return TransactionManager.callInTransaction(this.connectionSource, transaction);
        } catch (final SQLException e) {
            throw new UnrecoverableException("Error while running transaction", e);
        }
    }

}
