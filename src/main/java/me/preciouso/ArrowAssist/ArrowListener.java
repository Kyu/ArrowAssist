package me.preciouso.ArrowAssist;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArrowListener {
    public static HashMap<EntityArrow, Long> arrowTracker = new HashMap<>();

    public static long tickCount = 0;

    public boolean isArrowStuck(EntityArrow arrow) {
        return arrow.posX == arrow.prevPosX && arrow.posY == arrow.prevPosY && arrow.posZ == arrow.prevPosZ;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        // TODO allow bugs multiplayer

        if (event.phase == TickEvent.Phase.END) {  // Start or End of tick, doesn't matter just dont fire twice
            final ArrayList<EntityArrow> toRemove = new ArrayList<>();
            tickCount++;

            for (Map.Entry<EntityArrow, Long> entry : arrowTracker.entrySet()) {
                EntityArrow arr = entry.getKey();
                if (arr != null && isArrowStuck(arr)) {
                    Long now = Minecraft.getSystemTime();
                    Long stuckTIme = entry.getValue();

                    if (stuckTIme == 0L) { // Store for 7 seconds
                        World worldA = arr.getEntityWorld();
                        if (!worldA.isRemote) {
                            // Start tracking time
                            entry.setValue(now);

                            // Start particles
                            World worldIn = Minecraft.getMinecraft().theWorld;
                            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, arr.posX, arr.posY, arr.posZ, 0.0D, 10.0D, 0.0D, 1);
                        } else {
                            // TODO Works on multiplayer ?
                            toRemove.add(arr); // delete entry, world is not remote
                        }

                    } else if ((now - stuckTIme) >= 7 * 1000) {
                        toRemove.add(arr);
                    }  else {  // Else just spawn particle
                        World worldIn = Minecraft.getMinecraft().theWorld;
                        worldIn.spawnParticle(EnumParticleTypes.REDSTONE, arr.posX, arr.posY, arr.posZ, 0.0D, 10.0D, 0.0D, 1);
                    }
                } else if (arr == null) {
                    System.out.println("Null arrow!");
                }
            }
            // Remove arrows from Hashmap
            arrowTracker.entrySet().removeIf(e -> toRemove.contains(e.getKey()));

        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onArrowShoot(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.entity;
            if (arrow.shootingEntity != null) {  // Not world gen or world spawn
                arrowTracker.put(arrow, 0L);
            }
        }
    }
}