package net.kernelcraft.betterchunkformat;

import net.fabricmc.api.ModInitializer;
import net.kernelcraft.betterchunkformat.config.SimpleConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class BetterChunkFormat implements ModInitializer {
    private static BetterChunkFormat instance;

    private final Logger LOGGER = LogManager.getLogger("BetterChunkFormat");
    private final SimpleConfig CONFIG = SimpleConfig.of("better_chunk_format")
        .provider(this::provider)
        .request();

    public static BetterChunkFormat getInstance() {
        return instance;
    }

    private String provider(String filename) {
        return """
            # This is the config file for BetterChunkFormat.
            # It's a simple key-value store.
            
            # Supported formats: GZIP, DEFLATE, LZ4, ZSTD
            region_compression_type=LZ4
            """;
    }

    @Nullable
    public RegionCompressionType getRegionCompressionType() {
        return RegionCompressionType.getFromName(CONFIG.getOrDefault("region_compression_type", "LZ4"));
    }

    @Override
    public void onInitialize() {
        instance = this;
        var regionCompressionType = getRegionCompressionType();
        if (regionCompressionType == null) {
            LOGGER.error("Invalid region compression type: " + CONFIG.get("region_compression_type"));
            return;
        }
        LOGGER.info("Using region compression type: '%s'".formatted(regionCompressionType.getName()));
    }
}
