package me.missingdrift.anticrash.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import java.util.ArrayList;
import java.util.List;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.check.impl.creative.ItemCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;


public class MassiveBook extends BaseCheck implements ItemCheck {
    private final int maxBookPageSize = AnticrashConfig.getInstance().getMaxBookPageSize();
    private final double maxBookTotalSizeMultiplier = AnticrashConfig.getInstance().getMaxBookTotalSizeMultiplier();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        List<String> pageList = new ArrayList<>();

        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            WrapperPlayClientEditBook wrapper = new WrapperPlayClientEditBook(event);
            pageList.addAll(wrapper.getPages());
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);


            if (wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign")) {
                Object buffer = null;
                try {
                    buffer = UnpooledByteBufAllocationHelper.buffer();
                    ByteBufHelper.writeBytes(buffer, wrapper.getData());
                    PacketWrapper<?> universalWrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
                    com.github.retrooper.packetevents.protocol.item.ItemStack wrappedItemStack = universalWrapper.readItemStack();

                    if (invalidTitleOrAuthor(wrappedItemStack)) flagPacket(event);

                    pageList.addAll(this.getPages(wrappedItemStack));
                } finally {
                    ByteBufHelper.release(buffer);
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                if (invalidTitleOrAuthor(wrapper.getItemStack().get())) flagPacket(event);
                pageList.addAll(this.getPages(wrapper.getItemStack().get()));
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getCarriedItemStack() != null) {
                if (invalidTitleOrAuthor(wrapper.getCarriedItemStack())) flagPacket(event);
                pageList.addAll(this.getPages(wrapper.getCarriedItemStack()));
            }
        } else {
            return;
        }

        if (invalid(pageList)) {
            flagPacket(event);
        }
    }


    @Override
    public boolean handleCheck(PacketReceiveEvent event, ItemStack clickedStack, NBTCompound nbtCompound) {
        return invalid(this.getPages(clickedStack)) || invalidTitleOrAuthor(clickedStack);
    }

    private boolean invalid(List<String> pageList) {
        long byteTotal = 0;
        double multiplier = Math.min(1D, this.maxBookTotalSizeMultiplier);
        long byteAllowed = this.maxBookPageSize;

        for (String testString : pageList) {
            int byteLength = testString.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
            if (byteLength > 256 * 4) {

                return true;
            }
            byteTotal += byteLength;
            int length = testString.length();
            int multibytes = 0;
            if (byteLength != length) {
                for (char c : testString.toCharArray()) {
                    if (c > 127) {
                        multibytes++;
                    }
                }
            }
            byteAllowed += (this.maxBookPageSize * Math.min(1, Math.max(0.1D, (double) length / 255D))) * multiplier;

            if (multibytes > 1) {
                byteAllowed -= multibytes;
            }
        }

        return byteTotal > byteAllowed;
    }

    private boolean invalidTitleOrAuthor(ItemStack itemStack) {
        if (itemStack.getNBT() != null) {
            String title = itemStack.getNBT().getStringTagValueOrNull("title");
            if (title != null && title.length() > 100) {
                return true;
            }

            String author = itemStack.getNBT().getStringTagValueOrNull("author");
            return author != null && author.length() > 16;
        }
        return false;
    }

    private List<String> getPages(ItemStack itemStack) {
        List<String> pageList = new ArrayList<>();

        if (itemStack.getNBT() != null) {
            NBTList<NBTString> nbtList = itemStack.getNBT().getStringListTagOrNull("pages");
            if (nbtList != null) {
                for (NBTString tag : nbtList.getTags()) {
                    pageList.add(tag.getValue());
                }
            }
        }

        return pageList;
    }
}
