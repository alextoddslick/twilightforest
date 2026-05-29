package twilightforest.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.registries.DeferredRegister<T>}, backed by
 * a vanilla {@link Registry}. It lets Twilight Forest keep its existing {@code init/*} pattern —
 * declare entries as {@code static final RegistryObject<T>} fields, then flush them once during
 * mod init — without the Forge mod event bus.
 *
 * <h2>Usage (mirrors the Forge call shape)</h2>
 * <pre>{@code
 * // Forge:  DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID)
 * public static final DeferredRegister<SoundEvent> SOUNDS =
 *         DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TwilightForestMod.ID);
 *
 * public static final RegistryObject<SoundEvent> FOO = SOUNDS.register("foo", () -> ...);
 *
 * // In TwilightForest#onInitialize(), instead of Forge's SOUNDS.register(modBus):
 * SOUNDS.register();
 * }</pre>
 *
 * <h2>Scope</h2>
 * This handles the <b>static</b> {@code BuiltInRegistries}-backed registries (blocks, items,
 * entities, block entities, sounds, mob effects, enchantments, menus, particle types,
 * recipe serializers/types, banner patterns, custom stats, features, foliage placers,
 * trunk placers, placement modifier types, structure piece/type/placement-type, loot
 * condition/function types). These cover the overwhelming majority of TF's entries.
 * <p>
 * It deliberately does <b>not</b> cover:
 * <ul>
 *   <li><b>Datapack / dynamic registries</b> — biomes, configured/placed features, structures,
 *       structure sets, dimension types, noise settings. On Fabric these are datapack JSON
 *       (emitted via the datagen phase) or built with {@code RegistrySetBuilder}; they do not
 *       register through this shim. See HANDOFF.md §Phase 4 (Worldgen).</li>
 *   <li><b>New custom registries</b> — {@code DwarfRabbitVariant}, {@code TinyBirdVariant}. Create
 *       these with {@code FabricRegistryBuilder} in the initializer. See HANDOFF.md §Phase 1.</li>
 * </ul>
 *
 * <p><b>Ordering matters.</b> Unlike Forge's bus-resolved ordering, {@link #register()} is eager.
 * Flush registers in a deterministic order from a single place in the {@code ModInitializer}
 * (blocks before block items, etc.) — see HANDOFF.md §5 "Known sharp edges" #1.
 *
 * @param <T> the registry element type
 * @see RegistryObject
 */
public final class DeferredRegister<T> {

    private final Registry<T> registry;
    private final String modid;
    private final List<RegistryObject<? extends T>> entries = new ArrayList<>();
    private boolean flushed = false;

    private DeferredRegister(Registry<T> registry, String modid) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.modid = Objects.requireNonNull(modid, "modid");
    }

    /**
     * @param registry the vanilla registry to register into, e.g. {@code BuiltInRegistries.SOUND_EVENT}
     * @param modid    the namespace for all entries
     */
    public static <T> DeferredRegister<T> create(Registry<T> registry, String modid) {
        return new DeferredRegister<>(registry, modid);
    }

    /**
     * Declare an entry. The supplier is invoked lazily at {@link #register() flush} time, matching
     * Forge semantics (so referencing other not-yet-registered objects in the supplier body is safe
     * as long as they are flushed first).
     */
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
        if (this.flushed) {
            throw new IllegalStateException(
                "Cannot register '" + name + "' on " + this.registry.key().location()
                    + " after register() has already flushed this DeferredRegister.");
        }
        ResourceLocation id = new ResourceLocation(this.modid, name);
        ResourceKey<I> key = (ResourceKey<I>) ResourceKey.create(this.registry.key(), id);
        RegistryObject<I> obj = new RegistryObject<>(id, key, supplier);
        this.entries.add(obj);
        return obj;
    }

    /**
     * Perform the actual registration into the backing vanilla registry and bind every
     * {@link RegistryObject}. Call exactly once during {@code onInitialize}. Idempotent.
     */
    public void register() {
        if (this.flushed) {
            return;
        }
        this.flushed = true;
        for (RegistryObject<? extends T> entry : this.entries) {
            flush(entry);
        }
    }

    @SuppressWarnings("unchecked")
    private <I extends T> void flush(RegistryObject<I> entry) {
        Registry<I> reg = (Registry<I>) this.registry;
        I value = (I) entry.factory().get();
        Registry.register(reg, entry.getId(), value);
        Holder<I> holder = reg.getHolderOrThrow(entry.getKey());
        entry.bind(value, holder);
    }

    /** @return the namespace shared by all entries. */
    public String getNamespace() {
        return this.modid;
    }

    /** @return the backing vanilla registry. */
    public Registry<T> getRegistry() {
        return this.registry;
    }
}
