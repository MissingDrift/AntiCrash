package me.missingdrift.anticrash.check.impl.creative.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import me.missingdrift.anticrash.check.impl.creative.ItemCheck;
import me.missingdrift.anticrash.settings.AnticrashConfig;

public class PotionLimit implements ItemCheck {

    private final int maxPotionEffects = AnticrashConfig.getInstance().getCreativeConfig().getMaxPotionEffects();
    private final boolean allowNegativeAmplifiers = AnticrashConfig.getInstance().getCreativeConfig().isAllowNegativeAmplifiers();
    private final int maxPotionEffectAmplifier = AnticrashConfig.getInstance().getCreativeConfig().getMaxPotionEffectAmplifier();
    private final int maxPotionEffectDuration = AnticrashConfig.getInstance().getCreativeConfig().getMaxPotionEffectDuration();

    @Override
    public boolean handleCheck(PacketReceiveEvent event, ItemStack clickedStack, NBTCompound nbtCompound) {
        if (!nbtCompound.getTags().containsKey("CustomPotionEffects")) {
            return false;
        }

        NBTList<NBTCompound> potionEffects = nbtCompound.getCompoundListTagOrNull("CustomPotionEffects");


        if (potionEffects.size() >= maxPotionEffects) {
            return true;
        }

        for (int i = 0; i < potionEffects.size(); i++) {
            NBTCompound effect = potionEffects.getTag(i);

            if (effect.getTags().containsKey("Duration")) {
                NBTNumber nbtNumber = effect.getNumberTagOrNull("Duration");
                if (nbtNumber != null) {
                    if (nbtNumber.getAsInt() >= maxPotionEffectDuration) {
                        return true;
                    }
                }
            }

            if (effect.getTags().containsKey("Amplifier")) {

                NBTNumber nbtNumber = effect.getNumberTagOrNull("Amplifier");
                if (nbtNumber != null) {
                    if (nbtNumber.getAsInt() < 0 && !allowNegativeAmplifiers) {
                        return true;
                    }
                    if (nbtNumber.getAsInt() > maxPotionEffectAmplifier) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
