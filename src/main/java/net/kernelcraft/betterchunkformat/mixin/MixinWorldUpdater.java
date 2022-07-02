package net.kernelcraft.betterchunkformat.mixin;

import net.minecraft.world.updater.WorldUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldUpdater.class)
public class MixinWorldUpdater {
    @Shadow
    @Final
    private boolean eraseCache;

    @ModifyVariable(method = "updateWorld", ordinal = 0,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;contains(Ljava/lang/String;)Z", ordinal = 0,
            shift = At.Shift.AFTER))
    public boolean updateWorld(boolean bl3) {
        return this.eraseCache || bl3;
    }
}
