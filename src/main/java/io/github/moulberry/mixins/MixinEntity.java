package io.github.moulberry.mixins;

import io.github.moulberry.SBCustomMobTex;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;

    @Shadow
    public World worldObj;
    @Shadow
    public int ticksExisted;

    private Entity getThis() {
        return ((Entity)(Object)this);
    }

    private double distSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2);
    }


    @Inject(method = "getCustomNameTag", at = @At("HEAD"), cancellable = true)
    public void getCustomNameTag(CallbackInfoReturnable cir) {
        int bindTicks = 60;
        int deltaThreshold = 10;
        if(SBCustomMobTex.INSTANCE.shouldReplace(getThis())) {
            String bound = null;
            Pair<String, Integer> pair = SBCustomMobTex.INSTANCE.getPermPair((EntityLivingBase) getThis());

            if(pair.getLeft() != null && pair.getRight() >= bindTicks) {
                bound = pair.getLeft();
            }

            if(bound == null) {
                int sDelta = 99999;
                double smallestDist = 16;
                EntityArmorStand closest = null;
                for(Entity entity : worldObj.loadedEntityList) {
                    if(entity instanceof EntityArmorStand) {
                        EntityArmorStand stand = (EntityArmorStand) entity;

                        int delta = Math.abs(stand.ticksExisted - ticksExisted);// && delta >= 0 && delta <= 10
                        if((stand.isInvisible() || stand.func_181026_s()) && stand.hasCustomName()) {
                            double distSq = distSq(posX, posY, posZ, stand.posX, stand.posY, stand.posZ);

                            boolean update = false;

                            if(distSq < smallestDist) {
                                if(sDelta <= deltaThreshold) {
                                    if(delta <= deltaThreshold) {
                                        update = true;
                                    }
                                } else {
                                    update = true;
                                }
                            } else if(sDelta > deltaThreshold && delta <= deltaThreshold && distSq < 1) {
                                update = true;
                            }

                            if(update) {
                                smallestDist = distSq;
                                sDelta = delta;
                                closest = stand;
                            }
                        }
                    }
                }
                if(closest != null) {
                    if(pair.getLeft() == closest.getName()) {
                        if(pair.getValue() < bindTicks) {
                            pair.setValue(pair.getValue()+1);
                        }
                    } else {
                        pair = new MutablePair<>(closest.getName(), 0);
                        SBCustomMobTex.INSTANCE.setPermPair((EntityLivingBase) getThis(), pair);
                    }

                    bound = closest.getName();
                }
            }

            if(bound != null) {
                String name = bound.replaceAll("(?i)\\u00A7.", "");
                if(cir.getReturnValue() == null) {
                    cir.setReturnValue(";" + name);
                } else {
                    cir.setReturnValue(cir.getReturnValue() + ";" + name);
                }
            }
        }
    }

    @Inject(method = "hasCustomName", at = @At("HEAD"), cancellable = true)
    public void hasCustomName(CallbackInfoReturnable cir) {
        if(SBCustomMobTex.INSTANCE.shouldReplace(getThis())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getAlwaysRenderNameTag", at = @At("HEAD"), cancellable = true)
    public void getAlwaysRenderNameTag(CallbackInfoReturnable cir) {
        if(SBCustomMobTex.INSTANCE.shouldReplace(getThis()) && SBCustomMobTex.INSTANCE.isDebugEnabled()) {
            cir.setReturnValue(true);
        }
    }

}
