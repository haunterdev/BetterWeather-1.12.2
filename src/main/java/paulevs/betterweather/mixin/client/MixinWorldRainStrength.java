package paulevs.betterweather.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.betterweather.api.WeatherAPI;
import paulevs.betterweather.client.rendering.BetterWeatherRenderer;

/**
 * Client-side rain strength: the local rain density at the camera. Lives in the client mixin
 * config because it reads {@link Minecraft} and {@link BetterWeatherRenderer}; in the common
 * {@code MixinWorld} those references stopped a dedicated server from starting.
 */
@Mixin(World.class)
public class MixinWorldRainStrength {

    @Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
    private void betterweather_getRainStrength(float delta, CallbackInfoReturnable<Float> info) {
        World self = (World) (Object) this;
        if (!self.isRemote) return;
        // /bwtoggle rainstr: leave vanilla's rain strength (sky and lightmap dimming) in place.
        if (BetterWeatherRenderer.debugSkipRainStrength) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.getRenderViewEntity() == null) return;
        Entity entity = mc.getRenderViewEntity();
        float density = WeatherAPI.getRainDensity(
            mc.world, entity.posX, entity.posY, entity.posZ, true);
        info.setReturnValue(density);
    }
}
