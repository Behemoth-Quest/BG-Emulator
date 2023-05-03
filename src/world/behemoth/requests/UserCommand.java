package world.behemoth.requests;

import world.behemoth.Interface;
import world.behemoth.aqw.Settings;
import world.behemoth.db.objects.Area;
import world.behemoth.db.objects.Hall;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.tasks.Restart;
import world.behemoth.tasks.Shutdown;
import world.behemoth.world.Users;
import world.behemoth.world.World;
import world.behemoth.world.stats.Stats;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class UserCommand implements IRequest {
   public static final String JOIN_ROOM = "tfer";
   public static final String LIST_USERS = "who";
   public static final String IGNORE_LIST = "ignoreList";
   public static final String CHANGE_PREFERENCES = "uopref";
   public static final String GOTO = "goto";
   public static final String MUTE = "mute";
   public static final String UNMUTE = "unmute";
   public static final String SHUTDOWN = "shutdown";
   public static final String RESTART = "restart";
   public static final String SHUTDOWN_NOW = "shutdownnow";
   public static final String RESTART_NOW = "restartnow";
   public static final String HELP = "help";
   public static final String KICK = "kick";
   public static final String ITEM = "item";
   public static final String CLEAR = "clear";
   public static final String PULL = "pull";
   public static final String BAN = "ban";
   public static final String MOD_BAN = "modban";
   public static final String RATES = "rates";
   public static final String LEVEL = "level";
   public static final String ADD_GOLD = "addgold";
   public static final String ADD_CLASSPOINTS = "addcp";
   public static final String ADD_XP = "addxp";
   public static final String ADD_COIN = "addcoin";
   public static final String YELL = "iay";
   public static final String EMOTE_ALL = "emoteall";
   public static final String USER_COUNT = "usercount";
   public static final String QUEUE_COUNT = "queuecount";
   public static final String REPORT_LANG = "reportlang";

   public UserCommand() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String cmd = params[0];

      int muteTimeInMinutes;
      if(cmd.equals("reportlang")) {
         muteTimeInMinutes = world.db.jdbc.queryForInt("SELECT Access FROM users WHERE Name = ?", new Object[]{params[1].toLowerCase()});
         boolean seconds = false;
         if(muteTimeInMinutes >= 40) {
            throw new RequestException("Invalid /reportlang request.");
         }

         if(params[3].equals("Type reason for report here.")) {
            seconds = true;
         } else {
            if(((Integer)user.properties.get("level")).intValue() < 3) {
               throw new RequestException("Unknown Command!");
            }

            if(!seconds) {
               this.reportLang(user, world, params[1], Integer.parseInt(params[2]), params[3]);
            }
         }
      }

      String countryCode;
      String userFrame;
      String var15;
      int var17;

      if (cmd.equals(JOIN_ROOM)) {
         String roomName = params[2].toLowerCase().replaceAll("battleon", "faroff");
         world.rooms.basicRoomJoin(user, roomName);
      } else if(cmd.equals("who")) {
         JSONObject var16 = new JSONObject();
         JSONObject var18 = new JSONObject();
         User[] var19 = room.getAllUsers();
         int var21 = var19.length;

         for(int var23 = 0; var23 < var21; ++var23) {
            User var24 = var19[var23];
            JSONObject uObj = new JSONObject();
            uObj.put("iLvl", var24.properties.get("level"));
            uObj.put("ID", var24.properties.get("dbId"));
            uObj.put("sName", var24.properties.get("username"));
            uObj.put("sClass", var24.properties.get("classname"));
            var18.put(String.valueOf(var24.getUserId()), uObj);
         }

         var16.put("cmd", "who");
         var16.put("users", var18);
         world.send(var16, user);
      } else if(cmd.equals("goto")) {
         var15 = params[1].toLowerCase();
         User var20 = world.zone.getUserByName(var15);
         countryCode = (String)user.properties.get("language");
         if(var20 == null) {
            if(countryCode.equals("BR")) {
               throw new RequestException("Jogador \"" + var15 + "\" nao pode ser encontrado.");
            }

            throw new RequestException("Player \"" + var15 + "\" could not be found.");
         }

         if(((Integer)var20.properties.get("state")).intValue() == 2) {
            if(countryCode.equals("BR")) {
               throw new RequestException(var20.getName() + " e atualmente ocupado.");
            }

            throw new RequestException(var20.getName() + " is currently busy.");
         }

         if(!Settings.isAllowed("bGoto", user, var20)) {
            if(countryCode.equals("BR")) {
               throw new RequestException(var20.getName() + " esta ignorando os pedidos Goto.", "server");
            }

            throw new RequestException(var20.getName() + " is ignoring goto requests.", "server");
         }

         Room var22 = world.zone.getRoom(var20.getRoom());
         if(var22 == null) {
            return;
         }

         if(world.rooms.checkLimits(var22, user) != 0) {
            return;
         }

         userFrame = (String)var20.properties.get("frame");
         String var25 = (String)var20.properties.get("pad");
         world.rooms.joinRoom(var22, user, userFrame, var25);
      } else if(!cmd.equals("ignoreList")) {
         if(cmd.equals("uopref")) {
            world.users.changePreferences(user, params[1], Boolean.parseBoolean(params[2]));
         } else if(user.isAdmin()) {
            this.adminCommand(params, user, world, room);
            this.moderatorCommand(params, user, world, room);
         } else if(user.isModerator()) {
            this.moderatorCommand(params, user, world, room);
         } else if(cmd.equals("mute")) {
            muteTimeInMinutes = Integer.parseInt(params[1]);
            world.users.mute(user, muteTimeInMinutes, 12);
            var17 = world.users.getMuteTimeInSeconds(user);
            world.send(new String[]{"warning", world.users.getMuteMessage((double)var17)}, user);
         }
      }

   }

   private void adminCommand(String[] params, User user, World world, Room room) {
      String cmd = params[0];
      if(cmd.equals("shutdown")) {
         world.scheduleTask(new Shutdown(world), 0L, TimeUnit.SECONDS);
      } else if(cmd.equals("restart")) {
         world.scheduleTask(new Restart(world), 0L, TimeUnit.SECONDS);
      } else if(cmd.equals("shutdownnow")) {
         try {
            world.send(new String[]{"logoutWarning", "", "60"}, world.zone.getChannelList());
            world.shutdown();
            Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
            SmartFoxServer.getInstance().halt(user);
            Thread.sleep(TimeUnit.SECONDS.toMillis(5L));
            System.exit(0);
         } catch (InterruptedException var12) {
            ;
         }
      } else if(cmd.equals("restartnow")) {
         try {
            world.send(new String[]{"logoutWarning", "", "60"}, world.zone.getChannelList());
            world.shutdown();
            Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
            ExtensionHelper.instance().rebootServer();
         } catch (InterruptedException var11) {
            ;
         }
      } else if(cmd.equals("rates")) {
         int i$ = Integer.parseInt(params[2]);
         if(params[1].equals("gold")) {
            world.GOLD_RATE = i$;
            world.sendServerMessage("Gold rates has been changed to x" + i$ + ".");
            world.sendToUsers(new String[]{"administrator", "Gold rates has been changed to x" + i$ + "."});
         } else if(params[1].equals("drop")) {
            world.DROP_RATE = i$;
            world.sendServerMessage("Drop rates has been changed to x" + i$ + ".");
            world.sendToUsers(new String[]{"administrator", "Drop rates has been changed to x" + i$ + "."});
         } else if(params[1].equals("exp")) {
            world.EXP_RATE = i$;
            world.sendServerMessage("Experience rates has been changed to x" + i$ + ".");
            world.sendToUsers(new String[]{"administrator", "Experience rates has been changed to x" + i$ + "."});
         } else if(params[1].equals("rep")) {
            world.REP_RATE = i$;
            world.sendServerMessage("Reputation rates has been changed to x" + i$ + ".");
            world.sendToUsers(new String[]{"administrator", "Reputation rates has been changed to x" + i$ + "."});
         } else if(params[1].equals("cp")) {
            world.CP_RATE = i$;
            world.sendServerMessage("Class Point rates has been changed to x" + i$ + ".");
            world.sendToUsers(new String[]{"administrator", "Class Point rates has been changed to x" + i$ + "."});
         }
      } else if(cmd.equals("showdmg")) {
         Stats i$1 = (Stats)user.properties.get("stats");
         world.send(new String[]{"server", "minDmg: " + i$1.getMinDmg()}, user);
         world.send(new String[]{"server", "maxDmg: " + i$1.getMaxDmg()}, user);
      } else if(cmd.equals("queuecount")) {
         Iterator i$2 = world.warzoneQueue.warzoneQueues.entrySet().iterator();

         while(i$2.hasNext()) {
            Entry e = (Entry)i$2.next();
            LinkedBlockingQueue pq = (LinkedBlockingQueue)e.getValue();
            String warzone = (String)e.getKey();
            Area area = (Area)world.areas.get(warzone);
            if(area != null) {
               world.send(new String[]{"server", warzone + " queue: " + pq.size() + "/" + area.getMaxPlayers()}, user);
            }
         }
      } else if(cmd.equals("usercount")) {
         world.send(new String[]{"server", world.zone.getUserCount() + " currently online right now."}, user);
      } else if(cmd.equals("srates")) {
         world.send(new String[]{"server", "Experience: " + world.EXP_RATE}, user);
         world.send(new String[]{"server", "Reputation: " + world.REP_RATE}, user);
         world.send(new String[]{"server", "Gold: " + world.GOLD_RATE}, user);
         world.send(new String[]{"server", "Class Points: " + world.CP_RATE}, user);
         world.send(new String[]{"server", "Drop: " + world.DROP_RATE}, user);
      } else if(cmd.equals("help")) {
         world.send(new String[]{"server", "/shutdown"}, user);
         world.send(new String[]{"server", "/shutdownnow"}, user);
         world.send(new String[]{"server", "/restart"}, user);
         world.send(new String[]{"server", "/restartnow"}, user);
         world.send(new String[]{"server", "/rates (exp, drop, rep, cp, gold) (multiplier)"}, user);
         world.send(new String[]{"server", "/queuecount"}, user);
         world.send(new String[]{"server", "/usercount"}, user);
         world.send(new String[]{"server", "/srates"}, user);
      }

   }

   private void moderatorCommand(String[] params, User user, World world, Room room) throws RequestException {
      String cmd = params[0];
      User userResult;
      int userId;
      int access;
      if(cmd.equals("mute")) {
         userResult = world.zone.getUserByName(params[2].toLowerCase());
         if(userResult == null) {
            return;
         }

         userId = Integer.parseInt(params[1]);
         world.users.mute(userResult, userId, 12);
         access = world.users.getMuteTimeInSeconds(user);
         world.send(new String[]{"warning", world.users.getMuteMessage((double)access)}, userResult);
      } else if(cmd.equals("unmute")) {
         userResult = world.zone.getUserByName(params[1].toLowerCase());
         if(userResult == null) {
            return;
         }

         if(world.users.isMute(userResult)) {
            world.send(new String[]{"unmute"}, userResult);
            world.users.unmute(userResult);
         }
      } else if(cmd.equals("kick")) {
         userResult = world.zone.getUserByName(params[1].toLowerCase());
         if(userResult == null) {
            return;
         }

         world.users.kick(userResult);
      } else if(cmd.equals("clear")) {
         world.retrieveDatabaseObject(params[1]);
         world.send(new String[]{"server", "Server data cleared."}, user);
      } else if(cmd.equals("pull")) {
         userResult = world.zone.getUserByName(params[1].toLowerCase());
         if(userResult == null) {
            throw new RequestException("Player \"" + params[1].toLowerCase() + "\" could not be found.");
         }

         if(userResult.isAdmin() && !user.isAdmin()) {
            throw new RequestException("Invalid /pull request.");
         }

         Room var12 = world.zone.getRoom(userResult.getRoom());
         if(var12.equals(room)) {
            return;
         }

         String var14 = (String)user.properties.get("frame");
         String client = (String)user.properties.get("pad");
         world.rooms.joinRoom(room, userResult, var14, client);
      } else if(cmd.equals("iay")) {
         if(params[1].startsWith("@")) {
            world.sendServerMessage(params[1].substring(1));
         } else {
            String var11 = user.getName();
            String var13 = params[1];
            if(params[1].contains("@")) {
               var11 = params[1].split("@")[0].toLowerCase();
               var13 = params[1].split("@")[1];
            }

            if(user.isAdmin()) {
               world.send(new String[]{"administrator", "(" + var11 + "): " + var13}, world.zone.getChannelList());
            } else {
               world.send(new String[]{"moderator", "(" + var11 + "): " + var13}, world.zone.getChannelList());
            }
            Interface.jTextArea2.append("iay:"+user.getName()+":"+var13);
         }
      } else if(cmd.equals("addgold")) {
//         world.users.giveRewards(user, 0, Integer.parseInt(params[1]), 0, 0, -1, user.getUserId(), "p");
         world.users.giveRewards(user, 0, Integer.parseInt(params[1]), 0, 0, 0, -1, user.getUserId(), "p");
      } else if(cmd.equals("addcp")) {
//         world.users.giveRewards(user, 0, 0, Integer.parseInt(params[1]), 0, -1, user.getUserId(), "p");
         world.users.giveRewards(user, 0, 0, 0, Integer.parseInt(params[1]), 0, -1, user.getUserId(), "p");
      } else if(cmd.equals("addxp")) {
//         world.users.giveRewards(user, Integer.parseInt(params[1]), 0, 0, 0, -1, user.getUserId(), "p");
         world.users.giveRewards(user, Integer.parseInt(params[1]), 0, 0, 0, 0, -1, user.getUserId(), "p");
      } else if(cmd.equals("addcoin")) {
         int var15 = Integer.parseInt(params[1]);
         JSONObject var16 = new JSONObject();
         var16.put("cmd", "sellItem");
         var16.put("intAmount", Integer.valueOf(var15));
         var16.put("CharItemID", Integer.valueOf(user.hashCode()));
         var16.put("bCoins", Integer.valueOf(1));
         world.send(var16, user);
         world.db.jdbc.run("UPDATE users SET Coins = (Coins + ?) WHERE id=?", new Object[]{Integer.valueOf(var15), user.properties.get("dbId")});
         world.send(new String[]{"server", var15 + "ACs has been added to your account."}, user);
      } else if(cmd.equals("level")) {
         world.users.levelUp(user, Integer.parseInt(params[1]));
      } else if(cmd.equals("emoteall")) {
         User[] var17 = room.getAllUsers();
         User[] var18 = var17;
         access = var17.length;

         for(int var19 = 0; var19 < access; ++var19) {
            User playerInRoom = var18[var19];
            world.sendToRoom(new String[]{"emotea", params[1], Integer.toString(playerInRoom.getUserId())}, playerInRoom, room);
         }
      } else if(!cmd.equals("ban") && !cmd.equals("modban")) {
         if(cmd.equals("item")) {
            world.users.dropItem(user, Integer.parseInt(params[1]), Integer.parseInt(params[2]));
         } else if(cmd.equals("help")) {
            world.send(new String[]{"server", "/item (item id) (quantity)"}, user);
            world.send(new String[]{"server", "/modban or ban (username)"}, user);
            world.send(new String[]{"server", "/kick (username)"}, user);
            world.send(new String[]{"server", "/mute (minutes) (username)"}, user);
            world.send(new String[]{"server", "/unmute (username)"}, user);
            world.send(new String[]{"server", "/clear (map, shop, quest, item, enhshop, setting)"}, user);
            world.send(new String[]{"server", "/pull (username)"}, user);
            world.send(new String[]{"server", "/iay (message)"}, user);
            world.send(new String[]{"server", "/addgold, /addcp, /addxp, /addcoin (amount)"}, user);
            world.send(new String[]{"server", "/level (level)"}, user);
            world.send(new String[]{"server", "/shop (shop id)"}, user);
         }
      } else {
         QueryResult var20 = world.db.jdbc.query("SELECT id, Access FROM users WHERE Name = ? FOR UPDATE", new Object[]{params[1]});
         if(!var20.next()) {
            var20.close();
            throw new RequestException("Player \"" + params[1].toLowerCase() + "\" could not be found.");
         }

         userId = var20.getInt("id");
         access = var20.getInt("Access");
         var20.close();
         User var21;
         if(access <= 0) {
            world.db.jdbc.run("UPDATE users SET Access = 1 WHERE id = ?", new Object[]{Integer.valueOf(userId)});
            SmartFoxServer.log.warning(user.getName() + " just unbanned : " + params[1].toLowerCase());
            var21 = world.zone.getUserByName(params[1].toLowerCase());
            if(var21 != null) {
               var21.properties.put("access", Integer.valueOf(1));
            }

            world.users.log(user, "Modban Command", "User \"" + params[1].toLowerCase() + "\" unbanned by " + user.properties.get("username") + ".");
            world.send(new String[]{"server", "User \"" + params[1].toLowerCase() + "\" has been unbanned."}, user);
         } else {
            if(access >= 40) {
               throw new RequestException("Invalid /modban request.");
            }

            world.db.jdbc.run("UPDATE users SET Access = 0 WHERE id = ?", new Object[]{Integer.valueOf(userId)});
            SmartFoxServer.log.warning(user.getName() + " just banned : " + params[1].toLowerCase());
            var21 = world.zone.getUserByName(params[1].toLowerCase());
            if(var21 != null) {
               world.users.kick(var21);
            }

            world.users.log(user, "Modban Command", "User \"" + params[1].toLowerCase() + "\" banned by " + user.properties.get("username") + ".");
            world.send(new String[]{"server", "User \"" + params[1].toLowerCase() + "\" has been banned."}, user);
         }

         var20.close();
      }

   }

   public void reportLang(User user, World world, String TargetName, int Category, String Description) {
      String Shit = null;
      if(Category == 0) {
         Shit = "Abusive Language (Swearing)";
      } else if(Category == 1) {
         Shit = "Following/Griefing Another Player";
      } else if(Category == 2) {
         Shit = "Harassment/Scamming";
      }

      world.db.jdbc.run("INSERT INTO users_reports (UserID, TargetName, Category, Description, DateSubmitted) VALUES (?, ?, ?, ?, NOW())", new Object[]{user.properties.get("dbId"), String.valueOf(TargetName), String.valueOf(Shit), String.valueOf(Description)});
      world.send(new String[]{"server", "You have reported " + TargetName + ".Our moderators will review your report as soon as possible!"}, user);
   }
}
