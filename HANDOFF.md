# Twilight Forest — Forge 1.19.2 → Fabric 1.20.1 Port: HANDOFF

**Branch:** `fabric/1.20.1`  •  **Fork:** `github.com/alextoddslick/twilightforest` (`origin`), upstream `github.com/TeamTwilight/twilightforest`
> Named `fabric/1.20.1`, **not** `1.20.1`, because the upstream/fork already has an official **NeoForge** `1.20.1`
> branch with unrelated history. This branch is the Fabric port off `1.19.2`; the name avoids clobbering that reference.
**Last updated:** 2026-05-29 (foundation session)

> This is the living status doc. The full subsystem-by-subsystem roadmap (effort, gotchas,
> phase ordering, runtime sharp edges) lives in **[PORT_PLAN.md](PORT_PLAN.md)** — read it before porting any subsystem.

---

## 📉 Compile-error burndown (`./gradlew compileJava`)

| Step | Errors | Δ |
|---|---:|---:|
| Baseline (foundation only) | **8,140** | — |
| Mechanical renames: `@OnlyIn`→`@Environment`, `javax.annotation`→`org.jetbrains`, drop nonnull-meta | 7,431 | −709 |
| Access-widener field entries (Entity.level, Parrot.MOB_SOUND_MAP, StructurePiece.*, StructureBlockInfo.*) — descriptors verified via `javap` | 7,215 | −216 |
| Global `RegistryObject`/`DeferredRegister` import → shim; `MaterialColor`→`MapColor` | 6,713 | −502 |
| `BlockBehaviour.Properties.of(Material[, color])` → `of()[.mapColor(color)]` | **6,545** | −168 |

**~20% cleared by safe, verified mechanical passes.** The remaining ~6,545 are genuine subsystem rewrites (see
"What's left" below) — they need per-file engineering and are interdependent, so error count now drops in vertical
slices (a whole subsystem at a time), not via global scripts.

### What's left (by the numbers, largest first)
- **Datagen model providers** (~500: `ModelFile`, `BlockModelBuilder`, `ConfiguredModel`, `ExistingFileHelper`, `getMultipartBuilder`, `models()`) — Forge datagen → Fabric `FabricModelProvider`/data-gen API. Isolated to `data/`. (Phase 6)
- **`Material` removal — behavior fidelity** (~150: `state.getMaterial()`, `Material.X` comparisons) — semantic; map to `state.liquid()`/`state.isSolid()`/`blocksMotion()`/tags. NOTE: the `Properties.of()` script above dropped Material's *implied* behavior (non-solid PLANT, no-collision BARRIER, flammability) — blocks need explicit `.noCollission()`/`.replaceable()`/etc. for parity. **Sharp edge.**
- **`getLevel()` → `level()`** (~191) — only on `Entity` subclasses (BlockEntity/UseOnContext keep `getLevel()`), so it's receiver-type-dependent — not a blind replace.
- **Registration create() mapping** — `ForgeRegistries.X`/`Registry.X_REGISTRY` → `BuiltInRegistries.Y` in `init/*` (static) ; dynamic ones (biomes/features/structures/dimensions) → Phase 4 datapack JSON.
- **Events** (`@SubscribeEvent` ×65 + Forge event classes) → Fabric callbacks + mixins (Phase 2).
- **Networking** (`SimpleChannel`/`PacketDistributor`) → `ServerPlay/ClientPlayNetworking` (Phase 2).
- **Config** (`ForgeConfigSpec` ×41) → POJO+Gson (Phase 7).
- **Vector3f/Matrix4f/Quaternion** (~90) → JOML (`org.joml.*`) + `Axis.rotationDegrees` (client/render).
- **Worldgen codecs** (`BIOME_REGISTRY` static refs ×50, ChunkGenerator/BiomeSource codecs) → `RegistryOps` context (Phase 4 — the long pole).

---

## ⚠️ Read this first: what this actually is

The official `1.19.2` branch is the **Forge** build (it is *not*, and never was, a Fabric mod — every TeamTwilight
branch from 1.7.10 → 26.1.x is Forge/NeoForge, and an official **NeoForge `1.20.1`** branch already exists upstream).
This branch is a **ground-up Forge→Fabric conversion** of that 1.19.2 code onto MC 1.20.1, started from scratch here.

**Scale:** 1,200 Java files / ~120k LOC; 292 files import `net.minecraftforge`. Realistic effort to a playable mod:
**~7–10 weeks for one experienced Fabric modder** (worldgen alone is the 2–3 week long pole). This session built the
**foundation + analysis**, not a finished mod. **The full project does not compile yet** — that is expected; the
remaining ~1,190 files still use Forge APIs and are ported phase by phase per PORT_PLAN.md.

---

## ✅ Done this session (foundation — verified)

| Area | What | Verified? |
|---|---|---|
| **Build system** | Replaced ForgeGradle with **Fabric Loom 1.7.4**; new `build.gradle`, `settings.gradle`, `gradle.properties` | ✅ `./gradlew help` → BUILD SUCCESSFUL; Loom downloaded & remapped MC 1.20.1 + Fabric API (54 modules) |
| **Gradle** | Wrapper `7.2` → **`8.8`** (Loom requirement) | ✅ runs on arm64 JDK 17 |
| **Mappings** | **Official Mojang mappings** (`loom.officialMojangMappings()`) — keeps ~120k vanilla calls unchanged | ✅ MC jar mapped to Mojmap, on classpath |
| **Mod metadata** | `fabric.mod.json` (entrypoints `main`/`client`, mixins, accessWidener, deps), `pack.mcmeta` → format 15 | — |
| **Access widener** | `accesstransformer.cfg` (164 lines, SRG names) → `twilightforest.accesswidener` (Mojmap). Version-stable entries active; rest staged as a **pre-resolved migration list** with `⚠ REMOVED/CHANGED in 1.20.1` flags | partial (by design — grows with compile errors) |
| **Registration shim** | `twilightforest.util.DeferredRegister` + `RegistryObject` — Fabric-backed, mirrors the Forge API surface the code actually uses (`get`/`getId`×358/`getKey`×37/`isPresent`×20/`getHolder`×3) | ✅ **compiles in isolation against MC 1.20.1 + Fabric** |
| **Worked example** | `TFSounds.java` fully converted to the shim (`BuiltInRegistries.SOUND_EVENT`, `SoundEvent.createVariableRangeEvent`) — the canonical pattern for the other ~30 `init/*` files | — |
| **Entrypoints** | `TwilightForest` (ModInitializer, flushes registration in deterministic order — registers sounds, all others as an ordered checklist) + `client.TFClient` (ClientModInitializer stub) | — |
| **Mixin configs** | Empty-but-valid `twilightforest.mixins.json` + `twilightforest.client.mixins.json` (for ASMHooks→Mixin + events) | — |
| **Analysis** | 14-subsystem conversion map + phased master plan → **PORT_PLAN.md** | — |

### Key decisions (and why)
1. **Official Mojang mappings, not Yarn.** The Forge branch already used Mojmap names (via Parchment), so vanilla
   calls compile unchanged. Yarn would force renaming ~120k lines — the whole reason to pick Mojmap. (PORT_PLAN §4.2)
2. **A `DeferredRegister`/`RegistryObject` shim** (vs. rewriting every call site to bare `Registry.register`).
   ~358 `.getId()` + hundreds of `.get()` call sites stay untouched; only the `init/*` `create(...)` lines and the
   flush in the initializer change. The shim handles **static** `BuiltInRegistries` registries only — dynamic/datapack
   registries (biomes, features, structures, dimension types, noise) and the 2 custom registries are out of scope by
   design (Phase 4 + `FabricRegistryBuilder`; documented in the shim Javadoc).
3. **Access widener grows incrementally.** Loom hard-fails on a widener entry pointing at a member missing in 1.20.1,
   and several 1.19.2 AT entries reference removed members (e.g. `BlockBehaviour.material` — `Material` was deleted in
   1.20). So only version-stable entries are active; the rest are pre-resolved (Mojmap name + descriptor known) and
   activated as each using-class is ported.

---

## How to build / run

```bash
# Use an arm64 JDK 17 (the system default JDK 25 x86_64 is rejected by Gradle 8.8):
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

./gradlew help          # validates the build foundation (works today)
./gradlew build         # ❌ fails today — ~1,190 files still use Forge APIs (expected; that's the port)
./gradlew runClient     # available once enough of Phase 1–3 compiles
```

Compile-check the shim in isolation (how this session verified it):
```bash
# classpath was dumped via:  ./gradlew printCompileCp -q --init-script /tmp/printcp.gradle
javac -cp "$(cat /tmp/tf_cp.txt)" -d /tmp/out \
  src/main/java/twilightforest/util/RegistryObject.java \
  src/main/java/twilightforest/util/DeferredRegister.java
```

---

## ▶️ Next steps (immediate)

Per PORT_PLAN.md phase order. The next engineer should:

1. **Finish Phase 1 (Registration).** Convert the remaining `init/*` static-registry classes to the shim, following the
   `TFSounds` pattern. For each: swap the 3 Forge imports → `twilightforest.util.*` + `BuiltInRegistries`; map the
   registry (`ForgeRegistries.BLOCKS`→`BuiltInRegistries.BLOCK`, `ForgeRegistries.ITEMS`→`BuiltInRegistries.ITEM`,
   `Registry.X_REGISTRY`→`BuiltInRegistries.X` / `Registries.X`, etc.); then uncomment its line in
   `TwilightForest#registerContent()` (order matters — blocks before block items). The `init/custom` custom registries
   (`DwarfRabbitVariant`, `TinyBirdVariant`) use `FabricRegistryBuilder` instead.
2. **Convert `TwilightForestMod`** from the Forge `@Mod` class into the static-helper holder only (keep `ID`, `prefix`,
   `LOGGER`, rarity, texture helpers; delete the Forge constructor/event wiring — that lives in `TwilightForest` now).
3. **Then Phase 2** (networking → capabilities → events) and **start the Phase 4 worldgen track in parallel** — it's the
   long pole and shouldn't wait on content phases.

### Registry mapping cheat-sheet (Phase 1)
`ForgeRegistries.BLOCKS→BuiltInRegistries.BLOCK` · `ITEMS→ITEM` · `BLOCK_ENTITY_TYPES→BLOCK_ENTITY_TYPE` ·
`ENTITY_TYPES→ENTITY_TYPE` · `MENU_TYPES→MENU` · `MOB_EFFECTS→MOB_EFFECT` · `ENCHANTMENTS→ENCHANTMENT` ·
`PARTICLE_TYPES→PARTICLE_TYPE` · `SOUND_EVENTS→SOUND_EVENT` · `RECIPE_SERIALIZERS→RECIPE_SERIALIZER` ·
`FEATURES→FEATURE` · `FOLIAGE_PLACER_TYPES→FOLIAGE_PLACER_TYPE` · `TREE_DECORATOR_TYPES→TREE_DECORATOR_TYPE`.
`Registry.X_REGISTRY` → `BuiltInRegistries.X` (static) or `Registries.X` (the ResourceKey). Datapack registries
(`STRUCTURE`, `STRUCTURE_SET`, `NOISE_GENERATOR_SETTINGS`, `DIMENSION_TYPE`, biomes) → Phase 4, not the shim.

---

## Repo notes
- `src/main/resources/META-INF/{mods.toml,accesstransformer.cfg}` are **kept as reference** during the port (Fabric
  ignores them). The access widener Javadoc/comments reference the AT. Delete once the port is complete.
- `.gitignore` was updated: `settings.gradle` is no longer ignored (ForgeGradle ignored it; Loom requires it committed).
- `-Werror`/`-Xlint:all` from the Forge build were intentionally dropped so the port can land incrementally; re-tighten
  once it compiles.
