package me.zziger.createenhancedfilters;

import com.simibubi.create.Create;
import com.simibubi.create.compat.computercraft.AttachedComputerPacket;
import com.simibubi.create.content.contraptions.ContraptionBlockChangedPacket;
import com.simibubi.create.content.contraptions.ContraptionColliderLockPacket;
import com.simibubi.create.content.contraptions.ContraptionDisassemblyPacket;
import com.simibubi.create.content.contraptions.ContraptionRelocationPacket;
import com.simibubi.create.content.contraptions.ContraptionStallPacket;
import com.simibubi.create.content.contraptions.TrainCollisionPacket;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionDisableActorPacket;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInputPacket;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsStopControllingPacket;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactEditPacket;
import com.simibubi.create.content.contraptions.elevator.ElevatorFloorListPacket;
import com.simibubi.create.content.contraptions.elevator.ElevatorTargetFloorPacket;
import com.simibubi.create.content.contraptions.gantry.GantryContraptionUpdatePacket;
import com.simibubi.create.content.contraptions.glue.GlueEffectPacket;
import com.simibubi.create.content.contraptions.glue.SuperGlueRemovalPacket;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;
import com.simibubi.create.content.contraptions.minecart.CouplingCreationPacket;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartControllerUpdatePacket;
import com.simibubi.create.content.contraptions.sync.ClientMotionPacket;
import com.simibubi.create.content.contraptions.sync.ContraptionFluidPacket;
import com.simibubi.create.content.contraptions.sync.ContraptionInteractionPacket;
import com.simibubi.create.content.contraptions.sync.ContraptionSeatMappingPacket;
import com.simibubi.create.content.contraptions.sync.LimbSwingUpdatePacket;
import com.simibubi.create.content.equipment.bell.SoulPulseEffectPacket;
import com.simibubi.create.content.equipment.blueprint.BlueprintAssignCompleteRecipePacket;
import com.simibubi.create.content.equipment.clipboard.ClipboardEditPacket;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripInteractionPacket;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonPacket;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileTypeManager;
import com.simibubi.create.content.equipment.symmetryWand.ConfigureSymmetryWandPacket;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryEffectPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxDisposeAllPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxEquipPacket;
import com.simibubi.create.content.equipment.zapper.ZapperBeamPacket;
import com.simibubi.create.content.equipment.zapper.terrainzapper.ConfigureWorldshaperPacket;
import com.simibubi.create.content.fluids.transfer.FluidSplashPacket;
import com.simibubi.create.content.kinetics.gauge.GaugeObservedPacket;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmPlacementPacket;
import com.simibubi.create.content.kinetics.transmission.sequencer.ConfigureSequencedGearshiftPacket;
import com.simibubi.create.content.logistics.depot.EjectorAwardPacket;
import com.simibubi.create.content.logistics.depot.EjectorElytraPacket;
import com.simibubi.create.content.logistics.depot.EjectorPlacementPacket;
import com.simibubi.create.content.logistics.depot.EjectorTriggerPacket;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.content.logistics.funnel.FunnelFlapPacket;
import com.simibubi.create.content.logistics.tunnel.TunnelFlapPacket;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkConfigurationPacket;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerBindPacket;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerInputPacket;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerStopLecternPacket;
import com.simibubi.create.content.redstone.thresholdSwitch.ConfigureThresholdSwitchPacket;
import com.simibubi.create.content.schematics.cannon.ConfigureSchematicannonPacket;
import com.simibubi.create.content.schematics.packet.InstantSchematicPacket;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import com.simibubi.create.content.schematics.packet.SchematicSyncPacket;
import com.simibubi.create.content.schematics.packet.SchematicUploadPacket;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.TrainHUDUpdatePacket;
import com.simibubi.create.content.trains.entity.CarriageDataUpdatePacket;
import com.simibubi.create.content.trains.entity.TrainPacket;
import com.simibubi.create.content.trains.entity.TrainPromptPacket;
import com.simibubi.create.content.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.trains.graph.TrackGraphRequestPacket;
import com.simibubi.create.content.trains.graph.TrackGraphRollCallPacket;
import com.simibubi.create.content.trains.graph.TrackGraphSyncPacket;
import com.simibubi.create.content.trains.schedule.ScheduleEditPacket;
import com.simibubi.create.content.trains.signal.SignalEdgeGroupPacket;
import com.simibubi.create.content.trains.station.StationEditPacket;
import com.simibubi.create.content.trains.station.TrainEditPacket;
import com.simibubi.create.content.trains.track.CurvedTrackDestroyPacket;
import com.simibubi.create.content.trains.track.CurvedTrackSelectionPacket;
import com.simibubi.create.content.trains.track.PlaceExtendedCurvePacket;
import com.simibubi.create.foundation.blockEntity.RemoveBlockEntityPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.config.ui.CConfigureConfigPacket;
import com.simibubi.create.foundation.gui.menu.ClearMenuPacket;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;
import com.simibubi.create.foundation.networking.ISyncPersistentData;
import com.simibubi.create.foundation.networking.LeftClickPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.infrastructure.command.HighlightPacket;
import com.simibubi.create.infrastructure.command.SConfigureConfigPacket;
import com.simibubi.create.infrastructure.debugInfo.ServerDebugInfoPacket;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import me.zziger.createenhancedfilters.durability.DurabilityFilterScreenPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.function.Function;

import static com.simibubi.create.foundation.networking.SimplePacketBase.NetworkDirection.PLAY_TO_CLIENT;
import static com.simibubi.create.foundation.networking.SimplePacketBase.NetworkDirection.PLAY_TO_SERVER;

public enum AllPackets {

	CONFIGURE_DURABILITY_FILTER(DurabilityFilterScreenPacket.class, DurabilityFilterScreenPacket::new, PLAY_TO_SERVER),

	;

	public static final ResourceLocation CHANNEL_NAME = CreateEnhancedFilters.asResource("main");
	public static final int NETWORK_VERSION = 3;
	public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
	private static SimpleChannel channel;

	private AllPackets.PacketType<?> packetType;

	<T extends SimplePacketBase> AllPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
											SimplePacketBase.NetworkDirection direction) {
		packetType = new AllPackets.PacketType<>(type, factory, direction);
	}

	public static void registerPackets() {
		channel = new SimpleChannel(CHANNEL_NAME);
		for (AllPackets packet : values())
			packet.packetType.register();
	}

	public static SimpleChannel getChannel() {
		return channel;
	}

	public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
		getChannel().sendToClientsAround((S2CPacket) message, (ServerLevel) world, pos, range);
	}

	private static class PacketType<T extends SimplePacketBase> {
		private static int index = 0;

		private Function<FriendlyByteBuf, T> decoder;
		private Class<T> type;
		private SimplePacketBase.NetworkDirection direction;

		private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, SimplePacketBase.NetworkDirection direction) {
			decoder = factory;
			this.type = type;
			this.direction = direction;
		}

		private void register() {
			switch (direction) {
				case PLAY_TO_CLIENT -> getChannel().registerS2CPacket(type, index++, decoder);
				case PLAY_TO_SERVER -> getChannel().registerC2SPacket(type, index++, decoder);
			}
		}
	}

}
