package net.technicpack.eightinseven;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.chunk.Chunk;
import net.technicpack.eightinseven.coremod.EightInSevenResourcePack;
import net.technicpack.eightinseven.mixin.VisibilityChunkMixin;
import net.technicpack.eightinseven.visibility.ChunkVisibilityWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EightInSeven extends DummyModContainer {
    public static final String modId = "eightinseven";
    public static final String version = "1.0.0";

    @Mod.Instance(modId)
    public static EightInSeven instance;

    @SidedProxy(clientSide = "net.technicpack.eightinseven.ClientProxy", serverSide = "net.technicpack.eightinseven.CommonProxy")
    public static CommonProxy proxy;

    private ExecutorService visibilityRecalcService = null;

    public EightInSeven() {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = EightInSeven.modId;
        metadata.version = EightInSeven.version;
        metadata.name = "Eight In Seven";
        metadata.authorList = ImmutableList.of("Cannibalvox");
        metadata.url = "http://www.technicpack.net/";
        metadata.credits = "Developed by Technic";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public Class<?> getCustomResourcePackClass() { return EightInSevenResourcePack.class; }

    @SubscribeEvent
    public void starting(FMLServerStartingEvent event) {
        if (visibilityRecalcService != null && !visibilityRecalcService.isShutdown())
            visibilityRecalcService.shutdown();

        visibilityRecalcService = Executors.newFixedThreadPool(6);
    }

    @SubscribeEvent
    public void stopping(FMLServerStoppingEvent event) {
        if (visibilityRecalcService != null && !visibilityRecalcService.isShutdown())
            visibilityRecalcService.shutdown();
    }

    public void queueChunkRecalculate(Chunk chunk, VisibilityChunkMixin mixinChunk, int subChunk) {
        visibilityRecalcService.execute(new ChunkVisibilityWorker(chunk, mixinChunk, subChunk));
    }
}
