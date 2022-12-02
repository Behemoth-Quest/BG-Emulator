package world.behemoth.requests.party;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class PartyDecline implements IRequest {
   public PartyDecline() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int partyId = Integer.parseInt(params[1]);
      PartyInfo pi = world.parties.getPartyInfo(partyId);
      JSONObject pd = new JSONObject();
      pd.put("unm", user.properties.get("username"));
      pd.put("cmd", "pd");
      if(pi.getMemberCount() <= 0) {
         world.parties.removeParty(partyId);
         pi.getOwnerObject().properties.put("partyId", Integer.valueOf(-1));
      }

      world.send(pd, pi.getOwnerObject());
      world.send(new String[]{"server", "You have declined the invitation."}, user);
   }
}
