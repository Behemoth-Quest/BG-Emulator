package world.behemoth.requests.party;

import world.behemoth.aqw.Settings;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class PartyAcceptSummon implements IRequest {
   public PartyAcceptSummon() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int partyId = ((Integer)user.properties.get("partyId")).intValue();
      PartyInfo pi = world.parties.getPartyInfo(partyId);
      User client = pi.getOwnerObject();
      if(!Settings.isAllowed("bGoto", client, user)) {
         world.send(new String[]{"warning", user.getName() + " failed to be summoned."}, client);
         world.send(new String[]{"warning", "Summon failed. Please do not block goto requests."}, user);
      } else {
         world.send(new String[]{"server", user.getName() + " accepted your summon."}, client);
      }

   }
}
