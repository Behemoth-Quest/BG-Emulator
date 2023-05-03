package world.behemoth.world;

import world.behemoth.config.ConfigData;
import world.behemoth.db.Database;
import world.behemoth.db.objects.*;
import world.behemoth.db.objects.Class;
import world.behemoth.discord.Bot;
import world.behemoth.tasks.ACGiveaway;
import world.behemoth.tasks.FreeDbPool;
import world.behemoth.tasks.FreeSFSPool;
import world.behemoth.tasks.WarzoneQueue;
import world.behemoth.tasks.Restart;

import world.behemoth.world.stats.Stats;
import com.google.common.collect.ArrayListMultimap;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.data.Zone;
import it.gotoandplay.smartfoxserver.extensions.AbstractExtension;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import jdbchelper.BeanCreator;
import jdbchelper.QueryResult;
import jdbchelper.ResultSetMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.AbstractMap.SimpleEntry;

public class World {
   private static final ResultSetMapper<String, Double> coreValuesMapper = new ResultSetMapper()
   {
     public SimpleEntry<String, Double> mapRow(ResultSet rs) throws SQLException {
      return new SimpleEntry(rs.getString("name"), Double.valueOf(rs.getDouble("value")));
   }
  };
   private static final ResultSetMapper<Integer, String> factionsMapper = new ResultSetMapper()
   {
     public SimpleEntry<Integer, String> mapRow(ResultSet rs) throws SQLException {
      return new SimpleEntry(Integer.valueOf(rs.getInt("id")), rs.getString("Name"));
     }
   };
   private static final ResultSetMapper<String, Integer> chatFiltersMapper = new ResultSetMapper()
   {
     public SimpleEntry<String, Integer> mapRow(ResultSet rs) throws SQLException {
      return new SimpleEntry(rs.getString("Swear"), Integer.valueOf(rs.getInt("TimeToMute")));
   }
   };
   private static final ResultSetMapper<Integer, Integer> itemSkillsMapper = new ResultSetMapper()
   {
     public SimpleEntry<Integer, Integer> mapRow(ResultSet rs) throws SQLException {
      return new SimpleEntry(Integer.valueOf(rs.getInt("ItemID")), Integer.valueOf(rs.getInt("SkillID")));
   }    
   };
   private static final ResultSetMapper<Integer, Double> wheelsMapper = new ResultSetMapper()
   {
     public SimpleEntry<Integer, Double> mapRow(ResultSet rs) throws SQLException {
      return new SimpleEntry(Integer.valueOf(rs.getInt("ItemID")), Double.valueOf(rs.getDouble("Chance")));
   }  
   };
   private static final BeanCreator<String> newsCreator = new BeanCreator()
   {
     public String createBean(ResultSet rs) throws SQLException {
      StringBuilder sb = new StringBuilder();
      sb.append(rs.getString("name"));
      sb.append("=");
      sb.append(rs.getString("value"));

      while(rs.next()) {
         sb.append(",");
         sb.append(rs.getString("name"));
         sb.append("=");
         sb.append(rs.getString("value"));
      }

      return sb.toString();
   }  
   };
   public HashMap<String, Area> areas;
   public HashMap<Integer, Item> items;
   public HashMap<Integer, Shop> shops;
   public HashMap<Integer, Hair> hairs;
   public HashMap<Integer, Skill> skills;
   public HashMap<Integer, Enhancement> enhancements;
   public HashMap<Integer, EnhancementPattern> patterns;
   public HashMap<Integer, Monster> monsters;
   public HashMap<Integer, Aura> auras;
   public HashMap<Integer, AuraEffects> effects;
   public HashMap<Integer, Hairshop> hairshops;
   public HashMap<Integer, Quest> quests;
   public HashMap<Integer, String> factions;
   public HashMap<Integer, Double> wheelsItems;
   public HashMap<String, Double> coreValues;
   public HashMap<String, Integer> chatFilters;
   public HashMap<Integer, Integer> specialskills;
    public HashMap<Integer, GameMenu> gameMenu;


    public Database db;
   public Users users;
   public Rooms rooms;
   public Parties parties;
   public WarzoneQueue warzoneQueue;
   public Zone zone;
   public String messageOfTheDay;
   public String newsString;
   public int EXP_RATE = 1;
   public int CP_RATE = 1;
   public int GOLD_RATE = 1;

    public int COIN_RATE = 1;
    public int REP_RATE = 1;
   public int DROP_RATE = 1;
   private AbstractExtension ext;

    public Restart restart;
    private ScheduledExecutorService tasks;
    public Bot discord;

    public World(AbstractExtension ext, Zone zone) {
      super();
      this.ext = ext;
      this.zone = zone;
      this.db = new Database(ConfigData.DB_MAX_CONNECTIONS);
      this.rooms = new Rooms(zone, this);
      this.users = new Users(zone, this);
      this.parties = new Parties();
      this.tasks = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
      this.warzoneQueue = new WarzoneQueue(this);
      this.discord = new Bot(ConfigData.DISCORD_BOT_TOKEN, this);
      this.tasks.scheduleAtFixedRate(this.warzoneQueue, 5L, 5L, TimeUnit.SECONDS);
//      this.tasks.scheduleAtFixedRate(new ACGiveaway(this), 30L, 30L, TimeUnit.MINUTES);
        this.tasks.scheduleAtFixedRate(new FreeDbPool(this.db), 30L, 30L, TimeUnit.MINUTES);
        this.tasks.scheduleAtFixedRate(new FreeSFSPool(), 30L, 30L, TimeUnit.MINUTES);
      this.retrieveDatabaseObject("all");
      SmartFoxServer.log.info("World initialized.");
   }

   public void shutdown() {
      this.db.jdbc.run("UPDATE users SET CurrentServer = \'Offline\'", new Object[0]);
   }

   public void destroy() {
      this.coreValues = null;
      this.factions = null;
      this.hairshops = null;
      this.effects = null;
      this.auras = null;
      this.monsters = null;
      this.enhancements = null;
      this.patterns = null;
      this.skills = null;
      this.hairs = null;
      this.areas = null;
      this.shops = null;
      this.items = null;
      this.tasks.shutdown();
      this.tasks = null;
      this.rooms = null;
      this.users = null;
      this.parties = null;
      this.db.destroy();
      this.db = null;
      this.zone = null;
      this.ext = null;
      SmartFoxServer.log.info("World destroyed.");
   }

    public String clearHTMLTags(String text) {
        return text.replaceAll("\\<.*?\\>", "");
    }

    public String getServerRates(String name) {
        String type = "";
        switch (name.toLowerCase()) {
            case "exp":
                type = "Experience";
                break;
            case "cp":
                type = "Class Point";
                break;
            case "gold":
                type = "Gold";
                break;
            case "coin":
                type = "Adventure Coin";
                break;
            case "rep":
                type = "Reputation";
                break;
            case "drop":
                type = "Drop";
                break;
            case "all":
                type = "All";
                break;
        }

        return type;
    }

    public void updateServerRates(String rates, int chance) {
        switch(rates.toLowerCase()){
            case "exp":
                this.EXP_RATE = chance;
                this.coreValues.put("serverRatesExp", Double.valueOf(chance));
                this.db.getJdbc().run("UPDATE settings_rates SET value = ? WHERE name = ?", new Object[]{chance, "serverRatesExp"});
                break;
            case "cp":
                this.CP_RATE = chance;
                this.coreValues.put("serverRatesCP", Double.valueOf(chance));
                this.db.getJdbc().run("UPDATE settings_rates SET value = ? WHERE name = ?", new Object[]{chance, "serverRatesCP"});
                break;
            case "gold":
                this.GOLD_RATE = chance;
                this.coreValues.put("serverRatesGold", Double.valueOf(chance));
                this.db.getJdbc().run("UPDATE settings_rates SET value = ? WHERE name = ?", new Object[]{chance, "serverRatesGold"});
                break;
            case "coin":
                this.COIN_RATE = chance;
                this.coreValues.put("serverRatesCoin", Double.valueOf(chance));
                this.db.getJdbc().run("UPDATE settings_rates SET value = ? WHERE name = ?", new Object[]{chance, "serverRatesCoin"});
                break;
            case "rep":
                this.REP_RATE = chance;
                this.coreValues.put("serverRatesRep", Double.valueOf(chance));
                this.db.getJdbc().run("UPDATE settings_rates SET value = ? WHERE name = ?", new Object[]{chance, "serverRatesRep"});
                break;
            case "drop":
                this.DROP_RATE = chance;
                this.coreValues.put("serverRatesDrop", Double.valueOf(chance));
                this.db.getJdbc().run("UPDATE settings_rates SET value = ? WHERE name = ?", new Object[]{chance, "serverRatesDrop"});
                break;
            case "all":
                updateServerRates("exp", chance);
                updateServerRates("cp", chance);
                updateServerRates("gold", chance);
                updateServerRates("coin", chance);
                updateServerRates("rep", chance);
                updateServerRates("drop", chance);
                break;
        }
    }

   public final boolean retrieveDatabaseObject(String type) {
      HashMap coreValuesData;
      Iterator chatFiltersData;
      HashMap chatFiltersData1;
       HashMap<Integer, GameMenu> gameMenu;

       if(type.equals("item")) {
         coreValuesData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM items WHERE id > 0", Item.resultSetMapper, new Object[0]));
         chatFiltersData = coreValuesData.values().iterator();

         HashMap hairshop;
         while(chatFiltersData.hasNext()) {
            Item i$ = (Item)chatFiltersData.next();
            hairshop = new HashMap(this.db.getJdbc().queryForMap("SELECT * FROM items_requirements WHERE ItemID = ?", Item.requirementMapper, Integer.valueOf(i$.getId())));
            i$.requirements = hairshop;
            if(i$.getEquipment().equals("ar") && !i$.getType().equals("Enhancement")) {
               Class reward = (Class)this.db.jdbc.queryForObject("SELECT * FROM classes WHERE ItemID = ?", Class.beanCreator, new Object[]{Integer.valueOf(i$.getId())});
               if(reward == null) {
                  throw new NullPointerException("An item with the equipment type \'Class\' does not have a matching id in the classes table. ItemID: " + i$.getId());
               }

               reward.skills = (Set)this.db.jdbc.queryForObject("SELECT id FROM skills WHERE ItemID = ?", Class.beanSkills, new Object[]{Integer.valueOf(i$.getId())});
               if(reward.skills == null) {
                  throw new NullPointerException("A class contains an empty skill set, please delete this item first. ItemID: " + i$.getId());
               }

               i$.classObj = reward;
            }
         }

         this.items = coreValuesData;
         chatFiltersData1 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM skills WHERE id > 0", Skill.resultSetMapper, new Object[0]));
         this.skills = chatFiltersData1;
         HashMap i$1 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM skills_auras WHERE id > 0", Aura.resultSetMapper, new Object[0]));
         this.auras = i$1;
         Iterator hairshop1 = i$1.values().iterator();

         while(hairshop1.hasNext()) {
            Aura reward1 = (Aura)hairshop1.next();
            reward1.effects = (Set)this.db.jdbc.queryForObject("SELECT * FROM skills_auras_effects WHERE AuraID = ?", Aura.beanEffects, new Object[]{Integer.valueOf(reward1.getId())});
            if(reward1.effects == null) {
               reward1.effects = Collections.EMPTY_SET;
            }
         }

         hairshop = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM skills_auras_effects", AuraEffects.resultSetMapper, new Object[0]));
         this.effects = hairshop;
         HashMap reward2 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM hairs", Hair.resultSetMapper, new Object[0]));
         this.hairs = reward2;
         HashMap enhancementsData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM enhancements", Enhancement.resultSetMapper, new Object[0]));
         this.enhancements = enhancementsData;
         HashMap patternsData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM enhancements_patterns WHERE id > 0", EnhancementPattern.resultSetMapper, new Object[0]));
         this.patterns = patternsData;
         HashMap factionsData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM factions", factionsMapper, new Object[0]));
         this.factions = factionsData;
         HashMap wheelsData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM wheels_rewards", wheelsMapper, new Object[0]));
         this.wheelsItems = wheelsData;
         HashMap specialsData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM items_skills", itemSkillsMapper, new Object[0]));
         this.specialskills = specialsData;
         SmartFoxServer.log.info("Item objects retrieved.");
      } else {
         Iterator i$3;
         if(type.equals("map")) {
            coreValuesData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM maps", Area.resultSetMapper, new Object[0]));

            Area i$2;
            for(chatFiltersData = coreValuesData.values().iterator(); chatFiltersData.hasNext(); i$2.cells = this.db.jdbc.queryForMap("SELECT * FROM maps_cells WHERE MapID = ?", Cell.resultSetMapper, new Object[]{Integer.valueOf(i$2.getId())})) {
               i$2 = (Area)chatFiltersData.next();
               i$2.monsters = (Set)this.db.jdbc.queryForObject("SELECT * FROM maps_monsters WHERE MapID = ?", MapMonster.setCreator, new Object[]{Integer.valueOf(i$2.getId())});
               if(i$2.monsters == null) {
                  i$2.monsters = Collections.EMPTY_SET;
               }

               i$2.items = (Set)this.db.jdbc.queryForObject("SELECT * FROM maps_items WHERE MapID = ?", Area.beanItems, new Object[]{Integer.valueOf(i$2.getId())});
               if(i$2.items == null) {
                  i$2.items = Collections.EMPTY_SET;
               }
            }

            if(this.areas != null) {
               chatFiltersData1 = new HashMap(this.areas);
               i$3 = chatFiltersData1.entrySet().iterator();

               while(i$3.hasNext()) {
                  Entry hairshop2 = (Entry)i$3.next();
                  if(!((String)hairshop2.getKey()).contains("house-")) {
                     i$3.remove();
                  }
               }

               coreValuesData.putAll(chatFiltersData1);
            }

            this.areas = coreValuesData;
            chatFiltersData1 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM monsters", Monster.resultSetMapper, new Object[0]));
            i$3 = chatFiltersData1.values().iterator();

            while(i$3.hasNext()) {
               Monster hairshop3 = (Monster)i$3.next();
               hairshop3.drops = (Set)this.db.jdbc.queryForObject("SELECT * FROM monsters_drops WHERE MonsterID = ?", Monster.beanDrops, new Object[]{Integer.valueOf(hairshop3.getId())});
               hairshop3.skills = (Set)this.db.jdbc.queryForObject("SELECT * FROM monsters_skills WHERE MonsterID = ?", Monster.beanSkills, new Object[]{Integer.valueOf(hairshop3.getId())});
               if(hairshop3.drops == null) {
                  hairshop3.drops = Collections.EMPTY_SET;
               }

               if(hairshop3.skills == null) {
                  hairshop3.skills = Collections.EMPTY_SET;
               }
            }

            this.monsters = chatFiltersData1;
            SmartFoxServer.log.info("Map objects retrieved.");
         } else if(type.equals("quest")) {
            coreValuesData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM quests", Quest.resultSetMapper, new Object[0]));
            chatFiltersData = coreValuesData.values().iterator();

            while(chatFiltersData.hasNext()) {
               Quest i$4 = (Quest)chatFiltersData.next();
               i$4.reqd = this.db.jdbc.queryForMap("SELECT * FROM quests_reqditems WHERE QuestID = ?", Quest.requirementsRewardsMapper, new Object[]{Integer.valueOf(i$4.getId())});
               i$4.rewards = ArrayListMultimap.create();
               QueryResult hairshop4 = this.db.jdbc.query("SELECT * FROM quests_rewards WHERE QuestID = ?", new Object[]{Integer.valueOf(i$4.getId())});

               while(hairshop4.next()) {
                  QuestReward reward3 = new QuestReward();
                  reward3.itemId = hairshop4.getInt("ItemID");
                  reward3.quantity = hairshop4.getInt("Quantity");
                  reward3.rate = hairshop4.getDouble("Rate");
                  reward3.type = hairshop4.getString("RewardType");
                  i$4.rewards.put(Integer.valueOf(hairshop4.getInt("ItemID")), reward3);
               }

               hairshop4.close();
               i$4.requirements = this.db.jdbc.queryForMap("SELECT * FROM quests_requirements WHERE QuestID = ?", Quest.requirementsRewardsMapper, new Object[]{Integer.valueOf(i$4.getId())});
               i$4.locations = (Set)this.db.jdbc.queryForObject("SELECT * FROM quests_locations WHERE QuestID = ?", Quest.beanLocations, new Object[]{Integer.valueOf(i$4.getId())});
               if(i$4.locations == null) {
                  i$4.locations = Collections.EMPTY_SET;
               }
            }

            this.quests = coreValuesData;
            SmartFoxServer.log.info("Quest objects retrieved.");
         } else if(type.equals("shop")) {
            coreValuesData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM shops", Shop.resultSetMapper, new Object[0]));
            chatFiltersData = coreValuesData.values().iterator();

            while(chatFiltersData.hasNext()) {
               Shop i$5 = (Shop)chatFiltersData.next();
               i$5.items = this.db.jdbc.queryForMap("SELECT id, ItemID FROM shops_items WHERE ShopID = ?", Shop.shopItemsMapper, new Object[]{Integer.valueOf(i$5.getId())});
               i$5.locations = (Set)this.db.jdbc.queryForObject("SELECT * FROM shops_locations WHERE ShopID = ?", Shop.beanLocations, new Object[]{Integer.valueOf(i$5.getId())});
               if(i$5.locations == null) {
                  i$5.locations = Collections.EMPTY_SET;
               }
            }

            this.shops = coreValuesData;
            chatFiltersData1 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM hairs_shops", Hairshop.resultSetMapper, new Object[0]));

            Hairshop hairshop5;
            for(i$3 = chatFiltersData1.values().iterator(); i$3.hasNext(); hairshop5.female = (Set)this.db.jdbc.queryForObject("SELECT * FROM hairs_shops_items WHERE Gender = ? AND ShopID = ?", Hairshop.beanHairshopItems, new Object[]{"F", Integer.valueOf(hairshop5.getId())})) {
               hairshop5 = (Hairshop)i$3.next();
               hairshop5.male = (Set)this.db.jdbc.queryForObject("SELECT * FROM hairs_shops_items WHERE Gender = ? AND ShopID = ?", Hairshop.beanHairshopItems, new Object[]{"M", Integer.valueOf(hairshop5.getId())});
            }

            this.hairshops = chatFiltersData1;
            SmartFoxServer.log.info("Shop objects retrieved.");
         } else if(type.equals("enhshop")) {
            coreValuesData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM enhancements", Enhancement.resultSetMapper, new Object[0]));
            this.enhancements = coreValuesData;
            chatFiltersData1 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM enhancements_patterns WHERE id > 0", EnhancementPattern.resultSetMapper, new Object[0]));
            this.patterns = chatFiltersData1;
            SmartFoxServer.log.info("Enhancements objects retrieved.");
         } else if(type.equals("settings")) {
            this.messageOfTheDay = this.db.jdbc.queryForString("SELECT MOTD FROM servers WHERE Name = ?", new Object[]{ConfigData.SERVER_NAME});
            this.newsString = (String)this.db.jdbc.queryForObject("SELECT * FROM settings_login", newsCreator, new Object[0]);

            coreValuesData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM settings_rates", coreValuesMapper, new Object[0]));
            this.coreValues = coreValuesData;

            chatFiltersData1 = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM settings_filters", chatFiltersMapper, new Object[0]));
            this.chatFilters = chatFiltersData1;

             gameMenu = new HashMap<>(this.db.jdbc.queryForMap("SELECT * FROM settings_menu", GameMenu.resultSetMapper));
             this.gameMenu = gameMenu;

             if (! (ConfigData.DISCORD_BOT_TOKEN).isEmpty()) {
                 HashMap commandData = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM discords_commands", DiscordCommand.resultSetMapper));
                 this.discord.commands = commandData;
//                 HashMap commandSetting = new HashMap(this.db.jdbc.queryForMap("SELECT * FROM discords_settings", DiscordCommand.resultSetMapper));
//                 this.discord.settings = commandSetting;
             };

            SmartFoxServer.log.info("Server settings retrieved.");
         } else {
            if(!type.equals("all")) {
               throw new IllegalArgumentException("Type not found");
            }

            this.retrieveDatabaseObject("item");
            this.retrieveDatabaseObject("map");
            this.retrieveDatabaseObject("quest");
            this.retrieveDatabaseObject("shop");
            this.retrieveDatabaseObject("settings");
         }
      }

      return true;
   }

   public ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit tu) {
      return this.scheduleTask(task, delay, tu, false);
   }

   public ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit tu, boolean repeat) {
      return repeat?this.tasks.scheduleAtFixedRate(task, delay, delay, tu):this.tasks.schedule(task, delay, tu);
   }

   public int roundTens(int val) {
      int x = val;

      for(int i = 0; i < 9 && x % 10 != 0; ++i) {
         ++x;
      }

      return x;
   }

   public int getExpToLevel(int playerLevel) {
      return playerLevel < ((Double)this.coreValues.get("intLevelMax")).intValue()?this.roundTens(this.getBaseValueByLevel(1000, 850000, 1.66D, playerLevel).intValue()):200000000;
   }

   public int getGuildExpToLevel(int guildLevel) {
      return guildLevel < 50?this.roundTens(this.getBaseValueByGuildLevel(50, '\ua604', 1.66D, guildLevel).intValue()):200000000;
   }

   public Double getBaseValueByGuildLevel(int base, int delta, double curve, int guildLevel) {
      byte levelCap = 50;
      int level = guildLevel < 1?1:(guildLevel > levelCap?levelCap:guildLevel);
      double x = (double)(level - 1) / (double)(levelCap - 1);
      return Double.valueOf((double)base + Math.pow(x, curve) * (double)delta);
   }

   public int getManaByLevel(int level) {
      int base = ((Double)this.coreValues.get("PCmpBase1")).intValue();
      int delta = ((Double)this.coreValues.get("PCmpBase100")).intValue();
      double curve = ((Double)this.coreValues.get("curveExponent")).doubleValue() + (double)base / (double)delta;
      return this.getBaseValueByLevel(base, delta, curve, level).intValue();
   }

   public int getHealthByLevel(int level) {
      int base = ((Double)this.coreValues.get("PChpGoal1")).intValue();
      int delta = ((Double)this.coreValues.get("PChpGoal100")).intValue();
      double curve = 1.5D + (double)base / (double)delta;
      return this.getBaseValueByLevel(base, delta, curve, level).intValue();
   }

   public int getBaseHPByLevel(int level) {
      int base = ((Double)this.coreValues.get("PChpBase1")).intValue();
      double curve = ((Double)this.coreValues.get("curveExponent")).doubleValue();
      int delta = ((Double)this.coreValues.get("PChpDelta")).intValue();
      return this.getBaseValueByLevel(base, delta, curve, level).intValue();
   }

   public int getIBudget(int itemLevel, int iRty) {
      int GstBase = ((Double)this.coreValues.get("GstBase")).intValue();
      int GstGoal = ((Double)this.coreValues.get("GstGoal")).intValue();
      double statsExponent = ((Double)this.coreValues.get("statsExponent")).doubleValue();
      int rarity = iRty < 1?1:iRty;
      int level = itemLevel + rarity - 1;
      int delta = GstGoal - GstBase;
      return this.getBaseValueByLevel(GstBase, delta, statsExponent, level).intValue();
   }

   public int getInnateStats(int userLevel) {
      int PCstBase = ((Double)this.coreValues.get("PCstBase")).intValue();
      int PCstGoal = ((Double)this.coreValues.get("PCstGoal")).intValue();
      double statsExponent = ((Double)this.coreValues.get("statsExponent")).doubleValue();
      int delta = PCstGoal - PCstBase;
      return this.getBaseValueByLevel(PCstBase, delta, statsExponent, userLevel).intValue();
   }

   public Double getBaseValueByLevel(int base, int delta, double curve, int userLevel) {
      int levelCap = ((Double)this.coreValues.get("intLevelCap")).intValue();
      int level = userLevel < 1?1:(userLevel > levelCap?levelCap:userLevel);
      double x = (double)(level - 1) / (double)(levelCap - 1);
      return Double.valueOf((double)base + Math.pow(x, curve) * (double)delta);
   }

   public Map<String, Double> getItemStats(Enhancement enhancement, String equipment) {
      LinkedHashMap itemStats = new LinkedHashMap();
      itemStats.put("END", Double.valueOf(0.0D));
      itemStats.put("STR", Double.valueOf(0.0D));
      itemStats.put("INT", Double.valueOf(0.0D));
      itemStats.put("DEX", Double.valueOf(0.0D));
      itemStats.put("WIS", Double.valueOf(0.0D));
      itemStats.put("LCK", Double.valueOf(0.0D));
      if(enhancement != null) {
         int patternId = enhancement.getPatternId();
         int rarity = enhancement.getRarity();
         int level = enhancement.getLevel();
         int iBudget = (int)Math.round((double)this.getIBudget(level, rarity) * ((Double)Stats.ratioByEquipment.get(equipment)).doubleValue());
         Map statPattern = ((EnhancementPattern)this.patterns.get(Integer.valueOf(patternId))).getStats();
         Set keyEntry = itemStats.keySet();
         double valTotal = 0.0D;

         double key;
         for(Iterator keyArray = keyEntry.iterator(); keyArray.hasNext(); valTotal += key) {
            String i = (String)keyArray.next();
            key = (double)(iBudget * ((Integer)statPattern.get(i)).intValue() / 100);
            itemStats.put(i, Double.valueOf(key));
         }

         Object[] var17 = keyEntry.toArray();
         int var18 = 0;

         while(valTotal < (double)iBudget) {
            String var19 = (String)var17[var18];
            double statVal = ((Double)itemStats.get(var19)).doubleValue() + 1.0D;
            itemStats.put(var19, Double.valueOf(statVal));
            ++valTotal;
            ++var18;
            if(var18 > var17.length - 1) {
               var18 = 0;
            }
         }
      }

      return itemStats;
   }

   public void applyFloodFilter(User user, String message) {
      long lastMsgTime = ((Long)user.properties.get("lastmessagetime")).longValue() + ConfigData.ANTI_MESSAGEFLOOD_MIN_MSG_TIME;
      if(lastMsgTime >= System.currentTimeMillis()) {
         ++user.floodCounter;
         if(user.floodCounter >= ConfigData.ANTI_MESSAGEFLOOD_TOLERANCE) {
            ++user.floodWarningsCounter;
            user.floodCounter = 0;
            this.send(new String[]{"warning", "Please do not flood the server with messages."}, user);
         }
      } else {
         user.floodCounter = 0;
      }

      if(message.equals(user.lastMessage)) {
         ++user.repeatedMsgCounter;
         if(user.repeatedMsgCounter >= ConfigData.ANTI_MESSAGEFLOOD_MAX_REPEATED) {
            ++user.floodWarningsCounter;
            user.repeatedMsgCounter = 0;
            this.send(new String[]{"warning", "Please do not flood the server with messages."}, user);
         }
      } else {
         user.repeatedMsgCounter = 0;
         user.lastMessage = message;
      }

      if(user.floodWarningsCounter >= ConfigData.ANTI_MESSAGEFLOOD_WARNINGS) {
         this.users.mute(user, 2, 12);
         user.floodWarningsCounter = 0;
      }

      user.properties.put("lastmessagetime", Long.valueOf(System.currentTimeMillis()));
   }

   public void sendServerMessage(String message) {
      JSONObject umsg = new JSONObject();
      umsg.put("cmd", "umsg");
      umsg.put("s", message);
      this.send(umsg, this.zone.getChannelList());
   }

   public void sendToUsers(JSONObject params) {
      this.ext.sendResponse(params, -1, (User)null, this.zone.getChannelList());
   }

   public void sendToUsers(String[] params) {
      this.ext.sendResponse(params, -1, (User)null, this.zone.getChannelList());
   }

   public void send(JSONObject params, LinkedList<SocketChannel> channels) {
      this.ext.sendResponse(params, -1, (User)null, channels);
   }

   public void send(JSONObject params, User user) {
      if(user != null && params != null) {
         LinkedList channels = new LinkedList();
         channels.add(user.getChannel());
         this.ext.sendResponse(params, -1, user, channels);
      }
   }

   public void send(String[] params, SocketChannel chan) {
      if(chan != null && params != null) {
         LinkedList channels = new LinkedList();
         channels.add(chan);
         this.ext.sendResponse(params, -1, (User)null, channels);
      }
   }

   public void send(String[] params, User user) {
      if(user != null && params != null) {
         LinkedList channels = new LinkedList();
         channels.add(user.getChannel());
         this.ext.sendResponse(params, -1, user, channels);
      }
   }

   public void send(String[] params, LinkedList<SocketChannel> channels) {
      this.ext.sendResponse(params, -1, (User)null, channels);
   }

   public void sendToRoom(JSONObject params, User user, Room room) {
      if(user != null && room != null) {
         if(user.getRoom() == room.getId()) {
            this.ext.sendResponse(params, -1, (User)null, room.getChannellList());
         } else {
            this.users.kick(user);
         }

      }
   }

   public void sendToRoom(String[] params, User user, Room room) {
      if(user != null && room != null) {
         if(user.getRoom() == room.getId()) {
            this.ext.sendResponse(params, -1, (User)null, room.getChannellList());
         } else {
            this.users.kick(user);
         }

      }
   }

   public void sendToRoomButOne(JSONObject o, User _user, Room room) {
      if(_user != null && room != null) {
         User[] _users = room.getAllUsersButOne(_user);
         LinkedList channels = new LinkedList();
         User[] arr$ = _users;
         int len$ = _users.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            User user = arr$[i$];
            if(user != null) {
               channels.add(user.getChannel());
            }
         }

         this.send(o, channels);
      }
   }

   public void sendToRoomButOne(String[] o, User _user, Room room) {
      if(_user != null && room != null) {
         User[] _users = room.getAllUsersButOne(_user);
         LinkedList channels = new LinkedList();
         User[] arr$ = _users;
         int len$ = _users.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            User user = arr$[i$];
            if(user != null) {
               channels.add(user.getChannel());
            }
         }

         this.send(o, channels);
      }
   }

   public void sendToGuild(JSONObject params, JSONObject guildObj) {
      JSONArray members = (JSONArray)guildObj.get("ul");
      if(members != null && members.size() > 0) {
         Iterator it = members.iterator();

         while(it.hasNext()) {
            JSONObject member = (JSONObject)it.next();
            User guildMember = this.zone.getUserByName(member.get("userName").toString().toLowerCase());
            if(guildMember != null) {
               this.send(params, guildMember);
            }
         }
      }

   }

   public void sendToGuild(String[] params, JSONObject guildObj) {
      JSONArray members = (JSONArray)guildObj.get("ul");
      if(members != null && members.size() > 0) {
         Iterator it = members.iterator();

         while(it.hasNext()) {
            JSONObject member = (JSONObject)it.next();
            User guildMember = this.zone.getUserByName(member.get("userName").toString().toLowerCase());
            if(guildMember != null) {
               this.send(params, guildMember);
            }
         }
      }

   }

   public void sendGuildUpdate(JSONObject guildObj) {
      this.sendGuildUpdateButOne((User)null, guildObj);
   }

   public void sendGuildUpdateButOne(User user, JSONObject guildObj) {
      JSONObject updateGuild = new JSONObject();
      JSONArray members = (JSONArray)guildObj.get("ul");
      if(members != null && members.size() > 0) {
         Iterator it = members.iterator();

         while(true) {
            User guildMember;
            do {
               do {
                  if(!it.hasNext()) {
                     return;
                  }

                  JSONObject member = (JSONObject)it.next();
                  guildMember = this.zone.getUserByName(member.get("userName").toString().toLowerCase());
               } while(guildMember == null);
            } while(user != null && guildMember.equals(user));

            guildMember.properties.put("guildobj", guildObj);
            updateGuild.put("cmd", "updateGuild");
            updateGuild.put("guild", guildObj);
            this.send(updateGuild, guildMember);
         }
      }
   }
}
