package world.behemoth.requests.trade;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Map;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class TradeToInventory implements IRequest {
   public TradeToInventory() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int userDbId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[0]);
      int charItemId = Integer.parseInt(params[1]);
      User client = SmartFoxServer.getInstance().getUserById(Integer.valueOf(Integer.parseInt(params[2])));
      if(client == null) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException("Trade has been canceled due to other player can\'t be found!");
      } else if(client.getUserId() != ((Integer)user.properties.get("tradetgt")).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getUserId() != ((Integer)client.properties.get("tradetgt")).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getName().equals(client.getName())) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else {
         world.db.jdbc.beginTransaction();

         try {
            QueryResult je = world.db.jdbc.query("SELECT ItemID, EnhID, UserID, Quantity FROM users_items WHERE id = ? FOR UPDATE", new Object[]{Integer.valueOf(charItemId)});
            if(je.next() && userDbId == je.getInt("UserID") && itemId == je.getInt("ItemID")) {
               je.close();
               Map offers = (Map)user.properties.get("offer");
               if(offers.get(Integer.valueOf(itemId)) != null) {
                  world.users.removeOfferItem(user, itemId, ((Integer)offers.get(Integer.valueOf(itemId))).intValue());
               }

               JSONObject tfi = new JSONObject();
               tfi.element("cmd", "tradeToInv");
               tfi.element("ItemID", itemId);
               tfi.element("Type", 1);
               world.send(tfi, user);
               tfi.element("Type", 2);
               world.send(tfi, client);
               JSONObject tr;
               if(((Boolean)user.properties.get("tradelock")).booleanValue()) {
                  user.properties.put("tradelock", Boolean.valueOf(false));
                  user.properties.put("tradedeal", Boolean.valueOf(false));
                  tr = new JSONObject();
                  tr.element("cmd", "tradeUnlock");
                  tr.element("bitSuccess", 1);
                  world.send(tr, user);
               } else if(((Boolean)client.properties.get("tradelock")).booleanValue()) {
                  client.properties.put("tradelock", Boolean.valueOf(false));
                  client.properties.put("tradedeal", Boolean.valueOf(false));
                  tr = new JSONObject();
                  tr.element("cmd", "tradeUnlock");
                  tr.element("bitSuccess", 1);
                  world.send(tr, client);
               }
            }
         } catch (JdbcException var16) {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.rollbackTransaction();
            }

            SmartFoxServer.log.severe("Error in trade to inventory transaction: " + var16.getMessage());
         } finally {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.commitTransaction();
            }

         }

      }
   }
}
