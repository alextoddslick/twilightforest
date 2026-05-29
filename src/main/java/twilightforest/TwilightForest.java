package twilightforest;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twilightforest.init.TFSounds;

/**
 * Fabric {@link ModInitializer} entrypoint (declared as {@code "main"} in fabric.mod.json).
 *
 * <p>This replaces the Forge {@code @Mod}-annotated {@link TwilightForestMod} constructor. During the
 * port, {@link TwilightForestMod} is being kept as the home of the mod's static helpers
 * ({@code ID}, {@code prefix(...)}, {@code LOGGER}, rarity, texture helpers) and will be stripped of
 * its Forge wiring; the lifecycle wiring lives here.
 *
 * <p><b>Status:</b> foundation only. The single converted registry ({@link TFSounds}) is flushed
 * below to prove the {@link twilightforest.util.DeferredRegister}/{@link twilightforest.util.RegistryObject}
 * shim end-to-end. Every other registry is listed in the exact Forge registration order from
 * {@code TwilightForestMod} as a commented checklist — uncomment each line as its {@code init/*}
 * class is converted to the shim. See HANDOFF.md (Phase 1) and §5 sharp-edge #1 (ordering).
 */
public final class TwilightForest implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(TwilightForestMod.ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Twilight Forest (Fabric port for 1.20.1) initializing — registration foundation only.");

        registerContent();
        // TODO(fabric-port) Phase 2: TFPacketHandler.init();  (Forge SimpleChannel → ServerPlay/ClientPlayNetworking)
        // TODO(fabric-port) Phase 2: capabilities → Fabric Data Attachment API (AttachmentRegistry)
        // TODO(fabric-port) Phase 2: events → Fabric callbacks + mixins (see twilightforest/events/*)
        // TODO(fabric-port) Phase 5: TFBlocks.tfCompostables()/tfBurnables()/tfPots() → ComposterBlock/FlammableBlockRegistry
        // TODO(fabric-port) Phase 5: TFDispenserBehaviors.init();
        // TODO(fabric-port) Phase 3: TFEntities.registerSpawnPlacements() + FabricDefaultAttributeRegistry
        // TODO(fabric-port) Phase 7: TFConfig → POJO+Gson (optionally Cloth Config)
        // TODO(fabric-port) Phase 4: biome source / chunk generator codec registration (was registerSerializers)
        // TODO(fabric-port) Phase 5: command registration → CommandRegistrationCallback (was registerCommands)
    }

    /**
     * Flush all static ({@code BuiltInRegistries}-backed) DeferredRegisters in a deterministic order.
     * Mirrors {@code TwilightForestMod}'s Forge {@code modbus} registration block. Uncomment each as
     * the corresponding {@code init/*} class is ported to {@link twilightforest.util.DeferredRegister}.
     */
    private static void registerContent() {
        // ── CONVERTED ──────────────────────────────────────────────────────────
        TFSounds.SOUNDS.register();

        // ── PENDING (order preserved from the Forge branch) ─────────────────────
        // TFBannerPatterns.BANNER_PATTERNS.register();
        // TFBlocks.BLOCKS.register();              // blocks BEFORE block items
        // TFBlockEntities.BLOCK_ENTITIES.register();
        // TFItems.ITEMS.register();                // includes spawn eggs / block items
        // TFMenuTypes.CONTAINERS.register();
        // TFEnchantments.ENCHANTMENTS.register();
        // TFEntities.ENTITIES.register();
        // TFEntities.SPAWN_EGGS.register();
        // TFMobEffects.MOB_EFFECTS.register();
        // TFParticleType.PARTICLE_TYPES.register();
        // TFRecipes.RECIPE_SERIALIZERS.register();
        // TFRecipes.RECIPE_TYPES.register();
        // TFStats.STATS.register();
        // TFLoot.CONDITIONS.register();
        // TFLoot.FUNCTIONS.register();
        // TFFeatures.FEATURES.register();
        // TFFeatureModifiers.FOLIAGE_PLACERS.register();
        // TFFeatureModifiers.TRUNK_PLACERS.register();
        // TFFeatureModifiers.TREE_DECORATORS.register();
        // TFFeatureModifiers.PLACEMENT_MODIFIERS.register();
        // TFStructurePieceTypes.STRUCTURE_PIECE_TYPES.register();
        // TFStructureTypes.STRUCTURE_TYPES.register();
        // TFStructurePlacementTypes.STRUCTURE_PLACEMENT_TYPES.register();
        // TFLootModifiers.LOOT_MODIFIERS.register();   // Phase: loot-modifiers (also LootTableEvents.MODIFY)

        // ── DYNAMIC / DATAPACK registries — NOT via the shim (Phase 4 worldgen) ──
        //   BiomeKeys.BIOMES, TFStructures.STRUCTURES, TFStructureSets.STRUCTURE_SETS,
        //   TFDimensionSettings.DIMENSION_TYPES / NOISE_GENERATORS
        //   → emit as datapack JSON via FabricDynamicRegistryProvider, or build with RegistrySetBuilder.

        // ── CUSTOM registries — FabricRegistryBuilder (Phase 1) ──────────────────
        //   DwarfRabbitVariant.DWARF_RABBITS, TinyBirdVariant.TINY_BIRDS
    }
}
