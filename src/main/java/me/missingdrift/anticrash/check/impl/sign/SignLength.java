package me.missingdrift.anticrash.check.impl.sign;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;
import me.missingdrift.anticrash.settings.AnticrashConfig;

public class SignLength extends BaseCheck {

    private final int maxCharactersPerLine = AnticrashConfig.getInstance().getMaxSignCharactersPerLine() + 2;

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
            for (String textLine : wrapper.getTextLines()) {
                if (textLine.length() > this.maxCharactersPerLine) {
                    flagPacket(event, "Length: " + textLine.length(), false);
                }
            }
        }
    }
}
