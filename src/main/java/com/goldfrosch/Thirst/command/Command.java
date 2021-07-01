package com.goldfrosch.Thirst.command;

import com.goldfrosch.Thirst.ThirstPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static java.lang.Integer.parseInt;

public class Command extends AbstractCommand {
  public Command(ThirstPlugin plugin, String cmd) {
    super(plugin, cmd);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
    return null;
  }

  public int ThirstCheck(int check) {
    if (check >= 100) {
      check = 100;
    } else if (check <= 0) {
      check = 0;
    }
    return check;
  }

  public String ThirstCautionColor(int a) {
    int b = a / 20;
    String c = null;
    switch (b) {
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
  public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
    String prefix = plugin.getConfig().getString("Prefix").replace("&", "§");
    if (sender instanceof Player) {
      Player player = (Player) sender;
      //equalsIgnoreCase로 대소문자 상관없이 하게 만들어주기
      if (label.equalsIgnoreCase("thirst")) {
        
        if (args.length == 0) {
          player.sendMessage(prefix + "명령어의 인자가 너무 적거나 없습니다! " + ChatColor.YELLOW + "/thirst help" + ChatColor.RED + " 명령어를 통해 도움말을 확인할 수 있습니다.");
        } 
        //도움말
        else if (args[0].equalsIgnoreCase("help")) {
          player.sendMessage(ChatColor.GRAY + "==================================================");
          player.sendMessage(ChatColor.AQUA + plugin.getPdfFile().getName() + ChatColor.WHITE + " 플러그인에 포함된 명령어입니다.");
          player.sendMessage(ChatColor.GREEN + "/thirst help" + ChatColor.WHITE + " : 이 플러그인의 도움말을 출력합니다.");
          player.sendMessage(ChatColor.GREEN + "/thirst info" + ChatColor.WHITE + " : 현재 플레이어의 갈증량 확인");
          player.sendMessage(ChatColor.GRAY + "==================================================");
        } 
        //기타 명령어
        else {
          int thirst = ThirstCheck(plugin.getThirst().get(player.getUniqueId()));
            //Reload
            if (args[0].equalsIgnoreCase("reload")) {
              if(player.hasPermission("thirst.control")){
                plugin.getConfig().get("Prefix");
                plugin.getConfig().getConfigurationSection("Setting");
                super.plugin.reloadConfig();
                plugin.saveDefaultConfig();
                plugin.getConfig().options().copyDefaults(true);
                plugin.saveConfig();
                player.sendMessage(prefix + "플러그인 리로드에 성공했습니다");
              }
              else{
                player.sendMessage(prefix + "권한이 없습니다");
              }
            }
            //Thirst add
            else if (args[0].equalsIgnoreCase("add")) {
              if(player.hasPermission("thirst.control")){
                if(args[1].matches("[+-]?\\d*(\\.\\d+)?")){
                  plugin.getThirst().put(Bukkit.getPlayer(player.getUniqueId()).getUniqueId(), ThirstCheck(thirst + parseInt(args[1])));
                  thirst = plugin.getThirst().get(Bukkit.getPlayer(player.getUniqueId()).getUniqueId());
                  player.sendMessage(prefix + Bukkit.getPlayer(player.getUniqueId()).getName() + "님의 " + "현재 갈증량 :" + thirst);
                }
                else{
                  try {
                    if(Bukkit.getPlayer(args[1]).isOnline()){
                      try{
                        if(args[2].matches("[+-]?\\d*(\\.\\d+)?")){
                          plugin.getThirst().put(Bukkit.getPlayer(args[1]).getUniqueId(), ThirstCheck(thirst + parseInt(args[2])));
                          thirst = plugin.getThirst().get(Bukkit.getPlayer(args[1]).getUniqueId());
                          player.sendMessage(prefix + Bukkit.getPlayer(args[1]).getName() + "님의 " + "현재 갈증량 :" + thirst);
                        }
                        else{
                          player.sendMessage(prefix + "숫자를 입력해주세요");
                        }
                      }catch(ArrayIndexOutOfBoundsException e){

                      }
                    }
                  }catch(NullPointerException e){
                    player.sendMessage(prefix + "현재 플레이어가 온라인이 아니거나 존재하지 않습니다");
                  }
                }


              }
              else{
                player.sendMessage(prefix + "권한이 없습니다");
              }
            }
            //Thirst del
            else if (args[0].equalsIgnoreCase("del")) {
              if(player.hasPermission("thirst.control")){
                if(args[1].matches("[+-]?\\d*(\\.\\d+)?")){
                  plugin.getThirst().put(Bukkit.getPlayer(player.getUniqueId()).getUniqueId(), ThirstCheck(thirst - parseInt(args[1])));
                  thirst = plugin.getThirst().get(Bukkit.getPlayer(player.getUniqueId()).getUniqueId());
                  player.sendMessage(prefix + Bukkit.getPlayer(player.getUniqueId()).getName() + "님의 " + "현재 갈증량 :" + thirst);
                }
                else{
                  try {
                    if(Bukkit.getPlayer(args[1]).isOnline()){
                      try{
                        if(args[2].matches("[+-]?\\d*(\\.\\d+)?")){
                          plugin.getThirst().put(Bukkit.getPlayer(args[1]).getUniqueId(), ThirstCheck(thirst - parseInt(args[2])));
                          thirst = plugin.getThirst().get(Bukkit.getPlayer(args[1]).getUniqueId());
                          player.sendMessage(prefix + Bukkit.getPlayer(args[1]).getName() + "님의 " + "현재 갈증량 :" + thirst);
                        }
                        else{
                          player.sendMessage(prefix + "숫자를 입력해주세요");
                        }
                      }catch(ArrayIndexOutOfBoundsException e){

                      }
                    }
                  }catch(NullPointerException e){
                    player.sendMessage(prefix + "현재 플레이어가 온라인이 아니거나 존재하지 않습니다");
                  }
                }


              }
              else{
                player.sendMessage(prefix + "권한이 없습니다");
              }
            }
          else {
            if (args[0].equalsIgnoreCase("info")) {
              String gage = new String(new char[thirst / 2]).replace("\0", "|");
              player.sendMessage(prefix + player.getName() + "님의 " + "현재 갈증량 :" + ThirstCautionColor(thirst) + gage + "(" + thirst + "%)");
            } else {
              player.sendMessage(prefix + "알 수 없는 커맨드 입니다");
            }
          }
        }
      }
    }
    return false;
  }
}
