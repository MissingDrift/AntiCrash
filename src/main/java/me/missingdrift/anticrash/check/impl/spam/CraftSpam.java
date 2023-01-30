package me.missingdrift.anticrash.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.util.Ticker;


public class CraftSpam extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.CRAFT_RECIPE_REQUEST) {
            int currentTick = Ticker.getInstance().getCurrentTick();
            if (data.getLastCraftRequestTick() + 10 > currentTick) {
                flagPacket(event, false);
                getPlayer(event).updateInventory();
            } else {
                data.setLastCraftRequestTick(currentTick);
            }
        }
    }
}
