package net.technicpack.eightinseven;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.*;
import net.technicpack.eightinseven.coremod.EightInSevenResourcePack;

public class EightInSeven extends DummyModContainer {
    public static final String modId = "eightinseven";
    public static final String version = "1.0.0";

    @Mod.Instance(modId)
    public static EightInSeven instance;

    @SidedProxy(clientSide = "net.technicpack.eightinseven.ClientProxy", serverSide = "net.technicpack.eightinseven.CommonProxy")
    public static CommonProxy proxy;

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
}
