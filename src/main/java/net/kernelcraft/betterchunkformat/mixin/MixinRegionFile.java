package net.kernelcraft.betterchunkformat.mixin;

import java.nio.file.Path;

import net.kernelcraft.betterchunkformat.BetterChunkFormat;
import net.kernelcraft.betterchunkformat.compression.ChunkLZ4Compression;
import net.kernelcraft.betterchunkformat.compression.ChunkZSTDCompression;
import net.minecraft.world.storage.ChunkStreamVersion;
import net.minecraft.world.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionFile.class)
public class MixinRegionFile {
    @Shadow
    ChunkStreamVersion outputChunkStreamVersion;

    @Inject(method = "<init>(Ljava/nio/file/Path;Ljava/nio/file/Path;Z)V", at = @At("TAIL"))
    private void onInit(Path file, Path directory, boolean dsync, CallbackInfo ci) {
        var regionCompressionType = BetterChunkFormat.getInstance().getRegionCompressionType();
        if (regionCompressionType == null) {
            this.outputChunkStreamVersion = ChunkLZ4Compression.LZ4;
            return;
        }

        switch (regionCompressionType) {
            case LZ4 -> this.outputChunkStreamVersion = ChunkLZ4Compression.LZ4;
            case GZIP -> this.outputChunkStreamVersion = ChunkStreamVersion.GZIP;
            case DEFLATE -> this.outputChunkStreamVersion = ChunkStreamVersion.DEFLATE;
            case NONE -> this.outputChunkStreamVersion = ChunkStreamVersion.UNCOMPRESSED;
            case ZSTD -> this.outputChunkStreamVersion = ChunkZSTDCompression.ZSTD;
        }
    }
}
