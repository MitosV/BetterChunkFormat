package net.kernelcraft.betterchunkformat.compression;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.kernelcraft.betterchunkformat.RegionCompressionType;
import net.minecraft.world.storage.ChunkStreamVersion;

public class ChunkLZ4Compression {
    private static final LZ4FastDecompressor lz4Decompressor = LZ4Factory.fastestJavaInstance().fastDecompressor();
    private static final LZ4Compressor lz4Compressor = LZ4Factory.fastestJavaInstance().fastCompressor();
    public static final ChunkStreamVersion LZ4 = ChunkStreamVersion.add(
        new ChunkStreamVersion(RegionCompressionType.LZ4.getType(),
            in -> new net.jpountz.lz4.LZ4BlockInputStream(in, lz4Decompressor),
            out -> new net.jpountz.lz4.LZ4BlockOutputStream(out, 32 * 1024, lz4Compressor)
        ));
}
