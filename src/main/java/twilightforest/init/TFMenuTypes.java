package twilightforest.init;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import twilightforest.util.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import twilightforest.util.RegistryObject;
import twilightforest.TwilightForestMod;
import twilightforest.client.UncraftingScreen;
import twilightforest.inventory.UncraftingMenu;

public class TFMenuTypes {

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, TwilightForestMod.ID);

	public static final RegistryObject<MenuType<UncraftingMenu>> UNCRAFTING = CONTAINERS.register("uncrafting",
			() -> new MenuType<>(UncraftingMenu::fromNetwork));

	@Environment(EnvType.CLIENT)
	public static void renderScreens() {
		MenuScreens.register(UNCRAFTING.get(), UncraftingScreen::new);
	}
}
