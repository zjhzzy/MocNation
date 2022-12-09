package asia.mocnation;

import java.util.List;

public class Nation_Class {
    // 国家名字
    String Name = null;
    // 战争状态
    boolean War = false;
    // 宣言
    String Lore = null;
    // 意识形态
    String Ism = "无意识形态";
    // 国家领导人
    String Lender = null;
    // 对外政策
    String Mode = "和平外交";
    // 国家话事人
    List<String> State_Speaker;
    // 国家内的玩家
    List<String> Playerlist;
}
