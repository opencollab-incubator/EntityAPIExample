package dev.opencollab;

import me.zimzaza4.geyserutils.geyser.GeyserUtils;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.entity.custom.CustomEntityDefinition;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
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

public class MainExtension implements Extension {
    private static final String PACK_NAME = "example_pack.mcpack";

    public MainExtension() {
        GeyserUtils.addCustomEntity("example:chest_guy");
    }

//    @Subscribe
//    public void onInit(GeyserPostInitializeEvent event) {
//        GeyserUtils.addCustomEntity("example:chest_guy");
//    }

    private Path extractResourcePack() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(PACK_NAME)) {
            if (in == null) {
                logger().error(PACK_NAME + " is missing from the extension jar");
                return null;
            }
            Files.createDirectories(this.dataFolder());
            Files.copy(in, this.dataFolder().resolve(PACK_NAME), StandardCopyOption.REPLACE_EXISTING);
            return this.dataFolder().resolve(PACK_NAME);
        } catch (IOException e) {
            logger().error("Failed to extract " + PACK_NAME, e);
            return null;
        }
    }

    @Subscribe
    public void onDefineResourcePacks(GeyserDefineResourcePacksEvent event) {
        Path resourcePack = extractResourcePack();
        if (resourcePack == null) {
            logger().warning("No resource pack available, Bedrock players won't see the custom entity visuals");
            return;
        }
        event.register(ResourcePack.create(PackCodec.path(resourcePack)));
    }
}
