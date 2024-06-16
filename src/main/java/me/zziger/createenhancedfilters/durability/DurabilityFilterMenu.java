package me.zziger.createenhancedfilters.durability;

import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.foundation.utility.Pair;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import me.zziger.createenhancedfilters.CreateEnhancedFilters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DurabilityFilterMenu extends AbstractFilterMenu {

	boolean includeNonDamageable;
	int threshold;
	DurabilityFilterItemStack.OperationMode operationMode;
	boolean usePercentage;
	boolean isBlacklist;

	public DurabilityFilterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public DurabilityFilterMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
		super(type, id, inv, stack);
	}

	public static DurabilityFilterMenu create(int id, Inventory inv, ItemStack stack) {
		return new DurabilityFilterMenu(CreateEnhancedFilters.DURABILITY_FILTER_MENU.get(), id, inv, stack);
	}

	@Override
	protected int getPlayerInventoryXOffset() {
		return 51;
	}

	@Override
	protected int getPlayerInventoryYOffset() {
		return 107;
	}

	@Override
	protected void addFilterSlots() {
		this.addSlot(new SlotItemHandler(ghostInventory, 0, 16, 24));
//		this.addSlot(new SlotItemHandler(ghostInventory, 1, 22, 59) {
//			@Override
//			public boolean mayPickup(Player playerIn) {
//				return false;
//			}
//		});
	}

	@Override
	protected ItemStackHandler createGhostInventory() {
		return new ItemStackHandler(1);
	}

	@Override
	public void clearContents() {
	}

	@Override
	protected void initAndReadInventory(ItemStack filterItem) {
		super.initAndReadInventory(filterItem);

		if (filterItem.hasTag()) {
			CompoundTag tag = filterItem.getOrCreateTag();

			includeNonDamageable = tag.getBoolean("IncludeNonDamageable");
			threshold = tag.getInt("Threshold");
			operationMode = DurabilityFilterItemStack.OperationMode.values()[tag.getInt("OperationMode")];
			usePercentage = tag.getBoolean("UsePercentage");
			isBlacklist = tag.getBoolean("Blacklist");
		} else {
			includeNonDamageable = false;
			threshold = 50;
			operationMode = DurabilityFilterItemStack.OperationMode.LESS_THAN;
			usePercentage = true;
			isBlacklist = false;
		}
	}

	@Override
	protected void saveData(ItemStack filterItem) {
		CompoundTag tag = filterItem.getOrCreateTag();
		tag.putBoolean("IncludeNonDamageable", includeNonDamageable);
		tag.putInt("Threshold", threshold);
		tag.putInt("OperationMode", operationMode.ordinal());
		tag.putBoolean("UsePercentage", usePercentage);
		tag.putBoolean("Blacklist", isBlacklist);
	}

}
