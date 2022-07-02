package net.kernelcraft.betterchunkformat.compression;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdDictDecompress;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteStreams;
import net.kernelcraft.betterchunkformat.RegionCompressionType;
import net.minecraft.world.storage.ChunkStreamVersion;
import org.jetbrains.annotations.NotNull;

public class ChunkZSTDCompression {
    private static final short CURRENT_ZSTD_VERSION = 0;
    private static final byte ZSTD_LEVEL = 6;

    private static final LoadingCache<Short, byte[]> ZSTD_DICTS = CacheBuilder.newBuilder().build(
        new CacheLoader<>() {
            @Override
            public byte @NotNull [] load(@NotNull Short key) throws Exception {
                var zstdDict = getClass().getClassLoader().getResourceAsStream("zstd.dict." + key);

                if (zstdDict == null) {
                    throw new IOException("Could not get Zstd dictionary version " + key + ".");
                }

                return ByteStreams.toByteArray(zstdDict);
            }
        }
    );

    public static final ChunkStreamVersion ZSTD = ChunkStreamVersion.add(
        new ChunkStreamVersion(RegionCompressionType.ZSTD.getType(),
            in -> {
                int high = in.read();
                int low = in.read();
                if ((high | low) == -1) {
                    throw new EOFException("Zstd dictionary version could not be read from chunk.");
                }
                short version = (short) ((high << 8) | low);
                ZstdInputStream retVal = new ZstdInputStream(in);
                try {
                    retVal.setDict(new ZstdDictDecompress(ZSTD_DICTS.get(version)));
                } catch (ExecutionException ex) {
                    throw new IOException("Could not get Zstd dictionary for chunk.", ex);
                }
                return retVal;
            },
            out -> {
                out.write((CURRENT_ZSTD_VERSION >> 8) & 0xFF);
                out.write(CURRENT_ZSTD_VERSION & 0xFF);
                ZstdOutputStream retVal = new ZstdOutputStream(out);
                try {
                    retVal.setDict(new ZstdDictCompress(ZSTD_DICTS.get(CURRENT_ZSTD_VERSION), ZSTD_LEVEL));
                } catch (ExecutionException ex) {
                    throw new IOException("Could not get Zstd dictionary for chunk.", ex);
                }
                return retVal;
            }
        ));
}
