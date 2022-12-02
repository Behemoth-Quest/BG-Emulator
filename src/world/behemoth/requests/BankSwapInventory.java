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

public class BankSwapInventory implements IRequest {
   public BankSwapInventory() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int userDbId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[0]);
      int charItemId = Integer.parseInt(params[1]);
      int itemId2 = Integer.parseInt(params[2]);
      int charItemId2 = Integer.parseInt(params[3]);
      if(((Item)world.items.get(Integer.valueOf(itemId))).isTemporary() || ((Item)world.items.get(Integer.valueOf(itemId2))).isTemporary()) {
         world.db.jdbc.run("UPDATE users SET Access = 0, PermamuteFlag = 0 WHERE id = ?", new Object[]{user.properties.get("dbId")});
         world.users.kick(user);
         world.users.log(user, "Packet Edit [BankToInventory]", "Attempting to transfer temporary items.");
      }

      if(!((Item)world.items.get(Integer.valueOf(itemId))).isCoins()) {
         int je = world.users.getBankCount(user);
         int item1 = ((Integer)user.properties.get("bankslots")).intValue();
         if(je >= item1) {
            throw new RequestException("Bank Inventory Full!");
         }
      }

      world.db.jdbc.beginTransaction();

      try {
         QueryResult je1 = world.db.jdbc.query("SELECT ItemID, UserID, EnhID, Quantity FROM users_items WHERE id IN (?, ?) FOR UPDATE", new Object[]{Integer.valueOf(charItemId), Integer.valueOf(charItemId2)});
         boolean item11 = false;
         boolean item2 = false;

         while(true) {
            while(je1.next()) {
               if(userDbId == je1.getInt("UserID") && itemId == je1.getInt("ItemID")) {
                  item11 = true;
               } else if(userDbId == je1.getInt("UserID") && itemId2 == je1.getInt("ItemID")) {
                  item2 = true;
               }
            }

            je1.close();
            if(item11 && item2) {
               world.db.jdbc.run("UPDATE users_items SET Bank = 1 WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
               world.db.jdbc.run("UPDATE users_items SET Bank = 0 WHERE id = ?", new Object[]{Integer.valueOf(charItemId2)});
               JSONObject bsi = new JSONObject();
               bsi.put("cmd", "bankSwapInv");
               bsi.put("invItemID", Integer.valueOf(itemId));
               bsi.put("bankItemID", Integer.valueOf(itemId2));
               world.send(bsi, user);
            } else {
               world.users.kick(user);
               world.users.log(user, "Packet Edit [BankSwapInventory]", "Attemping to swap items not in possession.");
            }
            break;
         }
      } catch (JdbcException var17) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error bank swap inventory transaction: " + var17.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
