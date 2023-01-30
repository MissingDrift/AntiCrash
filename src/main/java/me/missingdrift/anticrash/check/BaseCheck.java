package me.missingdrift.anticrash.check;

import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import java.util.Optional;
import me.missingdrift.anticrash.data.DataManager;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.MessagesConfig;
import me.missingdrift.anticrash.settings.AnticrashConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class BaseCheck implements Check {
    private final AnticrashConfig AnticrashConfig = me.missingdrift.anticrash.settings.AnticrashConfig.getInstance();
    private final MessagesConfig messagesConfig = AnticrashConfig.getMessagesConfig();

    @Override
    public void flagPacket(ProtocolPacketEvent<Object> event, String info, boolean kick) {
        event.setCancelled(true);

        User user = event.getUser();
        this.alert(user, info);
        if (kick) {
            this.disconnect(user);
        }
    }

    @Override
    public void flagPacket(ProtocolPacketEvent<Object> event, String info) {
        this.flagPacket(event, info, true);
    }

    @Override
    public void flagPacket(ProtocolPacketEvent<Object> event, boolean kick) {
        this.flagPacket(event, "", kick);
    }

    protected void disconnect(User user) {
        user.sendPacket(new WrapperPlayServerDisconnect(messagesConfig.getKickMessage(this.getClass().getSimpleName())));
        user.closeConnection();
    }

    protected void alert(User user, String info) {
        Component component = messagesConfig.getNotification(user.getName(), this.getClass().getSimpleName(), info);
        Bukkit.getLogger().info(messagesConfig.getComponentSerializer().serialize(component));
        for (PlayerData playerData : DataManager.getInstance().getPlayerData().values()) {
            if (playerData.isReceivingAlerts()) {
                playerData.getUser().sendMessage(component);
            }
        }
    }

    protected Player getPlayer(ProtocolPacketEvent<Object> event) {
        return Optional.ofNullable((Player) event.getPlayer()).orElse(Bukkit.getPlayer(event.getUser().getUUID()));
    }
}
