package dev.opencollab;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.zimzaza4.geyserutils.spigot.api.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class Plugin extends JavaPlugin implements Listener {
    private static final Set<Integer> CHEST_GUYS = new HashSet<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        for (int id : CHEST_GUYS) {
            EntityUtils.setCustomEntity(event.getPlayer(), id, "example:chest_guy");
        }
    }

    // This can be handled better, EntityAddToWorldEvent is called too late
    // so we can't use it, this only happens on spawn, not load, either
    // a persistent config can be used storing their UUID and then mapping that
    // back to an entity ID for GeyserUtils or find a better event (one called
    // before EntityAddToWorldEvent)
    @EventHandler
    private void onAddToWorld(EntitySpawnEvent event) {
        if (event.getEntity().getType() == EntityType.PIG) {
            if (((Pig) event.getEntity()).getEquipment().getHelmet().getType() == Material.DIAMOND_HELMET) {
                CHEST_GUYS.add(event.getEntity().getEntityId());

                for (Player player : Bukkit.getOnlinePlayers()) {
                    EntityUtils.setCustomEntity(player, event.getEntity().getEntityId(), "example:chest_guy");
                }
            }
        }
    }

    @EventHandler
    private void onRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        CHEST_GUYS.remove(event.getEntity().getEntityId());
    }
}
