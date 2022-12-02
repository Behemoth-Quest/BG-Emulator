package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class TradeLock implements IRequest {
   public TradeLock() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int gold = 0;
      int coins = 0;

      try {
         gold = Integer.parseInt(params[1]);
         coins = Integer.parseInt(params[2]);
      } catch (NumberFormatException var10) {
         JSONObject userResult = new JSONObject();
         userResult.put("cmd", "tradeLock");
         userResult.put("bitSuccess", Integer.valueOf(0));
         userResult.put("msg", "Invalid gold/coins input!");
         world.send(userResult, user);
      }

      User client = SmartFoxServer.getInstance().getUserById(Integer.valueOf(Integer.parseInt(params[0])));
      if(client == null) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException("Trade has been canceled due to other player can\'t be found!");
      } else if(user.getName().equals(client.getName())) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(gold >= 0 && coins >= 0) {
         if(client.getUserId() != ((Integer)user.properties.get("tradetgt")).intValue()) {
            (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
            throw new RequestException(client.getName() + " has canceled the trade.");
         } else if(user.getUserId() != ((Integer)client.properties.get("tradetgt")).intValue()) {
            (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
            throw new RequestException(client.getName() + " has canceled the trade.");
         } else if(!((Boolean)user.properties.get("tradedeal")).booleanValue() && !((Boolean)user.properties.get("tradedeal")).booleanValue() && !((Boolean)client.properties.get("tradedeal")).booleanValue()) {
            QueryResult userResult1 = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
            if(userResult1.next() && userResult1.getInt("Coins") >= ((Integer)user.properties.get("tradecoins")).intValue() && userResult1.getInt("Gold") >= ((Integer)user.properties.get("tradegold")).intValue()) {
               user.properties.put("tradelock", Boolean.valueOf(true));
               user.properties.put("tradegold", Integer.valueOf(gold));
               user.properties.put("tradecoins", Integer.valueOf(coins));
               JSONObject di = new JSONObject();
               if(((Boolean)client.properties.get("tradelock")).booleanValue() && ((Boolean)user.properties.get("tradelock")).booleanValue()) {
                  di.put("Deal", Integer.valueOf(1));
               }

               di.put("cmd", "tradeLock");
               di.put("bitSuccess", Integer.valueOf(1));
               di.put("gold", (Integer)user.properties.get("tradegold"));
               di.put("coins", (Integer)user.properties.get("tradecoins"));
               world.send(di, client);
               di.put("gold", (Integer)client.properties.get("tradegold"));
               di.put("coins", (Integer)client.properties.get("tradegold"));
               di.put("Self", Integer.valueOf(1));
               world.send(di, user);
            }

         }
      } else {
         (new TradeCancel()).process(new String[]{Integer.toString(user.getUserId())}, client, world, world.zone.getRoom(client.getRoom()));
         SmartFoxServer.log.warning("Attempting to put negative gold/coins amount: " + user.properties.get("username"));
         world.users.log(user, "Packet Edit [TradeLock]", "Attempting to put negative gold/coins amount");
         world.users.kick(user);
      }
   }
}
