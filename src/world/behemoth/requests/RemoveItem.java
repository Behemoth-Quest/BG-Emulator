package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class RemoveItem implements IRequest {
   public RemoveItem() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int userId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[0]);
      int charItemId = Integer.parseInt(params[1]);
      int quantityToRemove = params.length > 2?Integer.parseInt(params[2]):1;
      Item item = (Item)world.items.get(Integer.valueOf(itemId));
      if(quantityToRemove < 1) {
         world.users.log(user, "Packet Edit [RemoveItem]", "Quantity should not be lesser than 1");
      } else {
         QueryResult itemResult = world.db.jdbc.query("SELECT Quantity, UserID, ItemID FROM users_items WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
         if(itemResult.next()) {
            int quantity = itemResult.getInt("Quantity");
            int userDbId = itemResult.getInt("UserID");
            int itemDbId = itemResult.getInt("ItemID");
            itemResult.close();
            if(userDbId == userId && itemDbId == itemId) {
               if(item.getStack() > 1) {
                  int delete = quantity - quantityToRemove;
                  if(delete > 0) {
                     world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE id = ?", new Object[]{Integer.valueOf(delete), Integer.valueOf(charItemId)});
                  } else {
                     world.db.jdbc.run("DELETE FROM users_items WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
                  }
               } else {
                  world.db.jdbc.run("DELETE FROM users_items WHERE id = ?", new Object[]{Integer.valueOf(charItemId)});
               }

               JSONObject delete1 = new JSONObject();
               delete1.put("cmd", "removeItem");
               delete1.put("bitSuccess", Integer.valueOf(1));
               delete1.put("CharItemID", Integer.valueOf(charItemId));
               if(quantityToRemove > 1) {
                  delete1.put("iQty", Integer.valueOf(quantityToRemove));
               }

               world.send(delete1, user);
            } else {
               world.users.log(user, "Packet Edit [RemoveItem]", "Attempted to delete an item not in possession");
            }
         }

         itemResult.close();
      }
   }
}
