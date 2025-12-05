package cn.edu.usst;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

//1.角色存档类
class Saves {
   private static Role saveRole = null;
   public static void save(Role role) {
       saveRole = role;
       System.out.println("【存档成功】已保存角色："+role.getName()+"的游戏信息！");
   }
   public static Role reading() {
       if (saveRole == null) {
           System.out.println("【读取存档失败】暂无存档！");
           return null;
       }
       System.out.println("【读取存档成功】您已成功登录角色："+saveRole.getName()+"的存档！");
       return saveRole;
   }
   public static boolean ifSave() {
       return saveRole != null;
   }
}

//2.角色面板类
class RoleInfo implements Serializable {
    private int hp;
    private int mp;
    private int def;
    public RoleInfo() {
        this.hp = 100;
        this.mp = 100;
        this.def = 0;
    }
    //伤害计算
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - def);
        this.hp = Math.max(0, this.hp - actualDamage);
        System.out.println("——>受到"+actualDamage+"点伤害！ 当前生命值："+this.hp);
    }
    //战后打坐回复
    public void recovery() {
        this.hp = Math.min(100, hp + 50);
        this.mp = Math.min(100, mp + 50);
        System.out.println("【打坐】生命值已回复至"+hp+"，能量回复至"+mp+"！");
    }
    //使用物品回复
    public void useItem(Item item) {
        if(item.getType() == ItemType.hprecovery) {
            this.hp = Math.min(100, hp+item.getValue());
            System.out.println("【使用物品】"+item.getName()+"，生命值回复"+item.getValue()+"点！当前生命值："+this.hp);
        }
        else if(item.getType() == ItemType.mprecovery) {
            this.mp = Math.min(100, mp+item.getValue());
            System.out.println("【使用物品】"+item.getName()+"，能量回复"+item.getValue()+"点！当前能量："+this.mp);
        }
    }
    //能量消耗
    public boolean mpconsumption(int comsumption) {
        if(this.mp >= comsumption) {
            this.mp -= comsumption;
            System.out.println("——>消耗"+comsumption+"点能量！当前能量："+this.mp);
            return true;
        }
        System.out.println("——>能量不足"+comsumption+"点，无法释放该技能！");
        return false;
    }
    public int getHp() {
        return hp;
    }
    public int getMp() {
        return mp;
    }
    public int getDef() {
        return def;
    }
    public void setHp(int hp) {
        this.hp = Math.min(100, Math.max(0, hp));
    }
    public void setMp(int mp) {
        this.mp = Math.min(100, Math.max(0, mp));
    }
    public void setDef(int def) {
        this.def = Math.max(0, def);
    }
}

//3.技能类
class Skill implements Serializable {
    private String skillname;
    private int level;
    private int skillid;
    private int basedamage;
    private int basecomsumption;
    public Skill(String skillname, int skillid, int basedamage, int basecomsumption) {
        this.skillname = skillname;
        this.level = 1;
        this.skillid = skillid;
        this.basedamage = basedamage;
        this.basecomsumption = basecomsumption;
    }
    public void practice() {
        this.level++;
        this.basedamage += 5;
        this.basecomsumption = Math.max(1, this.basecomsumption - 2);
        System.out.println("【修炼成功】《"+skillname+"》升级至Lv"+level+"，基础伤害提升至"+basedamage+"，能量消耗降至"+basecomsumption+"！");
    }
    public int caldamage() {
        return basedamage + new Random().nextInt(7                          )-3;
    }
    public String getSkillname() {
        return skillname;
    }
    public int getLevel() {
        return level;
    }
    public int getSkillid() {
        return skillid;
    }
    public int getBasedamage() {
        return basedamage;
    }
    public int getBasecomsumption() {
        return basecomsumption;
    }
}

//4.背包类
enum ItemType {
    hprecovery,
    mprecovery,
}
class Item implements Serializable {
    private String name;
    private ItemType type;
    private int value;
    private int count;
    public Item(String name, ItemType type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.count = 1;
    }
    public void addcount(int num) {
        this.count += num;
    }
    public String getName() {
        return name;
    }
    public ItemType getType() {
        return type;
    }
    public int getValue() {
        return value;
    }
    public int getCount() {
        return count;
    }
    public void setcount(int count) {
        this.count = count;
    }
}
class Backpack implements Serializable {
    private List<Item> items;
    public Backpack() {
        this.items = new ArrayList<>();
    }
    //添加物品
    public void addItem(Item newItem) {
        for(Item i : items) {
            if(i.getName().equals(newItem.getName())) {
                i.addcount(1);
                System.out.println("【获得物品】"+newItem.getName()+"×1，当前数量；"+i.getCount());
                return;
            }
        }
        items.add(newItem);
        System.out.println("【获得物品】"+newItem.getName()+"×1！");
    }
    //使用物品
    public boolean useItem(int index, RoleInfo info) {
        if(index < 0 || index >= items.size()) {
            System.out.println("【提示】无效的物品序号！");
            return false;
        }
        Item item = items.get(index);
        if(item.getCount() <= 0) {
            System.out.println("【提示】该物品已用完！");
            return false;
        }
        info.useItem(item);
        item.setcount(item.getCount() - 1);
        if(item.getCount() <= 0) {
            items.remove(index);
        }
        return true;
    }
    //查看背包
    public void showBackpack() {
        System.out.println("\n【背包】当前物品：");
        if(items.isEmpty()) {
            System.out.println("空空如也~");
            return;
        }
        for(int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String kind = item.getType() ==  ItemType.hprecovery ? "回复生命"+item.getValue()+"点" : "回复能量"+item.getValue()+"点";
            System.out.println((i+1) + "." + item.getName() + "×" + item.getCount() + "|" + kind);
        }
    }
    public List<Item> getItems() {
        return items;
    }
}

//5.场景类
class Scene {
    private String sceneType;
    private int eventType;
    public Scene() {
        Random random = new Random();
        int sceneid = random.nextInt(4);
        int eventid = random.nextInt(100);
        switch(sceneid) {
            case 0:
                sceneType = "【昆仑绝顶】万年冰川横亘如脊，寒雾在冰峰间流转，月光洒在冰面，折射出冷冽的银辉，风卷着碎雪掠过崖边，卷起漫天雪沫...";
                break;
            case 1:
                sceneType = "【江南烟雨巷】青石板路被雨水浸得油亮，两侧粉墙黛瓦爬满青苔，乌篷船划过窄窄水道，船桨搅碎水面倒映的檐角，雨丝如帘，模糊了巷陌尽头的轮廓...";
                break;
            case 2:
                sceneType = "【荒郊破庙】残垣断壁间蛛网密布，断碑斜倚在墙角，被岁月侵蚀得字迹模糊，夕阳透过破损的窗棂，在地面投下斑驳的光影，风穿庙门发出呜呜的回响...";
                break;
            case 3:
                sceneType = "【漠北戈壁】黄沙漫天，落日如血，连绵的沙丘在风中流动成金色的浪涛，远处孤烟直上，枯木斜插在沙砾中，被风沙打磨得只剩干裂的纹理...";
                break;
        }
        if(eventid < 25) {
            eventType = 2;
            sceneType += "\n突然，从天而降一个戴着黑色面罩的神秘人！";
        }
        else if(eventid < 45) {
            eventType = 1;
            sceneType += "\n脚边的草丛传来了细微的沙沙声...";
        }
        else if(eventid < 60) {
            eventType = 3;
            sceneType += "\n地上有本泛黄的书册，看起来是本远古秘籍！";
        }
        else if(eventid < 75) {
            eventType = 4;
            sceneType += "\n你在角落里发现了一个红色的包裹，里面似乎藏着东西？";
        }
        else {
            eventType = 0;
            sceneType += "四周一片寂静，没什么特别的发现。";
        }
    }
    public String getSceneType() {
        return sceneType;
    }
    public int getEventType() {
        return eventType;
    }
}

//6.角色类
class Role implements Serializable {
    private String name;
    private RoleInfo info;
    private Backpack backpack;
    private List<Skill> skills;
    public Role(String name) {
        this.name = name;
        this.backpack = new Backpack();
        this.info = new RoleInfo();
        this.skills = new ArrayList<>();
        this.skills.add(new Skill("普通攻击", 108, 10, 0));
        this.backpack.addItem(new Item("疗伤丹", ItemType.hprecovery, 30));
        this.backpack.addItem(new Item("凝神丹", ItemType.mprecovery, 20));
    }
    //学习技能
    public void learnSkill(Skill newSkill) {
        if(newSkill == null) return;
        for(Skill skill : skills) {
            if(skill.getSkillid() == newSkill.getSkillid()) {
                System.out.println("【提示】你已经学会《"+newSkill.getSkillname()+"》了！");
                return;
            }
        }
        skills.add(newSkill);
        System.out.println("【习得技能】成功学会《"+newSkill.getSkillname()+"》！");
    }
    //修炼功法
    public void practiceSkill() {
        if(skills.size() <= 1) {
            System.out.println("【提示】技能太少，无法修炼！");
            return;
        }
        Random random = new Random();
        int idx = random.nextInt(skills.size()-1)+1;
        Skill chosen = skills.get(idx);
        chosen.practice();
    }
    //玩家战斗
    public void takeAction(Role enemy) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n【你的回合】请选择行动：");
        System.out.println("1.攻击（选择技能）  2.防御（本回合防御+8）  3.使用物品");
        int action = sc.nextInt();
        if(action == 1) {
            System.out.println("请选择技能（输入序号）：");
            for(int i = 0; i < skills.size(); i++) {
                Skill skill = skills.get(i);
                System.out.println((i+1)+".《"+skill.getSkillname()+"》基础伤害："+skill.getBasedamage()+"|能量消耗："+ skill.getBasecomsumption());
            }
            int skillidx = sc.nextInt()-1;
            if(skillidx < 0 || skillidx >= skills.size()) {
                System.out.println("【提示】无效的技能序号！");
                takeAction(enemy);
                return;
            }
            Skill chosen = skills.get(skillidx);
            if(this.getInfo().mpconsumption(chosen.getBasecomsumption())) {
                int damage = chosen.caldamage();
                System.out.println("你使出《"+chosen.getSkillname()+"》，向"+enemy.getName()+"打出"+damage+"点伤害！");
                enemy.getInfo().takeDamage(damage);
            }
            else {
                System.out.println("【提示】能量不足，无法释放该技能！请重新选择此次行动！");
                takeAction(enemy);
                return;
            }
        }
        else if(action == 2) {
            this.getInfo().setDef(this.getInfo().getDef() + 8);
            System.out.println("你摆出防御姿态，本回合防御+8");
        }
        else if(action == 3) {
            this.getBackpack().showBackpack();
            if(this.getBackpack().getItems().isEmpty()) {
                System.out.println("【提示】背包为空，无法使用物品！请重新选择此次行动！");
                takeAction(enemy);
                return;
            }
            System.out.println("请选择要使用的物品（输入序号）：");
            int itemidx = sc.nextInt()-1;
            this.getBackpack().useItem(itemidx, this.getInfo());
        }
        else {
            System.out.println("【提示】无效的行动指令！请重新选择！");
            takeAction(enemy);
            return;
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public RoleInfo getInfo() {
        return info;
    }
    public Backpack getBackpack() {
        return backpack;
    }
    public List<Skill> getSkills() {
        return skills;
    }
    public void clearSkills() {
        this.skills.clear();
    }
    public void addAllSkills(List<Skill> skills) {
        this.skills.addAll(skills);
    }
}

//7.NPC类
class NPC extends Role {
    private String dialog;
    private boolean hostile;
    private Skill teachSkill;
    private List<Skill> fixedSkills;

    public NPC() {
        super("NPC");
        Random random = new Random();
        int npcType = random.nextInt(4);
        switch(npcType) {
            case 0:
                this.setName("扫地老僧");;
                dialog = "贫僧法号了尘，于藏经阁扫地三十余载，只扫浮尘，不扫俗扰。看施主内力郁结，似遇修行瓶颈，愿赠《清心诀》一卷，不求精进，但求调和气息，心无挂碍。";
                hostile = true;
                teachSkill = new Skill("清心决", 101, 0, 5);
                fixedSkills = Arrays.asList(new Skill("清心决", 101, 0, 5), new Skill("大伏魔掌", 102, 50, 20));
                break;
            case 1:
                this.setName("拦路劫匪");
                dialog = "此路是我开！留下买路财！";
                hostile = false;
                teachSkill = null;
                fixedSkills = Arrays.asList(new Skill("蛮力挥砍", 103, 20, 10), new Skill("双手防御", 104, 0, 10));
                break;
            case 2:
                this.setName("宗门天骄");
                dialog = "我乃帝统宗门扶天宗的内门弟子，观阁下不凡，欲切磋一番，请赐教！";
                hostile = false;
                teachSkill = new Skill("混元拳", 105, 25, 23);
                fixedSkills = Arrays.asList(new Skill("混元拳", 105, 25, 23), new Skill("混元帝经", 106, 0, 30));
                break;
            default:
                this.setName("路边乞丐");
                dialog = "请少侠给点饭吃吧，我已经三天没吃饭了...";
                hostile = random.nextBoolean();
                teachSkill = new Skill("打狗棍法", 111, 27, 17);
                fixedSkills = Arrays.asList(new Skill("打狗棍法", 107, 18, 16));
        }
        this.clearSkills();
        this.addAllSkills(fixedSkills);
    }

    @Override
    public void takeAction(Role role) {
        Random random = new Random();
        int action = random.nextInt(3);
        switch(action) {
            case 0://攻击
                List<Skill> attackSkills = fixedSkills.stream().filter(s -> s.getBasedamage() > 0).toList();
                if(!attackSkills.isEmpty()) {
                    Skill chosen = attackSkills.get(random.nextInt(attackSkills.size()));
                    if(this.getInfo().mpconsumption(chosen.getBasecomsumption())) {
                        int damage = chosen.caldamage();
                        System.out.println(getName()+"使出《"+chosen.getSkillname()+"》，造成"+damage+"点伤害！");
                        role.getInfo().takeDamage(damage);
                    }
                    else {
                        System.out.println(getName()+"能量不足，只能普通攻击！");
                        role.getInfo().takeDamage(10);
                    }
                }
                else {
                    System.out.println("没有攻击技能，选择防御！");
                    this.getInfo().setDef(this.getInfo().getDef() + 8);
                    System.out.println(getName()+"举起双手防御，本回合防御+8！");
                }
                break;
            case 1:
                this.getInfo().setDef(this.getInfo().getDef()+8);
                System.out.println(getName()+"举起双手防御，本回合防御+8！");
                break;
            case 2:
                if(!this.getBackpack().getItems().isEmpty()) {
                    int itemId = random.nextInt(this.getBackpack().getItems().size());
                    Item item = this.getBackpack().getItems().get(itemId);
                    System.out.println(getName()+"使用了"+item.getName()+"！");
                    this.getBackpack().useItem(itemId, this.getInfo());
                }
                else {
                    System.out.println(getName()+"没有物品可用，选择攻击！");
                    this.takeAction(role);
                }
                break;
        }
    }
    public void interact(Role role) {
        System.out.println("【"+getName()+"】："+dialog);
        if(!hostile) {
            System.out.println("【"+getName()+"】对你发起了攻击！");
            boolean result = Battle.fight(role, this);
            if(result && teachSkill != null) {
                System.out.println("在下输得心服口服，我愿交出这本功法...");
                role.learnSkill(teachSkill);
            }
        }
        else {
            role.learnSkill(teachSkill);
        }
    }
    public boolean ifHostile() {
        return hostile;
    }
}

//8.战斗类
class Battle {
    public static boolean fight(Role role, Role enemy) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n===== 【战斗触发】=====");
        System.out.println("对手："+enemy.getName()+"|生命值："+enemy.getInfo().getHp()+"|防御："+enemy.getInfo().getDef());
        System.out.println("你的状态：生命值："+role.getInfo().getHp()+"|能量："+role.getInfo().getMp()+"|防御："+role.getInfo().getDef());
        while(role.getInfo().getHp() > 0 && enemy.getInfo().getHp() > 0) {
            role.takeAction(enemy);
            //敌人死亡判定
            if(enemy.getInfo().getHp() <= 0) {
                System.out.println("\n===== 【战斗胜利】 =====");
                System.out.println("你击败了"+enemy.getName()+"！");
                Random random = new Random();
                int rewardType = random.nextInt(2);
                if(rewardType == 0) {
                    role.getBackpack().addItem(new Item("疗伤丹", ItemType.hprecovery, 30));
                }
                else {
                    role.getBackpack().addItem(new Item("凝神丹", ItemType.mprecovery, 20));
                }
                role.getInfo().recovery();
                return true;
            }
            //敌人回合
            System.out.println("\n【"+enemy.getName()+"的回合】");
            enemy.takeAction(role);
            if(role.getInfo().getHp() <= 0) {
                System.out.println("\n===== 【战斗失败】 =====");
                System.out.println("你被"+enemy.getName()+"击败了！游戏结束！");
                return false;
            }
        }
        return false;
    }
}

//9，读文件
class FileReader {
    private static final String FILEPATH="D:\\JAVA\\MUD_GAME_TXT.txt";
    public static boolean saveRoleToTxt(Role role) {
        // 确保父目录存在（避免文件创建失败）
        File saveFile = new File(FILEPATH);
        File parentDir = saveFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        // 写入文本文件（自定义清晰的分隔格式）
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8))) {
            // 1. 角色基础信息：姓名|生命值|能量|防御
            writer.write("BASE:" + role.getName() + "|" + role.getInfo().getHp() + "|"
                    + role.getInfo().getMp() + "|" + role.getInfo().getDef() + "\n");
            // 2. 技能列表：技能名|等级|基础伤害|能量消耗|技能ID
            writer.write("SKILLS:\n");
            for (Skill skill : role.getSkills()) {
                writer.write(skill.getSkillname() + "|" + skill.getLevel() + "|"
                        + skill.getBasedamage() + "|" + skill.getBasecomsumption() + "|"
                        + skill.getSkillid() + "\n");
            }
            // 3. 物品列表：物品名|类型(hp/mp)|数值|数量
            writer.write("ITEMS:\n");
            for (Item item : role.getBackpack().getItems()) {
                String type = item.getType() == ItemType.hprecovery ? "hp" : "mp";
                writer.write(item.getName() + "|" + type + "|" + item.getValue() + "|"
                        + item.getCount() + "\n");
            }
            System.out.println("✅ 角色数据已保存到文本文件：" + FILEPATH);
            return true;
        } catch (IOException e) {
            System.err.println("❌ 保存失败：" + e.getMessage());
            return false;
        }
    }
    public static Role loadRoleFromTxt() {
        File saveFile = new File(FILEPATH);
        if (!saveFile.exists()) {
            System.out.println("❌ 未找到存档文件：" + FILEPATH);
            return null;
        }
        Role role = null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(saveFile), StandardCharsets.UTF_8))) {
            String line;
            boolean isReadingSkills = false;
            boolean isReadingItems = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // 解析基础信息
                if (line.startsWith("BASE:")) {
                    String[] baseData = line.substring(5).split("\\|");
                    if (baseData.length == 4) {
                        String name = baseData[0];
                        int hp = Integer.parseInt(baseData[1]);
                        int mp = Integer.parseInt(baseData[2]);
                        int def = Integer.parseInt(baseData[3]);
                        // 创建角色并初始化基础属性
                        role = new Role(name);
                        role.getInfo().setHp(hp);
                        role.getInfo().setMp(mp);
                        role.getInfo().setDef(def);
                        // 清空初始技能/物品（避免和存档重复）
                        role.clearSkills();
                        role.getBackpack().getItems().clear();
                    }
                }

                // 解析技能列表
                else if (line.equals("SKILLS:")) {
                    isReadingSkills = true;
                    isReadingItems = false;
                    continue;
                } else if (line.equals("ITEMS:")) {
                    isReadingSkills = false;
                    isReadingItems = true;
                    continue;
                }

                // 读取技能数据
                else if (isReadingSkills && role != null) {
                    String[] skillData = line.split("\\|");
                    if (skillData.length == 5) {
                        Skill skill = new Skill(
                                skillData[0],          // 技能名
                                Integer.parseInt(skillData[4]), // 技能ID
                                Integer.parseInt(skillData[2]), // 基础伤害
                                Integer.parseInt(skillData[3])  // 能量消耗
                        );
                        // 还原技能等级（模拟修炼次数）
                        int targetLevel = Integer.parseInt(skillData[1]);
                        for (int i = 1; i < targetLevel; i++) {
                            skill.practice();
                        }
                        role.learnSkill(skill);
                    }
                }
                // 读取物品数据
                else if (isReadingItems && role != null) {
                    String[] itemData = line.split("\\|");
                    if (itemData.length == 4) {
                        ItemType type = itemData[1].equals("hp") ? ItemType.hprecovery : ItemType.mprecovery;
                        Item item = new Item(
                                itemData[0],          // 物品名
                                type,                 // 类型
                                Integer.parseInt(itemData[2]) // 数值
                        );
                        item.setcount(Integer.parseInt(itemData[3])); // 数量
                        role.getBackpack().getItems().add(item);
                    }
                }
            }
            if (role != null) {
                System.out.println("✅ 成功读取存档：角色「" + role.getName() + "」");
            }
        } catch (Exception e) {
            System.err.println("❌ 读取存档失败：" + e.getMessage());
            role = null;
        }
        return role;
    }
    public static boolean isSaveFileExist() {
        return new File(FILEPATH).exists();
    }
}

//10.主函数
public class MudGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("===== 【笑傲江湖】 =====");
        Role role = null;
        if(FileReader.isSaveFileExist()) {
            System.out.println("检测到存档，是否读取存档？（1=读档，2=新建存档）");
            int choice = sc.nextInt();
            if(choice == 1) {
                role = FileReader.loadRoleFromTxt();
                if(role != null) {
                    startGame(role, sc);
                    return;
                }
            }
        }
        System.out.println("请输入你的角色名称：");
        String name = sc.next();
        role = new Role(name);
        System.out.println("【角色创建成功】欢迎你："+name+"侠士！");
        System.out.println("初始状态：生命值100，能量100，防御5");
        System.out.println("初始技能：普通攻击");
        System.out.println("初始物品：疗伤丹×1，凝神丹×1");
        //开始游戏
        startGame(role, sc);
    }
    private static void startGame(Role role, Scanner sc) {
        while(true) {
            System.out.println("\n===== 【主菜单】 =====");
            System.out.println("1.探索江湖  2.查看状态  3.查看背包  4.查看技能  5.修炼功法  6.存档  7.退出游戏");
            System.out.println("请选择操作（输入序号）：");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
                    explore(role);
                    break;
                case 2:
                    showStatus(role);
                    break;
                case 3:
                    role.getBackpack().showBackpack();
                    break;
                case 4:
                    showSkills(role);
                    break;
                case 5:
                    role.practiceSkill();
                    break;
                case 6:
                    FileReader.saveRoleToTxt(role);
                    Saves.save(role);
                    break;
                case 7:
                    System.out.println("【成功退出】正在保存存档...");
                    FileReader.saveRoleToTxt(role);
                    System.out.println("【游戏结束】感谢您的游玩！");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("【提示】无效的操作指令！请重新选择！");
                    break;
            }
        }
    }
    private static void explore(Role role) {
        System.out.println("\n===== 【探索江湖】 =====");
        Scene scene = new Scene();
        System.out.println(scene.getSceneType());
        switch(scene.getEventType()) {
            case 1:
                NPC friendlynpc = new NPC();
                while(friendlynpc.ifHostile()) {
                    friendlynpc = new NPC();
                }
                friendlynpc.interact(role);
                break;
            case 2:
                NPC hostilenpc =  new NPC();
                while(!hostilenpc.ifHostile()) {
                    hostilenpc = new NPC();
                }
                hostilenpc.interact(role);
                break;
            case 3:
                Random random = new Random();
                Skill newSkill;
                if(random.nextBoolean()) {
                    newSkill = new Skill("降龙十八掌", 109, 32, 25);
                }
                else {
                    newSkill = new Skill("易筋经", 110, 0, 25);
                }
                System.out.println("你捡起书册，成功学会《"+newSkill.getSkillname()+"》！");
                role.learnSkill(newSkill);
                break;
            case 4:
                Random random1 = new Random();
                Item reward = random1.nextBoolean() ? new Item("疗伤丹", ItemType.hprecovery, 30) : new Item("凝神丹", ItemType.mprecovery, 20);
                role.getBackpack().addItem(reward);
                break;
            case 0:
                System.out.println("你在原地调息片刻，恢复了少量状态。");
                role.getInfo().recovery();
                break;
        }
    }
    private static void showStatus(Role player) {
        System.out.println("\n===== 【角色状态】 =====");
        System.out.println("姓名：" + player.getName());
        System.out.println("生命值：" + player.getInfo().getHp() + "/100");
        System.out.println("能量值：" + player.getInfo().getMp() + "/100");
        System.out.println("防御值：" + player.getInfo().getDef());
        System.out.println("技能数量：" + player.getSkills().size() + "个");
        System.out.println("物品数量：" + player.getBackpack().getItems().size() + "种");
    }
    // 查看技能列表
    private static void showSkills(Role player) {
        System.out.println("\n===== 【技能列表】 =====");
        List<Skill> skills = player.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            System.out.println((i+1) + ". 《" + s.getSkillname() + "》");
            System.out.println("   等级：Lv" + s.getLevel());
            System.out.println("   基础伤害：" + s.getBasedamage());
            System.out.println("   能量消耗：" + s.getBasecomsumption());
        }
    }
}