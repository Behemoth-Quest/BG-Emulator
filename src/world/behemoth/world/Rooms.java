package world.behemoth.world;

import world.behemoth.ai.MonsterAI;
import world.behemoth.db.objects.Area;
import world.behemoth.db.objects.Hall;
import world.behemoth.db.objects.House;
import world.behemoth.db.objects.MapMonster;
import world.behemoth.db.objects.Monster;
import world.behemoth.tasks.WarpUser;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.data.Zone;
import it.gotoandplay.smartfoxserver.exceptions.ExtensionHelperException;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Rooms {
   public static final String PVP_FACTIONS = "pvpfactions";
   public static final String PVP_DONE = "done";
   public static final String RED_TEAM_SCORE = "rscore";
   public static final String BLUE_TEAM_SCORE = "bscore";
   public static final String BLUE_TEAM_NAME = "bteamname";
   public static final String RED_TEAM_NAME = "rteamname";
   public static final String MONSTERS = "monsters";
   public static final int ROOM_LOCKED = 6;
   public static final int ROOM_STAFF_ONLY = 5;
   public static final int ROOM_REQUIRE_UPGRADE = 4;
   public static final int ROOM_LEVEL_LIMIT = 3;
   public static final int ROOM_USER_INSIDE = 2;
   public static final int ROOM_FULL = 1;
   public static final int ROOM_OK = 0;
   private final Zone zone;
   private final World world;
   private final ExtensionHelper helper;
   private final Random privKeyGenerator;

   public Rooms(Zone zone, World world) {
      super();
      this.world = world;
      this.zone = zone;
      this.helper = ExtensionHelper.instance();
      this.privKeyGenerator = new Random();
   }

   public void exit(Room room, User user) {
      String[] exit = new String[]{"exitArea", String.valueOf(user.getUserId()), user.getName()};
      this.world.sendToRoomButOne(exit, user, room);
      if(this.world.areas.get(room.getName().split("-")[0]) != null && ((Area)this.world.areas.get(room.getName().split("-")[0])).isPvP()) {
         this.world.sendToRoomButOne(new String[]{"server", user.getName() + " has left the match."}, user, room);
         User[] arrUsers = room.getAllUsers();
         int BlueCount = 0;
         int RedCount = 0;
         User[] ct = arrUsers;
         int len$ = arrUsers.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            User playerInRoom = ct[i$];
            if(((Integer)playerInRoom.properties.get("pvpteam")).intValue() == 0) {
               ++BlueCount;
            } else if(((Integer)playerInRoom.properties.get("pvpteam")).intValue() == 1) {
               ++RedCount;
            }
         }

         if(room.getUserCount() > 0) {
            if(BlueCount <= 0) {
               this.addPvPScore(room, 1000, 1);
            } else if(RedCount <= 0) {
               this.addPvPScore(room, 1000, 0);
            }

            JSONObject var11 = new JSONObject();
            var11.put("cmd", "ct");
            var11.put("pvp", this.getPvPResult(room));
            this.world.send(var11, room.getChannellList());
         }
      }

   }

   private void moveToArea(Room room, User user) {
      JSONObject mta = new JSONObject();
      String mapName = room.getName().split("-")[0].equals("house")?room.getName():room.getName().split("-")[0];
      Area area = (Area)this.world.areas.get(mapName);
      JSONArray uoBranch = new JSONArray();
      User[] users = room.getAllUsers();
      User[] bs = users;
      int rs = users.length;

      for(int pvpScore = 0; pvpScore < rs; ++pvpScore) {
         User userInRoom = bs[pvpScore];
         JSONObject userObj = this.world.users.getProperties(userInRoom, room);
         uoBranch.add(userObj);
      }

      mta.put("cmd", "moveToArea");
      mta.put("areaId", Integer.valueOf(room.getId()));
      mta.put("areaName", room.getName());
      mta.put("sExtra", "");
      mta.put("strMapFileName", area.getFile());
      mta.put("strMapName", mapName);
      mta.put("uoBranch", uoBranch);
      mta.put("monBranch", this.getMonBranch(room, area));
      mta.put("intType", Integer.valueOf(2));
      if(area instanceof House) {
         mta.put("houseData", ((House)area).getData());
      }

      if(area instanceof Hall) {
         mta.put("guildData", this.world.users.getGuildHallData(((Hall)area).getGuildId()));
         mta.put("strMapName", "guildhall");
      }

      if(area.isPvP()) {
         mta.put("pvpTeam", user.properties.get("pvpteam"));
         mta.put("PVPFactions", room.properties.get("pvpfactions"));
         JSONObject var13 = new JSONObject();
         var13.put("v", room.properties.get("bscore"));
         JSONObject var14 = new JSONObject();
         var14.put("v", room.properties.get("rscore"));
         JSONArray var15 = new JSONArray();
         var15.add(var13);
         var15.add(var14);
         mta.put("pvpScore", var15);
      }

      if(!area.monsters.isEmpty()) {
         mta.put("mondef", this.getMonsterDefinition(area));
         mta.put("monmap", this.getMonMap(area));
      }

      this.world.send(mta, user);
   }

   public void basicRoomJoin(User user, String roomName, String roomFrame, String roomPad) {
      String mapName = roomName.split("-")[0];
      if(!this.world.areas.containsKey(mapName)) {
         this.world.send(new String[]{"warning", "\"" + mapName + "\" is not a recognized map name."}, user);
      } else {
         Room roomToJoin = this.lookForRoom(roomName);
         if(roomToJoin == null) {
            roomToJoin = this.generateRoom(roomName);
         }

         if(this.checkLimits(roomToJoin, user) == 0) {
            this.joinRoom(roomToJoin, user, roomFrame, roomPad);
         }

      }
   }

   public void basicRoomJoin(User user, String roomName) {
      this.basicRoomJoin(user, roomName, "Enter", "Spawn");
   }

   public void basicGuildHallJoin(User user, String roomName) {
      this.basicRoomJoin(user, roomName, "r1", "Down1");
   }

   public void joinRoom(Room room, User user) {
      this.joinRoom(room, user, "Enter", "Spawn");
   }

   public void joinRoom(Room room, User user, String frame, String pad) {
      if(room != null && user != null) {
         try {
            user.properties.put("frame", frame);
            user.properties.put("pad", pad);
            user.properties.put("tx", Integer.valueOf(0));
            user.properties.put("ty", Integer.valueOf(0));
            this.helper.joinRoom(user, user.getRoom(), room.getId(), true, "", false, true);
            this.moveToArea(room, user);
            this.world.send(new String[]{"server", "You joined \"" + room.getName() + "\"!"}, user);
         } catch (ExtensionHelperException var6) {
            SmartFoxServer.log.warning("Error joining room: " + var6.getMessage());
         }

      }
   }

   public Room lookForRoom(String name) {
      Room room = this.zone.getRoomByName(name);
      if(room != null) {
         return room;
      } else {
         String[] arr = name.split("-");
         String areaName = arr[0];
         int i;
         if(arr.length > 1) {
            try {
               i = Integer.parseInt(arr[1]);
               if(i > 90000) {
                  return this.generateRoom(name);
               }
            } catch (NumberFormatException var8) {
               ;
            }
         }

         for(i = 1; i < 1000; ++i) {
            String search = areaName + "-" + i;
            Room test = this.zone.getRoomByName(search);
            if(test != null && test.getMaxUsers() > test.howManyUsers()) {
               return test;
            }
         }

         return null;
      }
   }

   public int checkLimits(Room room, User user) {
      if(room == null) {
         throw new NullPointerException("room is null");
      } else {
         String areaName = room.getName().split("-")[0].equals("house")?room.getName():room.getName().split("-")[0];
         Area area = (Area)this.world.areas.get(areaName);
         if(area.getReqLevel() > ((Integer)user.properties.get("level")).intValue()) {
            this.world.send(new String[]{"warning", "\"" + areaName + "\" requires level " + area.getReqLevel() + " and above to enter."}, user);
            return 3;
         } else if(area.isPvP()) {
            this.world.send(new String[]{"warning", "\"" + areaName + "\" is locked zone."}, user);
            return 6;
         } else if(area.isStaff() && !user.isAdmin() && !user.isModerator()) {
            this.world.send(new String[]{"warning", "\"" + areaName + "\" is not a recognized map name."}, user);
            return 5;
         } else if(area.isUpgrade() && ((Integer)user.properties.get("upgdays")).intValue() <= 0) {
            this.world.send(new String[]{"warning", "\"" + areaName + "\" is member only."}, user);
            return 4;
         } else if(room.contains(user.getName())) {
            this.world.send(new String[]{"warning", "Cannot join a room you are currently in!"}, user);
            return 2;
         } else if(area instanceof Hall && ((Hall)area).getGuildId() != ((Integer)user.properties.get("guildid")).intValue()) {
            this.world.send(new String[]{"warning", "You cannot access other guild halls!"}, user);
            return 6;
         } else if(room.howManyUsers() >= room.getMaxUsers()) {
            this.world.send(new String[]{"warning", "Room join failed, destination room is full."}, user);
            return 1;
         } else {
            return 0;
         }
      }
   }

   public Room generateRoom(String name) {
      if(name.contains("-")) {
         try {
            int areaName = Integer.parseInt(name.split("-")[1]);
            String var8;
            if(areaName >= 90000) {
               var8 = name.split("-")[0] + "-" + (this.privKeyGenerator.nextInt(9999) + 90000);
               return this.createRoom(var8);
            }

            if(areaName >= 1000) {
               var8 = name.split("-")[0] + "-" + areaName;
               return this.createRoom(var8);
            }
         } catch (NumberFormatException var6) {
            ;
         }
      }

      String var7 = name.split("-")[0];

      for(int i = 1; i < 1000; ++i) {
         String search = var7 + "-" + i;
         Room test = this.zone.getRoomByName(search);
         if(test == null) {
            return this.createRoom(search);
         }
      }

      return null;
   }

   public Room createRoom(String name) {
      HashMap map = new HashMap();
      String mapName = name.split("-")[0].equals("house")?name:name.split("-")[0];
      Area area = (Area)this.world.areas.get(mapName);
      map.put("isGame", "false");
      map.put("maxU", String.valueOf(area.getMaxPlayers()));
      map.put("name", name);
      map.put("uCount", "false");

      try {
         Room ex = this.helper.createRoom(this.zone, map, (User)null, false, true);
         ConcurrentHashMap monsters = new ConcurrentHashMap();
         if(!area.monsters.isEmpty()) {
            Iterator b = area.monsters.iterator();

            while(b.hasNext()) {
               MapMonster r = (MapMonster)b.next();
               MonsterAI PVPFactions = new MonsterAI(r, this.world, ex);
               monsters.put(Integer.valueOf(r.getMonMapId()), PVPFactions);
            }
         }

         if(area.isPvP()) {
            ex.properties.put("done", Boolean.valueOf(false));
            ex.properties.put("bscore", Integer.valueOf(0));
            ex.properties.put("rscore", Integer.valueOf(0));
            JSONObject b1 = new JSONObject();
            b1.put("id", Integer.valueOf(8));
            b1.put("sName", "Infinity");
            ex.properties.put("bteamname", "Team Infinity");
            JSONObject r1 = new JSONObject();
            r1.put("id", Integer.valueOf(7));
            r1.put("sName", "Arts");
            ex.properties.put("rteamname", "Team Arts");
            JSONArray PVPFactions1 = new JSONArray();
            PVPFactions1.add(b1);
            PVPFactions1.add(r1);
            ex.properties.put("pvpfactions", PVPFactions1);
         }

         ex.properties.put("monsters", monsters);
         return ex;
      } catch (ExtensionHelperException var10) {
         return null;
      }
   }

   private JSONArray getMonMap(Area area) {
      JSONArray monMap = new JSONArray();
      Iterator i$ = area.monsters.iterator();

      while(i$.hasNext()) {
         MapMonster mapMonster = (MapMonster)i$.next();
         JSONObject monInfo = new JSONObject();
         monInfo.put("MonID", String.valueOf(mapMonster.getMonsterId()));
         monInfo.put("MonMapID", String.valueOf(mapMonster.getMonMapId()));
         monInfo.put("bRed", Integer.valueOf(0));
         monInfo.put("intRSS", String.valueOf(-1));
         monInfo.put("strFrame", mapMonster.getFrame());
         monMap.add(monInfo);
      }

      return monMap;
   }

   private JSONArray getMonsterDefinition(Area area) {
      JSONArray monDef = new JSONArray();
      Iterator i$ = area.monsters.iterator();

      while(i$.hasNext()) {
         MapMonster mapMonster = (MapMonster)i$.next();
         JSONObject monInfo = new JSONObject();
         Monster monster = (Monster)this.world.monsters.get(Integer.valueOf(mapMonster.getMonsterId()));
         monInfo.put("MonID", String.valueOf(mapMonster.getMonsterId()));
         monInfo.put("intHP", Integer.valueOf(monster.getHealth()));
         monInfo.put("intHPMax", Integer.valueOf(monster.getHealth()));
         monInfo.put("intLevel", Integer.valueOf(monster.getLevel()));
         monInfo.put("intMP", Integer.valueOf(monster.getMana()));
         monInfo.put("intMPMax", Integer.valueOf(monster.getMana()));
         monInfo.put("sRace", monster.getRace());
         monInfo.put("strBehave", "walk");
         monInfo.put("strElement", monster.getElement());
         monInfo.put("strLinkage", monster.getLinkage());
         monInfo.put("strMonFileName", monster.getFile());
         monInfo.put("strMonName", monster.getName());
         monDef.add(monInfo);
      }

      return monDef;
   }

   private JSONArray getMonBranch(Room room, Area area) {
      JSONArray monBranch = new JSONArray();
      ConcurrentHashMap monsters = (ConcurrentHashMap)room.properties.get("monsters");

      JSONObject mon;
      for(Iterator i$ = monsters.values().iterator(); i$.hasNext(); monBranch.add(mon)) {
         MonsterAI actMon = (MonsterAI)i$.next();
         mon = new JSONObject();
         Monster monster = (Monster)this.world.monsters.get(Integer.valueOf(actMon.getMonsterId()));
         mon.put("MonID", String.valueOf(actMon.getMonsterId()));
         mon.put("MonMapID", String.valueOf(actMon.getMapId()));
         mon.put("bRed", "0");
         mon.put("iLvl", Integer.valueOf(monster.getLevel()));
         mon.put("intHP", Integer.valueOf(actMon.getHealth()));
         mon.put("intHPMax", Integer.valueOf(monster.getHealth()));
         mon.put("intMP", Integer.valueOf(actMon.getMana()));
         mon.put("intMPMax", Integer.valueOf(monster.getMana()));
         mon.put("intState", Integer.valueOf(actMon.getState()));
         mon.put("wDPS", Integer.valueOf(monster.getDPS()));
         if(area.isPvP()) {
            JSONArray react = new JSONArray();
            if(monster.getTeamId() > 0) {
               react.add(Integer.valueOf(0));
               react.add(Integer.valueOf(1));
            } else {
               react.add(Integer.valueOf(1));
               react.add(Integer.valueOf(0));
            }

            mon.put("react", react);
         }
      }

      return monBranch;
   }

   public void addPvPScore(Room room, int score, int teamId) {
      if(!((Boolean)room.properties.get("done")).booleanValue()) {
         int rScore = ((Integer)room.properties.get("rscore")).intValue();
         int bScore = ((Integer)room.properties.get("bscore")).intValue();
         switch(teamId) {
         case 0:
            room.properties.put("bscore", Integer.valueOf(score + bScore >= 1000?1000:score + bScore));
            break;
         case 1:
            room.properties.put("rscore", Integer.valueOf(score + rScore >= 1000?1000:score + rScore));
         }

      }
   }

   public void relayPvPEvent(MonsterAI ai, int teamId) {
      Monster monster = (Monster)this.world.monsters.get(Integer.valueOf(ai.getMonsterId()));
      String monName = monster.getName();
      Room room = ai.getRoom();
      JSONObject pvpe = new JSONObject();
      pvpe.put("cmd", "PVPE");
      pvpe.put("typ", "kill");
      pvpe.put("team", Integer.valueOf(teamId));
      if(monName.contains("Restorer")) {
         pvpe.put("val", "Restorer");
         this.addPvPScore(room, 50, teamId);
      } else if(monName.contains("Brawler")) {
         pvpe.put("val", "Brawler");
         this.addPvPScore(room, 25, teamId);
      } else if(monName.contains("Captain")) {
         pvpe.put("val", "Captain");
         this.addPvPScore(room, 1000, teamId);
      } else if(monName.contains("General")) {
         pvpe.put("val", "General");
         this.addPvPScore(room, 100, teamId);
      } else if(monName.contains("Knight")) {
         pvpe.put("val", "Knight");
         this.addPvPScore(room, 100, teamId);
      } else {
         this.addPvPScore(room, monster.getLevel(), teamId);
      }

      if(pvpe.containsKey("val")) {
         this.world.send(pvpe, room.getChannellList());
      }

   }

   public JSONObject getPvPResult(Room room) {
      JSONObject pvpcmd = new JSONObject();
      pvpcmd.put("cmd", "PVPS");
      JSONArray pvpScore = new JSONArray();
      JSONObject bs = new JSONObject();
      JSONObject rs = new JSONObject();
      int redScore = ((Integer)room.properties.get("rscore")).intValue();
      int blueScore = ((Integer)room.properties.get("bscore")).intValue();
      rs.put("v", Integer.valueOf(redScore));
      bs.put("v", Integer.valueOf(blueScore));
      if(!((Boolean)room.properties.get("done")).booleanValue() && (redScore >= 1000 || blueScore >= 1000)) {
         pvpcmd.put("cmd", "PVPC");
         String rName = (String)room.properties.get("rteamname");
         String bName = (String)room.properties.get("bteamname");
         int users;
         User[] arr$;
         int len$;
         int i$;
         User user;
         HashSet var15;
         if(redScore >= 1000) {
            if(room.getName().toLowerCase().split("-")[0].equals("1v1")) {
               this.world.sendServerMessage("<font color=\"#ffffff\">" + rName + "</font> won the match against <font color=\"#ffffff\">" + bName + "</font>");
            }

            if(room.getName().toLowerCase().split("-")[0].equals("guildwars")) {
               users = this.world.db.jdbc.queryForInt("SELECT id FROM guilds WHERE Name = ?", new Object[]{rName});
               this.world.db.jdbc.run("UPDATE guilds SET Wins = (Wins + 1) WHERE Name = ?", new Object[]{rName});
               this.world.db.jdbc.run("UPDATE guilds SET Loses = (Loses + 1) WHERE Name = ?", new Object[]{bName});
               this.world.sendGuildUpdate(this.world.users.getGuildObject(users));
            }

            var15 = new HashSet();
            arr$ = room.getAllUsers();
            len$ = arr$.length;

            for(i$ = 0; i$ < len$; ++i$) {
               user = arr$[i$];
               if(((Integer)user.properties.get("pvpteam")).intValue() == 1) {
                  var15.add(user);
               }
            }

            this.world.scheduleTask(new WarpUser(this.world, var15), 5L, TimeUnit.SECONDS);
         } else if(blueScore >= 1000) {
            if(room.getName().toLowerCase().split("-")[0].equals("1v1")) {
               this.world.sendServerMessage("<font color=\"#ffffff\">" + bName + "</font> won the match against <font color=\"#ffffff\">" + rName + "</font>");
            }

            if(room.getName().toLowerCase().split("-")[0].equals("guildwars")) {
               users = this.world.db.jdbc.queryForInt("SELECT id FROM guilds WHERE Name = ?", new Object[]{bName});
               this.world.db.jdbc.run("UPDATE guilds SET Wins = (Wins + 1) WHERE Name = ?", new Object[]{bName});
               this.world.db.jdbc.run("UPDATE guilds SET Loses = (Loses + 1) WHERE Name = ?", new Object[]{rName});
               this.world.sendGuildUpdate(this.world.users.getGuildObject(users));
            }

            var15 = new HashSet();
            arr$ = room.getAllUsers();
            len$ = arr$.length;

            for(i$ = 0; i$ < len$; ++i$) {
               user = arr$[i$];
               if(((Integer)user.properties.get("pvpteam")).intValue() == 0) {
                  var15.add(user);
               }
            }

            this.world.scheduleTask(new WarpUser(this.world, var15), 9L, TimeUnit.SECONDS);
         }

         room.properties.put("done", Boolean.valueOf(true));
      }

      pvpScore.add(bs);
      pvpScore.add(rs);
      pvpcmd.put("pvpScore", pvpScore);
      return pvpcmd;
   }
}
