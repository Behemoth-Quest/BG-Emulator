package world.behemoth.requests;

import world.behemoth.db.objects.House;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.sql.Date;
import jdbchelper.NoResultException;
import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HouseJoin implements IRequest {
   public HouseJoin() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String username = params[0].toLowerCase();
      QueryResult result = world.db.jdbc.query("SELECT id, Name, HouseInfo FROM users WHERE Name = ?", new Object[]{username});
      result.setAutoClose(true);
      if(result.next()) {
         int userDbId = result.getInt("id");

         try {
            int nre = world.db.jdbc.queryForInt("SELECT ItemID FROM users_items LEFT JOIN items ON items.id = ItemID WHERE Equipment = \'ho\' AND Equipped = 1 AND UserID = ?", new Object[]{Integer.valueOf(userDbId)});
            if(!world.areas.containsKey("house-" + userDbId)) {
               House roomToJoin = new House(this.getHouseItems(world, userDbId), result.getString("HouseInfo"), result.getString("Name").toLowerCase(), userDbId);
               roomToJoin.setFile(((Item)world.items.get(Integer.valueOf(nre))).getFile());
               world.areas.put("house-" + userDbId, roomToJoin);
            }

            Room roomToJoin1 = world.zone.getRoomByName("house-" + userDbId);
            if(roomToJoin1 != null) {
               world.rooms.joinRoom(roomToJoin1, user);
            }

            roomToJoin1 = world.rooms.createRoom("house-" + userDbId);
            world.rooms.joinRoom(roomToJoin1, user);
         } catch (NoResultException var10) {
            if(result.getString("Name").toLowerCase().equals(user.getName())) {
               world.rooms.basicRoomJoin(user, "buyhouse");
            } else {
               world.send(new String[]{"warning", "This player does not own a house!"}, user);
            }
         }

         result.close();
      } else {
         result.close();
         throw new RequestException("Player \"" + username + "\" could not be found!");
      }
   }

   public JSONArray getHouseItems(World world, int userDbId) {
      JSONArray items = new JSONArray();

      QueryResult result;
      JSONObject item;
      for(result = world.db.jdbc.query("SELECT users_items.id, ItemID, Equipped, users_items.Quantity, DatePurchased FROM users_items LEFT JOIN items ON items.id = ItemID WHERE Equipment IN (\'ho\',\'hi\') AND UserID = ?", new Object[]{Integer.valueOf(userDbId)}); result.next(); items.add(item)) {
         int itemId = result.getInt("ItemID");
         Item itemObj = (Item)world.items.get(Integer.valueOf(itemId));
         item = Item.getItemJSON(itemObj);
         item.put("CharItemID", Integer.valueOf(result.getInt("id")));
         item.put("bEquip", result.getString("Equipped"));
         item.put("iQty", result.getString("Quantity"));
         if(itemObj.isCoins()) {
            Date startDate = result.getDate("DatePurchased");
            java.util.Date endDate = new java.util.Date();
            long diff = endDate.getTime() - startDate.getTime();
            long diffHours = diff / 3600000L;
            item.put("iHrs", Long.valueOf(diffHours));
            item.put("dPurchase", result.getString("DatePurchased").replaceAll(" ", "T"));
         }
      }

      result.close();
      return items;
   }
}
