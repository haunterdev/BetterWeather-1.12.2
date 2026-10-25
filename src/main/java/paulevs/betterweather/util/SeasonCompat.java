package paulevs.betterweather.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Rain or snow for a column, with Serene Seasons when it is installed (GitHub issue #4).
 *
 * <p>Serene Seasons patches vanilla's {@code renderRainSnow} so the rain/snow choice reads its
 * seasonal temperature through {@code sereneseasons.api.season.BiomeHooks.getFloatTemperature}.
 * BetterWeather replaces that renderer, so it asks the same hook here, using vanilla's 0.15
 * snow line. Without Serene Seasons it uses the biome's temperature at the position, as vanilla's
 * renderRainSnow and snow layer placement do, so cold high ground (Extreme Hills, Taiga peaks)
 * snows where its ground is snowy. The biome snow flag alone left those columns raining.
 *
 * <p>Callers pass the column at its rain height, the block the snow layer would sit in.
 */
public final class SeasonCompat {

    private static final MethodHandle SEASON_TEMPERATURE = find();

    private SeasonCompat() {
    }

    private static MethodHandle find() {
        if (!Loader.isModLoaded("sereneseasons")) {
            return null;
        }
        try {
            Class<?> hooks = Class.forName("sereneseasons.api.season.BiomeHooks");
            return MethodHandles.publicLookup().findStatic(hooks, "getFloatTemperature",
                    MethodType.methodType(float.class, World.class, Biome.class, BlockPos.class));
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    /** True when precipitation at this column falls as snow. */
    public static boolean isSnow(World world, Biome biome, BlockPos pos) {
        if (SEASON_TEMPERATURE != null) {
            try {
                return (float) SEASON_TEMPERATURE.invokeExact(world, biome, pos) < 0.15F;
            } catch (Throwable ignored) {
                // fall through to the vanilla temperature
            }
        }
        return biome.getTemperature(pos) < 0.15F;
    }
}
