package twilightforest.client.particle.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import twilightforest.init.TFParticleType;

import org.jetbrains.annotations.NotNull;

public class LeafParticleData implements ParticleOptions {
	public final int r;
	public final int g;
	public final int b;

	public LeafParticleData(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@NotNull
	@Override
	public ParticleType<?> getType() {
		return TFParticleType.FALLEN_LEAF.get();
	}

	public static Codec<LeafParticleData> codecLeaf() {
		return RecordCodecBuilder.create((instance) -> instance.group(
				Codec.INT.fieldOf("r").forGetter((obj) -> obj.r),
				Codec.INT.fieldOf("g").forGetter((obj) -> obj.g),
				Codec.INT.fieldOf("b").forGetter((obj) -> obj.b))
				.apply(instance, LeafParticleData::new));
	}

	@Override
	public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(r);
		buf.writeVarInt(g);
		buf.writeVarInt(b);
	}

	@NotNull
	@Override
	public String writeToString() {
		return String.format("%d %d %d", r, g, b);
	}

	public static class Deserializer implements ParticleOptions.Deserializer<LeafParticleData> {
		@NotNull
		@Override
		public LeafParticleData fromCommand(@NotNull ParticleType<LeafParticleData> type, @NotNull StringReader reader) throws CommandSyntaxException {
			reader.skipWhitespace();
			int r = reader.readInt();
			reader.skipWhitespace();
			int g = reader.readInt();
			reader.skipWhitespace();
			int b = reader.readInt();
			return new LeafParticleData(r, g, b);
		}

		@NotNull
		@Override
		public LeafParticleData fromNetwork(@NotNull ParticleType<LeafParticleData> type, FriendlyByteBuf buf) {
			return new LeafParticleData(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
		}
	}
}
