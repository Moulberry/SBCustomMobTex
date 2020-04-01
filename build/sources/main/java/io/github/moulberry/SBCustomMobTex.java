package io.github.moulberry;

import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Session;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Proxy;
import java.util.*;

@Mod(modid = SBCustomMobTex.MODID, version = SBCustomMobTex.VERSION)
public class SBCustomMobTex {
    public static final String MODID = "sbcustommobtex";
    public static final String VERSION = "1.0";

    public static SBCustomMobTex INSTANCE;
    private HashMap<EntityLivingBase, Pair<String, Integer>> permMap = new HashMap<>();
    private boolean isOnSkyblock = false;
    private boolean debugMode = false;

    private String lastCustomHover = "";

    //Stolen from Biscut and used for detecting whether in skyblock
    private static final Set<String> SKYBLOCK_IN_ALL_LANGUAGES = Sets.newHashSet("SKYBLOCK","\u7A7A\u5C9B\u751F\u5B58");

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new SBCreatureDebugCommand());
        INSTANCE = this;
    }

    @SubscribeEvent
    public void tick(TickEvent event) {
        System.out.println(permMap.size());
        updateIsOnSkyblock();
        cleanMap();
    }

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event) {
        if(isDebugEnabled() && isOnSkyblock()) {
            if(Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
                Entity hit = Minecraft.getMinecraft().objectMouseOver.entityHit;

                if(shouldReplace(hit) && hit.hasCustomName()) {
                    lastCustomHover = hit.getClass().getSimpleName() + ";CUSTOM_NAME=" + hit.getName();
                }
            }

            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(lastCustomHover, 5, 5, Color.WHITE.getRGB());
        }
    }

    public void toggleDebugMode() {
        debugMode = !debugMode;
    }

    public void cleanMap() {
        World w = Minecraft.getMinecraft().theWorld;
        if(w != null) {
            Set<EntityLivingBase> toRemove = new HashSet<>();
            for(EntityLivingBase key : permMap.keySet()) {
                if(!w.loadedEntityList.contains(key)) {
                    toRemove.add(key);
                }
            }
            for(EntityLivingBase key : toRemove) {
                permMap.remove(key);
            }
        }
    }

    public Pair<String, Integer> getPermPair(EntityLivingBase key) {
        Pair<String, Integer> pair = permMap.get(key);
        if(pair == null) {
            pair = new MutablePair<>(null, 0);
            permMap.put(key, pair);
        }
        return pair;
    }

    public void setPermPair(EntityLivingBase key, Pair<String, Integer> pair) {
        permMap.put(key, pair);
    }

    public boolean isOnSkyblock() {
        return isOnSkyblock;
    }

    public boolean isDebugEnabled() {
        return debugMode;
    }

    //Stolen from Biscut's SkyblockAddons
    private void updateIsOnSkyblock() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc != null && mc.theWorld != null) {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
            if (sidebarObjective != null) {
                String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
                for (String skyblock : SKYBLOCK_IN_ALL_LANGUAGES) {
                    if (objectiveName.startsWith(skyblock)) {
                        isOnSkyblock = true;
                        return;
                    }
                }
            }
        }

        isOnSkyblock = false;
    }

    public boolean shouldReplace(Entity entity) {
        if(!isOnSkyblock()) return false;
        if(entity instanceof EntityArmorStand) return false;
        if(entity instanceof EntityPlayer) return false;

        return entity instanceof EntityLivingBase;
    }
}
