package me.missingdrift.anticrash.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.check.impl.creative.ItemCheck;
import me.missingdrift.anticrash.data.PlayerData;

public class Book extends BaseCheck implements ItemCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getCarriedItemStack() != null) {
                if (wrapper.getCarriedItemStack().getType() == ItemTypes.WRITTEN_BOOK || wrapper.getCarriedItemStack().getType() == ItemTypes.WRITABLE_BOOK) {
                    flagPacket(event);
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                com.github.retrooper.packetevents.protocol.item.ItemStack wrappedItemStack = wrapper.getItemStack().get();
                if (wrappedItemStack.getType() == ItemTypes.WRITTEN_BOOK || wrappedItemStack.getType() == ItemTypes.WRITABLE_BOOK) {
                    flagPacket(event);
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);

            if (wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign")) {
                flagPacket(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            flagPacket(event);
        }
    }

    @Override
    public boolean handleCheck(PacketReceiveEvent event, ItemStack clickedStack, NBTCompound nbtCompound) {
        return clickedStack.getType() == ItemTypes.WRITTEN_BOOK || clickedStack.getType() == ItemTypes.WRITABLE_BOOK;
    }
}
