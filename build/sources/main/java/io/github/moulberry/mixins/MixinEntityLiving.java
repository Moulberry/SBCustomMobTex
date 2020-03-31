package io.github.moulberry.mixins;

import io.github.moulberry.SBCustomMobTex;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public class MixinEntityLiving {

    private boolean shouldReplace() {
        //TODO: Skyblock check
        if(!SBCustomMobTex.INSTANCE.isOnSkyblock()) return false;
        if(getThis() instanceof EntityLivingBase && !(getThis() instanceof EntityArmorStand)
                && getThis() != Minecraft.getMinecraft().thePlayer) {
            return true;
        }
        return false;
    }

    private EntityLivingBase getThis() {
        return ((EntityLivingBase)(Object)this);
    }

    private void processEquipmentSlot(CallbackInfoReturnable cir) {
        if(shouldReplace()) {
            if(cir.getReturnValue() != null) {
                ItemStack r = ((ItemStack) cir.getReturnValue()).copy();
                String display = r.getDisplayName();
                if(true || display == null || display.isEmpty()) {
                    Pair<String, Integer> pair = SBCustomMobTex.INSTANCE.getPermPair(getThis());
                    if(pair.getLeft() != null) {
                        String name = pair.getLeft().replaceAll("(?i)\\u00A7.", "");
                        r.setStackDisplayName(name);
                        cir.setReturnValue(r);
                    }
                }
            }
        }
    }

    @Inject(method = "getHeldItem", at = @At("RETURN"), cancellable = true)
    public void getHeldItem(CallbackInfoReturnable cir) {
        processEquipmentSlot(cir);
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    @Inject(method = "getEquipmentInSlot", at = @At("RETURN"), cancellable = true)
    public void getEquipmentInSlot(int slotIn, CallbackInfoReturnable cir) {
        processEquipmentSlot(cir);
    }

    @Inject(method = "getCurrentArmor", at = @At("RETURN"), cancellable = true)
    public void getCurrentArmor(int slotIn, CallbackInfoReturnable cir) {
        processEquipmentSlot(cir);
    }

}
