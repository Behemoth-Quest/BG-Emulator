package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.requests.trade.TradeCancel;
import world.behemoth.world.Users;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class TradeUnlock implements IRequest {
   public TradeUnlock() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = SmartFoxServer.getInstance().getUserById(Integer.valueOf(Integer.parseInt(params[0])));
      if(client == null) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException("Trade has been canceled due to other player can\'t be found!");
      } else if(client.getUserId() != ((Integer)user.properties.get(Users.TRADE_TARGET)).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getUserId() != ((Integer)client.properties.get(Users.TRADE_TARGET)).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getName().equals(client.getName())) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else {
         user.properties.put(Users.TRADE_LOCK, Boolean.valueOf(false));
         user.properties.put(Users.TRADE_DEAL, Boolean.valueOf(false));
         client.properties.put(Users.TRADE_LOCK, Boolean.valueOf(false));
         client.properties.put(Users.TRADE_DEAL, Boolean.valueOf(false));
         JSONObject tr = new JSONObject();
         tr.element("cmd", "tradeUnlock");
         tr.element("bitSuccess", 1);
         world.send(tr, user);
         world.send(tr, client);
      }
   }
}
