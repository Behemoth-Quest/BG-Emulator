package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Map;
import java.util.Queue;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class GetDrop implements IRequest
{

   @Override
   public void process(String[] params, User user, World world, Room room) throws RequestException
   {
      Map drops = (Map) user.properties.get("drops");
      int itemId = Integer.parseInt(params[0]);
      Item item = (Item) world.items.get(Integer.valueOf(itemId));

      if (!drops.containsKey(Integer.valueOf(itemId))) {
         world.users.log(user, "Packet Edit [GetDrop]", "Attemped get undropped item: " + item.getName());
      } else {
//         Queue quantities = (Queue)drops.get(Integer.valueOf(itemId));
         int quantityToDrop = (Integer) drops.get(itemId);

//         if (quantities != null) {
//            int quantityToDrop = ((Integer)quantities.poll()).intValue();
            JSONObject gd = new JSONObject();
            gd.put("cmd", "getDrop");
            gd.put("ItemID", Integer.valueOf(itemId));
            gd.put("bSuccess", "0");
            world.db.jdbc.beginTransaction();

            try {
               QueryResult je = world.db.jdbc.query("SELECT id, Bank FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
               int charItemId;
               boolean inBank = false;
               int qtyNow = 0;

               if (je.next()) {
                  charItemId = je.getInt("id");
                  inBank = je.getInt("Bank") == 1 ? true : false;
                  je.close();

                  if (item.getStack() > 1) {
                     int quantity = world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
                     qtyNow = quantity;
                     if (quantity >= item.getStack()) {
                        world.db.jdbc.rollbackTransaction();
                        world.send(gd, user);
                        return;
                     }

                     world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(quantity + quantityToDrop), Integer.valueOf(itemId), user.properties.get("dbId")});
                     gd.put("iQtyNow", Integer.valueOf(qtyNow + quantityToDrop));
                     if (inBank)
                        gd.put("showDrop", "1");
                  } else if (item.getStack() == 1) {
                     world.db.jdbc.rollbackTransaction();
                     world.send(gd, user);
                     return;
                  }
               } else {
                  world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, NOW())", new Object[]{user.properties.get("dbId"), Integer.valueOf(itemId), Integer.valueOf(item.getEnhId()), Integer.valueOf(quantityToDrop)});
                  charItemId = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
               }

               je.close();
               if (charItemId > 0) {
                  gd.put("CharItemID", Integer.valueOf(charItemId));
//                  gd.put("bBank", Boolean.valueOf(false));
                  gd.put("bBank", inBank ? 1 : 0);
                  gd.put("iQty", Integer.valueOf(quantityToDrop));
                  gd.put("bSuccess", "1");

                  if (!item.getReqQuests().isEmpty()) {
                     gd.put("showDrop", "1");
                  }

                  world.send(gd, user);
                  drops.remove(itemId);
//                  if(quantities.isEmpty()) {
//                     drops.remove(Integer.valueOf(itemId));
//                  }
               } else {
                  world.db.jdbc.rollbackTransaction();
               }
            } catch (JdbcException var17) {
               if (world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.rollbackTransaction();
               }
               SmartFoxServer.log.severe("Error in get drop transaction: " + var17.getMessage());
            } finally {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.commitTransaction();
               }
            }
//         }
      }
   }
}
