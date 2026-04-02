package me.leon.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class BungeePlayerSync extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, new ForceLobby(this));
        getLogger().info("BungeePlayerSync wurde aktiviert!");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subchannel = in.readUTF();

        if (subchannel.equals("PlayerList")) {
            String request = in.readUTF();

            if (request.equals("ALL")) {
                if (event.getSender() instanceof Server) {
                    Server server = (Server) event.getSender();

                    Map<String, List<String>> serverPlayers = new HashMap<>();

                    for (net.md_5.bungee.api.config.ServerInfo serverInfo : getProxy().getServers().values()) {
                        List<String> players = serverInfo.getPlayers().stream()
                                .map(ProxiedPlayer::getName)
                                .collect(Collectors.toList());

                        if (!players.isEmpty()) {
                            serverPlayers.put(serverInfo.getName(), players);
                        }
                    }

                    for (Map.Entry<String, List<String>> entry : serverPlayers.entrySet()) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("PlayerList");
                        out.writeUTF(entry.getKey());
                        out.writeUTF(String.join(", ", entry.getValue()));

                        server.sendData("BungeeCord", out.toByteArray());
                    }
                }
            }
        }
    }
}