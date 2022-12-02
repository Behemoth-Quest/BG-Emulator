package world.behemoth.requests.customfunctions;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GuildWarsAccept implements IRequest {
   public GuildWarsAccept() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String userName = params[0].toLowerCase();
      User client = world.zone.getUserByName(userName);
      if(client == null) {
         throw new RequestException("Player \"" + userName + "\" could not be found.");
      } else {
         Set userRequestedDuel = (Set)user.properties.get("requestedduel");
         if(userRequestedDuel.contains(Integer.valueOf(client.getUserId()))) {
            userRequestedDuel.remove(Integer.valueOf(client.getUserId()));
            JSONObject userGuildDatas = (JSONObject)user.properties.get("guildobj");
            JSONObject clientGuildDatas = (JSONObject)client.properties.get("guildobj");
            JSONArray userMembers = (JSONArray)userGuildDatas.get("ul");
            JSONArray clientMembers = (JSONArray)clientGuildDatas.get("ul");
            String roomNumber = "guildwars-" + (new Random()).nextInt(99999);
            Room newRoom = world.rooms.createRoom(roomNumber);
            JSONObject b = new JSONObject();
            b.put("id", Integer.valueOf(user.getUserId()));
            b.put("sName", userGuildDatas.get("Name"));
            newRoom.properties.put("bteamname", userGuildDatas.get("Name"));
            JSONObject r = new JSONObject();
            r.put("id", Integer.valueOf(client.getUserId()));
            r.put("sName", clientGuildDatas.get("Name"));
            newRoom.properties.put("rteamname", clientGuildDatas.get("Name"));
            JSONArray PVPFactions = new JSONArray();
            PVPFactions.add(b);
            PVPFactions.add(r);
            newRoom.properties.put("pvpfactions", PVPFactions);
            Iterator duelEx;
            JSONObject i$;
            User userId;
            JSONObject challenger;
            JSONObject PVPI;
            if(userMembers != null && userMembers.size() > 0) {
               duelEx = userMembers.iterator();

               while(duelEx.hasNext()) {
                  i$ = (JSONObject)duelEx.next();
                  userId = world.zone.getUserByName(i$.get("userName").toString().toLowerCase());
                  if(userId != null) {
                     userId.properties.put("roomqueued", newRoom);
                  }

                  userId.properties.put("pvpteam", Integer.valueOf(0));
                  challenger = new JSONObject();
                  challenger.put("cmd", "PVPQ");
                  challenger.put("bitSuccess", Integer.valueOf(1));
                  challenger.put("warzone", "guildwars");
                  challenger.put("avgWait", Integer.valueOf(-1));
                  world.send(challenger, userId);
                  PVPI = new JSONObject();
                  PVPI.put("cmd", "PVPI");
                  PVPI.put("warzone", "guildwars");
                  world.send(new String[]{"server", "A new Guild Battle Warzone has started!"}, userId);
                  world.send(PVPI, userId);
               }
            }

            if(clientMembers != null && clientMembers.size() > 0) {
               duelEx = clientMembers.iterator();

               while(duelEx.hasNext()) {
                  i$ = (JSONObject)duelEx.next();
                  userId = world.zone.getUserByName(i$.get("userName").toString().toLowerCase());
                  if(userId != null) {
                     userId.properties.put("roomqueued", newRoom);
                  }

                  userId.properties.put("pvpteam", Integer.valueOf(1));
                  challenger = new JSONObject();
                  challenger.put("cmd", "PVPQ");
                  challenger.put("bitSuccess", Integer.valueOf(1));
                  challenger.put("warzone", "guildwars");
                  challenger.put("avgWait", Integer.valueOf(-1));
                  world.send(challenger, userId);
                  PVPI = new JSONObject();
                  PVPI.put("cmd", "PVPI");
                  PVPI.put("warzone", "guildwars");
                  world.send(new String[]{"server", "A new Guild Warzone Battle has started!"}, userId);
                  world.send(PVPI, userId);
               }
            }

            JSONObject duelEx1 = new JSONObject();
            duelEx1.put("cmd", "GuildWarEX");
            Iterator i$1 = userRequestedDuel.iterator();

            while(i$1.hasNext()) {
               int userId1 = ((Integer)i$1.next()).intValue();
               User challenger1 = ExtensionHelper.instance().getUserById(userId1);
               world.send(duelEx1, challenger1);
            }
         } else {
            SmartFoxServer.log.warning("Kicking for forcing guild duel accept: " + user.properties.get("username"));
            world.users.kick(user);
            world.users.log(user, "Packet Edit [GuildWarsAccept]", "Force duel accept.");
         }

      }
   }
}
