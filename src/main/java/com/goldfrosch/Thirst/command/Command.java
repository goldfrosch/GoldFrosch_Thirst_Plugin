package com.goldfrosch.Thirst.command;

import com.goldfrosch.Thirst.ThirstPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static java.lang.Integer.parseInt;

public class Command extends AbstractCommand{
  public Command(ThirstPlugin plugin, String cmd) {
    super(plugin, cmd);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
    return null;
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
  public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args){
    if (sender instanceof Player) {
      Player player = (Player) sender;
      //equalsIgnoreCase로 대소문자 상관없이 하게 만들어주기
      if(label.equalsIgnoreCase("thirst")){
        if (args.length == 0) {
          player.sendMessage(ChatColor.RED + plugin.getConfig().getString("Prefix") + "명령어의 인자가 너무 적거나 없습니다! " + ChatColor.YELLOW + "/thirst help" + ChatColor.RED + " 명령어를 통해 도움말을 확인할 수 있습니다.");
        }
        else if(args[0].equalsIgnoreCase("help")){
          player.sendMessage(ChatColor.GRAY + "==================================================");
          player.sendMessage(ChatColor.AQUA + plugin.getPdfFile().getName() + ChatColor.WHITE + " 플러그인에 포함된 명령어입니다.");
          player.sendMessage(ChatColor.GREEN + "/thirst help" + ChatColor.WHITE + " : 이 플러그인의 도움말을 출력합니다.");
          player.sendMessage(ChatColor.GREEN + "/thirst info" + ChatColor.WHITE + " : 현재 플레이어의 갈증량 확인");
          player.sendMessage(ChatColor.GRAY + "==================================================");
        }
        else{
          int thirst = ThirstCheck(plugin.getThirst().get(player.getUniqueId()));
          if(args[0].equalsIgnoreCase("info")){
            player.sendMessage(ChatColor.AQUA + plugin.getConfig().getString("Prefix") + ChatColor.WHITE + player.getName() + "님의 " + "현재 갈증량 :" + thirst);
          }
          else if(args[0].equalsIgnoreCase("reload")){

            player.sendMessage(ChatColor.AQUA + "Thirst >>" + ChatColor.WHITE + "플러그인 리로드에 성공했습니다");
          }
          else if(args[0].equalsIgnoreCase("testinfo")){
            String gage = new String(new char[thirst/2]).replace("\0", "|");
            player.sendMessage(ChatColor.AQUA + plugin.getConfig().getString("Prefix") + ChatColor.WHITE + player.getName() + "님의 " + "현재 갈증량 :" + ThirstCautionColor(thirst) + gage + "(" + thirst + "%)");
          }
          else if(args[0].equalsIgnoreCase("add")){
            if (args.length == 1){
              player.sendMessage(ChatColor.RED + "뒤에 값을 입력해주세요");
            }
            else {
              if(args[1].matches("[+-]?\\d*(\\.\\d+)?")){
                plugin.getThirst().put(player.getUniqueId(),ThirstCheck(thirst + parseInt(args[1])));
                thirst = plugin.getThirst().get(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + plugin.getConfig().getString("Prefix") + ChatColor.WHITE + player.getName() + "님의 " + "현재 갈증량 :" + thirst);
              }
              else{
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("Prefix") + "뒤에 숫자를 입력해주세요");
              }
            }
          }
          else if(args[0].equalsIgnoreCase("del")){
            if (args.length == 1){
              player.sendMessage(ChatColor.RED + plugin.getConfig().getString("Prefix") + "뒤에 값을 입력해주세요");
            }
            else {
              if(args[1].matches("[+-]?\\d*(\\.\\d+)?")){
                plugin.getThirst().put(player.getUniqueId(),ThirstCheck(thirst - parseInt(args[1])));
                thirst = plugin.getThirst().get(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + plugin.getConfig().getString("Prefix") + ChatColor.WHITE + player.getName() + "님의 " + "현재 갈증량 :" + thirst);
              }
              else{
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("Prefix") + "뒤에 숫자를 입력해주세요");
              }
            }
          }
        }
      }
    }
    return false;
  }
}
