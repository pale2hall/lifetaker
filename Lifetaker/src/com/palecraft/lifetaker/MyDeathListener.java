package com.palecraft.lifetaker;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MyDeathListener implements Listener {
    public Lifetaker lifetakerPlugin;

    public ConfigAccessor lifeFile;

    //needs constructor which would then take the argument passed and store it as foo
    public MyDeathListener(Lifetaker l) {
        this.lifetakerPlugin = l;
        this.lifeFile = new ConfigAccessor(this.lifetakerPlugin, "lives.yml");
        this.lifeFile.getConfig();
    }

    @EventHandler // EventPriority.NORMAL by default
    public void boyDunDied(PlayerDeathEvent event) throws SQLException{
        int remainingLives = 0;
        Player player = event.getEntity();
        String whoDiedName = player.getName();
        remainingLives = this.decrementLives(whoDiedName);
        event.getEntity().sendMessage(toChat("You have lost a life.  You have "+remainingLives+" left."));

        if (remainingLives < 1) this.outOfLives((Player)player);

        if(player.getKiller() != null) { 
            Entity killer = player.getKiller(); 
            if (killer instanceof Player) {
                //They were MURDERED!?!
                Player killer_player = (Player) killer;
                String killerName = killer_player.getName();
                player.sendMessage(toChat(killerName+" gained your life."));
                killer_player.sendMessage(toChat("You gained a life from "+whoDiedName+
                        ".  You have "+remainingLives+" left."));
                remainingLives = this.incrementLives(killerName);
            }
        }
        //Save to the file when done.
        this.lifeFile.saveConfig();
    }

    public String toChat(String msg)
    {
        return ChatColor.RED + "[Lifetaker] " + ChatColor.RESET + msg;
    }
    public int incrementLives(String player){
        //The player doesn't exist.
        if(!this.lifeFile.getConfig().isSet(player)){
            //get default life count
            int defaultLifeCount = this.lifetakerPlugin.getConfig().getInt("basics.startwith");
            //set that in our lifeFile
            this.lifeFile.getConfig().set(player, defaultLifeCount+1);
            return defaultLifeCount+1;
        }

        //current life count, set that count in the file.
        int lifeCount = this.lifeFile.getConfig().getInt(player);
        lifeCount++;
        this.lifeFile.getConfig().set(player, lifeCount);
        //log the info.
        lifetakerPlugin.getLogger().info(player+" gained a life! "+player+" has "+lifeCount+" lives left");
        return lifeCount;
    }
    public int decrementLives(String player){
        //The player doesn't exist.
        if(!this.lifeFile.getConfig().isSet(player)){
            //get default life count
            int defaultLifeCount = this.lifetakerPlugin.getConfig().getInt("basics.startwith");
            //set that in our lifeFile
            this.lifeFile.getConfig().set(player, defaultLifeCount-1);
            return defaultLifeCount-1;
        }
        //current life count, set that count in the file.
        int lifeCount = this.lifeFile.getConfig().getInt(player);
        lifeCount--;
        this.lifeFile.getConfig().set(player, lifeCount);
        //log the info.
        lifetakerPlugin.getLogger().info(player+" lost a life! "+player+" has "+lifeCount+" lives left");
        return lifeCount;
    }
    public void outOfLives(Player player){
        lifetakerPlugin.getLogger().info(player.getName()+" has run out of lives!");
        player.setBanned(true);
        Bukkit.getServer().broadcastMessage(this.toChat(player.getName()+ChatColor.RED+" has run out of lives, and has been banned from this server.  "+
                "For more info, please see youtu.be/jsaTElBljOE"));
        player.kickPlayer("You have run our of lives, and are banned from this server.");
        
    }
    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player p = event.getPlayer();
        int lifeCount;
        if(!this.lifeFile.getConfig().isSet(p.getName())){
            //get default life count
            int defaultLifeCount = this.lifetakerPlugin.getConfig().getInt("basics.startwith");
            p.sendMessage(toChat("This server uses Lifetaker.  When you reach 0 lives you will "+
            "be banned.  You lose a life every time you die, but you can gain a life if you kill"+
                    " another player.  Be careful."));
            lifeCount = defaultLifeCount;
        } else {

        lifeCount = this.lifeFile.getConfig().getInt(p.getName());
        }
        //current life count, set that count in the file.
        p.sendMessage(toChat("You have "+lifeCount+" lives left."));
    }
}
