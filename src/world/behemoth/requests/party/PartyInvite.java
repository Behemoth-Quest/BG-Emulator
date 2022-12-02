package world.behemoth.requests.party;

import world.behemoth.aqw.Settings;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import net.sf.json.JSONObject;

public class PartyInvite implements IRequest {
   public PartyInvite() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String username = params[1].toLowerCase();
      User client = world.zone.getUserByName(username);
      if(client == null) {
         throw new RequestException("Player \"" + username + "\" could not be found.");
      } else {
         int partyId = ((Integer)user.properties.get("partyId")).intValue();
         int clientPID = ((Integer)client.properties.get("partyId")).intValue();
         if(!Settings.isAllowed("bParty", user, client)) {
            throw new RequestException(client.getName() + " cannot recieve party invitations.");
         } else if(((Integer)client.properties.get("state")).intValue() == 2) {
            throw new RequestException(client.getName() + " is currently busy.");
         } else if(clientPID > 0) {
            throw new RequestException("User is already in a party!");
         } else {
            if(partyId < 0) {
               partyId = world.parties.getPartyId(user);
            }

            Set requestedParty = (Set)client.properties.get("requestedparty");
            requestedParty.add(Integer.valueOf(partyId));
            JSONObject pi = new JSONObject();
            pi.put("cmd", "pi");
            pi.put("pid", Integer.valueOf(partyId));
            pi.put("owner", user.properties.get("username"));
            world.send(pi, client);
            world.send(new String[]{"server", "You have invited " + client.getName() + " to join your party."}, user);
         }
      }
   }
}
