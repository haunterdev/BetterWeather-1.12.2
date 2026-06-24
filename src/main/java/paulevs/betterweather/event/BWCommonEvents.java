package paulevs.betterweather.event;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import paulevs.betterweather.util.LightningUtil;

import java.util.ArrayList;

public class BWCommonEvents {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.world.isRemote) return;

        WorldServer worldServer = (WorldServer) event.world;
        LightningUtil.tick();

        // Snapshot before iterating: processChunk can spawn lightning and probe
        // neighbour-chunk blocks, either of which may load/unload chunks and
        // structurally modify the backing fastutil Long2ObjectOpenHashMap.
        // fastutil iterators are not fail-fast - mutation mid-iteration corrupts
        // the iterator and throws NPE in nextEntry (see GH issue #2).
        for (Chunk chunk : new ArrayList<>(worldServer.getChunkProvider().getLoadedChunks())) {
            LightningUtil.processChunk(worldServer, chunk.x, chunk.z);
        }
    }
}
