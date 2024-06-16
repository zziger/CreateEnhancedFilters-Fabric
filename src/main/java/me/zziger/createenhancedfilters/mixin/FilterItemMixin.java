package me.zziger.createenhancedfilters.mixin;

import com.simibubi.create.content.logistics.filter.FilterItem;

import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import me.zziger.createenhancedfilters.EnhancedFilterItem;

import me.zziger.createenhancedfilters.durability.DurabilityFilterItemStack;
import me.zziger.createenhancedfilters.durability.DurabilityFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mixin(FilterItem.class)
public class FilterItemMixin implements EnhancedFilterItem {
	@Unique
	EnhancedType enhancedType = EnhancedType.NONE;

	@Inject(method = "makeSummary(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
	private void makeSummary(ItemStack filter, CallbackInfoReturnable<List<Component>> cir) {
		if (enhancedType == EnhancedType.DURABILITY) {
			cir.setReturnValue(DurabilityFilterItemStack.makeSummary(filter));
		}
	}

	@Inject(method = "createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;", at = @At("HEAD"), cancellable = true)
	private void createMenu(int id, Inventory inv, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir) {
		ItemStack heldItem = player.getMainHandItem();
		if (enhancedType == EnhancedType.DURABILITY)
			cir.setReturnValue(DurabilityFilterMenu.create(id, inv, heldItem));
	}

	@Override
	public void setEnhancedType(EnhancedType type) {
		enhancedType = type;
	}

	@Override
	public EnhancedType getEnhancedType() {
		return enhancedType;
	}
}
