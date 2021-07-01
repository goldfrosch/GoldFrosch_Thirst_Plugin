package com.goldfrosch.Thirst;

import com.goldfrosch.Thirst.command.Command;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class ThirstPlugin extends JavaPlugin implements Listener {
    //Load plugin.yml

    PluginDescriptionFile pdfFile = this.getDescription();
    //이벤트 실행에 필요한 플러그인 매니저
    PluginManager pm = Bukkit.getPluginManager();
    //Load Plugin Name
    String pfName = pdfFile.getName() + " v" + pdfFile.getVersion();

    public PluginDescriptionFile getPdfFile(){
        return pdfFile;
    }

    //Create Hash Map to Thirst
    HashMap<UUID, Integer> thirst = new HashMap<>();
    HashMap<UUID, Double> thirst_gage = new HashMap<>();
    HashMap<UUID, Boolean> water_cooldown = new HashMap<>();
    HashMap<UUID, Double> gage_bonus_run = new HashMap<>();
    HashMap<UUID, Double> gage_bonus_nether = new HashMap<>();

    public HashMap<UUID, Integer> getThirst() {
        return thirst;
    }

    public int ThirstCheck(int check){
        if(check >= 100){
            check = 100;
        }
        else if(check <= 0){
            check = 0;
        }
        return check;
    }

    public String ThirstCautionColor(int a){
        int b = a / 20;
        String c;
        switch (b){
            case 0:
                c = ChatColor.DARK_RED + "";
                break;
            case 1:
                c = ChatColor.RED + "";
                break;
            case 2:
                c = ChatColor.GOLD + "";
                break;
            case 3:
                c = ChatColor.DARK_AQUA + "";
                break;
            default:
                c = ChatColor.BLUE + "";
        }
        return c;
    }
    @Override
    public void onEnable() {
        //이벤트 사용 선언
        pm.registerEvents(this,this);
        //Command에서 불러오는 역할
        Command cmd = new Command(this,"thirst");
        getCommand(cmd.getcommand()).setExecutor(cmd);
        getCommand(cmd.getcommand()).setTabCompleter(cmd);

        //config파일 있는지 파악 후 생성
        if(!getDataFolder().exists()){
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        else{
            saveConfig();
        }

        //정수한 물 아이템 생성
        ItemStack BoiledWater = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) BoiledWater.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "정수한 물");
        meta.setLore(Arrays.asList(ChatColor.GRAY + " - 끓여서 정수한 깨끗한 물!",ChatColor.GRAY + " - 식혀서 먹어야 좋다"));
        meta.setBasePotionData(new PotionData(PotionType.WATER));
        BoiledWater.setItemMeta(meta);

        NamespacedKey nc = new NamespacedKey(this, "boiledwater");

        Bukkit.addRecipe(new FurnaceRecipe(nc, BoiledWater, Material.POTION, 0, getConfig().getInt("Setting.potion.Cooking_Time")));

        //플러그인 활성화
        ThirstPlugin.console(ChatColor.YELLOW + pfName + ChatColor.WHITE + " 이(가) 활성화되었습니다!");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        for(Player p: Bukkit.getServer().getOnlinePlayers()){
            getConfig().set("Users."+p.getUniqueId()+".thirst",thirst.get(p.getUniqueId()));
            getConfig().set("Users."+p.getUniqueId()+".gage",thirst_gage.get(p.getUniqueId()));
            getConfig().set("Users."+p.getUniqueId()+".cool",water_cooldown.get(p.getUniqueId()));
            saveConfig();
        }
        ThirstPlugin.console(ChatColor.YELLOW + pfName + ChatColor.WHITE + " 이(가) 비활성화되었습니다!");
        super.onDisable();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        getConfig().set("Users."+e.getPlayer().getUniqueId()+".thirst",thirst.get(e.getPlayer().getUniqueId()));
        getConfig().set("Users."+e.getPlayer().getUniqueId()+".gage",thirst_gage.get(e.getPlayer().getUniqueId()));
        getConfig().set("Users."+e.getPlayer().getUniqueId()+".cool",water_cooldown.get(e.getPlayer().getUniqueId()));
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        gage_bonus_run.put(e.getPlayer().getUniqueId(), 0.0);
        gage_bonus_nether.put(e.getPlayer().getUniqueId(), 0.0);
        water_cooldown.put(e.getPlayer().getUniqueId(), true);
        thirst.put(e.getPlayer().getUniqueId(),getConfig().getInt("Users."+e.getPlayer().getUniqueId()+".thirst"));
        thirst_gage.put(e.getPlayer().getUniqueId(),getConfig().getDouble("Users."+e.getPlayer().getUniqueId()+".gage"));

        if(e.getPlayer().getLocation().getWorld().getEnvironment().equals(World.Environment.NETHER)){
            gage_bonus_nether.put(e.getPlayer().getUniqueId(), getConfig().getDouble("Setting.minus.nether"));
        }

        if(!e.getPlayer().hasPlayedBefore()){
            thirst.put(e.getPlayer().getUniqueId(), 100);
            thirst_gage.put(e.getPlayer().getUniqueId(), 100.0);
            getConfig().set("Users."+e.getPlayer().getUniqueId()+".thirst",thirst.get(e.getPlayer().getUniqueId()));
            getConfig().set("Users."+e.getPlayer().getUniqueId()+".gage",thirst_gage.get(e.getPlayer().getUniqueId()));
            saveConfig();
        }

        BukkitScheduler actionbar = getServer().getScheduler();

        actionbar.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Boolean world_check = true;

                for(String world : getConfig().getStringList("Setting.Worlds")){
                    if(e.getPlayer().getWorld().getName().equals(world)){
                        world_check = false;
                    }
                }
                if(world_check){
                    //1초마다 목마름 게이지에 영향을 주는 게이지가 내려감
                    thirst_gage.put(e.getPlayer().getUniqueId(), thirst_gage.get(e.getPlayer().getUniqueId()) - (getConfig().getDouble("Setting.minus.Default") + gage_bonus_run.get(e.getPlayer().getUniqueId()) + gage_bonus_nether.get(e.getPlayer().getUniqueId())));
                    //목마름 게이지에 영향을 주는 게이지가 0일때에 대한 행동
                    if(thirst_gage.get(e.getPlayer().getUniqueId()) <= 0){
                        thirst.put(e.getPlayer().getUniqueId(), thirst.get(e.getPlayer().getUniqueId()) - 1);
                        thirst_gage.put(e.getPlayer().getUniqueId(), 100.0);
                    }
                    int gage = ThirstCheck(thirst.get(e.getPlayer().getUniqueId()));
                    //액션바 생성
                    String thirst_gage = new String(new char[gage/2]).replace("\0", "|");
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ThirstCautionColor(gage) + "현재 게이지: " + thirst_gage + "(" + gage + "%)"));
                    //갈증도 낮아짐에 따른 디버프
                    if(gage < 40){
                        e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.SLOW,100,2)));
                        if(gage < 20){
                            int potion_duration = getConfig().getInt("Setting.thirst.buff_power") - 1;
                            e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.WEAKNESS,100,potion_duration)));
                            e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.HUNGER,100,potion_duration)));
                            e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.SLOW_DIGGING,100,potion_duration)));
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

//    //걸을때 게이지 떨어지는 활동
//    @EventHandler
//    public void onPlayerMoving(PlayerMoveEvent e){
//        thirst_gage.put(e.getPlayer().getUniqueId(), thirst_gage.get(e.getPlayer().getUniqueId()) - 0.1);
//        //목마름 게이지에 영향을 주는 게이지가 0일때에 대한 행동
//        if(thirst_gage.get(e.getPlayer().getUniqueId()) <= 0){
//            thirst.put(e.getPlayer().getUniqueId(), thirst.get(e.getPlayer().getUniqueId()) - 1);
//            thirst_gage.put(e.getPlayer().getUniqueId(), 100.0);
//        }
//    }

    //플레이어가 달릴 떄 일어나는 이벤트
    @EventHandler
    public void onPlayerRunning(PlayerToggleSprintEvent e){
        if(e.isSprinting()){
            gage_bonus_run.put(e.getPlayer().getUniqueId(),1.0);
        }
        else if(!e.isSprinting()){
            gage_bonus_run.put(e.getPlayer().getUniqueId(),0.0);
        }
    }

    @EventHandler
    public void onPlayerConsumeSomething(PlayerItemConsumeEvent e){
        //끓인 물
        ItemStack BoiledWater = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) BoiledWater.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "정수한 물");
        meta.setLore(Arrays.asList(ChatColor.GRAY + " - 끓여서 정수한 깨끗한 물!",ChatColor.GRAY + " - 식혀서 먹어야 좋다"));
        meta.setBasePotionData(new PotionData(PotionType.WATER));
        BoiledWater.setItemMeta(meta);

        if(e.getPlayer().getInventory().getItemInMainHand().equals(BoiledWater)){
            thirst.put(e.getPlayer().getUniqueId(),thirst.get(e.getPlayer().getUniqueId()) + getConfig().getInt("Setting.potion.Thirst_Increase"));
        }
    }


    //지옥으로 갔을때의 이벤트
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        if(e.getPlayer().getLocation().getWorld().getEnvironment().equals(World.Environment.NETHER)){
            gage_bonus_nether.put(e.getPlayer().getUniqueId(),getConfig().getDouble("Setting.minus.nether"));
        }
        else {
            gage_bonus_nether.put(e.getPlayer().getUniqueId(),0.0);
        }
    }
    //우클릭 이벤트
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //선인장 뭉탱이
            if(e.getClickedBlock().getType().equals(Material.CACTUS) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SHEARS)){
                thirst.put(e.getPlayer().getUniqueId(), ThirstCheck(thirst.get(e.getPlayer().getUniqueId()) + getConfig().getInt("Setting.Cactus.num")));
                e.getClickedBlock().setType(Material.AIR);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION,1f,1f);
            }
            //물마시기 뭉탱이
            else if(e.getClickedBlock().getRelative(e.getBlockFace()).getType() == Material.WATER){
                //물마시기 쿨타임 타이머 생성
                Timer timer = new Timer();
                TimerTask TTask = new TimerTask() {
                    @Override
                    public void run() {
                        water_cooldown.put(e.getPlayer().getUniqueId(), true);
                    }
                };
                //조건 생성 5초 뒤에 true로 변경
                if(water_cooldown.get(e.getPlayer().getUniqueId())) {
                    water_cooldown.put(e.getPlayer().getUniqueId(), false);
                    timer.schedule(TTask,getConfig().getInt("Setting.Water.Cooldown") * 1000);
                    thirst.put(e.getPlayer().getUniqueId(), ThirstCheck(thirst.get(e.getPlayer().getUniqueId()) + getConfig().getInt("Setting.Water.Increment")));
                    e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION,getConfig().getInt("Setting.Water.Debuff") * 20,2)));
                    e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.HUNGER,getConfig().getInt("Setting.Water.Debuff") * 20,2)));
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION,1f,1f);
                }
            }
        }
    }

    public static void console(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

}
