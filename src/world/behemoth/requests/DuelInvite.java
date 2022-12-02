package world.behemoth.requests;

import world.behemoth.aqw.Settings;
import world.behemoth.db.objects.Area;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import net.sf.json.JSONObject;

public class DuelInvite implements IRequest {
   public DuelInvite() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String username = params[0].toLowerCase();
      User client = world.zone.getUserByName(username);
      if(client == null) {
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException("Jogador \"" + username + "\" nao apode ser encontrado.");
         } else {
            throw new RequestException("Player \"" + username + "\" could not be found.");
         }
      } else if(client.equals(user)) {
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException("Voce nao pode desafiar a si mesmo para um duelo!");
         } else {
            throw new RequestException("You cannot challenge yourself to a duel!");
         }
      } else if(!Settings.isAllowed("bDuel", user, client)) {
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException("Jogador \"" + username + "\" nao esta aceitando convites duelo.");
         } else {
            throw new RequestException("Player \"" + username + "\" is not accepting duel invites.");
         }
      } else if(((Integer)user.properties.get("state")).intValue() == 2) {
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException(client.getName() + " e atualmente ocupado.");
         } else {
            throw new RequestException(client.getName() + " is currently busy.");
         }
      } else if((((Integer)client.properties.get("level")).intValue() < 10) || (((Integer)user.properties.get("level")).intValue() < 10)){
          throw new RequestException("You and "+client.getName()+" have to be higher than level 10 to duel.");
      } else {
         Room clientRoom = world.zone.getRoom(client.getRoom());
         Area area = (Area)world.areas.get(room.getName().split("-")[0]);
         Area clientArea = (Area)world.areas.get(clientRoom.getName().split("-")[0]);
         if(area != null && area.isPvP()) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException("Nao e possivel iniciar duelo, enquanto no campo de batalha.");
            } else {
               throw new RequestException("Cannot initiate duel while in battlefield.");
            }
         } else if(clientArea != null && clientArea.isPvP()) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException(client.getName() + " e atualmente ocupado.");
            } else {
               throw new RequestException(client.getName() + " is currently busy.");
            }
         } else {
            Set requestedDuel = (Set)client.properties.get("requestedduel");
            requestedDuel.add(Integer.valueOf(user.getUserId()));
            JSONObject di = new JSONObject();
            di.put("owner", user.getName());
            di.put("cmd", "di");
            world.send(di, client);
            if(user.properties.get("language").equals("BR")) {
               world.send(new String[]{"server", "Voce desafiado " + username + " para um duelo."}, user);
            } else {
               world.send(new String[]{"server", "You have challenged " + username + " to a duel."}, user);
            }

         }
      }
   }
}
