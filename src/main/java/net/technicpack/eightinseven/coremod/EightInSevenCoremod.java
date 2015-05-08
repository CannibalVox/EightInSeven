package net.technicpack.eightinseven.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.Map;

public class EightInSevenCoremod implements IFMLLoadingPlugin {

    public EightInSevenCoremod() {
        MixinBootstrap.init();
        MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();
        env.addConfiguration("mixins.eightinseven.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "net.technicpack.eightinseven.EightInSeven";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return "net.technicpack.eightinseven.coremod.EightInSevenAccessTransformer";
    }
}
