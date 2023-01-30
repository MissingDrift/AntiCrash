package me.missingdrift.anticrash.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import java.util.concurrent.TimeUnit;
import me.missingdrift.anticrash.Anticrash;
import me.missingdrift.anticrash.data.DataManager;
import me.missingdrift.anticrash.data.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@CommandAlias("Anticrash")
public class AnticrashCommand extends BaseCommand {
    @Subcommand("info")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @CommandPermission("Anticrash.notification")
    public void info(Player executor, OnlinePlayer onlinePlayer) {
        Anticrash plugin = Anticrash.getPlugin();
        PlayerData playerData = DataManager.getInstance().getPlayerData(onlinePlayer.getPlayer().getUniqueId());

        Component message = plugin.getComponentSerializer().deserialize(
                "&7&m--------&r&7 &aAntiCrash &7&m--------\n" +
                        "&r"
                        + "&eClient Version: &f" + playerData.getUser().getClientVersion().getReleaseName() + "\n"
                        + "&ePacket Count: &f" + playerData.getPacketCount() + "\n"
                        + "&ePacket Allowance: &f" + playerData.getPacketAllowance() + "\n"
                        + "&eBytes Sent: &f" + playerData.getBytesSent() + " of " + plugin.getAnticrashConfig().getMaxBytesPerSecond() + "\n"
                        + "&r\n" +
                        "&7&m-----------------------------------\n"

        );

        DataManager.getInstance().getPlayerData(executor.getUniqueId()).getUser().sendMessage(message);
    }

    @Subcommand("debug")
    @CommandPermission("Anticrash.notification")
    public void debug(Player executor) {
        Anticrash plugin = Anticrash.getPlugin();

        long delta = System.currentTimeMillis() - plugin.getTicker().getLastReset();
        Component message = plugin.getComponentSerializer().deserialize(
                "&7&m--------&r&7 &eAntiCrash &7&m--------\n"
                        + "&eTime Since Playerdata Reset: &f" + TimeUnit.MILLISECONDS.toSeconds(delta) + "\n"
        );

        DataManager.getInstance().getPlayerData(executor.getUniqueId()).getUser().sendMessage(message);
    }

    @Subcommand("information")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @CommandPermission("Anticrash.notification")
    public void informationAlias(Player executor, OnlinePlayer onlinePlayer) {
        this.info(executor, onlinePlayer);
    }
}
