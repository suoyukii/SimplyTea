package knightminer.simplytea.data.gen;

import knightminer.simplytea.SimplyTea;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LootTableGenerator extends LootTableProvider {
	public LootTableGenerator(PackOutput output) {
		super(output, Set.of(),
				List.of(new SubProviderEntry(BlockLootTableGenerator::new, LootContextParamSets.BLOCK))
		);
	}

	@Override
	protected void validate(Map<ResourceLocation,LootTable> map, ValidationContext validationtracker) {
		map.forEach((loc, table) -> table.validate(validationtracker));
		// Remove vanilla's tables, which we also loaded so we can redirect stuff to them.
		// This ensures the remaining generator logic doesn't write those to files.
		map.keySet().removeIf((loc) -> !loc.getNamespace().equals(SimplyTea.MOD_ID));
	}
}
