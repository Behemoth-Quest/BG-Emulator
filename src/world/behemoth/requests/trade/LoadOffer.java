package world.behemoth.requests.trade;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.Users;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LoadOffer implements IRequest {
   public LoadOffer() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      List types = Arrays.asList(params);
      JSONObject lb = new JSONObject();
      JSONArray items = new JSONArray();
      Map offers = (Map)user.properties.get(Users.TRADE_OFFERS);
      Iterator i$ = offers.entrySet().iterator();

      while(i$.hasNext()) {
         Entry entry = (Entry)i$.next();
         int itemId = ((Integer)entry.getKey()).intValue();
         int quantity = ((Integer)entry.getValue()).intValue();
         Item item = (Item)world.items.get(Integer.valueOf(itemId));
         if(item != null) {
            QueryResult result = world.db.getJdbc().query("SELECT id, EnhID FROM users_items WHERE  UserID = ? AND ItemID = ?", new Object[]{user.properties.get(Users.DATABASE_ID), Integer.valueOf(item.getId())});
            if(result.next()) {
               int enhId = result.getInt("EnhID");
               Enhancement enhancement = (Enhancement)world.enhancements.get(Integer.valueOf(enhId));
               if(types.contains(item.getType())) {
                  JSONObject itemObj = Item.getItemJSON(item, enhancement);
                  itemObj.element("bBank", "1");
                  itemObj.element("CharItemID", result.getString("id"));
                  itemObj.element("iQty", quantity);
                  itemObj.element("iReqCP", item.getReqClassPoints());
                  itemObj.element("iReqRep", item.getReqReputation());
                  itemObj.element("FactionID", item.getFactionId());
                  itemObj.element("sFaction", world.factions.get(Integer.valueOf(item.getFactionId())));
                  items.add(itemObj);
               }
            }

            result.close();
         }
      }

      lb.element("cmd", "loadOffer");
      lb.element("itemsA", items);
      lb.element("bitSuccess", 1);
      world.send(lb, user);
   }
}
