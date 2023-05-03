package world.behemoth.requests.trade;

import world.behemoth.aqw.Settings;
import world.behemoth.config.ConfigData;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.Users;
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
      } else if(((Integer)user.properties.get(Users.ACCESS)).intValue() == 5 || ((Integer)client.properties.get(Users.ACCESS)).intValue() == 5) {
         throw new RequestException("You\'re not able to trade!");
      } else if(((Integer)client.properties.get(Users.STATE)).intValue() == 2) {
         throw new RequestException("The user you\'re trying to trade with is currently busy!");
      } else if(!Settings.isAllowed("bTrade", user, client)) {
         throw new RequestException("Player \"" + UserName + "\" is not accepting trade invites.");
      } else if(((Integer)client.properties.get(Users.TRADE_TARGET)).intValue() > -1) {
         throw new RequestException(UserName + " is already in trade session with someone!");
//      } else if (user.properties.get(Users.ADDRESS).equals(client.properties.get(Users.ADDRESS)) || client.properties.get(Users.ADDRESS).equals(user.properties.get(Users.ADDRESS))) {
//         throw new RequestException("You can\'t do this.");
      } else {
         Set requestedTrade = (Set)client.properties.get(Users.REQUESTED_TRADE);
         requestedTrade.add(Integer.valueOf(user.getUserId()));
         JSONObject tradeRequest = new JSONObject();
         tradeRequest.element("cmd", "ti");
         tradeRequest.element("owner", user.properties.get(Users.USERNAME));
         world.send(tradeRequest, client);
         world.send(new String[]{"server", "You have requested " + client.getName() + " to a trade session."}, user);
      }
   }
}
