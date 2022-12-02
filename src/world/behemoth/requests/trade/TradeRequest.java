package world.behemoth.requests.trade;

import world.behemoth.aqw.Settings;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import net.sf.json.JSONObject;

public class TradeRequest implements IRequest {
   public TradeRequest() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String UserName = params[0].toLowerCase();
      User client = world.zone.getUserByName(UserName);
      if(client == null) {
         throw new RequestException("Player \"" + UserName + "\" could not be found.");
      } else if(client.isAdmin() || client.isModerator() && !user.isAdmin() && !user.isModerator()) {
         throw new RequestException("You\'re not able to trade with staffs!");
      } else if(((Integer)client.properties.get("state")).intValue() == 2) {
         throw new RequestException("The user you\'re trying to trade with is currently busy!");
      } else if(!Settings.isAllowed("bTrade", user, client)) {
         throw new RequestException("Player \"" + UserName + "\" is not accepting trade invites.");
      } else if(((Integer)client.properties.get("tradetgt")).intValue() > -1) {
         throw new RequestException(UserName + " is already in trade session with someone!");
      } else {
         Set requestedTrade = (Set)client.properties.get("requestedguild");
         requestedTrade.add(Integer.valueOf(user.getUserId()));
         JSONObject tradeRequest = new JSONObject();
         tradeRequest.element("cmd", "ti");
         tradeRequest.element("owner", user.properties.get("username"));
         world.send(tradeRequest, client);
         world.send(new String[]{"server", "You have requested " + client.getName() + " to a trade session."}, user);
      }
   }
}
