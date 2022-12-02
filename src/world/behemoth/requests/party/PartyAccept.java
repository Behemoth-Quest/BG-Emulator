package world.behemoth.requests.party;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.PartyInfo;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PartyAccept implements IRequest {
   public PartyAccept() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(((Integer)user.properties.get("partyId")).intValue() > 0) {
         throw new RequestException("You are already in a party!");
      } else {
         int partyId = Integer.parseInt(params[1]);
         Set requestedParty = (Set)user.properties.get("requestedparty");
         if(requestedParty.contains(Integer.valueOf(partyId))) {
            PartyInfo pi = world.parties.getPartyInfo(partyId);
            pi.addMember(user);
            JSONObject pa = new JSONObject();
            JSONArray ul = new JSONArray();
            ul.add(user.properties.get("username"));
            pa.put("cmd", "pa");
            pa.put("pid", Integer.valueOf(partyId));
            pa.put("ul", ul);
            pa.put("owner", pi.getOwner());
            world.send(pa, pi.getChannelListButOne(user));
            pa.put("ul", pi.getUsers());
            world.send(pa, user);
            requestedParty.remove(Integer.valueOf(partyId));
         } else {
            world.users.kick(user);
            world.users.log(user, "Packet Edit [PartyAccept]", "forcing party accept");
         }

      }
   }
}
