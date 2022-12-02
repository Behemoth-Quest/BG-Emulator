package world.behemoth.requests.party;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class PartyKick implements IRequest {
   public PartyKick() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String username = params[1].toLowerCase();
      User client = world.zone.getUserByName(username);
      if(client == null) {
         throw new RequestException("Player \"" + username + "\" could not be found.");
      } else {
         int partyId = ((Integer)user.properties.get("partyId")).intValue();
         PartyInfo pi = world.parties.getPartyInfo(partyId);
         if(!pi.isMember(client)) {
            throw new RequestException("That player is not in your party.");
         } else {
            JSONObject pr = new JSONObject();
            pr.put("cmd", "pr");
            pr.put("owner", pi.getOwner());
            pr.put("typ", "k");
            pr.put("unm", user.properties.get("username"));
            world.send(pr, pi.getChannelList());
            pi.removeMember(client);
            if(pi.getMemberCount() <= 0 && pi.getOwner().equals(user.getName())) {
               JSONObject pc = new JSONObject();
               pc.put("cmd", "pc");
               world.send(pc, pi.getOwnerObject());
               world.parties.removeParty(partyId);
               pi.getOwnerObject().properties.put("partyId", Integer.valueOf(-1));
            }

         }
      }
   }
}
