package io.github.moulberry.mixins;

import io.github.moulberry.SBCustomMobTex;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityPig;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Render.class)
public class MixinRender {

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    public void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if(SBCustomMobTex.INSTANCE.isOnSkyblock()) {
            if(entityIn instanceof EntityLivingBase) {
                Pair<EntityArmorStand, Integer> pair = SBCustomMobTex.INSTANCE.getPermPair((EntityLivingBase) entityIn);
                if(pair.getLeft() != null) {
                    ci.cancel();
                }
            }
        }
    }
}
