package me.zziger.createenhancedfilters.durability;

import com.simibubi.create.content.logistics.filter.FilterItemStack;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import me.zziger.createenhancedfilters.CreateEnhancedFilters;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DurabilityFilterItemStack extends FilterItemStack {
	public enum OperationMode {
		EQUAL,
		LESS_THAN,
		LESS_THAN_OR_EQUAL,
		GREATER_THAN,
		GREATER_THAN_OR_EQUAL,
	}

	public boolean includeNonDamageable;
	public int threshold;
	public OperationMode operationMode;
	public boolean usePercentage;
	public boolean isBlacklist;

	public DurabilityFilterItemStack(ItemStack filter) {
		super(filter);
		boolean defaults = !filter.hasTag();

		includeNonDamageable = defaults ? false : filter.getTag().getBoolean("IncludeNonDamageable");
		threshold = defaults ? 50 : filter.getTag().getInt("Threshold");
		operationMode = OperationMode.values()[defaults ? 1 : filter.getTag().getInt("OperationMode")];
		usePercentage = defaults ? true : filter.getTag().getBoolean("UsePercentage");
		isBlacklist = defaults ? false : filter.getTag().getBoolean("Blacklist");
	}

	@Override
	public boolean test(Level world, FluidStack stack, boolean matchNBT) {
		return false;
	}

	@Override
	public boolean test(Level world, ItemStack stack, boolean matchNBT) {
		if (!stack.isDamageableItem()) {
			if (includeNonDamageable) {
				return !isBlacklist;
			} else {
				return isBlacklist;
			}
		}

		int damage = stack.getMaxDamage() - stack.getDamageValue();

		if (usePercentage) {
			damage = Math.round(((float)damage / stack.getMaxDamage()) * 100);
		}

		boolean val = switch (operationMode) {
			case EQUAL -> damage == threshold;
			case LESS_THAN -> damage < threshold;
			case LESS_THAN_OR_EQUAL -> damage <= threshold;
			case GREATER_THAN -> damage > threshold;
			case GREATER_THAN_OR_EQUAL -> damage >= threshold;
		};

		if (isBlacklist) val = !val;

		return val;
	}

	public static final List<String> operations = new ArrayList<String>(Arrays.asList("=", "<", "≤", ">", "≥"));

	public static List<Component> makeSummary(ItemStack filter) {

		try {
			List<Component> list = new ArrayList<>();
			if (!filter.hasTag()) {
				return list;
			}

			boolean blacklist = filter.getTag().getBoolean("Blacklist");
			boolean includeNonDamageable = filter.getTag().getBoolean("IncludeNonDamageable");
			int operationIndex = filter.getTag().getInt("OperationMode");
			int threshold = filter.getTag().getInt("Threshold");
			String unit = filter.getTag().getBoolean("UsePercentage") ? "%" : "";

			list.add((blacklist ? Lang.translateDirect("gui.filter.deny_list")
					: Lang.translateDirect("gui.filter.allow_list")).withStyle(ChatFormatting.GOLD));

			if (includeNonDamageable) {
				list.add(Components.literal("- ").append(Component.translatable("create_enhanced_filters.gui.durability_filter.non_damageable")).withStyle(ChatFormatting.GRAY));
			}

			if (operationIndex >= 0 && operationIndex < operations.size()) {
				String operation = operations.get(filter.getTag().getInt("OperationMode"));

				list.add(Components.literal("- ").append(Component.translatable("create_enhanced_filters.gui.durability_filter.items_with", operation, Integer.toString(threshold), unit)).withStyle(ChatFormatting.GRAY));
			}

			return list;
		} catch(Exception e) {
			List<Component> list = new ArrayList<>();
			list.add(Components.literal("Exception: ").append(e.toString()).withStyle(ChatFormatting.RED));
			return list;
		}
	}
}
