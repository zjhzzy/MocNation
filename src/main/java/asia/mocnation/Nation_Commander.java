package asia.mocnation;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Nation_Commander implements CommandExecutor
{
    private Economy economy;
    public boolean onCommand(CommandSender Sender, Command command, String label, String[] args)
    {
        File NationFile = new File("nation.yml");
        FileConfiguration NationConfig = YamlConfiguration.loadConfiguration(NationFile);
        File PlayerFile = new File("player.yml");
        FileConfiguration PlayerConfig = YamlConfiguration.loadConfiguration(PlayerFile);
        File ConfigFile = new File("config.yml");
        FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);
        /* 玩家名字，位于Player.yml文件中的 也就是class中的 来确定是否有这个玩家的 */
        String Player_Name_value = PlayerConfig.getString(Sender.getName()+".Name",null);
        /* 玩家所在的国家 为null就不在国家内 */
        String Player_Nation_value = PlayerConfig.getString(Sender.getName()+".Nation_Name",null);
        /* 玩家是否为国家领导人 默认为false */
        boolean Player_Learde_value = PlayerConfig.getBoolean(Sender.getName()+".Lender",false);
        /* 是否为国家话事人 默认为false */
        boolean Player_Speak_value = PlayerConfig.getBoolean(Sender.getName()+".State_Speaker",false);
        /* 国家的玩家列表 */
        List<String> Nation_PlayerList = NationConfig.getStringList(Player_Nation_value+".Playerlist");
        /* 文件中的国家名 */
        String File_Nation_Name = NationConfig.getString(args[1]+".Name",null);
        if (Objects.equals(args[0], "help"))
        {
/*
            如果这个玩家在player.yml文件中没有名字就生成对象写入 但是我放弃了，代码复用性太高了
            我觉得还是写事件的好
            if (PlayerConfig.get(Sender.getName()+"Name",null)==null)
            {

            }
           Nation help
           显示help帮助
*/
            Sender.sendMessage(ChatColor.DARK_GREEN + "输入 " + ChatColor.GOLD + "/Nation [命令] ? " + ChatColor.DARK_GREEN + "查看更多信息");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "create " + ChatColor.GOLD + "- " + ChatColor.YELLOW + "创建国家");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "ism " + ChatColor.GOLD + "- " + ChatColor.YELLOW + "设置国家意识形态");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "war " + ChatColor.GOLD + "- " + ChatColor.YELLOW + "进入或退出战争状态");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "add " + ChatColor.GOLD + "- " + ChatColor.YELLOW + "向玩家发送加入邀请");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "del " + ChatColor.GOLD + "- " + ChatColor.YELLOW + "将玩家踢出国家");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "tpset " + ChatColor.GOLD + "- " + ChatColor.YELLOW + "设置首都传送点");
            Sender.sendMessage(" " + ChatColor.DARK_GREEN + "show <国家名>" + ChatColor.GOLD + "- " + ChatColor.YELLOW + "显示国家信息");
            return true;
        }
        /* Nation create [国家名] <主义> <> */
        else if (Objects.equals(args[0], "create"))
        {
            if (Objects.equals(args[1], "?") || Objects.equals(args[1], "？"))
            {
                Sender.sendMessage(ChatColor.YELLOW+"使用方法：" + ChatColor.GREEN+"/Nation " +ChatColor.DARK_GREEN + "create [国家名]" + ChatColor.GOLD + "- " + ChatColor.YELLOW + "创建国家");
                Sender.sendMessage(ChatColor.YELLOW+"需要 "+ChatColor.GOLD+Config.getInt("NationMoney",10000)+ChatColor.YELLOW+" 元");
                return true;
            }
            /* 必须不是任何一个国家内的成员且第二参数不能为null，得有配置文件中的钱 */
            else if (Player_Nation_value==null&&args[1]!=null&&File_Nation_Name==null&&economy.has((OfflinePlayer) Sender,Config.getInt("Nation_Money",100000)))
            {
                // 声明类
                Nation_Class Nation = new Nation_Class();
                Player_Class Player = new Player_Class();
                // 设置名字为args[1]
                Nation.Name = args[1];
                // 设置国家领导人
                Nation.Lender = Sender.getName();
                // 设置国家话事人
                Nation.State_Speaker.add(Sender.getName());
                // 设置玩家
                Nation.Playerlist.add(Sender.getName());
                // 设置自己为领导人
                Player.Leader = true;
                // 设置自己的职务
                Player.Office = "国家领导人";
                // 设置为国家话事人
                Player.State_Speaker = true;
                // 设置玩家所在国家
                Player.Nation_Name = args[1];
                // 写入国家数据
                NationConfig.set(args[1],Nation);
                // 写入玩家数据
                PlayerConfig.set(Sender.getName(),Player);
                // 写入创建时间
                LocalDateTime dateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                Nation.Time = dateTime.format(formatter);
                // 国家列表加入国家
                List<String> NationList = NationConfig.getStringList("NationList");
                NationList.add(args[1]);
                // 写入
                NationConfig.set("NationList",NationList);
                // 扣钱
                economy.withdrawPlayer((OfflinePlayer) Sender, Config.getInt("NationMoney", 100000));
                // 发送消息
                Sender.sendMessage(ChatColor.YELLOW+"国家创建成功,ID "+ChatColor.GOLD+"main");
                Sender.sendMessage(ChatColor.YELLOW+"已经创建国家 "+ChatColor.GOLD+args[1]);
                Sender.sendMessage(ChatColor.YELLOW+"已经扣除 "+ChatColor.GOLD+Config.getInt("NationMoney",100000)+ChatColor.YELLOW+" 元");
            }
            // 名字重叠
            else if (File_Nation_Name!=null)
            {
                Sender.sendMessage(ChatColor.RED +"你所创建的国家名与其他国家重复了");
                return false;
            }
            // 属于其他国家
            else if (Player_Nation_value!=null)
            {
                Sender.sendMessage(ChatColor.RED+"你已经属于 "+ChatColor.GOLD+Player_Nation_value);
                return false;
            }
            // 钱不够
            else if (!economy.has((OfflinePlayer) Sender,Config.getInt("Nation_Money",100000)))
            {
                Sender.sendMessage(ChatColor.RED+"你所拥有的金钱数不支持你创建一个国家");
                return false;
            }
        }
        else if (Objects.equals(args[0], "ism"))
        {
            if (Objects.equals(args[1], "?") || Objects.equals(args[1], "？"))
            {
                Sender.sendMessage(ChatColor.YELLOW+"使用方法："+ChatColor.GOLD+"/Nation ism [主义]");
                Sender.sendMessage(ChatColor.YELLOW+"你要是某国家内的领导人才能使用本命令");
                Sender.sendMessage(ChatColor.YELLOW+"[主义] 比如 共产主义 社会主义");
                return true;
            }
            /* 要属于一个国家并且要为领导人 */
            else if (Player_Nation_value!=null&&Player_Learde_value&&args[1]!=null)
            {
                NationConfig.set(Player_Nation_value+".Ism",args[1]);
                Sender.sendMessage(ChatColor.YELLOW+"已将 "+ChatColor.GOLD+Player_Nation_value+ChatColor.YELLOW+" 的主义更改为 "+ChatColor.GOLD+args[1]);
            }
            // 不属于任何一个国家
            else if (Player_Nation_value==null)
            {
                Sender.sendMessage(ChatColor.RED+"你不属于任何一个国家");
                return false;
            }
            // 不是国家领导人
            else if (!Player_Learde_value)
            {
                Sender.sendMessage(ChatColor.RED+"你不是国家领导人");
                return false;
            }
        }
        else if (Objects.equals(args[0], "war"))
        {
            if (args[1] == "?"||args[1]=="？")
            {
                Sender.sendMessage(ChatColor.YELLOW+"使用方法："+ChatColor.GOLD+"/Nation war true/false");
                Sender.sendMessage(ChatColor.YELLOW+"你要是某国家内的领导人才能使用本命令");
                Sender.sendMessage(ChatColor.YELLOW+"true为战争状态，false为非战争状态");
                return true;
            }
            // 为国家领导人或者国家话事人
            else if (Player_Learde_value||Player_Speak_value)
            {
                // 在一个国家内 参数为true
                if (Player_Nation_value!=null&&Objects.equals(args[1],true))
                {
                    NationConfig.set(Player_Nation_value+".War",true);
                    Sender.sendMessage(ChatColor.YELLOW+"已经进入战争状态");
                }
                else if (Player_Nation_value!=null&&Objects.equals(args[1],false))
                {
                    NationConfig.set(Player_Nation_value+".War",false);
                    Sender.sendMessage(ChatColor.YELLOW+"已经退出战争状态");
                }
                else if (Player_Nation_value!=null&&Objects.equals(args[1],null))
                {
                    Sender.sendMessage(ChatColor.RED+"参数呢？？？");
                    return false;
                }
                else if (Player_Nation_value==null)
                {
                    Sender.sendMessage(ChatColor.YELLOW+"你的数据貌似有点大问题啊，去GitHub提交问题吧，我好修改");
                    return false;
                }
            }
            else if (!Player_Learde_value&&!Player_Speak_value)
            {
                Sender.sendMessage(ChatColor.YELLOW+"你又不是国家领导人又不是国家话事人，你在搞啥啊？？？");
                return false;
            }
        }
        // Nation add [玩家名字] <职位>
        else if (Objects.equals(args[0], "add"))
        {
            if (args[1]=="？"||args[1]=="?")
            {
                Sender.sendMessage(ChatColor.YELLOW+"使用方法："+ChatColor.GOLD+"/Nation add [玩家名字]");
                Sender.sendMessage(ChatColor.YELLOW+"你要是某国家内的领导人或者话事人才能使用本命令");
                Sender.sendMessage(ChatColor.YELLOW+"玩家必须在线因为我，懒得搞啥邮件");
                return true;
            }
            else if (Player_Learde_value||Player_Speak_value)
            {
                if (args[1]!= null&&Player_Nation_value!=null)
                {
                    // 下午完善
                }
            }
        }
        /*
         保存文件 用try环绕
         我也不知道为啥要这样搞IDEA自己提示的，自己理解吧
         我只是一个写C++的Java开发者
         不要问我为啥这样写，因为降低代码复用性
         而且代码多了我自己都不记得自己写的是啥了
         这就是为啥我的注释那么多的原因
         你要是看到了这一大段注释那么代表我一点注释都没有去除
        */
        try {
            NationConfig.save(NationFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PlayerConfig.save(PlayerFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
