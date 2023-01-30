package me.missingdrift.anticrash.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import java.util.Map;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;

public class PacketCount extends BaseCheck {
    private final Map<PacketTypeCommon, Double> multiplierMap = AnticrashConfig.getInstance().getMultipliedPackets();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        double multiplier = multiplierMap.getOrDefault(event.getPacketType(), 1.0D);
        if (data.incrementPacketCount(multiplier) > data.getPacketAllowance()) {
            flagPacket(event, "Packet Count: " + data.getPacketCount() + " Packet Allowance: " + data.getPacketAllowance());
        } else {
            data.decrementPacketAllowance();
        }
    }
}
