package me.zziger.createenhancedfilters.durability;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;

import me.zziger.createenhancedfilters.AbstractFilterScreen;
import me.zziger.createenhancedfilters.AllPackets;
import me.zziger.createenhancedfilters.CreateEnhancedFilters;
import me.zziger.createenhancedfilters.EnhancedFilterItem;
import me.zziger.createenhancedfilters.EnhancedFiltersGuiTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DurabilityFilterScreen extends AbstractFilterScreen<DurabilityFilterMenu> {

	private static final String PREFIX = "create_enhanced_filters.gui.durability_filter.";

	private Component allowN = Component.translatable(PREFIX + "allow_list");
	private Component allowDESC = Component.translatable(PREFIX + "allow_list.description");
	private Component denyN = Component.translatable(PREFIX + "deny_list");
	private Component denyDESC = Component.translatable(PREFIX + "deny_list.description");

	private Component includeNonDamageableN = Component.translatable(PREFIX + "include_non_damageable");
	private Component includeNonDamageableDESC = Component.translatable(PREFIX + "include_non_damageable.description");
	private Component ignoreNonDamageableN = Component.translatable(PREFIX + "ignore_non_damageable");
	private Component ignoreNonDamageableDESC = Component.translatable(PREFIX + "ignore_non_damageable.description");

	private IconButton whitelist, blacklist;
	private IconButton includeNonDamageable, ignoreNonDamageable;
	private Indicator whitelistIndicator, blacklistIndicator;
	private Indicator includeNonDamageableIndicator, ignoreNonDamageableIndicator;

	private ItemStack lastItemScanned = ItemStack.EMPTY;
	private List<ItemAttribute> attributesOfItem = new ArrayList<>();
	private List<Component> selectedAttributes = new ArrayList<>();

	private ScrollInput thresholdSelector;
	private Label thresholdSelectorLabel;

	private SelectionScrollInput operatorSelector;
	private Label operatorSelectorLabel;

	private SelectionScrollInput unitSelector;
	private Label unitSelectorLabel;

	public DurabilityFilterScreen(DurabilityFilterMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, EnhancedFiltersGuiTextures.DURABILITY_FILTER);
	}

	public static final List<Component> operations = new ArrayList<>(Arrays.asList(Component.literal("="), Component.literal("<"), Component.literal("≤"), Component.literal(">"), Component.literal("≥")));
	public static final List<Component> units = new ArrayList<>(Arrays.asList(Component.literal("%"), Component.translatable("create_enhanced_filters.gui.durability_filter.pt")));

	protected void sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option option, int value) {
		AllPackets.getChannel()
				.sendToServer(new DurabilityFilterScreenPacket(option, value));
	}

	@Override
	protected void init() {
		setWindowOffset(-11, 7);
		super.init();

		int x = leftPos;
		int y = topPos;

		blacklist = new IconButton(x + 18, y + 61, AllIcons.I_BLACKLIST);
		blacklist.withCallback(() -> {
			menu.isBlacklist = true;
			sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.BLACKLIST, 0);
		});
		blacklist.setToolTip(denyN);
		whitelist = new IconButton(x + 36, y + 61, AllIcons.I_WHITELIST);
		whitelist.withCallback(() -> {
			menu.isBlacklist = false;
			sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.WHITELIST, 0);
		});
		whitelist.setToolTip(allowN);
		blacklistIndicator = new Indicator(x + 18, y + 55, Components.immutableEmpty());
		whitelistIndicator = new Indicator(x + 36, y + 55, Components.immutableEmpty());
		addRenderableWidgets(blacklist, whitelist, blacklistIndicator, whitelistIndicator);

		includeNonDamageable = new IconButton(x + 59, y + 61, AllIcons.I_RESPECT_NBT);
		includeNonDamageable.withCallback(() -> {
			menu.includeNonDamageable = true;
			sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.INCLUDE_ND, 0);
		});
		includeNonDamageable.setToolTip(includeNonDamageableN);
		ignoreNonDamageable = new IconButton(x + 77, y + 61, AllIcons.I_IGNORE_NBT);
		ignoreNonDamageable.withCallback(() -> {
			menu.includeNonDamageable = false;
			sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.IGNORE_ND, 0);
		});
		ignoreNonDamageable.setToolTip(ignoreNonDamageableN);
		includeNonDamageableIndicator = new Indicator(x + 59, y + 55, Components.immutableEmpty());
		ignoreNonDamageableIndicator = new Indicator(x + 77, y + 55, Components.immutableEmpty());
		addRenderableWidgets(includeNonDamageable, ignoreNonDamageable, includeNonDamageableIndicator, ignoreNonDamageableIndicator);

		handleIndicators();

		thresholdSelectorLabel = new Label(x + 70 + 16 - font.width(Integer.toString(menu.threshold)) / 2, y + 28, Components.immutableEmpty()).colored(0xF3EBDE)
				.withShadow();
		thresholdSelector = new ScrollInput(x + 66, y + 23, 40, 18)
				.withRange(0, menu.usePercentage ? 101 : 3001)
				.writingTo(thresholdSelectorLabel)
				.titled(Component.translatable("create_enhanced_filters.gui.durability_filter.threshold").plainCopy())
				.calling(state -> {
					sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.SET_THRESHOLD, state);
					menu.threshold = state;
					thresholdSelectorLabel.x = x + 70 + 16 - font.width(thresholdSelectorLabel.text) / 2;
				});
		thresholdSelector.setState(menu.threshold);

		addRenderableWidget(thresholdSelector);
		addRenderableWidget(thresholdSelectorLabel);

		String operator = operations.get(menu.operationMode.ordinal()).getString();
		operatorSelectorLabel = new Label(x + 23 + 16 - font.width(operator) / 2, y + 28, Components.immutableEmpty()).colored(0xF3EBDE)
				.withShadow();
		operatorSelector = new SelectionScrollInput(x + 20, y + 23, 40, 18);

		operatorSelector.forOptions(operations)
				.writingTo(operatorSelectorLabel)
				.titled(Component.translatable("create_enhanced_filters.gui.durability_filter.operation").plainCopy())
				.calling(state -> {
					sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.SET_OPERATOR, state);
					menu.operationMode = DurabilityFilterItemStack.OperationMode.values()[state];
					operatorSelectorLabel.x = x + 23 + 16 - font.width(operatorSelectorLabel.text) / 2;
				});
		operatorSelector.setState(menu.operationMode.ordinal());

		addRenderableWidget(operatorSelector);
		addRenderableWidget(operatorSelectorLabel);

		String unit = menu.usePercentage ? "%" : I18n.get("create_enhanced_filters.gui.durability_filter.pt");
		unitSelectorLabel = new Label(x + 117 + 7 - font.width(unit) / 2, y + 28, Components.immutableEmpty()).colored(0xF3EBDE)
				.withShadow();
		unitSelector = new SelectionScrollInput(x + 114, y + 23, 22, 18);

		unitSelector.forOptions(units)
				.writingTo(unitSelectorLabel)
				.titled(Component.translatable("create_enhanced_filters.gui.durability_filter.unit").plainCopy())
				.calling(state -> {
					menu.usePercentage = state == 0;
					if (menu.usePercentage)
						sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.PERCENTS, 0);
					else
						sendDurabilityOptionUpdate(DurabilityFilterScreenPacket.Option.POINTS, 0);
					unitChanged(menu.usePercentage);
					unitSelectorLabel.x = x + 117 + 7 - font.width(unitSelectorLabel.text) / 2;
				});
		unitSelector.setState(menu.usePercentage ? 0 : 1);

		addRenderableWidget(unitSelector);
		addRenderableWidget(unitSelectorLabel);

		unitChanged(menu.usePercentage);
	}

	private void unitChanged(boolean percents) {
		if (percents) {
			int value = thresholdSelector.getState();
			if (value < 0) thresholdSelector.setState(0);
			else if (value > 100) thresholdSelector.setState(100);
			thresholdSelector.withRange(0, 101);
		} else {
			thresholdSelector.withRange(0, 3001);
		}
	}

	@Override
	protected List<IconButton> getTooltipButtons() {
		return Arrays.asList(blacklist, whitelist, includeNonDamageable, ignoreNonDamageable);
	}

	@Override
	protected List<MutableComponent> getTooltipDescriptions() {
		return Arrays.asList(denyDESC.plainCopy(), allowDESC.plainCopy(), includeNonDamageableDESC.plainCopy(), ignoreNonDamageableDESC.plainCopy());
	}

	@Override
	protected List<Indicator> getIndicators() {
		return Arrays.asList(blacklistIndicator, whitelistIndicator, includeNonDamageableIndicator, ignoreNonDamageableIndicator);
	}

	@Override
	protected void contentsCleared() {
		super.contentsCleared();
	}

	@Override
	protected boolean isButtonEnabled(IconButton button) {
		if (button == blacklist)
			return !menu.isBlacklist;
		if (button == whitelist)
			return menu.isBlacklist;
		if (button == includeNonDamageable)
			return !menu.includeNonDamageable;
		if (button == ignoreNonDamageable)
			return menu.includeNonDamageable;
		return true;
	}

	@Override
	protected boolean isIndicatorOn(Indicator indicator) {
		if (indicator == blacklistIndicator)
			return menu.isBlacklist;
		if (indicator == whitelistIndicator)
			return !menu.isBlacklist;
		if (indicator == includeNonDamageableIndicator)
			return menu.includeNonDamageable;
		if (indicator == ignoreNonDamageableIndicator)
			return !menu.includeNonDamageable;
		return false;
	}
}
