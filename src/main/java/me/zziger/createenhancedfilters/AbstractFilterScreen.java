package me.zziger.createenhancedfilters;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.item.TooltipHelper;

import io.github.fabricators_of_create.porting_lib.util.ItemStackUtil;
import io.github.fabricators_of_create.porting_lib.util.PlayerEntityHelper;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collections;
import java.util.List;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public abstract class AbstractFilterScreen<F extends AbstractFilterMenu> extends AbstractSimiContainerScreen<F> {

	protected EnhancedFiltersGuiTextures background;
	private List<Rect2i> extraAreas = Collections.emptyList();

	private IconButton resetButton;
	private IconButton confirmButton;

	protected AbstractFilterScreen(F menu, Inventory inv, Component title, EnhancedFiltersGuiTextures background) {
		super(menu, inv, title);
		this.background = background;
	}

	@Override
	protected void init() {
		setWindowSize(Math.max(background.width, PLAYER_INVENTORY.width),
				background.height + 4 + PLAYER_INVENTORY.height);
		super.init();

		int x = leftPos;
		int y = topPos;

		resetButton = new IconButton(x + background.width - 62, y + background.height - 24, AllIcons.I_TRASH);
		resetButton.withCallback(() -> {
			menu.clearContents();
			contentsCleared();
			menu.sendClearPacket();
		});
		confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
			minecraft.player.closeContainer();
		});

		addRenderableWidget(resetButton);
		addRenderableWidget(confirmButton);

		extraAreas = ImmutableList.of(new Rect2i(x + background.width, y + background.height - 40, 80, 48));
	}

	@Override
	protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
		int invX = getLeftOfCentered(PLAYER_INVENTORY.width);
		int invY = topPos + background.height + 4;
		renderPlayerInventory(ms, invX, invY);

		int x = leftPos;
		int y = topPos;

		background.render(ms, x, y, this);
		font.draw(ms, title, x + (background.width - 8) / 2 - font.width(title) / 2, y + 4,
				AllItems.FILTER.isIn(menu.contentHolder) ? 0x303030 : 0x592424);

		GuiGameElement.of(menu.contentHolder).<GuiGameElement
						.GuiRenderBuilder>at(x + background.width + 8, y + background.height - 52, -200)
				.scale(4)
				.render(ms);
	}

	@Override
	protected void containerTick() {
		if (!ItemStackUtil.equals(menu.player.getMainHandItem(), menu.contentHolder, false))
			PlayerEntityHelper.closeScreen(menu.player);

		super.containerTick();

		handleTooltips();
		handleIndicators();
	}

	protected void handleTooltips() {
		List<IconButton> tooltipButtons = getTooltipButtons();

		for (IconButton button : tooltipButtons) {
			if (!button.getToolTip()
					.isEmpty()) {
				button.setToolTip(button.getToolTip()
						.get(0));
				button.getToolTip()
						.add(TooltipHelper.holdShift(TooltipHelper.Palette.YELLOW, hasShiftDown()));
			}
		}

		if (hasShiftDown()) {
			List<MutableComponent> tooltipDescriptions = getTooltipDescriptions();
			for (int i = 0; i < tooltipButtons.size(); i++)
				fillToolTip(tooltipButtons.get(i), tooltipDescriptions.get(i));
		}
	}

	public void handleIndicators() {
		for (IconButton button : getTooltipButtons())
			button.active = isButtonEnabled(button);
		for (Indicator indicator : getIndicators())
			indicator.state = isIndicatorOn(indicator) ? Indicator.State.ON : Indicator.State.OFF;
	}

	protected abstract boolean isButtonEnabled(IconButton button);

	protected abstract boolean isIndicatorOn(Indicator indicator);

	protected List<IconButton> getTooltipButtons() {
		return Collections.emptyList();
	}

	protected List<MutableComponent> getTooltipDescriptions() {
		return Collections.emptyList();
	}

	protected List<Indicator> getIndicators() {
		return Collections.emptyList();
	}

	private void fillToolTip(IconButton button, Component tooltip) {
		if (!button.isHoveredOrFocused())
			return;
		List<Component> tip = button.getToolTip();
		tip.addAll(TooltipHelper.cutTextComponent(tooltip, TooltipHelper.Palette.ALL_GRAY));
	}

	protected void contentsCleared() {}

	protected void sendOptionUpdate(FilterScreenPacket.Option option) {
		AllPackets.getChannel()
				.sendToServer(new FilterScreenPacket(option));
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

}

