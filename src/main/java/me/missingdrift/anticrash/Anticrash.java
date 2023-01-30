package me.missingdrift.anticrash;

import co.aikar.commands.PaperCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.Setter;
import me.missingdrift.anticrash.check.CheckManager;
import me.missingdrift.anticrash.command.AnticrashCommand;
import me.missingdrift.anticrash.data.DataManager;
import me.missingdrift.anticrash.settings.AnticrashConfig;
import me.missingdrift.anticrash.util.Ticker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class Anticrash extends JavaPlugin {
    @Getter
    private static Anticrash plugin;

    private final LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.AMPERSAND_CHAR)
            .hexCharacter(LegacyComponentSerializer.HEX_CHAR).build();

    private AnticrashConfig AnticrashConfig;

    private Ticker ticker;

    private DataManager dataManager;
    private CheckManager checkManager;
    private PaperCommandManager commandManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        plugin = this;

        this.AnticrashConfig = new AnticrashConfig(this);

        this.ticker = new Ticker();

        this.dataManager = new DataManager();
        this.checkManager = new CheckManager();

        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new AnticrashCommand());

        if (!getServer().spigot().getConfig().getBoolean("settings.late-bind", true)) {
            Bukkit.getLogger().warning("[Anticrash] Late bind is disabled, this can allow players" +
                    " to join your server before the plugin loads leaving you vulnerable to crashers.");
        }

        //bStats
        new Metrics(this, 15258);

        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();

        this.ticker.getTask().cancel();
    }
}
