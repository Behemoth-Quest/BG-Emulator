package world.behemoth.requests;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Arrays;
import java.util.List;
import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LoadBank implements IRequest {
   public LoadBank() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      List types = Arrays.asList(params);
      JSONObject lb = new JSONObject();
      JSONArray items = new JSONArray();
      QueryResult result = world.db.jdbc.query("SELECT * FROM users_items WHERE Bank = 1 AND UserID = ?", new Object[]{user.properties.get("dbId")});

      while(result.next()) {
         int itemId = result.getInt("ItemID");
         int enhId = result.getInt("EnhID");
         Item item = (Item)world.items.get(Integer.valueOf(itemId));
         Enhancement enhancement = (Enhancement)world.enhancements.get(Integer.valueOf(enhId));
         if(types.contains(item.getType())) {
            JSONObject itemObj = Item.getItemJSON(item, enhancement);
            itemObj.put("bBank", "1");
            itemObj.put("CharItemID", result.getString("id"));
            itemObj.put("iQty", result.getString("quantity"));
            items.add(itemObj);
         }
      }

      result.close();
      lb.put("cmd", "loadBank");
      lb.put("items", items);
      lb.put("bitSuccess", Integer.valueOf(1));
      world.send(lb, user);
   }
}
