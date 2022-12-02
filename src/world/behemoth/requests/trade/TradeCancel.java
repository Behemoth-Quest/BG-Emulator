package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.HashMap;
import net.sf.json.JSONObject;

public class TradeCancel implements IRequest {
   public TradeCancel() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      try {
         JSONObject ex = new JSONObject();
         ex.element("cmd", "tradeCancel");
         ex.element("bitSuccess", 1);
         user.properties.put("offer", new HashMap());
         user.properties.put("offerenh", new HashMap());
         user.properties.put("tradetgt", Integer.valueOf(-1));
         user.properties.put("tradegold", Integer.valueOf(0));
         user.properties.put("tradecoins", Integer.valueOf(0));
         user.properties.put("tradelock", Boolean.valueOf(false));
         user.properties.put("tradedeal", Boolean.valueOf(false));
         world.send(ex, user);
         if(((Integer)user.properties.get("tradetgt")).intValue() > -1) {
            world.send(new String[]{"warning", "Trade session is no longer available."}, user);
         }

         User client = SmartFoxServer.getInstance().getUserById(Integer.valueOf(Integer.parseInt(params[0])));
         if(client == null) {
            return;
         }

         if(((Integer)client.properties.get("tradetgt")).intValue() == user.getUserId()) {
            client.properties.put("offer", new HashMap());
            client.properties.put("offerenh", new HashMap());
            client.properties.put("tradetgt", Integer.valueOf(-1));
            client.properties.put("tradegold", Integer.valueOf(0));
            client.properties.put("tradecoins", Integer.valueOf(0));
            client.properties.put("tradelock", Boolean.valueOf(false));
            client.properties.put("tradedeal", Boolean.valueOf(false));
            world.send(ex, client);
            world.send(new String[]{"warning", "Trade session is no longer available."}, client);
         }
      } catch (NumberFormatException var7) {
         world.send(new String[]{"warning", "Invalid Input!"}, user);
      }

   }
}
