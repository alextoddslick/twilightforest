package twilightforest.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.registries.RegistryObject<T>}.
 * <p>
 * Twilight Forest's {@code init/*} classes hold thousands of these as {@code static final}
 * fields. On Forge they are lazy handles resolved when the mod event bus fires; here they are
 * resolved the moment their owning {@link DeferredRegister} is {@link DeferredRegister#register()
 * flushed} during {@code onInitialize}. The public surface mirrors the subset of the Forge API
 * the codebase actually uses: {@link #get()}, {@link #getId()}, {@link #getKey()},
 * {@link #isPresent()} and {@link #getHolder()}.
 *
 * @see DeferredRegister
 */
public final class RegistryObject<T> implements Supplier<T> {

    private final ResourceLocation id;
    private final ResourceKey<T> key;
    private final Supplier<? extends T> factory;

    // Resolved at flush time by DeferredRegister.register().
    private T value;
    private Holder<T> holder;

    RegistryObject(ResourceLocation id, ResourceKey<T> key, Supplier<? extends T> factory) {
        this.id = Objects.requireNonNull(id, "id");
        this.key = Objects.requireNonNull(key, "key");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    /** Package-private: invoked by {@link DeferredRegister#register()} once the value is in the registry. */
    void bind(T value, Holder<T> holder) {
        this.value = value;
        this.holder = holder;
    }

    Supplier<? extends T> factory() {
        return factory;
    }

    /**
     * @return the registered value.
     * @throws IllegalStateException if accessed before the owning {@link DeferredRegister} was flushed.
     */
    @Override
    public T get() {
        if (this.value == null) {
            throw new IllegalStateException(
                "RegistryObject '" + this.id + "' accessed before registration. "
                    + "Call the owning DeferredRegister.register() during mod init before use.");
        }
        return this.value;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceKey<T> getKey() {
        return this.key;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    /** Mirrors Forge's {@code Optional<Holder<T>> getHolder()}. Empty until flushed. */
    public Optional<Holder<T>> getHolder() {
        return Optional.ofNullable(this.holder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistryObject<?> other)) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "RegistryObject{" + this.id + (this.value == null ? " (unbound)" : "") + "}";
    }
}
