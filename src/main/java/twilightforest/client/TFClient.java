package twilightforest.client;

import net.fabricmc.api.ClientModInitializer;
import twilightforest.TwilightForest;

/**
 * Fabric {@link ClientModInitializer} entrypoint (declared as {@code "client"} in fabric.mod.json).
 *
 * <p>Replaces the Forge client bootstrap ({@code ClientInitiator} + {@code FMLClientSetupEvent} and the
 * various {@code @SubscribeEvent} client-side registration events in {@code twilightforest.client.*}).
 *
 * <p><b>Status:</b> foundation stub. Client porting is Phase 6 (see HANDOFF.md). The work that lands here:
 * <ul>
 *   <li>{@code EntityRendererRegistry.register(...)} — entity renderers (was {@code EntityRenderersEvent.RegisterRenderers})</li>
 *   <li>{@code EntityModelLayerRegistry.registerModelLayer(...)} — model layers (was {@code RegisterLayerDefinitions})</li>
 *   <li>{@code BlockEntityRendererFactories.register(...)} — BER (was {@code RegisterRenderers})</li>
 *   <li>{@code ColorProviderRegistry.BLOCK/ITEM.register(...)} — color handlers (was {@code RegisterColorHandlersEvent})</li>
 *   <li>{@code BlockRenderLayerMap.INSTANCE.putBlock(...)} — render type per block (was {@code ItemBlockRenderTypes})</li>
 *   <li>{@code ParticleFactoryRegistry.getInstance().register(...)} — particle factories</li>
 *   <li>{@code ModelLoadingPlugin} — extra/baked models (was {@code ModelEvent.RegisterAdditional}/{@code BakingCompleted})</li>
 *   <li>{@code KeyBindingHelper.registerKeyBinding(...)} — keybinds</li>
 *   <li>Dimension special effects (Twilight sky/fog) — Forge-only event → Mixin into the effects registry (sharp-edge #9)</li>
 * </ul>
 */
public final class TFClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TwilightForest.LOGGER.info("Twilight Forest client init (Fabric port) — stub; renderers/colors/models pending Phase 6.");
        // TODO(fabric-port) Phase 6: see class javadoc for the registration calls to add here.
    }
}
