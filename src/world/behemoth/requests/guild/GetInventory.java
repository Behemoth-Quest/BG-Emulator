package world.behemoth.requests.guild;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class GetInventory implements IRequest {
   public GetInventory() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      JSONObject guildinv = new JSONObject();
      JSONObject guildInventory = new JSONObject();
      QueryResult result = world.db.jdbc.query("SELECT guilds_inventory.*, users.Name FROM guilds_inventory LEFT JOIN users ON UserId = users.id WHERE GuildID = ?", new Object[]{Integer.valueOf(guildId)});

      while(result.next()) {
         Item item = (Item)world.items.get(Integer.valueOf(result.getInt("ItemID")));
         String username = result.getString("Name");
         JSONObject items;
         if(guildInventory.containsKey(username)) {
            items = guildInventory.getJSONObject(username);
            items.put(String.valueOf(item.getId()), Item.getItemJSON(item));
         } else {
            items = new JSONObject();
            items.put(String.valueOf(item.getId()), Item.getItemJSON(item));
            guildInventory.put(username, items);
         }
      }

      result.close();
      guildinv.put("cmd", "guildinv");
      guildinv.put("guildInventory", guildInventory);
      world.send(guildinv, user);
   }
}
