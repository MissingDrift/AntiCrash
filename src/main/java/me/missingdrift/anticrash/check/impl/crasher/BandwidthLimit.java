package me.missingdrift.anticrash.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;

public class BandwidthLimit extends BaseCheck {

    private final int maxBytesPerSecond = AnticrashConfig.getInstance().getMaxBytesPerSecond();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        int readableBytes = ByteBufHelper.readableBytes(event.getByteBuf());
        int maxBytesPerSecond = this.maxBytesPerSecond * (event.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_8) ? 2 : 1);

        if (playerData.incrementBytesSent(readableBytes) > maxBytesPerSecond) {
            flagPacket(event, "Bytes Sent: " + playerData.getBytesSent() + " Max Bytes/s: " + maxBytesPerSecond);
        }
    }
}
