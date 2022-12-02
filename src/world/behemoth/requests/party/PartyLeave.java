package world.behemoth.requests.party;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class PartyLeave implements IRequest {
   public PartyLeave() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int partyId = ((Integer)user.properties.get("partyId")).intValue();
      if(partyId <= 0) {
         throw new RequestException("You are not in a party.");
      } else {
         PartyInfo pi = world.parties.getPartyInfo(partyId);
         if(pi.getOwner().equals(user.properties.get("username"))) {
            pi.setOwner(pi.getNextOwner());
         }

         pi.removeMember(user);
         JSONObject pr = new JSONObject();
         pr.put("cmd", "pr");
         pr.put("owner", pi.getOwner());
         pr.put("typ", "l");
         pr.put("unm", user.properties.get("username"));
         world.send(pr, pi.getChannelListButOne(user));
         world.send(pr, user);
         if(pi.getMemberCount() <= 0) {
            JSONObject pc = new JSONObject();
            pc.put("cmd", "pc");
            world.send(pc, pi.getOwnerObject());
            world.parties.removeParty(partyId);
            pi.getOwnerObject().properties.put("partyId", Integer.valueOf(-1));
         }

      }
   }
}
