package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.requests.trade.TradeCancel;
import world.behemoth.world.Users;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import net.sf.json.JSONObject;

public class TradeAccept implements IRequest {
   public TradeAccept() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String UserName = params[0].toLowerCase();
      User client = world.zone.getUserByName(UserName);
      if(client == null) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException("Trade has been canceled due to other player can\'t be found!");
      } else if(user.getName().equals(client.getName())) {
         throw new RequestException("You can\'t do this.");
      } else if(((Integer)user.properties.get(Users.TRADE_TARGET)).intValue() > -1) {
         throw new RequestException("You can\'t do this.");
      } else if(((Integer)client.properties.get(Users.TRADE_TARGET)).intValue() > -1) {
         throw new RequestException("You can\'t do this.");
//      } else if (user.properties.get(Users.ADDRESS).equals(client.properties.get(Users.ADDRESS)) || client.properties.get(Users.ADDRESS).equals(user.properties.get(Users.ADDRESS))) {
//         throw new RequestException("You can\'t do this.");
      } else {
         Set requestedTrade = (Set)user.properties.get(Users.REQUESTED_TRADE);
         if(requestedTrade.contains(Integer.valueOf(client.getUserId()))) {
            requestedTrade.remove(Integer.valueOf(user.getUserId()));
            if(client.isAdmin() || client.isModerator() && !user.isAdmin() && !user.isModerator()) {
               throw new RequestException("Cannot trade with staff member!");
            }

            if(Objects.equals(user.properties.get(Users.DATABASE_ID), client.properties.get(Users.DATABASE_ID))) {
               throw new RequestException("One does not simply trade with himself!");
            }

            user.properties.put(Users.TRADE_OFFERS, new HashMap());
            user.properties.put(Users.TRADE_OFFERS_ENHID, new HashMap());
            user.properties.put(Users.TRADE_TARGET, Integer.valueOf(client.getUserId()));
            user.properties.put(Users.TRADE_GOLD, Integer.valueOf(0));
            user.properties.put(Users.TRADE_COINS, Integer.valueOf(0));
            user.properties.put(Users.TRADE_LOCK, Boolean.valueOf(false));
            user.properties.put(Users.TRADE_DEAL, Boolean.valueOf(false));
            client.properties.put(Users.TRADE_OFFERS, new HashMap());
            client.properties.put(Users.TRADE_OFFERS_ENHID, new HashMap());
            client.properties.put(Users.TRADE_TARGET, Integer.valueOf(user.getUserId()));
            client.properties.put(Users.TRADE_GOLD, Integer.valueOf(0));
            client.properties.put(Users.TRADE_COINS, Integer.valueOf(0));
            client.properties.put(Users.TRADE_LOCK, Boolean.valueOf(false));
            client.properties.put(Users.TRADE_DEAL, Boolean.valueOf(false));
            JSONObject ti = new JSONObject();
            ti.element("userid", user.getUserId());
            ti.element("cmd", "startTrade");
            world.send(ti, client);
            ti.element("userid", client.getUserId());
            world.send(ti, user);
         }

      }
   }
}