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

public class BankFromInventory implements IRequest {
   public BankFromInventory() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int userDbId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[0]);
      int charItemId = Integer.parseInt(params[1]);
      if(((Item)world.items.get(Integer.valueOf(itemId))).isTemporary()) {
         world.db.jdbc.run("UPDATE users SET Access = 0, PermamuteFlag = 1 WHERE id = ?", new Object[]{user.properties.get("dbId")});
         world.users.kick(user);
         world.users.log(user, "Packet Edit [BankToInventory]", "Attempting to transfer temporary items.");
      }

      int success;
      if(!((Item)world.items.get(Integer.valueOf(itemId))).isCoins()) {
         int je = world.users.getBankCount(user);
         success = ((Integer)user.properties.get("bankslots")).intValue();
         if(je >= success) {
            if(user.properties.get("language").equals("BR")) {
               throw new RequestException("Inventario cheia de banco!");
            }

            throw new RequestException("Bank Inventory Full!");
         }
      }

      world.db.jdbc.beginTransaction();

      try {
         QueryResult je1 = world.db.jdbc.query("SELECT ItemID, UserID FROM users_items WHERE id = ? FOR UPDATE", new Object[]{Integer.valueOf(charItemId)});
         if(je1.next()) {
            if(userDbId == je1.getInt("UserID") && itemId == je1.getInt("ItemID")) {
               success = world.db.jdbc.execute("UPDATE users_items SET Bank = 1 WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
               JSONObject bfi = new JSONObject();
               bfi.put("cmd", "bankFromInv");
               bfi.put("ItemID", Integer.valueOf(itemId));
               bfi.put("bSuccess", Integer.valueOf(success));
               if(success == 0) {
                  if(user.properties.get("language").equals("BR")) {
                     bfi.put("msg", "Ocorreu um erro durante a transferencia de seu item para banco.");
                  } else {
                     bfi.put("msg", "An error occured while transferring your item to bank.");
                  }
               }

               world.send(bfi, user);
            } else {
               world.users.kick(user);
               world.users.log(user, "Packet Edit [BankFromInventory]", "Attemping to put an item into the bank not in possession.");
            }
         }

         je1.close();
      } catch (JdbcException var14) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in bank from inventory transaction: " + var14.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
