package world.behemoth.requests.guild;

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

public class GuildBuyItem implements IRequest {
   public GuildBuyItem() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int userId = ((Integer)user.properties.get("dbId")).intValue();
      int itemId = Integer.parseInt(params[1]);
      world.db.jdbc.beginTransaction();

      try {
         Item je = (Item)world.items.get(Integer.valueOf(itemId));
         int cost = je.getCost();
         QueryResult userResult = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
         userResult.setAutoClose(true);
         if(userResult.next()) {
            int coins = userResult.getInt("Coins");
            int gold = userResult.getInt("Gold");
            userResult.close();
            boolean valid = je.isCoins() && cost <= coins?true:cost <= gold;
            if(valid) {
               world.db.jdbc.run("INSERT INTO guilds_inventory (GuildID, ItemID, UserID) VALUES (? ,? ,?)", new Object[]{Integer.valueOf(guildId), Integer.valueOf(itemId), Integer.valueOf(userId)});
               int guildhall;
               if(!je.isCoins()) {
                  guildhall = gold - cost;
                  world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id=?", new Object[]{Integer.valueOf(guildhall), user.properties.get("dbId")});
               } else {
                  guildhall = coins - cost;
                  world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id=?", new Object[]{Integer.valueOf(guildhall), user.properties.get("dbId")});
               }

               JSONObject guildhall1 = new JSONObject();
               guildhall1.put("cmd", "guildhall");
               guildhall1.put("gCmd", "buyItem");
               guildhall1.put("Item", Item.getItemJSON(je));
               guildhall1.put("bitSuccess", Integer.valueOf(1));
               world.send(guildhall1, user);
            }
         }

         userResult.close();
      } catch (JdbcException var18) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in guild item buy transaction: " + var18.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
