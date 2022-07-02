package net.kernelcraft.betterchunkformat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum RegionCompressionType {
    GZIP(1, "gzip"),
    DEFLATE(2, "deflate"),
    NONE(3, "none"),
    LZ4(30, "lz4"),
    ZSTD(31, "zstd");

    private final int type;
    private final String name;

    RegionCompressionType(int type, @NotNull String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public static RegionCompressionType getFromName(@NotNull String name) {
        for (RegionCompressionType value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }

    @Nullable
    public static RegionCompressionType getFromType(int type) {
        for (RegionCompressionType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }

    public String toString() {
        return name;
    }
}