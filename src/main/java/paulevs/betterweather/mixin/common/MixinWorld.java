package paulevs.betterweather.mixin.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.betterweather.api.WeatherAPI;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void betterweather_isRaining(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    private void betterweather_isThundering(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }

    @Inject(method = "isRainingAt", at = @At("HEAD"), cancellable = true)
    private void betterweather_isRainingAt(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(WeatherAPI.isRaining(
            (World) (Object) this, pos.getX(), pos.getY(), pos.getZ()));
    }

    // getRainStrength (client only) lives in mixin.client.MixinWorldRainStrength.

    @Inject(method = "getThunderStrength", at = @At("HEAD"), cancellable = true)
    private void betterweather_getThunderStrength(float delta, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(0.0F);
    }
}
