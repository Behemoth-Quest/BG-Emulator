package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.sql.Date;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class SellItem implements IRequest {
   public SellItem() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int userId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[0]);
      int charItemId = Integer.parseInt(params[2]);
      JSONObject sell = new JSONObject();
      sell.put("cmd", "sellItem");
      world.db.jdbc.beginTransaction();

      try {
         QueryResult je = world.db.jdbc.query("SELECT Quantity, DatePurchased, ItemID, UserID FROM users_items WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
         if(je.next()) {
            int quantity = je.getInt("Quantity");
            int userDbId = je.getInt("UserID");
            int itemDbId = je.getInt("ItemID");
            Date purchase = je.getDate("DatePurchased");
            je.close();
            Item item = (Item)world.items.get(Integer.valueOf(itemId));
            if(userDbId == userId && itemDbId == itemId) {
               QueryResult userResult = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{Integer.valueOf(userId)});
               if(userResult.next()) {
                  int coins = userResult.getInt("Coins");
                  int gold = userResult.getInt("Gold");
                  userResult.close();
                  java.util.Date endDate = new java.util.Date();
                  long diff = endDate.getTime() - purchase.getTime();
                  long diffHours = diff / 3600000L;
                  int quantityLeft;
                  int totalCoins;
                  if(!item.isCoins()) {
                     quantityLeft = item.getCost() / 5 / 2;
                     totalCoins = gold + quantityLeft;
                     sell.put("intAmount", Integer.valueOf(quantityLeft));
                     world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id = ?", new Object[]{Integer.valueOf(totalCoins), Integer.valueOf(userId)});
                  } else {
                     quantityLeft = diffHours < 24L?item.getCost() / 10 * 9:item.getCost() / 4;
                     totalCoins = coins + quantityLeft;
                     sell.put("intAmount", Integer.valueOf(quantityLeft));
                     world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id = ?", new Object[]{Integer.valueOf(totalCoins), Integer.valueOf(userId)});
                  }

                  if(item.getStack() > 1) {
                     quantityLeft = quantity - 1;
                     if(quantityLeft > 0) {
                        world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE id = ?", new Object[]{Integer.valueOf(quantityLeft), Integer.valueOf(charItemId)});
                     } else {
                        world.db.jdbc.run("DELETE FROM users_items WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
                     }
                  } else {
                     world.db.jdbc.run("DELETE FROM users_items WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
                  }

                  sell.put("CharItemID", Integer.valueOf(charItemId));
                  sell.put("bCoins", Integer.valueOf(item.isCoins()?1:0));
                  world.send(sell, user);
               }

               userResult.close();
            } else {
               world.users.log(user, "Packet Edit [SellItem]", "Attempted to sell an item not in possession");
            }
         }

         je.close();
      } catch (JdbcException var29) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in sell item transaction: " + var29.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
