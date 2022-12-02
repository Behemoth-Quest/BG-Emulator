package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class BankToInventory implements IRequest {
   public BankToInventory() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int userDbId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[0]);
      int charItemId = Integer.parseInt(params[1]);
      if(((Item)world.items.get(Integer.valueOf(itemId))).isTemporary()) {
         world.db.jdbc.run("UPDATE users SET Access = 0, PermamuteFlag = 0 WHERE id = ?", new Object[]{user.properties.get("dbId")});
         world.users.kick(user);
         world.users.log(user, "Packet Edit [BankToInventory]", "Attempting to transfer temporary items.");
      }

      int inventoryCount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment NOT IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{user.properties.get("dbId")});
      if(inventoryCount >= ((Integer)user.properties.get("bagslots")).intValue()) {
         if(user.properties.get("language").equals("BR")) {
            throw new RequestException("Inventario completo!");
         } else {
            throw new RequestException("Inventory Full!");
         }
      } else {
         world.db.jdbc.beginTransaction();

         try {
            QueryResult je = world.db.jdbc.query("SELECT ItemID, EnhID, UserID, Quantity FROM users_items WHERE id = ? FOR UPDATE", new Object[]{Integer.valueOf(charItemId)});
            if(je.next()) {
               if(userDbId == je.getInt("UserID") && itemId == je.getInt("ItemID")) {
                  world.db.jdbc.run("UPDATE users_items SET Bank = 0 WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
                  JSONObject bfi = new JSONObject();
                  bfi.put("cmd", "bankToInv");
                  bfi.put("ItemID", Integer.valueOf(itemId));
                  world.send(bfi, user);
               } else {
                  world.users.kick(user);
                  world.users.log(user, "Packet Edit [BankToInventory]", "Attemping to put an item into inventory from bank not in possession.");
               }
            }

            je.close();
         } catch (JdbcException var14) {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.rollbackTransaction();
            }

            SmartFoxServer.log.severe("Error in bank to inventory transaction: " + var14.getMessage());
         } finally {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.commitTransaction();
            }

         }

      }
   }
}
