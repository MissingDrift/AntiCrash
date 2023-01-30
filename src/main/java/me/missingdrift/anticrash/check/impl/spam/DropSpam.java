package me.missingdrift.anticrash.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.util.Ticker;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handlePlayerAction
public class DropSpam extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

            if (wrapper.getAction() != DiggingAction.DROP_ITEM) return;

            Player player = this.getPlayer(event);
            int currentTick = Ticker.getInstance().getCurrentTick();

            if (player.getGameMode() != GameMode.SPECTATOR) {
                if (data.getLastDropItemTick() != currentTick) {
                    data.setDropCount(0);
                    data.setLastDropItemTick(currentTick);
                } else {
                    data.incrementDropCount();
                    if (data.getDropCount() >= 20) {
                        flagPacket(event, true);
                    }
                }
            }
        }
    }
}
