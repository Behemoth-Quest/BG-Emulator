package world.behemoth.requests.party;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class PartySummon implements IRequest {
   public PartySummon() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[1].toLowerCase());
      if(client == null) {
         throw new RequestException("Player \"" + params[1].toLowerCase() + "\" could not be found.");
      } else {
         int partyId = ((Integer)user.properties.get("partyId")).intValue();
         int clientPID = ((Integer)client.properties.get("partyId")).intValue();
         if(partyId < 0) {
            throw new RequestException("You are not in a party!");
         } else if(partyId != clientPID) {
            throw new RequestException("The user you are trying to summon is not in your party.");
         } else {
            JSONObject pd = new JSONObject();
            pd.put("unm", user.properties.get("username"));
            pd.put("cmd", "ps");
            world.send(pd, client);
            world.send(new String[]{"server", "You attempt to summon " + client.getName() + " to you."}, user);
         }
      }
   }
}
