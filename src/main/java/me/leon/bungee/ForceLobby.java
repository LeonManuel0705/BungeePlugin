package me.leon.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class ForceLobby implements Listener {

    private final Plugin plugin;

    public ForceLobby(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        player.connect(plugin.getProxy().getServerInfo("lobby"));
    }
}
