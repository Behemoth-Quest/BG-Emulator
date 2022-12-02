package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
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
      } else if(((Integer)user.properties.get("tradetgt")).intValue() > -1) {
         throw new RequestException("You can\'t do this.");
      } else if(((Integer)client.properties.get("tradetgt")).intValue() > -1) {
         throw new RequestException("You can\'t do this.");
      } else {
         Set requestedTrade = (Set)user.properties.get("tradetgt");
         if(requestedTrade.contains(Integer.valueOf(client.getUserId()))) {
            requestedTrade.remove(Integer.valueOf(user.getUserId()));
            if(client.isAdmin() || client.isModerator() && !user.isAdmin() && !user.isModerator()) {
               throw new RequestException("Cannot trade with staff member!");
            }

            if(Objects.equals((Integer)user.properties.get("dbId"), (Integer)client.properties.get("dbId"))) {
               throw new RequestException("One does not simply trade with himself!");
            }

            user.properties.put("offer", new HashMap());
            user.properties.put("offerenh", new HashMap());
            user.properties.put("tradetgt", Integer.valueOf(client.getUserId()));
            user.properties.put("tradegold", Integer.valueOf(0));
            user.properties.put("tradecoins", Integer.valueOf(0));
            user.properties.put("tradelock", Boolean.valueOf(false));
            user.properties.put("tradedeal", Boolean.valueOf(false));
            client.properties.put("offer", new HashMap());
            client.properties.put("offerenh", new HashMap());
            client.properties.put("tradetgt", Integer.valueOf(user.getUserId()));
            client.properties.put("tradegold", Integer.valueOf(0));
            client.properties.put("tradecoins", Integer.valueOf(0));
            client.properties.put("tradelock", Boolean.valueOf(false));
            client.properties.put("tradedeal", Boolean.valueOf(false));
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
