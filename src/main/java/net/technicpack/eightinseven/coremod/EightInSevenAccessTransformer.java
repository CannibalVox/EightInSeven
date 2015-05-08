package net.technicpack.eightinseven.coremod;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class EightInSevenAccessTransformer extends AccessTransformer {
    public EightInSevenAccessTransformer() throws IOException {
        super("eightinseven_at.cfg");
    }
}
