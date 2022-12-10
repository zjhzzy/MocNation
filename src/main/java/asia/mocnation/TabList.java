package asia.mocnation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TabList implements TabCompleter {
    public List<String> onTabComplete(CommandSender Sender, Command cmd, String label, String[] args)
    {
        File NationFile = new File("nation.yml");
        FileConfiguration NationConfig = YamlConfiguration.loadConfiguration(NationFile);
        File PlayerFile = new File("player.yml");
        FileConfiguration PlayerConfig = YamlConfiguration.loadConfiguration(PlayerFile);
        /* 玩家所在的国家 为null就不在国家内 */
        String Player_Nation_value = PlayerConfig.getString(Sender.getName()+".Nation_Name",null);
        if (args.length==1)
        {
            String[] NationString = {"help","create","ism","war","add","del","tpset","show"};
            return List.of(NationString);
        }
        else if (args.length==2)
        {
            if (args[1]=="create")
            {
                return Collections.singletonList("[国家名]");
            }
            else if (args[1]=="ism")
            {
                return Collections.singletonList("[意识形态]");
            }
            else if (args[1]=="war")
            {
                return Collections.singletonList("[true/false]");
            }
            else if (args[1]=="del")
            {
                /* 国家的玩家列表 */
                return NationConfig.getStringList(Player_Nation_value+".Playerlist");
            }
            else if (args[1]=="tpset")
            {
                return null;
            }
            else if (args[1]=="show")
            {
                return NationConfig.getStringList("NationList");
            }
        }
        return null;
    }
}
