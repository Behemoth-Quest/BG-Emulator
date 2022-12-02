package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DuelAccept implements IRequest {
   public DuelAccept() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[0].toLowerCase());
      if(client != null) {
         Set userRequestedDuel = (Set)user.properties.get("requestedduel");
         if(client == null) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException("Jogador \"" + params[0].toLowerCase() + "\" nao pode ser encontrado.");
            } else {
               throw new RequestException("Player \"" + params[0].toLowerCase() + "\" could not be found.");
            }
         } else {
            if(userRequestedDuel.contains(Integer.valueOf(client.getUserId()))) {
               userRequestedDuel.remove(Integer.valueOf(client.getUserId()));
               String roomNumber = "deadlock-" + (new Random()).nextInt(99999);
               Room newRoom = world.rooms.createRoom(roomNumber);
               JSONObject b = new JSONObject();
               b.put("id", Integer.valueOf(user.getUserId()));
               b.put("sName", user.properties.get("username"));
               newRoom.properties.put("bteamname", user.properties.get("username"));
               JSONObject r = new JSONObject();
               r.put("id", Integer.valueOf(client.getUserId()));
               r.put("sName", client.properties.get("username"));
               newRoom.properties.put("rteamname", client.properties.get("username"));
               JSONArray PVPFactions = new JSONArray();
               PVPFactions.add(b);
               PVPFactions.add(r);
               newRoom.properties.put("pvpfactions", PVPFactions);
               user.properties.put("pvpteam", Integer.valueOf(0));
               client.properties.put("pvpteam", Integer.valueOf(1));
               world.rooms.joinRoom(newRoom, user, "Enter0", "Spawn");
               world.rooms.joinRoom(newRoom, client, "Enter1", "Spawn");
               JSONObject duelEx = new JSONObject();
               duelEx.put("cmd", "DuelEX");
               Iterator i$ = userRequestedDuel.iterator();

               while(i$.hasNext()) {
                  int userId = ((Integer)i$.next()).intValue();
                  User challenger = ExtensionHelper.instance().getUserById(userId);
                  world.send(duelEx, challenger);
               }
            } else {
               world.users.kick(user);
               world.users.log(user, "Packet Edit [DuelAccept]", "Force duel accept.");
            }

         }
      }
   }
}
