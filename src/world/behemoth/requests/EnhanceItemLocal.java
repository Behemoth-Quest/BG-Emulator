package world.behemoth.requests;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import net.sf.json.JSONObject;

public class EnhanceItemLocal implements IRequest {
   public EnhanceItemLocal() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject eil = new JSONObject();
      int userItemId = Integer.parseInt(params[0]);
      int enhancementItemId = Integer.parseInt(params[1]);
      Item item = (Item)world.items.get(Integer.valueOf(enhancementItemId));
      Enhancement enhance = (Enhancement)world.enhancements.get(Integer.valueOf(item.getEnhId()));
      if(world.users.turnInItem(user, enhancementItemId, 1)) {
         eil.put("EnhName", enhance.getName());
         eil.put("EnhPID", Integer.valueOf(enhance.getPatternId()));
         eil.put("EnhRng", Integer.valueOf(item.getRange()));
         eil.put("EnhRty", Integer.valueOf(enhance.getRarity()));
         eil.put("iCost", Integer.valueOf(item.getCost()));
         eil.put("bSuccess", Integer.valueOf(1));
         eil.put("EnhDPS", Integer.valueOf(enhance.getDPS()));
         eil.put("EnhLvl", Integer.valueOf(enhance.getLevel()));
         eil.put("EnhID", Integer.valueOf(enhancementItemId));
         eil.put("cmd", "enhanceItemLocal");
         eil.put("ItemID", Integer.valueOf(userItemId));
         world.db.jdbc.run("UPDATE users_items SET EnhID = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(item.getEnhId()), Integer.valueOf(userItemId), user.properties.get("dbId")});
         world.send(eil, user);
         JSONObject eqp = (JSONObject)user.properties.get("equipment");
         Iterator i$ = eqp.values().iterator();

         while(i$.hasNext()) {
            Object obj = i$.next();
            JSONObject equip = (JSONObject)obj;
            if(equip.getInt("ItemID") == userItemId) {
               world.users.sendStats(user);
               break;
            }
         }

         if(item.getEquipment().equals("ar") || item.getEquipment().equals("ba") || item.getEquipment().equals("he") || item.getEquipment().equals("Weapon")) {
            world.users.updateStats(user, enhance, item.getEquipment());
            world.users.sendStats(user);
         }
      } else {
         world.users.log(user, "Packet Edit [EnhanceItemLocal]", "Failed to pass turn in validation.");
      }

   }
}
