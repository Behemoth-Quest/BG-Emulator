package world.behemoth.requests;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class EnhanceItemShop implements IRequest {
   public EnhanceItemShop() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject eis = new JSONObject();
      int userItemId = Integer.parseInt(params[0]);
      int enhancementItemId = Integer.parseInt(params[1]);
      Item item = (Item)world.items.get(Integer.valueOf(enhancementItemId));
      Enhancement enhance = (Enhancement)world.enhancements.get(Integer.valueOf(item.getEnhId()));
      if(!item.requirements.isEmpty()) {
         world.users.kick(user);
         world.users.log(user, "Packet Edit [EnhanceItemShop]", "Trying to use an enhancement that can only be use locally.");
      } else {
         world.db.jdbc.beginTransaction();

         try {
            QueryResult eqp = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
            if(eqp.next()) {
               int i$ = eqp.getInt("Gold");
               int obj = eqp.getInt("Coins");
               eqp.close();
               int equip = item.getCost();
               int deltaGold;
               if(item.isCoins() && equip <= obj) {
                  deltaGold = obj - item.getCost();
                  world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id=?", new Object[]{Integer.valueOf(deltaGold), user.properties.get("dbId")});
               } else {
                  if(equip > i$) {
                     world.db.jdbc.rollbackTransaction();
                     world.users.log(user, "Packet Edit [EnhanceItemShop]", "Sent an enhancement request while lacking funds.");
                     return;
                  }

                  deltaGold = i$ - item.getCost();
                  world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id=?", new Object[]{Integer.valueOf(deltaGold), user.properties.get("dbId")});
               }
            }

            eqp.close();
         } catch (JdbcException var18) {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.rollbackTransaction();
            }

            SmartFoxServer.log.severe("Error in enhance item transaction: " + var18.getMessage());
         } finally {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.commitTransaction();
            }

         }

         eis.put("EnhName", enhance.getName());
         eis.put("EnhPID", Integer.valueOf(enhance.getPatternId()));
         eis.put("EnhRng", Integer.valueOf(item.getRange()));
         eis.put("EnhRty", Integer.valueOf(enhance.getRarity()));
         eis.put("iCost", Integer.valueOf(item.getCost()));
         eis.put("bSuccess", Integer.valueOf(1));
         eis.put("EnhDPS", Integer.valueOf(enhance.getDPS()));
         eis.put("EnhLvl", Integer.valueOf(enhance.getLevel()));
         eis.put("EnhID", Integer.valueOf(enhancementItemId));
         eis.put("cmd", "enhanceItemShop");
         eis.put("ItemID", Integer.valueOf(userItemId));
         world.db.jdbc.run("UPDATE users_items SET EnhID = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(item.getEnhId()), Integer.valueOf(userItemId), user.properties.get("dbId")});
         world.send(eis, user);
         JSONObject eqp1 = (JSONObject)user.properties.get("equipment");
         Iterator i$1 = eqp1.values().iterator();

         while(i$1.hasNext()) {
            Object obj1 = i$1.next();
            JSONObject equip1 = (JSONObject)obj1;
            if(equip1.getInt("ItemID") == userItemId) {
               world.users.sendStats(user);
               break;
            }
         }

         if(item.getEquipment().equals("ar") || item.getEquipment().equals("ba") || item.getEquipment().equals("he") || item.getEquipment().equals("Weapon")) {
            world.users.updateStats(user, enhance, item.getEquipment());
            world.users.sendStats(user);
         }

      }
   }
}
