package world.behemoth.world;

import world.behemoth.aqw.Achievement;
import world.behemoth.aqw.Quests;
import world.behemoth.aqw.Rank;
import world.behemoth.aqw.Settings;
import world.behemoth.config.ConfigData;
import world.behemoth.db.objects.Area;
import world.behemoth.db.objects.Aura;
import world.behemoth.db.objects.AuraEffects;
import world.behemoth.db.objects.Class;
import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Hair;
import world.behemoth.db.objects.Item;
import world.behemoth.db.objects.Skill;
import world.behemoth.tasks.KickUser;
import world.behemoth.tasks.Regeneration;
import world.behemoth.tasks.RemoveAura;
import world.behemoth.world.stats.Stats;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.data.Zone;
import it.gotoandplay.smartfoxserver.exceptions.LoginException;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import jdbchelper.BeanCreator;
import jdbchelper.JdbcException;
import jdbchelper.NoResultException;
import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class Users {
private static final BeanCreator<ConcurrentHashMap> userProperties = new BeanCreator()
{
     public ConcurrentHashMap<Object, Object> createBean(ResultSet rs) throws SQLException
    {
      ConcurrentHashMap properties = new ConcurrentHashMap();
      properties.put("dbId", Integer.valueOf(rs.getInt("id")));
      properties.put("username", rs.getString("Name"));
//      properties.put("gold", rs.getInt("Gold"));
        properties.put(Users.GOLD, rs.getInt("Gold"));
        properties.put(Users.COINS, rs.getInt("Coins"));
      properties.put("level", Integer.valueOf(rs.getInt("Level")));
      properties.put("access", Integer.valueOf(rs.getInt("Access")));
      properties.put("permamute", Integer.valueOf(rs.getInt("PermamuteFlag")));
      properties.put("gender", rs.getString("Gender"));
      properties.put("colorhair", Integer.valueOf(rs.getString("ColorHair"), 16));
      properties.put("colorskin", Integer.valueOf(rs.getString("ColorSkin"), 16));
      properties.put("coloreye", Integer.valueOf(rs.getString("ColorEye"), 16));
      properties.put("colorbase", Integer.valueOf(rs.getString("ColorBase"), 16));
      properties.put("colortrim", Integer.valueOf(rs.getString("ColorTrim"), 16));
      properties.put("coloraccessory", Integer.valueOf(rs.getString("ColorAccessory"), 16));
      properties.put("hairId", Integer.valueOf(rs.getInt("HairID")));
      properties.put("guildid", Integer.valueOf(rs.getInt("GuildID")));
      properties.put("bagslots", Integer.valueOf(rs.getInt("SlotsBag")));
      properties.put("bankslots", Integer.valueOf(rs.getInt("SlotsBank")));
      properties.put("houseslots", Integer.valueOf(rs.getInt("SlotsHouse")));
      properties.put("upgdays", Integer.valueOf(rs.getInt("UpgradeDays")));
      properties.put("lastarea", rs.getString("LastArea"));
      properties.put("quests1", rs.getString("Quests"));
      properties.put("quests2", rs.getString("Quests2"));
      properties.put("dailyquests0", Integer.valueOf(rs.getInt("DailyQuests0")));
      properties.put("dailyquests1", Integer.valueOf(rs.getInt("DailyQuests1")));
      properties.put("dailyquests2", Integer.valueOf(rs.getInt("DailyQuests2")));
      properties.put("monthlyquests0", Integer.valueOf(rs.getInt("MonthlyQuests0")));
      properties.put("settings", Integer.valueOf(rs.getInt("Settings")));
      properties.put("ia0", Integer.valueOf(rs.getInt("Achievement")));
      properties.put("guildid", Integer.valueOf(rs.getInt("GuildID")));
      properties.put("guildrank", Integer.valueOf(rs.getInt("Rank")));
      properties.put("rebirth", Integer.valueOf(rs.getInt("Rebirth")));
      properties.put("language", rs.getString("Country"));
      return properties;
   }
  };
   public static final String PERFECT_TIMINGS = "perfecttimings";
   public static final String LANGUAGE = "language";
   public static final String REBIRTH_COUNT = "rebirth";
   public static final String TRADE_TARGET = "tradetgt";
   public static final String TRADE_OFFERS = "offer";
   public static final String TRADE_OFFERS_ENHID = "offerenh";
   public static final String TRADE_GOLD = "tradegold";
   public static final String TRADE_COINS = "tradecoins";
   public static final String TRADE_LOCK = "tradelock";
   public static final String TRADE_DEAL = "tradedeal";
   public static final String REQUESTED_TRADE = "requestedguild";
   public static final String ACCESS = "access";
   public static final String ACHIEVEMENT = "ia0";
   public static final String PERMAMUTE_FLAG = "permamute";
   public static final String AFK = "afk";
   public static final String FRAME = "frame";
   public static final String HP = "hp";
   public static final String HP_MAX = "hpmax";
   public static final String MP = "mp";
   public static final String MP_MAX = "mpmax";

    public static final String GOLD = "gold";
    public static final String COINS = "coins";
   public static final String LEVEL = "level";
   public static final String PAD = "pad";
   public static final String STATE = "state";
   public static final String TARGETS = "targets";
   public static final String TX = "tx";
   public static final String TY = "ty";
   public static final String USERNAME = "username";
   public static final String ELEMENT = "none";
   public static final String FACTIONS = "factions";
   public static final String CLASS_NAME = "classname";
   public static final String CLASS_POINTS = "cp";
   public static final String CLASS_CATEGORY = "classcat";
   public static final String COLOR_ACCESSORY = "coloraccessory";
   public static final String COLOR_BASE = "colorbase";
   public static final String COLOR_EYE = "coloreye";
   public static final String COLOR_HAIR = "colorhair";
   public static final String COLOR_SKIN = "colorskin";
   public static final String COLOR_TRIM = "colortrim";
   public static final String DATABASE_ID = "dbId";
   public static final String GENDER = "gender";
   public static final String UPGRADE_DAYS = "upgdays";
   public static final String AURAS = "auras";
   public static final String EQUIPMENT = "equipment";
   public static final String GUILD_RANK = "guildrank";
   public static final String GUILD = "guildobj";
   public static final String GUILDCOLOR = "guildcolor";
   public static final String GUILD_ID = "guildid";
   public static final String PARTY_ID = "partyId";
   public static final String PVP_TEAM = "pvpteam";
   public static final String REQUESTED_FRIEND = "requestedfriend";
   public static final String REQUESTED_PARTY = "requestedparty";
   public static final String REQUESTED_DUEL = "requestedduel";
   public static final String REQUESTED_GUILD = "requestedguild";
   public static final String HAIR_ID = "hairId";
   public static final String LAST_AREA = "lastarea";
   public static final String SETTINGS = "settings";
   public static final String BOOST_XP = "xpboost";
   public static final String BOOST_GOLD = "goldboost";
   public static final String BOOST_CP = "cpboost";
   public static final String BOOST_REP = "repboost";
   public static final String SLOTS_BAG = "bagslots";
   public static final String SLOTS_BANK = "bankslots";
   public static final String SLOTS_HOUSE = "houseslots";
   public static final String ITEM_WEAPON = "weaponitem";
   public static final String ITEM_WEAPON_ENHANCEMENT = "weaponitemenhancement";
   public static final String ITEM_HOUSE_INVENTORY = "houseitems";
   public static final String DROPS = "drops";
   public static final String TEMPORARY_INVENTORY = "tempinventory";
   public static final String STATS = "stats";
   public static final String QUESTS = "quests";
   public static final String QUESTS_1 = "quests1";
   public static final String QUESTS_2 = "quests2";
   public static final String QUEST_DAILY_0 = "dailyquests0";
   public static final String QUEST_DAILY_1 = "dailyquests1";
   public static final String QUEST_DAILY_2 = "dailyquests2";
   public static final String QUEST_MONTHLY_0 = "monthlyquests0";
   public static final String REGENERATION = "regenaration";
   public static final String RESPAWN_TIME = "respawntime";
   public static final String LAST_MESSAGE_TIME = "lastmessagetime";
   public static final String REQUEST_COUNTER = "requestcounter";
   public static final String REQUEST_WARNINGS_COUNTER = "requestwarncounter";
   public static final String REQUEST_LAST = "requestlast";
   public static final String REQUEST_REPEATED_COUNTER = "requestrepeatedcounter";
   public static final String REQUEST_LAST_MILLISECONDS = "requestlastmili";
   public static final String REQUEST_BOTTING_COUNTER = "requestbotcounter";
   public static final String ROOM_QUEUED = "roomqueued";
   public static final String SKILLS = "skills";
   public static final int STATE_DEAD = 0;
   public static final int STATE_NORMAL = 1;
   public static final int STATE_COMBAT = 2;
   private final Zone zone;
   private final World world;
   private final ExtensionHelper helper;
   private final Map<String, Calendar> mutes = new HashMap();

   public Users(Zone zone, World world) {
      super();
      this.world = world;
      this.zone = zone;
      this.helper = ExtensionHelper.instance();
   }

   private void safeCloseChan(SocketChannel chan) {
      try {
         Thread.sleep(1000L);
         chan.close();
      } catch (IOException var3) {
         ;
      } catch (InterruptedException var4) {
         ;
      }

   }

   private boolean isLoggedIn(User user) {
      return user != null;
   }

   private void multiLogin(User user, SocketChannel chan) {
      this.world.send(new String[]{"multiLoginWarning"}, chan);
      this.kick(user);
      this.safeCloseChan(chan);
   }

   public void login(String name, String hash, SocketChannel chan) {
      try {
//         int ex = this.world.db.jdbc.queryForInt("SELECT id FROM users WHERE name = ? AND password = ? LIMIT 1", name, hash);
          int ex = this.world.db.jdbc.queryForInt("SELECT id FROM users WHERE name = ? AND password = ? LIMIT 1", new Object[]{name, hash});
         User userCheck = this.zone.getUserByName(name);
         if(this.isLoggedIn(userCheck)) {
            this.multiLogin(userCheck, chan);
            return;
         }

         User user = this.helper.canLogin(name, hash, chan, this.zone.getName(), true);
         user.properties = (Map)this.world.db.jdbc.queryForObject("SELECT users.*, users_guilds.GuildID, users_guilds.Rank FROM users LEFT JOIN users_guilds ON UserID = id WHERE id = ?", userProperties, new Object[]{Integer.valueOf(ex)});
         if (user.properties == null) {
            this.failLogin(name, chan);
            return;
         }

         String[] loginResponse = new String[]{"loginResponse", "true", String.valueOf(user.getUserId()), name, this.world.messageOfTheDay, (new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss")).format(new Date()), this.world.newsString};
         int accessLevel = ((Integer)user.properties.get("access")).intValue();
        if (accessLevel < 40 && ConfigData.STAFF_ONLY) {
            loginResponse = new String[]{"loginResponse", "false", "-1", name, "A game update/maintenance is currently on-going. Only the Oblivion staff can enter the server at the moment."};
            this.world.send(loginResponse, user);
            this.kick(user);
            return;
        }

         this.processLogin(user);
         if(user.getName().equals("iterator")) {
            loginResponse = new String[]{"loginIterator", "true", String.valueOf(user.getUserId()), name};
            this.world.send(loginResponse, user);
            return;
         }

         this.sendPreferences(user, "bParty");
         this.sendPreferences(user, "bGoto");
         this.sendPreferences(user, "bFriend");
         this.sendPreferences(user, "bWhisper");
         this.sendPreferences(user, "bTT");
         this.sendPreferences(user, "bDuel");
         this.sendPreferences(user, "bGuild");
         this.world.send(loginResponse, user);
      } catch (NoResultException var10) {
         this.failLogin(name, chan);
         SmartFoxServer.log.severe("NoResultException during login: " + var10.getMessage());
      } catch (JdbcException var11) {
         this.failLogin(name, chan);
         SmartFoxServer.log.severe("JdbcException during login: " + var11.getMessage());
      } catch (LoginException var12) {
         this.failLogin(name, chan);
         SmartFoxServer.log.severe("Login error: " + var12.getMessage());
      }
   }

   private void failLogin(String name, SocketChannel chan) {
      String[] loginResponse = new String[]{
              "loginResponse",
              "false",
              "-1",
              name,
              "User Data for \'" + name + "\' could not be retrieved. Please contact the Oblivion team to resolve the issue."
      };

      this.world.send(loginResponse, chan);
      this.safeCloseChan(chan);
   }

   public void sendUotls(User user, boolean showHp, boolean showHpMax, boolean showMp, boolean showMpMax, boolean showLevel, boolean showState) {
      JSONObject uotls = new JSONObject();
      JSONObject o = new JSONObject();
      uotls.put("cmd", "uotls");
      if(showHp) {
         o.put("intHP", (Integer)user.properties.get("hp"));
      }

      if(showHpMax) {
         o.put("intHPMax", (Integer)user.properties.get("hpmax"));
      }

      if(showMp) {
         o.put("intMP", (Integer)user.properties.get("mp"));
      }

      if(showMpMax) {
         o.put("intMPMax", (Integer)user.properties.get("mpmax"));
      }

      if(showLevel) {
         o.put("intLevel", (Integer)user.properties.get("level"));
      }

      if(showState) {
         o.put("intState", (Integer)user.properties.get("state"));
      }

      uotls.put("o", o);
      uotls.put("unm", user.getName());
      this.world.send(uotls, this.world.zone.getRoom(user.getRoom()).getChannellList());
   }

   public boolean isMute(User user) {
      if(this.mutes.containsKey(user.getName())) {
         Calendar cal = (Calendar)this.mutes.get(user.getName());
         if(cal.getTimeInMillis() > System.currentTimeMillis()) {
            return true;
         }

         this.mutes.remove(user.getName());
      }

      return false;
   }

   public void mute(User user, int value, int type) {
      Calendar cal = Calendar.getInstance();
      cal.add(type, value);
      this.mutes.put(user.getName(), cal);
   }

   public int getBankCount(User user) {
      int bankCount = 0;
      QueryResult bankResult = this.world.db.jdbc.query("SELECT ItemID FROM users_items WHERE Bank = 1 AND UserID = ?", new Object[]{user.properties.get("dbId")});

      while(bankResult.next()) {
         int itemid = bankResult.getInt("ItemID");
         if(!((Item)this.world.items.get(Integer.valueOf(itemid))).isCoins()) {
            ++bankCount;
         }
      }

      bankResult.close();
      return bankCount;
   }

   public void levelUp(User user, int level) {
      JSONObject levelUp = new JSONObject();
      int newLevel = level >= ((Double)this.world.coreValues.get("intLevelMax")).intValue()?((Double)this.world.coreValues.get("intLevelMax")).intValue():level;
      levelUp.put("cmd", "levelUp");
      levelUp.put("intLevel", Integer.valueOf(newLevel));
      levelUp.put("intExpToLevel", Integer.valueOf(this.world.getExpToLevel(newLevel)));
      user.properties.put("level", Integer.valueOf(newLevel));
      this.sendStats(user, true);
      this.world.db.jdbc.run("UPDATE users SET Level = ?, Exp = 0 WHERE id = ?", new Object[]{Integer.valueOf(newLevel), user.properties.get("dbId")});
      this.world.send(levelUp, user);
   }

   public void guildLevelUp(Integer guildId, int level) {
      QueryResult result = this.world.db.jdbc.query("SELECT * FROM guilds WHERE id = ?", new Object[]{guildId});
      if(result.next()) {
         JSONObject levelUp = new JSONObject();
         int newLevel = level >= 50?50:level;
         levelUp.put("cmd", "guildLevelUp");
         levelUp.put("Level", Integer.valueOf(newLevel));
         levelUp.put("ExpToLevel", Integer.valueOf(this.world.getGuildExpToLevel(newLevel)));
         JSONObject guild = this.getGuildObject(guildId.intValue());
         guild.put("Level", Integer.valueOf(newLevel));
         guild.put("Exp", Integer.valueOf(0));
         guild.put("ExpToLevel", Integer.valueOf(this.world.getGuildExpToLevel(newLevel)));
         this.world.sendGuildUpdate(guild);
         this.world.db.jdbc.run("UPDATE guilds SET Level = ?, Exp = 0 WHERE id = ?", new Object[]{Integer.valueOf(newLevel), guildId});
         this.world.sendToGuild(levelUp, guild);
      }

      result.close();
   }

   public void giveRewards(
           User user,
           int exp,
           int gold,
           int coins,
           int cp,
           int rep,
           int factionId,
           int fromId,
           String npcType
   ) {
      boolean xpBoost = ((Boolean)user.properties.get("xpboost")).booleanValue();
      boolean goldBoost = ((Boolean)user.properties.get("goldboost")).booleanValue();
      boolean repBoost = ((Boolean)user.properties.get("repboost")).booleanValue();
      boolean cpBoost = ((Boolean)user.properties.get("cpboost")).booleanValue();

      int calcExp = xpBoost?exp * (1 + this.world.EXP_RATE):exp * this.world.EXP_RATE;
      int calcGold = goldBoost?gold * (1 + this.world.GOLD_RATE):gold * this.world.GOLD_RATE;
       int calcCoin = (coins * this.world.COIN_RATE);
       int calcRep = repBoost?rep * (1 + this.world.REP_RATE):rep * this.world.REP_RATE;
      int calcCp = cpBoost?cp * (1 + this.world.CP_RATE):cp * this.world.CP_RATE;

//       Iterator iterator = ((JSONObject) user.properties.get(Users.EQUIPMENT)).values().iterator();
//       while (iterator.hasNext()) {
//           Item item = world.items.get(((JSONObject) iterator.next()).getInt("ItemID"));
//           if (item.effects.iterator().hasNext()) {
//               if (item.effects.iterator().next().getExp() != 0)
//                   calcExp *= 1.0 + item.effects.iterator().next().getExp();
//               if (item.effects.iterator().next().getGold() != 0)
//                   calcGold *= 1.0 + item.effects.iterator().next().getGold();
//               if (item.effects.iterator().next().getCoins() != 0)
//                   calcCoin *= 1.0 + item.effects.iterator().next().getCoins();
//               if (item.effects.iterator().next().getCP() != 0)
//                   calcCp *= 1.0 + item.effects.iterator().next().getCP();
//               if (item.effects.iterator().next().getReputation() != 0)
//                   calcRep *= 1.0 + item.effects.iterator().next().getReputation();
//           }
//       }

      int maxLevel = ((Double)this.world.coreValues.get("intLevelMax")).intValue();
      int expReward = ((Integer)user.properties.get("level")).intValue() < maxLevel?calcExp:0;
      int classPoints = ((Integer)user.properties.get("cp")).intValue();
      int userLevel = ((Integer)user.properties.get("level")).intValue();
      int userCp = calcCp + classPoints >= 302500?302500:calcCp + classPoints;
      int curRank = Rank.getRankFromPoints(((Integer)user.properties.get("cp")).intValue());

      JSONObject eqp = (JSONObject)user.properties.get("equipment");
      JSONObject oldItem = eqp.getJSONObject("ar");

      int itemQuantity = this.world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", oldItem.getInt("ItemID"), user.properties.get("dbId"));

      Map factions = (Map)user.properties.get("factions");
      JSONObject    addGoldExp = new JSONObject();
      addGoldExp.put("cmd", "addGoldExp");
      addGoldExp.put("id", Integer.valueOf(fromId));
      addGoldExp.put("intGold", Integer.valueOf(calcGold));
       addGoldExp.put("intCoins", Integer.valueOf(calcCoin));
       addGoldExp.put("typ", npcType);

      if (userLevel < maxLevel) {
         addGoldExp.put("intExp", Integer.valueOf(expReward));
         if(xpBoost) {
            addGoldExp.put("bonusExp", Integer.valueOf(expReward / 2));
         }
      }

      if (curRank != 10 && calcCp > 0) {
         addGoldExp.put("iCP", Integer.valueOf(calcCp));
         if(cpBoost) {
            addGoldExp.put("bonusCP", Integer.valueOf(calcCp / 2));
         }

         user.properties.put("cp", Integer.valueOf(userCp));
      }

      int userXp;
      if (factionId > 1) {
         int je = calcRep >= 302500?302500:calcRep;
         addGoldExp.put("FactionID", Integer.valueOf(factionId));
         addGoldExp.put("iRep", Integer.valueOf(calcRep));
         if(repBoost) {
            addGoldExp.put("bonusRep", Integer.valueOf(calcRep / 2));
         }

         if(factions.containsKey(Integer.valueOf(factionId))) {
            this.world.db.jdbc.run("UPDATE users_factions SET Reputation = (Reputation + ?) WHERE UserID = ? AND FactionID = ?", new Object[]{Integer.valueOf(je), user.properties.get("dbId"), Integer.valueOf(factionId)});
            factions.put(Integer.valueOf(factionId), Integer.valueOf(((Integer)factions.get(Integer.valueOf(factionId))).intValue() + je));
         }

         if(factions.containsKey(Integer.valueOf(factionId))) {
            this.world.db.jdbc.run("UPDATE users_factions SET Reputation = (Reputation + ?) WHERE UserID = ? AND FactionID = ?", new Object[]{Integer.valueOf(je), user.properties.get("dbId"), Integer.valueOf(factionId)});
            factions.put(Integer.valueOf(factionId), Integer.valueOf(((Integer)factions.get(Integer.valueOf(factionId))).intValue() + je));
         } else {
            this.world.db.jdbc.holdConnection();
            this.world.db.jdbc.run("INSERT INTO users_factions (UserID, FactionID, Reputation) VALUES (?, ?, ?)", new Object[]{user.properties.get("dbId"), Integer.valueOf(factionId), Integer.valueOf(je)});
            factions.put(Integer.valueOf(factionId), Integer.valueOf(je));
            userXp = Long.valueOf(this.world.db.jdbc.getLastInsertId()).intValue();
            this.world.db.jdbc.releaseConnection();
            JSONObject userGold = new JSONObject();
            userGold.put("FactionID", Integer.valueOf(factionId));
            userGold.put("bitSuccess", Integer.valueOf(1));
            userGold.put("CharFactionID", Integer.valueOf(userXp));
            userGold.put("sName", this.world.factions.get(Integer.valueOf(factionId)));
            userGold.put("iRep", Integer.valueOf(calcRep));
            eqp = new JSONObject();
            eqp.put("cmd", "addFaction");
            eqp.put("faction", userGold);
            this.world.send(eqp, user);
         }
      }

      this.world.send(addGoldExp, user);
      this.world.db.jdbc.beginTransaction();

      try {
         QueryResult var36 = this.world.db.jdbc.query("SELECT Gold, Coins, Exp FROM users WHERE id = ? FOR UPDATE", user.properties.get(Users.DATABASE_ID));
         if (var36.next()) {
            userXp = var36.getInt("Exp") + expReward;
            int var37 = var36.getInt("Gold") + calcGold;
             int userCoin = var36.getInt("Coins") + calcCoin;
             var36.close();

            while (userXp >= this.world.getExpToLevel(userLevel)) {
               userXp -= this.world.getExpToLevel(userLevel);
               ++userLevel;
            }

            if (userLevel != ((Integer)user.properties.get("level")).intValue()) {
               this.levelUp(user, userLevel);
               userXp = 0;
            }

//            if (calcGold > 0 || expReward > 0 && userLevel != maxLevel) {
             if (calcGold > 0 || calcCoin > 0 || expReward > 0 && userLevel != maxLevel) {
                 this.world.db.jdbc.run("UPDATE users SET Gold = ?, Coins = ?, Exp = ? WHERE id = ?", Integer.valueOf(var37), Integer.valueOf(userCoin), Integer.valueOf(userXp), user.properties.get("dbId"));
            }

             user.properties.put(Users.GOLD, var37);
             user.properties.put(Users.COINS, userCoin);

            if (curRank != 10 && calcCp > 0) {
               eqp = (JSONObject) user.properties.get("equipment");
               if (eqp.has("ar")) {
                  int itemId = oldItem.getInt("ItemID");
                  this.world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(userCp), Integer.valueOf(itemId), user.properties.get("dbId")});
                  if (Rank.getRankFromPoints(userCp) > curRank) {
                     this.loadSkills(user, (Item)this.world.items.get(Integer.valueOf(itemId)), userCp);
                  }
               }
            }

//            else if(curRank == 9){
//                int itemID = oldItem.getInt("ItemID");
//                int getItem = this.world.db.jdbc.queryForInt("SELECT EvolveID FROM items WHERE id=?", new Object[]{itemID});
//                if(getItem > 0){
//                    this.dropItem(user, getItem);
//                }
//            }
         }

         var36.close();
      } catch (JdbcException var34) {
         if(this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in rewards transaction: " + var34.getMessage());
      } finally {
         if(this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.commitTransaction();
         }

      }

   }

   public String getMuteMessage(double seconds) {
      if(seconds <= 60.0D) {
         return String.format("You are muted! Chat privileges have been temporarily revoked. (%d second(s) remaining)", new Object[]{Long.valueOf(Math.round(seconds))});
      } else {
         double minutes = seconds / 60.0D;
         if(minutes <= 60.0D) {
            return String.format("You are muted! Chat privileges have been temporarily revoked. (%d minute(s) and %d second(s) remaining)", new Object[]{Long.valueOf(Math.round(minutes)), Long.valueOf(Math.round(seconds % 60.0D))});
         } else {
            double hours = minutes / 60.0D;
            if(hours <= 24.0D) {
               return String.format("You are muted! Chat privileges have been temporarily revoked. (%d hour(s) and %d minute(s) remaining)", new Object[]{Long.valueOf(Math.round(hours)), Long.valueOf(Math.round(hours % 60.0D))});
            } else {
               double days = hours / 24.0D;
               return String.format("You are muted! Chat privileges have been temporarily revoked. (%d day(s) and %d hour(s) remaining)", new Object[]{Long.valueOf(Math.round(days)), Long.valueOf(Math.round(days % 24.0D))});
            }
         }
      }
   }

   public String getMuteMessageBR(double seconds) {
      if(seconds <= 60.0D) {
         return String.format("Voce esta silenciado! Privilegios de bate-papo foram temporariamente revogada. (%d segundo(s) restante)", new Object[]{Long.valueOf(Math.round(seconds))});
      } else {
         double minutes = seconds / 60.0D;
         if(minutes <= 60.0D) {
            return String.format("Voce esta silenciado! Privilegios de bate-papo foram temporariamente revogada. (%d minuto(s) and %d segundo(s) restante)", new Object[]{Long.valueOf(Math.round(minutes)), Long.valueOf(Math.round(seconds % 60.0D))});
         } else {
            double hours = minutes / 60.0D;
            if(hours <= 24.0D) {
               return String.format("Voce esta silenciado! Privilegios de bate-papo foram temporariamente revogada. (%d hora(s) and %d minuto(s) restante)", new Object[]{Long.valueOf(Math.round(hours)), Long.valueOf(Math.round(hours % 60.0D))});
            } else {
               double days = hours / 24.0D;
               return String.format("Voce esta silenciado! Privilegios de bate-papo foram temporariamente revogada. (%d dia(s) and %d hora(s) restante)", new Object[]{Long.valueOf(Math.round(days)), Long.valueOf(Math.round(days % 24.0D))});
            }
         }
      }
   }

   public int getMuteTimeInDays(User user) {
      return this.mutes.containsKey(user.getName())?Long.valueOf(TimeUnit.MILLISECONDS.toDays(((Calendar)this.mutes.get(user.getName())).getTimeInMillis() - System.currentTimeMillis())).intValue():0;
   }

   public int getMuteTimeInHours(User user) {
      return this.mutes.containsKey(user.getName())?Long.valueOf(TimeUnit.MILLISECONDS.toHours(((Calendar)this.mutes.get(user.getName())).getTimeInMillis() - System.currentTimeMillis())).intValue():0;
   }

   public int getMuteTimeInMinutes(User user) {
      return this.mutes.containsKey(user.getName())?Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(((Calendar)this.mutes.get(user.getName())).getTimeInMillis() - System.currentTimeMillis())).intValue():0;
   }

   public int getMuteTimeInSeconds(User user) {
      return this.mutes.containsKey(user.getName())?Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(((Calendar)this.mutes.get(user.getName())).getTimeInMillis() - System.currentTimeMillis())).intValue():0;
   }

   public void unmute(User user) {
      if(this.mutes.containsKey(user.getName())) {
         this.mutes.remove(user.getName());
      }

   }

   public boolean hasAura(User user, int auraId) {
      Set auras = (Set)user.properties.get("auras");
      Iterator i$ = auras.iterator();

      Aura aura;
      do {
         if(!i$.hasNext()) {
            return false;
         }

         RemoveAura ra = (RemoveAura)i$.next();
         aura = ra.getAura();
      } while(aura.getId() != auraId);

      return true;
   }

   public void removeAura(User user, RemoveAura ra) {
      Set auras = (Set)user.properties.get("auras");
      auras.remove(ra);
   }

   public RemoveAura applyAura(User user, Aura aura) {
      Set auras = (Set)user.properties.get("auras");
      RemoveAura ra = new RemoveAura(this.world, aura, user);
      ra.setRunning(this.world.scheduleTask(ra, (long)aura.getDuration(), TimeUnit.SECONDS));
      auras.add(ra);
      return ra;
   }

   private void processLogin(User user) {
      if(((Integer)user.properties.get("access")).intValue() >= 60) {
         user.setAsAdmin();
         SmartFoxServer.log.fine(user.getName() + " has administrator privileges.");
      } else if(((Integer)user.properties.get("access")).intValue() >= 40) {
         user.setAsModerator();
         SmartFoxServer.log.fine(user.getName() + " has moderator privileges.");
      }
      QueryResult rs = world.db.jdbc.query("SELECT * FROM users WHERE id=?", new Object[]{user.properties.get("dbId")});
      while(rs.next()){
      user.properties.put("requestcounter", Integer.valueOf(0));
      user.properties.put("requestwarncounter", Integer.valueOf(0));
      user.properties.put("requestrepeatedcounter", Integer.valueOf(0));
      user.properties.put("requestlast", "");
      user.properties.put("requestlastmili", Long.valueOf(System.currentTimeMillis()));
      user.properties.put("stats", new Stats(user, this.world));
      user.properties.put("equipment", new JSONObject());
      user.properties.put("regenaration", new Regeneration(user, this.world));
      user.properties.put("guildobj", this.getGuildObject(((Integer)user.properties.get("guildid")).intValue()));
      user.properties.put("userrefresh", this.getUserData(((Integer)user.properties.get("dbId")).intValue(), false));
      user.properties.put("auras", Collections.newSetFromMap(new ConcurrentHashMap()));
      user.properties.put("lastmessagetime", Long.valueOf(System.currentTimeMillis()));
      user.properties.put("partyId", Integer.valueOf(-1));
      user.properties.put("quests", new HashSet());
      user.properties.put("drops", new HashMap());
      user.properties.put("factions", new HashMap());
      user.properties.put("skills", new HashMap());
      user.properties.put("tempinventory", new HashMap());
      user.properties.put("xpboost", Boolean.valueOf(false));
      user.properties.put("goldboost", Boolean.valueOf(false));
      user.properties.put("cpboost", Boolean.valueOf(false));
      user.properties.put("repboost", Boolean.valueOf(false));
      user.properties.put("requestedparty", new HashSet());
      user.properties.put("requestedfriend", new HashSet());
      user.properties.put("requestedduel", new HashSet());
      user.properties.put("requestedguild", new HashSet());
      user.properties.put("afk", Boolean.valueOf(false));
      user.properties.put("hp", Integer.valueOf(100));
      user.properties.put("gold", rs.getInt("Gold"));
      user.properties.put("hpmax", Integer.valueOf(100));
      user.properties.put("mp", Integer.valueOf(100));
      user.properties.put("mpmax", Integer.valueOf(100));
      user.properties.put("state", Integer.valueOf(1));
      user.properties.put("pvpteam", Integer.valueOf(0));
      }
   }

   public void log(User user, String violation, String details)
   {
       log(user, violation, details, 0);
//      int userId = ((Integer)user.properties.get("dbId")).intValue();
//      this.world.db.jdbc.run("INSERT INTO users_logs (UserID, Violation, Details) VALUES (?, ?, ?)", Integer.valueOf(userId), violation, details);
//      if (!user.isBeingKicked) {
//         this.world.send(new String[]{"suspicious"}, user);
//      }
   }

    public void log(User user, String violation, String details, int map) {
        int userId = ((Integer) user.properties.get(Users.DATABASE_ID)).intValue();
        this.world.db.getJdbc().run("INSERT INTO users_logs (UserID, Violation, Details, MapID) VALUES (?, ?, ?, ?)", new Object[]{Integer.valueOf(userId), violation, details, map});
        if (!user.isBeingKicked) {
            this.world.send(new String[]{"suspicious"}, user);
        }
    }

   public void changePreferences(User user, String pref, boolean value) {
      int ia1 = ((Integer)user.properties.get("settings")).intValue();
      ia1 = Settings.setPreferences(pref, ia1, value);
      user.properties.put("settings", Integer.valueOf(ia1));
      JSONObject uotls = new JSONObject();
      uotls.put("cmd", "uotls");
      uotls.put("unm", user.getName());
      if(pref.equals("bHelm")) {
         uotls.put("o", (new JSONObject()).put("showHelm", Boolean.valueOf(Settings.getPreferences("bHelm", ia1))));
         this.world.sendToRoomButOne(uotls, user, this.world.zone.getRoom(user.getRoom()));
      }

      if(pref.equals("bCloak")) {
         uotls.put("o", (new JSONObject()).put("showCloak", Boolean.valueOf(Settings.getPreferences("bCloak", ia1))));
         this.world.sendToRoomButOne(uotls, user, this.world.zone.getRoom(user.getRoom()));
      }

      this.sendPreferences(user, pref);
      this.world.db.jdbc.run("UPDATE users SET Settings = ? WHERE id = ?", new Object[]{Integer.valueOf(ia1), user.properties.get("dbId")});
   }

   private void sendPreferences(User user, String pref) {
      int ia1 = ((Integer)user.properties.get("settings")).intValue();
      boolean value = Settings.getPreferences(pref, ia1);
      if(user.properties.get("language").equals("BR")) {
         if(pref.equals("bParty") && value) {
            this.world.send(new String[]{"server", "Aceitando convites para festas."}, user);
         } else if(pref.equals("bParty") && !value) {
            this.world.send(new String[]{"warning", "Ignorando convites para festas."}, user);
         }

         if(pref.equals("bGoto") && value) {
            this.world.send(new String[]{"server", "Aceitando solicitacoes de Goto."}, user);
         } else if(pref.equals("bGoto") && !value) {
            this.world.send(new String[]{"warning", "Ignorando solicitacoes de Goto."}, user);
         }

         if(pref.equals("bFriend") && value) {
            this.world.send(new String[]{"server", "Aceitando Pedidos de amizade."}, user);
         } else if(pref.equals("bFriend") && !value) {
            this.world.send(new String[]{"warning", "Ignorando Pedidos de amizade."}, user);
         }

         if(pref.equals("bWhisper") && value) {
            this.world.send(new String[]{"server", "Aceitando PMs."}, user);
         } else if(pref.equals("bWhisper") && !value) {
            this.world.send(new String[]{"warning", "Ignorando PMs."}, user);
         }

         if(pref.equals("bTT") && value) {
            this.world.send(new String[]{"server", "Dicas de ferramentas de habilidade ira mostrar sempre no mouseover."}, user);
         } else if(pref.equals("bTT") && !value) {
            this.world.send(new String[]{"warning", "Dicas de ferramentas de habilidade nao serao exibidos em mouseover durante o combate."}, user);
         }

         if(pref.equals("bDuel") && value) {
            this.world.send(new String[]{"server", "Aceitando convida duelo"}, user);
         } else if(pref.equals("bDuel") && !value) {
            this.world.send(new String[]{"warning", "Ignorando convida duelo."}, user);
         }

         if(pref.equals("bGuild") && value) {
            this.world.send(new String[]{"server", "Aceitando alianca convida."}, user);
         } else if(pref.equals("bGuild") && !value) {
            this.world.send(new String[]{"warning", "Ignorando alianca convida."}, user);
         }
      } else {
         if(pref.equals("bParty") && value) {
            this.world.send(new String[]{"server", "Accepting party invites."}, user);
         } else if(pref.equals("bParty") && !value) {
            this.world.send(new String[]{"warning", "Ignoring party invites."}, user);
         }

         if(pref.equals("bGoto") && value) {
            this.world.send(new String[]{"server", "Accepting goto requests."}, user);
         } else if(pref.equals("bGoto") && !value) {
            this.world.send(new String[]{"warning", "Blocking goto requests."}, user);
         }

         if(pref.equals("bFriend") && value) {
            this.world.send(new String[]{"server", "Accepting Friend requests."}, user);
         } else if(pref.equals("bFriend") && !value) {
            this.world.send(new String[]{"warning", "Ignoring Friend requests."}, user);
         }

         if(pref.equals("bWhisper") && value) {
            this.world.send(new String[]{"server", "Accepting PMs."}, user);
         } else if(pref.equals("bWhisper") && !value) {
            this.world.send(new String[]{"warning", "Ignoring PMs."}, user);
         }

         if(pref.equals("bTT") && value) {
            this.world.send(new String[]{"server", "Ability ToolTips will always show on mouseover."}, user);
         } else if(pref.equals("bTT") && !value) {
            this.world.send(new String[]{"warning", "Ability ToolTips will not show on mouseover during combat."}, user);
         }

         if(pref.equals("bDuel") && value) {
            this.world.send(new String[]{"server", "Accepting duel invites."}, user);
         } else if(pref.equals("bDuel") && !value) {
            this.world.send(new String[]{"warning", "Ignoring duel invites."}, user);
         }

         if(pref.equals("bGuild") && value) {
            this.world.send(new String[]{"server", "Accepting guild invites."}, user);
         } else if(pref.equals("bGuild") && !value) {
            this.world.send(new String[]{"warning", "Ignoring guild invites."}, user);
         }
      }

   }

   public void updateClass(User user, Item item, int classPoints) {
      JSONObject updateClass = new JSONObject();
      updateClass.put("cmd", "updateClass");
      updateClass.put("iCP", Integer.valueOf(classPoints));
      updateClass.put("sClassCat", item.classObj.getCategory());
      updateClass.put("sDesc", item.classObj.getDescription());
      updateClass.put("sStats", item.classObj.getStatsDescription());
      updateClass.put("uid", Integer.valueOf(user.getUserId()));
      if(item.classObj.getManaRegenerationMethods().contains(":")) {
         JSONArray aMRM = new JSONArray();
         String[] arr$ = item.classObj.getManaRegenerationMethods().split(",");
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            aMRM.add(s + "\r");
         }

         updateClass.put("aMRM", aMRM);
      } else {
         updateClass.put("aMRM", item.classObj.getManaRegenerationMethods());
      }

      updateClass.put("sClassName", item.getName());
      this.world.send(updateClass, user);
      updateClass.clear();
      updateClass.put("cmd", "updateClass");
      updateClass.put("iCP", Integer.valueOf(classPoints));
      updateClass.put("sClassCat", item.classObj.getCategory());
      updateClass.put("sClassName", item.getName());
      updateClass.put("uid", Integer.valueOf(user.getUserId()));
      user.properties.put("cp", Integer.valueOf(classPoints));
      user.properties.put("classname", item.getName());
      user.properties.put("classcat", item.classObj.getCategory());
      user.properties.put("none", item.getElement());
      this.world.sendToRoomButOne(updateClass, user, this.world.zone.getRoom(user.getRoom()));
      this.loadSkills(user, item, classPoints);
   }

   public void regen(User user) {
      Regeneration regen = (Regeneration)user.properties.get("regenaration");
      regen.setRunning(this.world.scheduleTask(regen, 4L, TimeUnit.SECONDS, true));
   }

   private void clearAuras(User user) {
      Set auras = (Set)user.properties.get("auras");
      Iterator stats = auras.iterator();

      while(stats.hasNext()) {
         RemoveAura ca = (RemoveAura)stats.next();
         ca.cancel();
      }

      auras.clear();
      Stats stats1 = (Stats)user.properties.get("stats");
      stats1.effects.clear();
      JSONObject ca1 = new JSONObject();
      ca1.put("cmd", "clearAuras");
      this.world.send(ca1, user);
   }

   private void applyPassiveAuras(User user, int rank, Class classObj) {
      if(rank >= 4) {
         JSONObject aurap = new JSONObject();
         JSONArray auras = new JSONArray();
         Stats stats = (Stats)user.properties.get("stats");
         Iterator i$ = classObj.skills.iterator();

         while(true) {
            Aura aura;
            do {
               Skill skill;
               do {
                  do {
                     if(!i$.hasNext()) {
                        aurap.put("auras", auras);
                        aurap.put("cmd", "aura+p");
                        aurap.put("tInf", "p:" + user.getUserId());
                        this.world.send(aurap, user);
                        return;
                     }

                     int skillId = ((Integer)i$.next()).intValue();
                     skill = (Skill)this.world.skills.get(Integer.valueOf(skillId));
                  } while(!skill.getType().equals("passive"));
               } while(!skill.hasAuraId());

               aura = (Aura)this.world.auras.get(Integer.valueOf(skill.getAuraId()));
            } while(aura.effects.isEmpty());

            JSONObject auraObj = new JSONObject();
            JSONArray effects = new JSONArray();
            Iterator i$1 = aura.effects.iterator();

            while(i$1.hasNext()) {
               int effectId = ((Integer)i$1.next()).intValue();
               AuraEffects ae = (AuraEffects)this.world.effects.get(Integer.valueOf(effectId));
               JSONObject effect = new JSONObject();
               effect.put("typ", ae.getType());
               effect.put("sta", ae.getStat());
               effect.put("id", Integer.valueOf(ae.getId()));
               effect.put("val", Double.valueOf(ae.getValue()));
               effects.add(effect);
               stats.effects.add(ae);
            }

            auraObj.put("nam", aura.getName());
            auraObj.put("e", effects);
            auras.add(auraObj);
         }
      }
   }

   public JSONArray getGuildHallData(int guildId) {
      JSONArray guildData = new JSONArray();
      QueryResult halls = this.world.db.jdbc.query("SELECT * FROM guilds_halls WHERE GuildID = ?", new Object[]{Integer.valueOf(guildId)});

      while(halls.next()) {
         JSONObject hall = new JSONObject();
         hall.put("intY", Integer.valueOf(halls.getInt("Y")));
         hall.put("intX", Integer.valueOf(halls.getInt("X")));
         hall.put("strLinkage", halls.getString("Linkage"));
         hall.put("ID", Integer.valueOf(halls.getInt("id")));
         hall.put("strCell", halls.getString("Cell"));
         hall.put("strBuildings", this.getBuildingString(halls.getInt("id")));
         hall.put("strConnections", this.getConnectionsString(halls.getInt("id")));
         hall.put("strInterior", halls.getString("Interior"));
         guildData.add(hall);
      }

      halls.close();
      return guildData;
   }

   public String getConnectionsString(int hallId) {
      StringBuilder sb = new StringBuilder();
      QueryResult result = this.world.db.jdbc.query("SELECT * FROM guilds_halls_connections WHERE HallID = ?", new Object[]{Integer.valueOf(hallId)});

      while(result.next()) {
         sb.append(result.getString("Pad")).append(",");
         sb.append(result.getString("Cell")).append(",");
         sb.append(result.getString("PadPosition")).append("|");
      }

      result.close();
      if(sb.length() <= 0) {
         return sb.toString();
      } else {
         int index = sb.length() - 1;
         return sb.deleteCharAt(index).toString();
      }
   }

   public String getBuildingString(int hallId) {
      StringBuilder sb = new StringBuilder();
      QueryResult result = this.world.db.jdbc.query("SELECT * FROM guilds_halls_buildings WHERE HallID = ?", new Object[]{Integer.valueOf(hallId)});

      while(result.next()) {
         Item index = (Item)this.world.items.get(Integer.valueOf(result.getInt("ItemID")));
         sb.append("slot:").append(result.getInt("Slot")).append(",");
         sb.append("size:").append(result.getInt("Size")).append(",");
         sb.append("itemID:").append(result.getInt("ItemID")).append(",");
         sb.append("linkage:").append(index.getLink()).append(",");
         sb.append("file:").append(index.getFile()).append("|");
      }

      result.close();
      if(sb.length() <= 0) {
         return sb.toString();
      } else {
         int index1 = sb.length() - 1;
         return sb.deleteCharAt(index1).toString();
      }
   }

   public JSONObject getGuildObject(int guildId) {
      JSONObject guild = new JSONObject();
      QueryResult result = this.world.db.jdbc.query("SELECT * FROM guilds WHERE id = ?", new Object[]{Integer.valueOf(guildId)});
      if(result.next()) {
         JSONArray members = new JSONArray();
         guild.put("Name", result.getString("Name"));
         guild.put("guildColor", result.getString("GuildColor"));
         guild.put("MOTD", result.getString("MessageOfTheDay").length() > 0?result.getString("MessageOfTheDay"):"undefined");
         guild.put("pending", new JSONObject());
         guild.put("MaxMembers", Integer.valueOf(result.getInt("MaxMembers")));
         guild.put("dateUpdated", (new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss")).format(result.getDate("LastUpdated")));
         guild.put("HallSize", Integer.valueOf(result.getInt("HallSize")));
         guild.put("Wins", Integer.valueOf(result.getInt("Wins")));
         guild.put("Loses", Integer.valueOf(result.getInt("Loss")));
         guild.put("TotalKills", Integer.valueOf(result.getInt("TotalKills")));
         guild.put("Level", Integer.valueOf(result.getInt("Level")));
         guild.put("Exp", Integer.valueOf(result.getInt("Experience")));
         guild.put("ExpToLevel", Integer.valueOf(this.world.getGuildExpToLevel(result.getInt("Level"))));
         guild.put("Gold", Integer.valueOf(1));
         guild.put("Coins", Integer.valueOf(1));
         guild.put("Silvers", Integer.valueOf(1));
         guild.put("guildHall", new JSONArray());
         result.close();
         QueryResult memberResult = this.world.db.jdbc.query("SELECT id, Name, Level, CurrentServer, Rank FROM users_guilds JOIN users WHERE id = UserID AND users_guilds.GuildID = ?", new Object[]{Integer.valueOf(guildId)});

         while(memberResult.next()) {
            JSONObject member = new JSONObject();
            member.put("ID", Integer.valueOf(memberResult.getInt("id")));
            member.put("userName", memberResult.getString("Name"));
            member.put("Level", memberResult.getString("Level"));
            member.put("Rank", Integer.valueOf(memberResult.getInt("Rank")));
            member.put("Server", memberResult.getString("CurrentServer"));
            members.add(member);
         }

         memberResult.close();
         guild.put("ul", members);
      }

      result.close();
      return guild;
   }

   public void loadSkills(User user, Item item, int classPoints) {
      int rank = Rank.getRankFromPoints(classPoints);
      Map skills = (Map)user.properties.get("skills");
      Item weaponItem = (Item)user.properties.get("weaponitem");
      JSONArray active = new JSONArray();
      JSONArray passive = new JSONArray();
      JSONObject sAct = new JSONObject();
      sAct.put("cmd", "sAct");
      Iterator actions = item.classObj.skills.iterator();

      while(actions.hasNext()) {
         int skill = ((Integer)actions.next()).intValue();
         Skill actObj = (Skill)this.world.skills.get(Integer.valueOf(skill));
         JSONObject arrAuras;
         if(actObj.getType().equals("passive")) {
            arrAuras = new JSONObject();
            arrAuras.put("desc", actObj.getDescription());
            arrAuras.put("fx", actObj.getEffects());
            arrAuras.put("icon", actObj.getIcon());
            arrAuras.put("id", Integer.valueOf(skill));
            arrAuras.put("nam", actObj.getName());
            arrAuras.put("range", Integer.valueOf(actObj.getRange()));
            arrAuras.put("ref", actObj.getReference());
            arrAuras.put("tgt", actObj.getTarget());
            arrAuras.put("typ", actObj.getType());
            JSONArray arrAuras1 = new JSONArray();
            arrAuras1.add(new JSONObject());
            arrAuras.put("auras", arrAuras1);
            if(rank < 4) {
               arrAuras.put("isOK", Boolean.valueOf(false));
            } else {
               arrAuras.put("isOK", Boolean.valueOf(true));
            }

            passive.add(arrAuras);
            skills.put(actObj.getReference(), Integer.valueOf(skill));
         } else {
            arrAuras = new JSONObject();
            arrAuras.put("anim", actObj.getAnimation());
            arrAuras.put("cd", String.valueOf(actObj.getCooldown()));
            arrAuras.put("damage", Double.valueOf(actObj.getDamage()));
            arrAuras.put("desc", actObj.getDescription());
            if(!actObj.getDsrc().isEmpty()) {
               arrAuras.put("dsrc", actObj.getDsrc());
            }

            arrAuras.put("fx", actObj.getEffects());
            arrAuras.put("icon", actObj.getIcon());
            arrAuras.put("id", Integer.valueOf(skill));
            arrAuras.put("isOK", Boolean.valueOf(true));
            arrAuras.put("mp", String.valueOf(actObj.getMana()));
            arrAuras.put("nam", actObj.getName());
            arrAuras.put("range", String.valueOf(actObj.getRange()));
            arrAuras.put("ref", actObj.getReference());
            if(!actObj.getStrl().isEmpty()) {
               arrAuras.put("strl", actObj.getStrl());
            }

            arrAuras.put("tgt", actObj.getTarget());
            arrAuras.put("typ", actObj.getType());
            if(rank < 2 && actObj.getReference().equals("a2")) {
               arrAuras.put("isOK", Boolean.valueOf(false));
            }

            if(rank < 3 && actObj.getReference().equals("a3")) {
               arrAuras.put("isOK", Boolean.valueOf(false));
            }

            if(rank < 5 && actObj.getReference().equals("a4")) {
               arrAuras.put("isOK", Boolean.valueOf(false));
            }

            if(actObj.getHitTargets() > 0) {
               arrAuras.put("tgtMax", String.valueOf(actObj.getHitTargets()));
               arrAuras.put("tgtMin", "1");
            }

            if(actObj.getReference().equals("aa")) {
               arrAuras.put("auto", Boolean.valueOf(true));
               arrAuras.put("typ", "aa");
               active.element(0, arrAuras);
            } else if(actObj.getReference().equals("a1")) {
               active.element(1, arrAuras);
            } else if(actObj.getReference().equals("a2")) {
               if(rank < 2) {
                  arrAuras.put("isOK", Boolean.valueOf(false));
               }

               active.element(2, arrAuras);
            } else if(actObj.getReference().equals("a3")) {
               if(rank < 3) {
                  arrAuras.put("isOK", Boolean.valueOf(false));
               }

               active.element(3, arrAuras);
            } else if(actObj.getReference().equals("a4")) {
               if(rank < 5) {
                  arrAuras.put("isOK", Boolean.valueOf(false));
               }

               active.element(4, arrAuras);
            }

            skills.put(actObj.getReference(), Integer.valueOf(skill));
         }
      }

      JSONObject actions1;
      if(weaponItem != null && this.world.specialskills.containsKey(Integer.valueOf(weaponItem.getId()))) {
         int actions2 = ((Integer)this.world.specialskills.get(Integer.valueOf(weaponItem.getId()))).intValue();
         Skill skill1 = (Skill)this.world.skills.get(Integer.valueOf(actions2));
         JSONObject actObj1;
         if(skill1.getType().equals("passive")) {
            actObj1 = new JSONObject();
            actObj1.put("desc", skill1.getDescription());
            actObj1.put("fx", skill1.getEffects());
            actObj1.put("icon", skill1.getIcon());
            actObj1.put("id", Integer.valueOf(actions2));
            actObj1.put("nam", skill1.getName());
            actObj1.put("range", Integer.valueOf(skill1.getRange()));
            actObj1.put("ref", skill1.getReference());
            actObj1.put("tgt", skill1.getTarget());
            actObj1.put("typ", skill1.getType());
            JSONArray arrAuras2 = new JSONArray();
            arrAuras2.add(new JSONObject());
            actObj1.put("auras", arrAuras2);
            if(rank < 4) {
               actObj1.put("isOK", Boolean.valueOf(false));
            } else {
               actObj1.put("isOK", Boolean.valueOf(true));
            }

            passive.add(actObj1);
            skills.put(skill1.getReference(), Integer.valueOf(actions2));
         } else {
            actObj1 = new JSONObject();
            actObj1.put("anim", skill1.getAnimation());
            actObj1.put("cd", String.valueOf(skill1.getCooldown()));
            actObj1.put("damage", Double.valueOf(skill1.getDamage()));
            actObj1.put("desc", skill1.getDescription());
            if(!skill1.getDsrc().isEmpty()) {
               actObj1.put("dsrc", skill1.getDsrc());
            }

            actObj1.put("fx", skill1.getEffects());
            actObj1.put("icon", skill1.getIcon());
            actObj1.put("id", Integer.valueOf(actions2));
            actObj1.put("isOK", Boolean.valueOf(true));
            actObj1.put("mp", String.valueOf(skill1.getMana()));
            actObj1.put("nam", skill1.getName());
            actObj1.put("range", String.valueOf(skill1.getRange()));
            actObj1.put("ref", skill1.getReference());
            if(!skill1.getStrl().isEmpty()) {
               actObj1.put("strl", skill1.getStrl());
            }

            actObj1.put("tgt", skill1.getTarget());
            actObj1.put("typ", skill1.getType());
            if(skill1.getHitTargets() > 0) {
               actObj1.put("tgtMax", String.valueOf(skill1.getHitTargets()));
            }

            actObj1.put("tgtMin", "1");
            active.element(5, actObj1);
            skills.put(skill1.getReference(), Integer.valueOf(actions2));
         }

         this.world.send(new String[]{"server", "Special skill activated"}, user);
      } else {
         actions1 = new JSONObject();
         actions1.put("anim", "Cheer");
         actions1.put("cd", "60000");
         actions1.put("desc", "Equip a potion or scroll from your inventory to use it here.");
         actions1.put("fx", "");
         actions1.put("icon", "icu1");
         actions1.put("isOK", Boolean.valueOf(true));
         actions1.put("mp", "0");
         actions1.put("nam", "Potions");
         actions1.put("range", Integer.valueOf(808));
         actions1.put("ref", "i1");
         actions1.put("str1", "");
         actions1.put("tgt", "f");
         actions1.put("typ", "i");
         active.element(5, actions1);
      }

      actions1 = new JSONObject();
      actions1.put("active", active);
      actions1.put("passive", passive);
      sAct.put("actions", actions1);
      this.clearAuras(user);
      this.applyPassiveAuras(user, rank, item.classObj);
      this.world.send(sAct, user);
   }

   public JSONObject getProperties(User user, Room room) {
      JSONObject userprop = new JSONObject();
      userprop.put("afk", (Boolean)user.properties.get("afk"));
      userprop.put("entID", Integer.valueOf(user.getUserId()));
      userprop.put("entType", "p");
      userprop.put("intHP", (Integer)user.properties.get("hp"));
      userprop.put("intHPMax", (Integer)user.properties.get("hpmax"));
      userprop.put("intLevel", (Integer)user.properties.get("level"));
      userprop.put("intMP", (Integer)user.properties.get("mp"));
      userprop.put("intMPMax", (Integer)user.properties.get("mpmax"));
      userprop.put("intState", (Integer)user.properties.get("state"));
      userprop.put("showCloak", Boolean.valueOf(true));
      userprop.put("showHelm", Boolean.valueOf(true));
      userprop.put("strFrame", user.properties.get("frame"));
      userprop.put("strPad", user.properties.get("pad"));
      userprop.put("strUsername", user.properties.get("username"));
      userprop.put("strElement", user.properties.get("none"));
      userprop.put("tx", (Integer)user.properties.get("tx"));
      userprop.put("ty", (Integer)user.properties.get("ty"));
      userprop.put("uoName", user.getName());
      if(!room.getName().contains("house") && ((Area)this.world.areas.get(room.getName().split("-")[0])).isPvP()) {
         userprop.put("pvpTeam", (Integer)user.properties.get("pvpteam"));
      }

      return userprop;
   }

   public void updateStats(User user, Enhancement enhancement, String equipment)
/* 1128:     */   {
/* 1129:1074 */     Map<String, Double> itemStats = this.world.getItemStats(enhancement, equipment);
/* 1130:1075 */     Stats stats = (Stats)user.properties.get("stats");
/* 1131:1077 */     if (equipment.equals("ar")) {
/* 1132:1078 */       for (Map.Entry<String, Double> entry : itemStats.entrySet()) {
/* 1133:1079 */         stats.armor.put(entry.getKey(), entry.getValue());
/* 1134:     */       }
/* 1135:1080 */     } else if (equipment.equals("Weapon")) {
/* 1136:1081 */       for (Map.Entry<String, Double> entry : itemStats.entrySet()) {
/* 1137:1082 */         stats.weapon.put(entry.getKey(), entry.getValue());
/* 1138:     */       }
/* 1139:1083 */     } else if (equipment.equals("ba")) {
/* 1140:1084 */       for (Map.Entry<String, Double> entry : itemStats.entrySet()) {
/* 1141:1085 */         stats.cape.put(entry.getKey(), entry.getValue());
/* 1142:     */       }
/* 1143:1086 */     } else if (equipment.equals("he")) {
/* 1144:1087 */       for (Map.Entry<String, Double> entry : itemStats.entrySet()) {
/* 1145:1088 */         stats.helm.put(entry.getKey(), entry.getValue());
/* 1146:     */       }
/* 1147:     */     } else {
/* 1148:1090 */       throw new IllegalArgumentException("equipment " + equipment + " cannot have stat values!");
/* 1149:     */     }
/* 1150:     */   }

   public void sendStats(User user) {
      this.sendStats(user, false);
   }

   public void sendStats(User user, boolean levelUp) {
      JSONObject stu = new JSONObject();
      JSONObject tempStat = new JSONObject();
      int userLevel = ((Integer)user.properties.get("level")).intValue();
      Stats stats = (Stats)user.properties.get("stats");
      stats.update();
      int END = (int)((double)stats.get$END() + stats.get_END());
      int WIS = (int)((double)stats.get$WIS() + stats.get_WIS());
      int intHPperEND = ((Double)this.world.coreValues.get("intHPperEND")).intValue();
      int intMPperWIS = ((Double)this.world.coreValues.get("intMPperWIS")).intValue();
      int addedHP = END * intHPperEND;
      int userHp = this.world.getHealthByLevel(userLevel);
      userHp += addedHP;
      int userMp = this.world.getManaByLevel(userLevel) + WIS * intMPperWIS;
      user.properties.put("hpmax", Integer.valueOf(userHp));
      user.properties.put("mpmax", Integer.valueOf(userMp));
      if(((Integer)user.properties.get("state")).intValue() == 1 || levelUp) {
         user.properties.put("hp", Integer.valueOf(userHp));
      }

      if(((Integer)user.properties.get("state")).intValue() == 1 || levelUp) {
         user.properties.put("mp", Integer.valueOf(userMp));
      }

      this.world.users.sendUotls(user, true, true, true, true, levelUp, false);
      JsonConfig config = new JsonConfig();
      config.setExcludes(new String[]{"maxDmg", "minDmg"});
      JSONObject stat = JSONObject.fromObject(stats, config);
      JSONObject ba = new JSONObject();
      JSONObject he = new JSONObject();
      JSONObject Weapon = new JSONObject();
      JSONObject innate = new JSONObject();
      JSONObject ar = new JSONObject();
      innate.put("INT", stats.innate.get("INT"));
      innate.put("STR", stats.innate.get("STR"));
      innate.put("DEX", stats.innate.get("DEX"));
      innate.put("END", stats.innate.get("END"));
      innate.put("LCK", stats.innate.get("LCK"));
      innate.put("WIS", stats.innate.get("WIS"));
      Iterator i$ = stats.armor.entrySet().iterator();

      Entry entry;
      while(i$.hasNext()) {
         entry = (Entry)i$.next();
         if(((Double)entry.getValue()).doubleValue() > 0.0D) {
            ar.put(entry.getKey(), Integer.valueOf(((Double)entry.getValue()).intValue()));
         }
      }

      i$ = stats.helm.entrySet().iterator();

      while(i$.hasNext()) {
         entry = (Entry)i$.next();
         if(((Double)entry.getValue()).doubleValue() > 0.0D) {
            he.put(entry.getKey(), Integer.valueOf(((Double)entry.getValue()).intValue()));
         }
      }

      i$ = stats.weapon.entrySet().iterator();

      while(i$.hasNext()) {
         entry = (Entry)i$.next();
         if(((Double)entry.getValue()).doubleValue() > 0.0D) {
            Weapon.put(entry.getKey(), Integer.valueOf(((Double)entry.getValue()).intValue()));
         }
      }

      i$ = stats.cape.entrySet().iterator();

      while(i$.hasNext()) {
         entry = (Entry)i$.next();
         if(((Double)entry.getValue()).doubleValue() > 0.0D) {
            ba.put(entry.getKey(), Integer.valueOf(((Double)entry.getValue()).intValue()));
         }
      }

      if(!ba.isEmpty()) {
         tempStat.put("ba", ba);
      }

      if(!ar.isEmpty()) {
         tempStat.put("ar", ar);
      }

      if(!Weapon.isEmpty()) {
         tempStat.put("Weapon", Weapon);
      }

      if(!he.isEmpty()) {
         tempStat.put("he", he);
      }

      tempStat.put("innate", innate);
      stu.put("tempSta", tempStat);
      stu.put("cmd", "stu");
      stu.put("sta", stat);
      stu.put("wDPS", Integer.valueOf(stats.wDPS));
      this.world.send(stu, user);
   }

   public JSONArray getFriends(User user) {
      JSONArray friends = new JSONArray();
      QueryResult result = this.world.db.jdbc.query("SELECT id, Level, Name, CurrentServer FROM users LEFT JOIN users_friends ON FriendID = id WHERE UserID = ?", new Object[]{user.properties.get("dbId")});

      while(result.next()) {
         JSONObject temp = new JSONObject();
         temp.put("iLvl", Integer.valueOf(result.getInt("Level")));
         temp.put("ID", Integer.valueOf(result.getInt("id")));
         temp.put("sName", result.getString("Name"));
         temp.put("sServer", result.getString("CurrentServer"));
         friends.add(temp);
      }

      result.close();
      return friends;
   }

   public void dropItem(User user, int itemId) {
      this.dropItem(user, itemId, 1);
   }

   public void dropItem(User user, int itemId, int quantity)
   {
      Item itemObj = (Item) this.world.items.get(Integer.valueOf(itemId));
      if (!itemObj.getReqQuests().isEmpty()) {
         Boolean di = Boolean.valueOf(false);
         String[] tempInventory;
         if (itemObj.getReqQuests().contains(",")) {
            tempInventory = itemObj.getReqQuests().split(",");
         } else {
            tempInventory = new String[]{itemObj.getReqQuests()};
         }

         Set arrItems = (Set) user.properties.get("quests");
         String[] item = tempInventory;
         int userDrops = tempInventory.length;

         for (int quantities = 0; quantities < userDrops; ++quantities) {
            String questId = item[quantities];
            if (arrItems.contains(Integer.valueOf(Integer.parseInt(questId)))) {
               di = Boolean.valueOf(true);
               break;
            }
         }

         if (!di.booleanValue()) {
            return;
         }
      }

      Map var12 = (Map)user.properties.get("tempinventory");
      if (!itemObj.isTemporary()) {
         QueryResult var13 = this.world.db.jdbc.query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
         if (var13.next()) {
            int var15 = var13.getInt("Quantity");
            var13.close();
            if (var15 >= itemObj.getStack()) {
               return;
            }
         }
         var13.close();
      } else if (var12.containsKey(Integer.valueOf(itemId)) && ((Integer)var12.get(Integer.valueOf(itemId))).intValue() >= itemObj.getStack()) {
         return;
      }

      JSONObject var14 = new JSONObject();
      JSONObject var16 = new JSONObject();
      JSONObject var17;
      if (itemObj.getType().equals("Enhancement")) {
         var17 = Item.getItemJSON(itemObj, (Enhancement) this.world.enhancements.get(Integer.valueOf(itemObj.getEnhId())));
      } else {
         var17 = Item.getItemJSON(itemObj);
      }

      var17.put("iQty", Integer.valueOf(quantity));
      var16.put(String.valueOf(itemId), var17);
      var14.put("items", var16);
      var14.put("cmd", itemObj.isTemporary() ? "addItems" : "dropItem");
      if (itemObj.isTemporary()) {
         if (var12.containsKey(Integer.valueOf(itemId))) {
            if (((Integer)var12.get(Integer.valueOf(itemId))).intValue() < itemObj.getStack()) {
               this.addTemporaryItem(user, itemId, quantity);
            }
         } else {
            this.addTemporaryItem(user, itemId, quantity);
         }
      } else {
          Map userDrops = (Map) user.properties.get(Users.DROPS);
          int totalDropQty = quantity;
          if (userDrops.containsKey(itemId)) {
              if (itemObj.getStack() == 1)
                  return;
              totalDropQty += (Integer) userDrops.get(itemId);
          }
          userDrops.put(itemId, totalDropQty);

//         Map var18 = (Map)user.properties.get("drops");
//         if(var18.containsKey(Integer.valueOf(itemId))) {
//            Queue var19 = (Queue)var18.get(Integer.valueOf(itemId));
//            var19.add(Integer.valueOf(quantity));
//         } else {
//            LinkedBlockingQueue var20 = new LinkedBlockingQueue();
//            var20.add(Integer.valueOf(quantity));
//            var18.put(Integer.valueOf(itemId), var20);
//         }
      }

      this.world.send(var14, user);
   }

//   public void setQuestValue(User user, int index, int value) {
//      if(index > 99) {
//         user.properties.put("quests2", Quests.updateValue((String)user.properties.get("quests2"), index - 100, value));
//         this.world.db.jdbc.run("UPDATE users SET Quests2 = ? WHERE id =  ?", new Object[]{user.properties.get("quests2"), user.properties.get("dbId")});
//      } else {
//         user.properties.put("quests1", Quests.updateValue((String)user.properties.get("quests1"), index, value));
//         this.world.db.jdbc.run("UPDATE users SET Quests = ? WHERE id = ?", new Object[]{user.properties.get("quests1"), user.properties.get("dbId")});
//      }
//
//      JSONObject updateQuest = new JSONObject();
//      updateQuest.put("cmd", "updateQuest");
//      updateQuest.put("iIndex", Integer.valueOf(index));
//      updateQuest.put("iValue", Integer.valueOf(value));
//      this.world.send(updateQuest, user);
//   }

    public void setQuestValue(User user, int index, int value)
    {
        if (index > 99) {
            user.properties.put(Users.QUESTS_2, Quests.updateValue((String) user.properties.get(Users.QUESTS_2), index - 100, value));
            this.world.db.getJdbc().run("UPDATE users SET Quests2 = ? WHERE id =  ?", new Object[]{user.properties.get(Users.QUESTS_2), user.properties.get(Users.DATABASE_ID)});
        } else {
            user.properties.put(Users.QUESTS_1, Quests.updateValue((String) user.properties.get(Users.QUESTS_1), index, value));
            this.world.db.getJdbc().run("UPDATE users SET Quests = ? WHERE id = ?", new Object[]{user.properties.get(Users.QUESTS_1), user.properties.get(Users.DATABASE_ID)});
        }

        JSONObject updateQuest = new JSONObject();
        updateQuest.put("cmd", "updateQuest");
        updateQuest.put("iIndex", Integer.valueOf(index));
        updateQuest.put("iValue", Integer.valueOf(value));
        this.world.send(updateQuest, user);
    }

//   public int getQuestValue(User user, int index) {
//      return index > 99?Quests.lookAtValue((String)user.properties.get("quests2"), index - 100):Quests.lookAtValue((String)user.properties.get("quests1"), index);
//   }

    public int getQuestValue(User user, int index) {
        if (index > 99) {
            return Quests.lookAtValue((String) user.properties.get(Users.QUESTS_2), index - 100);
        } else {
            return Quests.lookAtValue((String) user.properties.get(Users.QUESTS_1), index);
        }
    }

   public void setAchievement(String field, int index, int value, User user) {
      if(field.equals("ia0")) {
         user.properties.put("ia0", Integer.valueOf(Achievement.update(((Integer)user.properties.get("ia0")).intValue(), index, value)));
         this.world.db.jdbc.run("UPDATE users SET Achievement = ? WHERE id = ?", new Object[]{user.properties.get("ia0"), user.properties.get("dbId")});
      } else if(field.equals("id0")) {
         user.properties.put("dailyquests0", Integer.valueOf(Achievement.update(((Integer)user.properties.get("dailyquests0")).intValue(), index, value)));
         this.world.db.jdbc.run("UPDATE users SET DailyQuests0 = ? WHERE id = ?", new Object[]{user.properties.get("dailyquests0"), user.properties.get("dbId")});
      } else if(field.equals("id1")) {
         user.properties.put("dailyquests1", Integer.valueOf(Achievement.update(((Integer)user.properties.get("dailyquests1")).intValue(), index, value)));
         this.world.db.jdbc.run("UPDATE users SET DailyQuests1 = ? WHERE id = ?", new Object[]{user.properties.get("dailyquests1"), user.properties.get("dbId")});
      } else if(field.equals("id2")) {
         user.properties.put("dailyquests2", Integer.valueOf(Achievement.update(((Integer)user.properties.get("dailyquests2")).intValue(), index, value)));
         this.world.db.jdbc.run("UPDATE users SET DailyQuests2 = ? WHERE id = ?", new Object[]{user.properties.get("dailyquests2"), user.properties.get("dbId")});
      } else if(field.equals("im0")) {
         user.properties.put("monthlyquests0", Integer.valueOf(Achievement.update(((Integer)user.properties.get("monthlyquests0")).intValue(), index, value)));
         this.world.db.jdbc.run("UPDATE users SET MonthlyQuests0 = ? WHERE id = ?", new Object[]{user.properties.get("monthlyquests0"), user.properties.get("dbId")});
      }

      JSONObject sa = new JSONObject();
      sa.put("cmd", "setAchievement");
      sa.put("field", field);
      sa.put("index", Integer.valueOf(index));
      sa.put("value", Integer.valueOf(value));
      this.world.send(sa, user);
   }

   public int getAchievement(String field, int index, User user) {
      return field.equals("ia0") ? Achievement.get(((Integer)user.properties.get("ia0")).intValue(), index):(field.equals("id0")?Achievement.get(((Integer)user.properties.get("dailyquests0")).intValue(), index):(field.equals("id1")?Achievement.get(((Integer)user.properties.get("dailyquests1")).intValue(), index):(field.equals("id2")?Achievement.get(((Integer)user.properties.get("dailyquests2")).intValue(), index):(field.equals("im0")?Achievement.get(((Integer)user.properties.get("monthlyquests0")).intValue(), index):-1))));
   }

   public String getGuildRank(int rank) {
      String rankName = "";
      switch(rank) {
      case 0:
         rankName = "duffer";
         break;
      case 1:
         rankName = "member";
         break;
      case 2:
         rankName = "officer";
         break;
      case 3:
         rankName = "leader";
      }

      return rankName;
   }

   public String getCustomGuildRank(Integer rank, Integer guildId) {
      String rankName = "";
      QueryResult result = this.world.db.jdbc.query("SELECT * FROM guilds WHERE id = ?", new Object[]{guildId});
      if(result.next()) {
         String Rank0 = this.world.db.jdbc.queryForString("SELECT Rank0Name FROM guilds_customranks WHERE id = ?", new Object[]{guildId});
         String Rank1 = this.world.db.jdbc.queryForString("SELECT Rank1Name FROM guilds_customranks WHERE id = ?", new Object[]{guildId});
         String Rank2 = this.world.db.jdbc.queryForString("SELECT Rank2Name FROM guilds_customranks WHERE id = ?", new Object[]{guildId});
         String Rank3 = this.world.db.jdbc.queryForString("SELECT Rank3Name FROM guilds_customranks WHERE id = ?", new Object[]{guildId});
         switch(rank.intValue()) {
         case 0:
            rankName = Rank0;
            break;
         case 1:
            rankName = Rank1;
            break;
         case 2:
            rankName = Rank2;
            break;
         case 3:
            rankName = Rank3;
         }
      }

      result.close();
      return rankName;
   }

   public boolean turnInItem(User user, int itemId, int quantity) {
      HashMap items = new HashMap();
      items.put(Integer.valueOf(itemId), Integer.valueOf(quantity));
      return this.turnInItems(user, items);
   }

   public boolean turnInItems(User user, Map<Integer, Integer> items)
   {
      boolean valid = true;
      StringBuilder sItems = new StringBuilder();
      this.world.db.jdbc.beginTransaction();

      try {
         Iterator ti = items.entrySet().iterator();

         while(ti.hasNext()) {
            Entry entry = (Entry) ti.next();
            int itemId = ((Integer) entry.getKey()).intValue();
            int quantityRequirement = ((Integer) entry.getValue()).intValue();
            Item item = (Item) this.world.items.get(Integer.valueOf(itemId));

            if (item.isTemporary()) {
//               Map itemResult = (Map) user.properties.get("tempinventory");
               Map itemResult = (Map) user.properties.get(Users.TEMPORARY_INVENTORY);

                if (!itemResult.containsKey(Integer.valueOf(itemId))) {
                  valid = false;
                  this.log(user, "Suspicous TurnIn", "Turning in a temporary item not found in temp. inventory.");
                  this.world.db.jdbc.rollbackTransaction();
                  break;
               }
               if (((Integer)itemResult.get(Integer.valueOf(itemId))).intValue() < quantityRequirement) {
                  valid = false;
                  this.log(user, "Suspicous TurnIn", "Quantity requirement for turning in item is lacking.");
                  this.world.db.jdbc.rollbackTransaction();
                  break;
               }
               itemResult.remove(Integer.valueOf(itemId));
               valid = true;
            } else {
                QueryResult itemResult1 = this.world.db.jdbc.query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
                if (!itemResult1.next()) {
                  valid = false;
                  itemResult1.close();
                  this.world.users.log(user, "Suspicous TurnIn", "Item to turn in not found in database.");
                  this.world.db.jdbc.rollbackTransaction();
                  break;
               }
               int quantity = itemResult1.getInt("Quantity");
               itemResult1.close();
               if (item.getStack() > 1) {
                  int quantityLeft = quantity - quantityRequirement;
                  if (quantityLeft > 0) {
                    this.world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(quantityLeft), Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
                  } else {
                    this.world.db.jdbc.run("DELETE FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
                  }
               } else {
                  this.world.db.jdbc.run("DELETE FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
               }
               valid = true;
               itemResult1.close();
            }
            sItems.append(itemId);
            sItems.append(":");
            sItems.append(quantityRequirement);
            sItems.append(",");
         }
      } catch (JdbcException var16) {
         if (this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.rollbackTransaction();
         }
         SmartFoxServer.log.severe("Error in turn in transaction: " + var16.getMessage());
      } finally {
         if (this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.commitTransaction();
         }
      }
      if (valid && !items.isEmpty()) {
         JSONObject ti1 = new JSONObject();
         ti1.put("cmd", "turnIn");
         ti1.put("sItems", sItems.toString().substring(0, sItems.toString().length() - 1));
         this.world.send(ti1, user);
      }
      return valid;
   }

   public void addTemporaryItem(User user, int itemId, int quantity) {
      Map tempInventory = (Map)user.properties.get("tempinventory");
      if(tempInventory.containsKey(Integer.valueOf(itemId))) {
         int deltaQuantity = ((Integer)tempInventory.get(Integer.valueOf(itemId))).intValue() + quantity;
         tempInventory.put(Integer.valueOf(itemId), Integer.valueOf(deltaQuantity));
      } else {
         tempInventory.put(Integer.valueOf(itemId), Integer.valueOf(quantity));
      }

   }

   public void lost(User user) {
      this.world.warzoneQueue.removeUserFromQueues(Integer.valueOf(user.getUserId()));
      int partyId = ((Integer)user.properties.get("partyId")).intValue();
      JSONObject updateFriend;
      JSONObject friendInfo;
      if(partyId > 0) {
         PartyInfo guildId = this.world.parties.getPartyInfo(partyId);
         if(guildId.getOwner().equals(user.properties.get("username"))) {
            guildId.setOwner(guildId.getNextOwner());
         }

         guildId.removeMember(user);
         updateFriend = new JSONObject();
         updateFriend.put("cmd", "pr");
         updateFriend.put("owner", guildId.getOwner());
         updateFriend.put("typ", "l");
         updateFriend.put("unm", user.properties.get("username"));
         this.world.send(updateFriend, guildId.getChannelListButOne(user));
         this.world.send(updateFriend, user);
         if(guildId.getMemberCount() <= 0) {
            friendInfo = new JSONObject();
            friendInfo.put("cmd", "pc");
            this.world.send(friendInfo, guildId.getOwnerObject());
            this.world.parties.removeParty(partyId);
            guildId.getOwnerObject().properties.put("partyId", Integer.valueOf(-1));
         }
      }

      this.world.db.jdbc.run("UPDATE users SET LastArea = ?, CurrentServer = \'Offline\' WHERE id = ?", new Object[]{user.properties.get("lastarea"), user.properties.get("dbId")});
      int guildId1 = ((Integer)user.properties.get("guildid")).intValue();
      if(guildId1 > 0) {
         this.world.sendGuildUpdate(this.getGuildObject(guildId1));
      }

      updateFriend = new JSONObject();
      friendInfo = new JSONObject();
      updateFriend.put("cmd", "updateFriend");
      friendInfo.put("iLvl", (Integer)user.properties.get("level"));
      friendInfo.put("ID", user.properties.get("dbId"));
      friendInfo.put("sName", user.properties.get("username"));
      friendInfo.put("sServer", "Offline");
      updateFriend.put("friend", friendInfo);
      QueryResult result = this.world.db.jdbc.query("SELECT Name FROM users LEFT JOIN users_friends ON FriendID = id WHERE UserID = ?", new Object[]{user.properties.get("dbId")});

      while(result.next()) {
         User client = this.world.zone.getUserByName(result.getString("Name").toLowerCase());
         if(client != null) {
            this.world.send(updateFriend, client);
            this.world.send(new String[]{"server", user.getName() + " has logged out."}, client);
         }
      }

      result.close();
   }

   public JSONObject getUserData(int id, boolean self) {
      JSONObject userData = new JSONObject();
      User user = this.helper.getUserById(id);
      if(user != null) {
         int hairId = ((Integer)user.properties.get("hairId")).intValue();
         Hair hair = (Hair)this.world.hairs.get(Integer.valueOf(hairId));
         String lastArea = (String)user.properties.get("lastarea");
         lastArea = lastArea.split("\\|")[0];
         userData.put("eqp", user.properties.get("equipment"));
         userData.put("iCP", (Integer)user.properties.get("cp"));
         userData.put("iUpgDays", (Integer)user.properties.get("upgdays"));
         userData.put("intAccessLevel", (Integer)user.properties.get("access"));
         userData.put("intColorAccessory", (Integer)user.properties.get("coloraccessory"));
         userData.put("intColorBase", (Integer)user.properties.get("colorbase"));
         userData.put("intColorEye", (Integer)user.properties.get("coloreye"));
         userData.put("intColorHair", (Integer)user.properties.get("colorhair"));
         userData.put("intColorSkin", (Integer)user.properties.get("colorskin"));
         userData.put("intColorTrim", (Integer)user.properties.get("colortrim"));
         userData.put("intLevel", (Integer)user.properties.get("level"));
         userData.put("intRebirth", (Integer)user.properties.get("rebirth"));
         userData.put("strElement", user.properties.get("none"));
         userData.put("strClassName", user.properties.get("classname"));
         userData.put("strGender", user.properties.get("gender"));
         userData.put("strHairFilename", hair.getFile());
         userData.put("strHairName", hair.getName());
         userData.put("strUsername", user.properties.get("username"));
         userData.put("strLanguage", user.properties.get("language"));
         if(((Integer)user.properties.get("guildid")).intValue() > 0) {
            JSONObject result = (JSONObject)user.properties.get("guildobj");
            JSONObject guild = new JSONObject();
            guild.put("id", user.properties.get("guildid"));
            guild.put("Name", result.get("Name"));
            guild.put("guildColor", result.get("guildColor"));
            guild.put("MOTD", result.get("MOTD"));
            userData.put("guild", guild);
            userData.put("guildRank", user.properties.get("guildrank"));
         }

         if(self) {
            QueryResult result1 = this.world.db.jdbc.query("SELECT HouseInfo, ActivationFlag, Gold, Coins, Exp, Country, Email, DateCreated, UpgradeExpire, Age, Upgraded FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
            if(result1.next()) {
               userData.put("CharID", (Integer)user.properties.get("dbId"));
               userData.put("HairID", Integer.valueOf(hairId));
               userData.put("UserID", Integer.valueOf(user.getUserId()));
               userData.put("bPermaMute", user.properties.get("permamute"));
               userData.put("bitSuccess", "1");
               userData.put("dCreated", (new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss")).format(result1.getDate("DateCreated")));
               userData.put("dUpgExp", (new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss")).format(result1.getDate("UpgradeExpire")));
               userData.put("iAge", result1.getString("Age"));
               userData.put("iBagSlots", (Integer)user.properties.get("bagslots"));
               userData.put("iBankSlots", (Integer)user.properties.get("bankslots"));
               userData.put("iBoostCP", Integer.valueOf(0));
               userData.put("iBoostG", Integer.valueOf(0));
               userData.put("iBoostRep", Integer.valueOf(0));
               userData.put("iBoostXP", Integer.valueOf(0));
               userData.put("iDBCP", (Integer)user.properties.get("cp"));
               userData.put("iDEX", Integer.valueOf(0));
               userData.put("iDailyAdCap", Integer.valueOf(6));
               userData.put("iDailyAds", Integer.valueOf(0));
               userData.put("iEND", Integer.valueOf(0));
               userData.put("iFounder", Integer.valueOf(0));
               userData.put("iHouseSlots", (Integer)user.properties.get("houseslots"));
               userData.put("iINT", Integer.valueOf(0));
               userData.put("iLCK", Integer.valueOf(0));
               userData.put("iSTR", Integer.valueOf(0));
               userData.put("iUpg", Integer.valueOf(result1.getInt("Upgraded")));
               userData.put("iWIS", Integer.valueOf(0));
               userData.put("ia0", (Integer)user.properties.get("ia0"));
               userData.put("ia1", (Integer)user.properties.get("settings"));
               userData.put("id0", (Integer)user.properties.get("dailyquests0"));
               userData.put("id1", (Integer)user.properties.get("dailyquests1"));
               userData.put("id2", (Integer)user.properties.get("dailyquests2"));
               userData.put("im0", (Integer)user.properties.get("monthlyquests0"));
               userData.put("intActivationFlag", Integer.valueOf(result1.getInt("ActivationFlag")));
               userData.put("intCoins", Integer.valueOf(result1.getInt("Coins")));
               userData.put("intDBExp", Integer.valueOf(result1.getInt("Exp")));
               userData.put("intDBGold", Integer.valueOf(result1.getInt("Gold")));
               userData.put("intExp", Integer.valueOf(result1.getInt("Exp")));
               userData.put("intExpToLevel", Integer.valueOf(this.world.getExpToLevel(((Integer)user.properties.get("level")).intValue())));
               userData.put("intGold", (Integer)user.properties.get("gold"));
               userData.put("intHP", (Integer)user.properties.get("hp"));
               userData.put("intHPMax", (Integer)user.properties.get("hpmax"));
               userData.put("intHits", Integer.valueOf(1267));
               userData.put("intMP", (Integer)user.properties.get("mp"));
               userData.put("intMPMax", (Integer)user.properties.get("mpmax"));
               userData.put("ip0", Integer.valueOf(0));
               userData.put("ip1", Integer.valueOf(0));
               userData.put("ip2", Integer.valueOf(0));
               userData.put("iq0", Integer.valueOf(0));
               userData.put("lastArea", lastArea);
               userData.put("sCountry", result1.getString("Country"));
               userData.put("sHouseInfo", result1.getString("HouseInfo"));
               userData.put("strEmail", result1.getString("Email"));
               userData.put("strMapName", this.zone.getRoom(user.getRoom()).getName().split("-")[0]);
               userData.put("strQuests", user.properties.get("quests1"));
               userData.put("strQuests2", user.properties.get("quests2"));
            }

            result1.close();
         }
      }

      return userData;
   }

   public void respawn(User user) {
      user.properties.put("hp", user.properties.get("hpmax"));
      user.properties.put("mp", user.properties.get("mpmax"));
      user.properties.put("state", Integer.valueOf(1));
      this.clearAuras(user);
      this.sendUotls(user, true, false, true, false, false, true);
   }

   public void kick(User user) {
      user.isBeingKicked = true;
      this.world.send(new String[]{"logoutWarning", "", "65"}, user);
      this.world.scheduleTask(new KickUser(user, this.world), 0L, TimeUnit.SECONDS);
   }

   public void die(User user) {
      user.properties.put("hp", Integer.valueOf(0));
      user.properties.put("mp", Integer.valueOf(0));
      user.properties.put("state", Integer.valueOf(0));
      user.properties.put("respawntime", Long.valueOf(System.currentTimeMillis()));
   }

   public void addOfferItem(User user, int itemId, int quantity, int enhId) {
      Map offers = (Map)user.properties.get("offer");
      Map enhances = (Map)user.properties.get("offerenh");
      if(offers.containsKey(Integer.valueOf(itemId))) {
         int deltaQuantity = ((Integer)offers.get(Integer.valueOf(itemId))).intValue() + quantity;
         offers.put(Integer.valueOf(itemId), Integer.valueOf(deltaQuantity));
      } else {
         offers.put(Integer.valueOf(itemId), Integer.valueOf(quantity));
      }

      enhances.put(Integer.valueOf(itemId), Integer.valueOf(enhId));
   }

   public void removeOfferItem(User user, int itemId, int quantity) {
      Map tempInventory = (Map)user.properties.get("offer");
      Map enhances = (Map)user.properties.get("offerenh");
      if(tempInventory.containsKey(Integer.valueOf(itemId))) {
         int deltaQuantity = ((Integer)tempInventory.get(Integer.valueOf(itemId))).intValue() - quantity;
         if(deltaQuantity < 1) {
            tempInventory.remove(Integer.valueOf(itemId));
            enhances.remove(Integer.valueOf(itemId));
         } else {
            tempInventory.put(Integer.valueOf(itemId), Integer.valueOf(deltaQuantity));
         }
      }

   }
}
