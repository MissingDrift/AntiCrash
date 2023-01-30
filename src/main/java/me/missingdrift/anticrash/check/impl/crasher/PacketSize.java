package me.missingdrift.anticrash.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;

public class PacketSize extends BaseCheck {

    private final int maxBytes = AnticrashConfig.getInstance().getMaxBytes();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        int capacity = ByteBufHelper.capacity(event.getByteBuf());
        int maxBytes = this.maxBytes * (event.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_8) ? 2 : 1);

        if (capacity > maxBytes) {
            flagPacket(event, "Bytes: " + capacity + " Max Bytes: " + maxBytes);
        }
    }
}
