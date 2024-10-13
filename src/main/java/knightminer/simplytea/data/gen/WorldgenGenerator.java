package knightminer.simplytea.data.gen;

import knightminer.simplytea.core.Registration;
import knightminer.simplytea.worldgen.TreeGenEnabledPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WorldgenGenerator extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, WorldgenGenerator::configuredFeatures)
            .add(Registries.PLACED_FEATURE, WorldgenGenerator::placedFeatures)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, WorldgenGenerator::biomeModifiers);

    public WorldgenGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, Set<String> modIds) {
        super(output, registries, BUILDER, modIds);
    }

    public static void configuredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(context, Registration.configured_tea_tree, Registration.tea_tree);
    }

    public static void placedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(context, Registration.placed_tea_tree, holdergetter.getOrThrow(Registration.configured_tea_tree), List.of(
                TreeGenEnabledPlacement.INSTANCE,
                RarityFilter.onAverageOnceEvery(128), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome(),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Registration.tea_sapling.defaultBlockState(), BlockPos.ZERO)),
                PlacementUtils.filteredByBlockSurvival(Registration.tea_sapling)
        ));
    }

    public static void biomeModifiers(BootstapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
        HolderSet<Biome> forest = biomeGetter.getOrThrow(BiomeTags.IS_FOREST);

        context.register(Registration.tea_tree_biome_modifier, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                forest,
                HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(Registration.placed_tea_tree)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }
}
