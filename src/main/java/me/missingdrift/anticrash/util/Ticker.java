package me.missingdrift.anticrash.util;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import lombok.Getter;
import me.missingdrift.anticrash.Anticrash;
import me.missingdrift.anticrash.data.DataManager;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class Ticker {
    @Getter
    private static Ticker instance;

    private int currentTick;

    private final BukkitTask task;

    private long lastReset;

    public Ticker() {
        instance = this;

        Anticrash plugin = Anticrash.getPlugin();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> currentTick++, 1, 1);

        this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            double maxPacketsPerSecond = AnticrashConfig.getInstance().getMaxPacketsPerSecond();
            double maxPacketAllowance = maxPacketsPerSecond * 2;

            for (PlayerData value : DataManager.getInstance().getPlayerData().values()) {
                value.setPacketAllowance(maxPacketAllowance);
                value.setPacketCount(0);
                value.setBytesSent(0);
            }

            this.lastReset = System.currentTimeMillis();
        }, 0, 20);
    }
}
