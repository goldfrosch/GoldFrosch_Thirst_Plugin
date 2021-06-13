package com.goldfrosch.Thirst;

import com.goldfrosch.Thirst.command.Command;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;


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
    HashMap<UUID, Integer> thirst = new HashMap<UUID, Integer>();
    HashMap<UUID, Integer> thirst_gage = new HashMap<UUID, Integer>();

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
        String c = null;
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

        ThirstPlugin.console(ChatColor.YELLOW + pfName + ChatColor.WHITE + " 이(가) 활성화되었습니다!");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        ThirstPlugin.console(ChatColor.YELLOW + pfName + ChatColor.WHITE + " 이(가) 비활성화되었습니다!");
        super.onDisable();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        thirst.put(e.getPlayer().getUniqueId(), 100);
        thirst_gage.put(e.getPlayer().getUniqueId(), 100);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                int gage = thirst.get(e.getPlayer().getUniqueId());
                String thirst_gage = new String(new char[gage/2]).replace("\0", "|");
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ThirstCautionColor(gage) + "현재 게이지: " + thirst_gage + "(" + gage + "%)"));
            }
        }, 0L, 20L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CACTUS) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SHEARS)) {
            thirst.put(e.getPlayer().getUniqueId(), ThirstCheck(thirst.get(e.getPlayer().getUniqueId()) + 3));
            e.getClickedBlock().setType(Material.AIR);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION,1f,1f);
        }

        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getRelative(e.getBlockFace()).getType() == Material.WATER){
                thirst.put(e.getPlayer().getUniqueId(), ThirstCheck(thirst.get(e.getPlayer().getUniqueId()) + 2));
                e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION,100,2)));
                e.getPlayer().addPotionEffect((new PotionEffect(PotionEffectType.HUNGER,100,2)));
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION,1f,1f);
        }
    }

    public static void console(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

}
