package me.preciouso.ArrowAssist;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ArrowAssist.MODID, version = ArrowAssist.VERSION)
public class ArrowAssist {
    public static final String MODID = "arrowassist";
    public static final String VERSION = "0.0.1";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ArrowListener());
    }
}
