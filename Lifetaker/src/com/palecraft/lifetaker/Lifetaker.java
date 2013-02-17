package com.palecraft.lifetaker;

import org.bukkit.plugin.java.JavaPlugin;

public final class Lifetaker extends JavaPlugin {

    @Override
    public void onEnable() {

        boolean livesWillBeTaken = true;
        
        //Saves default config, if none exists.
        this.saveDefaultConfig();
        
        if (this.getConfig().getString("db.mysql.user").equals("root"))
        if (!livesWillBeTaken) getLogger().info("Lives will not be taken untill the next restart.");

        getServer().getPluginManager().registerEvents(new MyDeathListener(this), this);

    }

    @Override
    public void onDisable() {

    }


}