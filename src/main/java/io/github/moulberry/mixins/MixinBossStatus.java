package io.github.moulberry.mixins;

import io.github.moulberry.SBCustomMobTex;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossStatus.class)
public class MixinBossStatus {

    @Inject(method = "setBossStatus", at = @At("HEAD"))
    public static void setBossStatus(IBossDisplayData displayData, boolean hasColorModifierIn, CallbackInfo ci) {
        SBCustomMobTex.INSTANCE.currentBoss = displayData;
    }

}
