package com.goldfrosch.Thirst.command;

import com.goldfrosch.Thirst.ThirstPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.Command;

import java.util.List;

public abstract class AbstractCommand implements TabExecutor {
  protected ThirstPlugin plugin;
  private String command;

  public AbstractCommand(ThirstPlugin plugin, String cmd){
    this.plugin = plugin;
    this.command = cmd;
  }

  public String getcommand(){
    return command;
  }

  public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
  public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
