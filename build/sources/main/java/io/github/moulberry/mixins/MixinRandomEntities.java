package io.github.moulberry.mixins;

import net.optifine.RandomEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomEntities.class)
public class MixinRandomEntities {

    @Inject(method = "getHorseTexturePath", at = @At("RETURN"), cancellable = true, remap = false)
    private static void getHorseTexturePath(String path, int pos, CallbackInfoReturnable cir) {
        if(cir.getReturnValue() == null) {
            cir.setReturnValue("");
        }
    }

}
