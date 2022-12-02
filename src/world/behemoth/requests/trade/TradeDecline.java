package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

import java.util.Set;

public class TradeDecline implements IRequest {
   public TradeDecline() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[0].toLowerCase());
      if(client != null) {
         Set requestedTrade = (Set)user.properties.get("requestedguild");
         requestedTrade.remove(Integer.valueOf(client.getUserId()));
         world.send(new String[]{"server", user.getName() + " declined your trade request."}, client);
      }
   }
}
