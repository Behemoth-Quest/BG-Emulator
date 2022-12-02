package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class BuyHouseSlots implements IRequest {
   public BuyHouseSlots() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int slotsToPurchase = Integer.parseInt(params[0]);
      if(slotsToPurchase <= 0) {
         SmartFoxServer.log.warning("Kicking for Invalid slotsToPurchase input: " + user.properties.get("username"));
         world.users.log(user, "Packet Edit [BuyHouseSlots]", "Invalid slotsToPurchase input.");
         world.db.jdbc.run("UPDATE users SET Access = 0 WHERE id = ?", new Object[]{user.properties.get("dbId")});
         world.users.kick(user);
      } else {
         int houseSlots = ((Integer)user.properties.get("houseslots")).intValue() + slotsToPurchase;
         int totalCost = slotsToPurchase * 200;
         if(houseSlots > 100) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException("Voc\u00ea j\u00e1 comprou o montante m\u00e1ximo!");
            } else {
               throw new RequestException("You have already purchased the maximum amount!");
            }
         } else {
            world.db.jdbc.beginTransaction();

            try {
               int je = world.db.jdbc.queryForInt("SELECT Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
               int coinsLeft = je - totalCost;
               if(coinsLeft < 0) {
                  if(user.properties.get("language").equals("BR")) {
                     throw new RequestException("Voc\u00ea n\u00e3o tem moedas suficientes!");
                  }

                  throw new RequestException("You don\'t have enough coins!");
               }

               world.db.jdbc.run("UPDATE users SET SlotsHouse = ?, Coins = ? WHERE id = ?", new Object[]{Integer.valueOf(houseSlots), Integer.valueOf(coinsLeft), user.properties.get("dbId")});
               user.properties.put("houseslots", Integer.valueOf(houseSlots));
               JSONObject object = new JSONObject();
               object.put("cmd", "buyHouseSlots");
               object.put("iSlots", Integer.valueOf(slotsToPurchase));
               object.put("bitSuccess", "1");
               world.send(object, user);
            } catch (JdbcException var14) {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.rollbackTransaction();
               }

               SmartFoxServer.log.severe("Error in buy house slots transaction: " + var14.getMessage());
            } finally {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.commitTransaction();
               }

            }

         }
      }
   }
}
