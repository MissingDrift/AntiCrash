package me.missingdrift.anticrash.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import org.bukkit.entity.Player;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handlePickItem
public class InvalidPickItem extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.PICK_ITEM) {
            WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);

            Player player = this.getPlayer(event);

            if (player == null) return;

            if (!(wrapper.getSlot() >= 0 && wrapper.getSlot() < player.getInventory().getContents().length)) {
                flagPacket(event);
            }
        }
    }
}
