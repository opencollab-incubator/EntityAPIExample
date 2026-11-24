package dev.opencollab;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.entity.custom.CustomEntityDefinition;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.java.ServerSpawnEntityEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineEntitiesEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineResourcePacksEvent;
import org.geysermc.geyser.api.pack.PackCodec;
import org.geysermc.geyser.api.pack.ResourcePack;
import org.geysermc.geyser.api.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Plugin extends JavaPlugin implements Listener, EventRegistrar {
    private static CustomEntityDefinition CHEST_GUY;
    private static Identifier PIG;

    public static final Set<UUID> CHEST_GUYS = ConcurrentHashMap.newKeySet();
    private static final String PACK_NAME = "example_pack.mcpack";

    @Override
    public void onEnable() {
        CHEST_GUY = CustomEntityDefinition.of(Identifier.of("example:chest_guy"));
        PIG = Identifier.of("minecraft:pig");
        GeyserApi.api().eventBus().subscribe(this, GeyserDefineResourcePacksEvent.class, this::onDefineResourcePacks);
        GeyserApi.api().eventBus().subscribe(this, GeyserDefineEntitiesEvent.class, this::onEntityRegistration);
        GeyserApi.api().eventBus().subscribe(this, ServerSpawnEntityEvent.class, this::onEntitySpawn);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private Path extractResourcePack() {
        try (InputStream in = this.getResource(PACK_NAME)) {
            if (in == null) {
                getSLF4JLogger().error(PACK_NAME + " is missing from the plugin jar");
                return null;
            }
            Files.createDirectories(this.getDataFolder().toPath());
            Files.copy(in, this.getDataFolder().toPath().resolve(PACK_NAME), StandardCopyOption.REPLACE_EXISTING);
            return this.getDataFolder().toPath().resolve(PACK_NAME);
        } catch (IOException e) {
            getSLF4JLogger().error("Failed to extract " + PACK_NAME, e);
            return null;
        }
    }

    @Subscribe
    public void onDefineResourcePacks(GeyserDefineResourcePacksEvent event) {
        Path resourcePack = extractResourcePack();
        if (resourcePack == null) {
            getSLF4JLogger().warn("No resource pack available, Bedrock players won't see the custom entity visuals");
            return;
        }
        event.register(ResourcePack.create(PackCodec.path(resourcePack)));
    }

    @Subscribe
    public void onEntityRegistration(GeyserDefineEntitiesEvent event) {
        event.register(CHEST_GUY);
    }

    @Subscribe
    public void onEntitySpawn(ServerSpawnEntityEvent event) {
        if (event.entityType().is(PIG)) {
            if (CHEST_GUYS.contains(event.uuid())) {
                event.definition(CHEST_GUY);
            }
        }
    }

    @EventHandler
    private void onAddToWorld(EntityAddToWorldEvent event) {
        if (event.getEntity().getType() == EntityType.PIG) {
            if (((Pig) event.getEntity()).getEquipment().getHelmet().getType() == Material.DIAMOND_HELMET)
                CHEST_GUYS.add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    private void onRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        CHEST_GUYS.remove(event.getEntity().getUniqueId());
    }
}
