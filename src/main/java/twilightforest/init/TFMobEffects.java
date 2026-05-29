package twilightforest.init;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import twilightforest.util.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import twilightforest.util.RegistryObject;
import twilightforest.TwilightForestMod;
import twilightforest.potions.FrostedEffect;

public class TFMobEffects {

	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, TwilightForestMod.ID);

	public static final RegistryObject<MobEffect> FROSTY = MOB_EFFECTS.register("frosted", () -> new FrostedEffect(MobEffectCategory.HARMFUL, 0x56CBFD));
}
