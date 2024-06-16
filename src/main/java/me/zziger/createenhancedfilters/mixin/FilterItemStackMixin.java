package me.zziger.createenhancedfilters.mixin;

import com.simibubi.create.content.logistics.filter.FilterItemStack;

import me.zziger.createenhancedfilters.CreateEnhancedFilters;
import me.zziger.createenhancedfilters.durability.DurabilityFilterItemStack;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FilterItemStack.class)
public class FilterItemStackMixin {
	@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
	@Inject(method = "of(Lnet/minecraft/class_1799;)Lcom/simibubi/create/content/logistics/filter/FilterItemStack;", at = @At("HEAD"), cancellable = true, remap = false)
	private static void injectMethod(ItemStack itemStack, CallbackInfoReturnable<FilterItemStack> info) {
		if (CreateEnhancedFilters.DURABILITY_FILTER_ITEM.isIn(itemStack))
		{
			info.setReturnValue(new DurabilityFilterItemStack(itemStack));
		}
	}
}
