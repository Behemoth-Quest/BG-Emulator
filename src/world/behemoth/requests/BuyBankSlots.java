package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class BuyBankSlots implements IRequest {
   public BuyBankSlots() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int slotsToPurchase = Integer.parseInt(params[0]);
      if(slotsToPurchase <= 0) {
         SmartFoxServer.log.warning("Kicking for Invalid slotsToPurchase input: " + user.properties.get("username"));
         world.users.log(user, "Packet Edit [BuyBankSlots]", "Invalid slotsToPurchase input.");
         world.db.jdbc.run("UPDATE users SET Access = 0 WHERE id = ?", new Object[]{user.properties.get("dbId")});
         world.users.kick(user);
      } else {
         int bankSlots = ((Integer)user.properties.get("bankslots")).intValue() + slotsToPurchase;
         int totalCost = slotsToPurchase * 200;
         if(bankSlots > 500) {
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

               world.db.jdbc.run("UPDATE users SET SlotsBank = ?, Coins = ? WHERE id = ?", new Object[]{Integer.valueOf(bankSlots), Integer.valueOf(coinsLeft), user.properties.get("dbId")});
               user.properties.put("bankslots", Integer.valueOf(bankSlots));
               JSONObject object = new JSONObject();
               object.put("cmd", "buyBankSlots");
               object.put("iSlots", Integer.valueOf(slotsToPurchase));
               object.put("bitSuccess", "1");
               world.send(object, user);
            } catch (JdbcException var14) {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.rollbackTransaction();
               }

               SmartFoxServer.log.severe("Error in buy bank slots transaction: " + var14.getMessage());
            } finally {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.commitTransaction();
               }

            }

         }
      }
   }
}
