package me.missingdrift.anticrash.listener;

import me.missingdrift.anticrash.Anticrash;
import me.missingdrift.anticrash.data.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class BukkitJoinListener implements Listener {
    private final Anticrash Anticrash = me.missingdrift.anticrash.Anticrash.getPlugin();

    @EventHandler(ignoreCancelled = true)
    void onJoin(PlayerJoinEvent event) {
        DataManager dataManager = this.Anticrash.getDataManager();

        if (event.getPlayer().hasPermission(this.Anticrash.getAnticrashConfig().getPermissionsConfig().getNotificationPermission()) || event.getPlayer().isOp()) {
            dataManager.getPlayerData().keySet().stream()
                    .filter(user -> user.getUUID().equals(event.getPlayer().getUniqueId())).findFirst()
                    .ifPresent(user -> dataManager.getPlayerData(user).setReceivingAlerts(true));
        }
    }
}
