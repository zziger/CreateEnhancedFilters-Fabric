package me.zziger.createenhancedfilters;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.Create;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.foundation.data.CreateRegistrate;

import com.tterrag.registrate.util.entry.ItemEntry;

import com.tterrag.registrate.util.entry.MenuEntry;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import me.zziger.createenhancedfilters.durability.DurabilityFilterMenu;
import me.zziger.createenhancedfilters.durability.DurabilityFilterScreen;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.simibubi.create.foundation.networking.SimplePacketBase.NetworkDirection.PLAY_TO_SERVER;

public class CreateEnhancedFilters implements ModInitializer {
	public static final String ID = "create_enhanced_filters";
	public static final String NAME = "Create Enhanced Filters";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static FilterItem CreateDurabilityFilter(Item.Properties properties) {
		FilterItem inst = FilterItem.attribute(properties);
		((EnhancedFilterItem) inst).setEnhancedType(EnhancedFilterItem.EnhancedType.DURABILITY);
		return inst;
	}

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID).creativeModeTab(() -> AllCreativeModeTabs.BASE_CREATIVE_TAB);

	public static final ItemEntry<FilterItem> DURABILITY_FILTER_ITEM = REGISTRATE.item("durability_filter", CreateEnhancedFilters::CreateDurabilityFilter)
			.lang("Durability Filter")
			.properties(properties -> {
				return properties.tab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
			})
			.register();

	public static final MenuEntry<DurabilityFilterMenu> DURABILITY_FILTER_MENU =
			REGISTRATE.menu("durability_filter", DurabilityFilterMenu::new, () -> DurabilityFilterScreen::new)
					.register();

	@Override
	public void onInitialize() {
		REGISTRATE.register();
		AllPackets.registerPackets();
		AllPackets.getChannel().initServerListener();

		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);
	}

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
