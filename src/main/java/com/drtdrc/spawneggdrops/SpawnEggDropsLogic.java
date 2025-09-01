package com.drtdrc.spawneggdrops;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

import java.util.HashMap;
import java.util.Map;

public final class SpawnEggDropsLogic {
    private static final float BASE_CHANCE       = 0.005f;  // 0.5%
    private static final float LOOTING_PER_LEVEL = 0.005f; // +0.5% per Looting level

    private static final Map<RegistryKey<LootTable>, Item> EGG_BY_KEY = new HashMap<>();

    public static void register() {
        Registries.ENTITY_TYPE.forEach(type -> {
            SpawnEggItem egg = SpawnEggItem.forEntity(type);
            if (egg == null) return;

            RegistryKey<LootTable> lootKey = type.getLootTableKey().isPresent() ? type.getLootTableKey().get() : null;
            EGG_BY_KEY.put(lootKey, egg);
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Item egg = EGG_BY_KEY.get(key);
            if (egg == null) return;

            LootPool.Builder pool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(KilledByPlayerLootCondition.builder())
                    .conditionally(RandomChanceWithEnchantedBonusLootCondition.builder(
                            registries, BASE_CHANCE, LOOTING_PER_LEVEL
                    ))
                    .with(ItemEntry.builder(egg));

            tableBuilder.pool(pool);
        });
    }

    private SpawnEggDropsLogic() {}
}
