package world.behemoth.requests.party;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class PartyPromote implements IRequest {
   public PartyPromote() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String username = params[1].toLowerCase();
      User newOwner = world.zone.getUserByName(username);
      if(newOwner == null) {
         throw new RequestException("Player \"" + params[1].toLowerCase() + "\" could not be found.");
      } else {
         int partyId = ((Integer)user.properties.get("partyId")).intValue();
         PartyInfo pi = world.parties.getPartyInfo(partyId);
         if(!pi.isMember(newOwner)) {
            throw new RequestException("That player is not in your party.");
         } else {
            pi.setOwner(newOwner);
            JSONObject pp = new JSONObject();
            pp.put("cmd", "pp");
            pp.put("owner", newOwner.getName());
            world.send(pp, pi.getChannelList());
         }
      }
   }
}
