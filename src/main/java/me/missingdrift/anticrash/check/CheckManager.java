package me.missingdrift.anticrash.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import java.util.HashMap;
import java.util.Map;
import me.missingdrift.anticrash.Anticrash;
import me.missingdrift.anticrash.check.impl.book.Book;
import me.missingdrift.anticrash.check.impl.book.MassiveBook;
import me.missingdrift.anticrash.check.impl.command.BlockedCommand;
import me.missingdrift.anticrash.check.impl.crasher.BandwidthLimit;
import me.missingdrift.anticrash.check.impl.crasher.Lectern;
import me.missingdrift.anticrash.check.impl.crasher.Log4J;
import me.missingdrift.anticrash.check.impl.crasher.PacketSize;
import me.missingdrift.anticrash.check.impl.creative.ItemCheck;
import me.missingdrift.anticrash.check.impl.creative.ItemCheckRunner;
import me.missingdrift.anticrash.check.impl.creative.impl.CreativeAnvil;
import me.missingdrift.anticrash.check.impl.creative.impl.CreativeClientBookCrash;
import me.missingdrift.anticrash.check.impl.creative.impl.CreativeMap;
import me.missingdrift.anticrash.check.impl.creative.impl.CreativeSkull;
import me.missingdrift.anticrash.check.impl.creative.impl.EnchantLimit;
import me.missingdrift.anticrash.check.impl.creative.impl.PotionLimit;
import me.missingdrift.anticrash.check.impl.firework.FireworkSize;
import me.missingdrift.anticrash.check.impl.invalid.ChannelCount;
import me.missingdrift.anticrash.check.impl.invalid.InvalidMove;
import me.missingdrift.anticrash.check.impl.invalid.InvalidPickItem;
import me.missingdrift.anticrash.check.impl.invalid.InvalidSlotChange;
import me.missingdrift.anticrash.check.impl.invalid.InvalidViewDistance;
import me.missingdrift.anticrash.check.impl.sign.SignLength;
import me.missingdrift.anticrash.check.impl.spam.BookSpam;
import me.missingdrift.anticrash.check.impl.spam.CraftSpam;
import me.missingdrift.anticrash.check.impl.spam.DropSpam;
import me.missingdrift.anticrash.check.impl.spam.PacketCount;
import me.missingdrift.anticrash.data.DataManager;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;

public class CheckManager {
    private final Map<Class<? extends BaseCheck>, BaseCheck> packetChecks = new HashMap<>();
    private final Map<Class<? extends ItemCheck>, ItemCheck> creativeChecks = new HashMap<>();

    public CheckManager() {
        this.initializeListeners();

        ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();

        if (AnticrashConfig.getInstance().getCreativeConfig().isEnabled()) {
            this.addCreativeChecks(
                    new CreativeSkull(),
                    new CreativeMap(),
                    new CreativeClientBookCrash(),
                    new PotionLimit(),
                    new CreativeAnvil()
            );
            if (AnticrashConfig.getInstance().getCreativeConfig().getMaxEnchantmentLevel() != -1) {
                this.addCreativeChecks(new EnchantLimit());
            }
        }

        this.addChecks(
                // Spam
                new BookSpam(),
                new DropSpam(),
                new PacketCount(),
                new CraftSpam(),

                // Invalid
                new InvalidMove(),
                new InvalidViewDistance(),
                new InvalidPickItem(),
                new InvalidSlotChange(),
                new ChannelCount(),

                // Crasher
                new Log4J(),
                new BandwidthLimit(),

                new BlockedCommand(),

                // Firework
                new FireworkSize(),

                // Sign
                new SignLength()
        );

        if (AnticrashConfig.getInstance().isNoBooks()) {
            this.addChecks(new Book());
        } else {
            this.addChecks(new MassiveBook());
        }

        if (AnticrashConfig.getInstance().getMaxBytes() != -1) {
            this.addChecks(new PacketSize());
        }

        if (AnticrashConfig.getInstance().getMaxBytesPerSecond() != -1) {
            this.addChecks(new BandwidthLimit());
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
            this.addChecks(new Lectern());
        }

        this.addChecks(new ItemCheckRunner(creativeChecks.values()));

        this.removeDisabledChecks();
    }

    private void initializeListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new SimplePacketListenerAbstract() {

            @Override
            public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
                for (BaseCheck check : packetChecks.values()) {
                    if (event.isCancelled()) {
                        return;
                    }

                    PlayerData data = DataManager.getInstance().getPlayerData(event.getUser());

                    if (data != null) {
                        check.handle(event, data);
                    }
                }
            }

            @Override
            public void onPacketPlaySend(PacketPlaySendEvent event) {
                for (BaseCheck check : packetChecks.values()) {
                    if (event.isCancelled()) {
                        return;
                    }

                    PlayerData data = DataManager.getInstance().getPlayerData(event.getUser());

                    if (data != null) {
                        check.handle(event, data);
                    }
                }
            }
        });
    }

    private void addChecks(BaseCheck... checks) {
        for (BaseCheck check : checks) {
            this.packetChecks.put(check.getClass(), check);

            if (check instanceof ItemCheck) {
                this.addCreativeChecks((ItemCheck) check);
            }
        }
    }

    private void addCreativeChecks(ItemCheck... checks) {
        for (ItemCheck check : checks) {
            this.creativeChecks.put(check.getClass(), check);
        }
    }

    private void removeDisabledChecks() {
        for (String disabledCheck : AnticrashConfig.getInstance().getDisabledChecks()) {
            this.creativeChecks.keySet().removeIf(clazz -> clazz.getName().contains(disabledCheck));
            this.packetChecks.keySet().removeIf(clazz -> clazz.getName().contains(disabledCheck));
            Anticrash.getPlugin().getLogger().info(disabledCheck + " has been disabled if it exists!");
        }
    }
}
