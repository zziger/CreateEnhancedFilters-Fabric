package me.zziger.createenhancedfilters.durability;

import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class DurabilityFilterScreenPacket extends SimplePacketBase {

	public enum Option {
		WHITELIST, BLACKLIST, INCLUDE_ND, IGNORE_ND, PERCENTS, POINTS, SET_OPERATOR, SET_THRESHOLD;
	}

	private final Option option;
	private final int value;

	public DurabilityFilterScreenPacket(Option option) {
		this(option, 0);
	}

	public DurabilityFilterScreenPacket(Option option, int value) {
		this.option = option;
		this.value = value;
	}

	public DurabilityFilterScreenPacket(FriendlyByteBuf buffer) {
		option = Option.values()[buffer.readInt()];
		value = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(option.ordinal());
		buffer.writeInt(value);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null)
				return;

			if (player.containerMenu instanceof DurabilityFilterMenu) {
				DurabilityFilterMenu c = (DurabilityFilterMenu) player.containerMenu;

				if (option == Option.WHITELIST)
					c.isBlacklist = false;
				if (option == Option.BLACKLIST)
					c.isBlacklist = true;

				if (option == Option.INCLUDE_ND)
					c.includeNonDamageable = true;
				if (option == Option.IGNORE_ND)
					c.includeNonDamageable = false;

				if (option == Option.PERCENTS)
					c.usePercentage = true;
				if (option == Option.POINTS)
					c.usePercentage = false;

				if (option == Option.SET_OPERATOR) {
					if (value >= 0 && value < DurabilityFilterItemStack.OperationMode.values().length) {
						c.operationMode = DurabilityFilterItemStack.OperationMode.values()[value];
					}
				}

				if (option == Option.SET_THRESHOLD) {
					c.threshold = value;
				}
			}
		});
		return true;
	}

}
