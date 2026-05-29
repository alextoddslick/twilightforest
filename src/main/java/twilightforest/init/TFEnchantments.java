package twilightforest.init;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import twilightforest.util.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import twilightforest.util.RegistryObject;
import twilightforest.TwilightForestMod;
import twilightforest.enchantment.*;
import twilightforest.item.ChainBlockItem;

public class TFEnchantments {

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, TwilightForestMod.ID);

	public static final RegistryObject<Enchantment> FIRE_REACT = ENCHANTMENTS.register("fire_react", () -> new FireReactEnchantment(Enchantment.Rarity.UNCOMMON));
	public static final RegistryObject<Enchantment> CHILL_AURA = ENCHANTMENTS.register("chill_aura", () -> new ChillAuraEnchantment(Enchantment.Rarity.UNCOMMON));
	public static final RegistryObject<Enchantment> DESTRUCTION = ENCHANTMENTS.register("destruction", () -> new DestructionEnchantment(Enchantment.Rarity.RARE));

	public static final EnchantmentCategory BLOCK_AND_CHAIN = EnchantmentCategory.create("twilightforest_block_and_chain", item -> item instanceof ChainBlockItem);
}
