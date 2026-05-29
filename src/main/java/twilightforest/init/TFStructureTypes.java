package twilightforest.init;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import twilightforest.util.DeferredRegister;
import twilightforest.util.RegistryObject;
import twilightforest.TwilightForestMod;
import twilightforest.world.components.structures.start.LegacyLandmark;

public class TFStructureTypes {
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, TwilightForestMod.ID);

	public static final RegistryObject<StructureType<LegacyLandmark>> LEGACY_LANDMARK = STRUCTURE_TYPES.register("legacy_landmark", () -> () -> LegacyLandmark.CODEC);
}
