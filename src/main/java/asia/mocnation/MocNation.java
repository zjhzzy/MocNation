package asia.mocnation;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MocNation extends JavaPlugin {

    @Override
    public void onEnable() {
        if(!initVault()){
            getLogger().info(ChatColor.RED+"vault插件挂钩失败，请检查是否安装了vault插件。");
        }
        System.out.println(ChatColor.AQUA+"MocNation插件启动成功！！！");
        if (Bukkit.getPluginCommand("Nation") != null) {
            Bukkit.getPluginCommand("Nation").setExecutor(new Nation_Commander());
        }
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.AQUA+"MocNation插件成功卸载");
        // Plugin shutdown logic
    }
    private boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider == null) return false;
        else return true;
    }
}
