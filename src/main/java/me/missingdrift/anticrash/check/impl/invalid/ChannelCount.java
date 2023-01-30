package me.missingdrift.anticrash.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.google.common.base.Charsets;
import me.missingdrift.anticrash.check.BaseCheck;
import me.missingdrift.anticrash.data.PlayerData;


public class ChannelCount extends BaseCheck {



    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            String payload = new String(wrapper.getData(), Charsets.UTF_8);

            String[] channels = payload.split("\0");

            if (wrapper.getChannelName().equals("REGISTER")) {
                if (playerData.getChannels().size() + channels.length > 124 || channels.length > 124) {
                    flagPacket(event);
                } else {
                    for (String channel : channels) {
                        playerData.getChannels().add(channel);
                    }
                }
            } else if (wrapper.getChannelName().equals("UNREGISTER")) {
                for (String channel : channels) {
                    playerData.getChannels().remove(channel);
                }
            }
        }
    }
}
