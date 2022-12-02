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

public class GuildSellItem implements IRequest {
   public GuildSellItem() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int itemId = Integer.parseInt(params[1]);
      int shopId = Integer.parseInt(params[2]);
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int userId = ((Integer)user.properties.get("dbId")).intValue();
      world.db.jdbc.beginTransaction();

      try {
         int je = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM guilds_inventory WHERE GuildID = ? AND ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(guildId), Integer.valueOf(itemId), Integer.valueOf(userId)});
         if(je > 0) {
            QueryResult userResult = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{Integer.valueOf(userId)});
            userResult.setAutoClose(true);
            if(userResult.next()) {
               int coins = userResult.getInt("Coins");
               int gold = userResult.getInt("Gold");
               userResult.close();
               Item item = (Item)world.items.get(Integer.valueOf(itemId));
               JSONObject guildhall = new JSONObject();
               guildhall.put("cmd", "guildhall");
               guildhall.put("gCmd", "removeItem");
               guildhall.put("ItemID", Integer.valueOf(itemId));
               guildhall.put("bitSuccess", Integer.valueOf(1));
               world.send(guildhall, user);
               int coinPrice;
               int totalCoins;
               if(!item.isCoins()) {
                  coinPrice = item.getCost() / 5 / 2;
                  totalCoins = gold + coinPrice;
                  guildhall.put("iCost", Integer.valueOf(coinPrice));
                  world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id = ?", new Object[]{Integer.valueOf(totalCoins), Integer.valueOf(userId)});
               } else {
                  coinPrice = item.getCost() / 4;
                  totalCoins = coins + coinPrice;
                  guildhall.put("iCost", Integer.valueOf(coinPrice));
                  world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id = ?", new Object[]{Integer.valueOf(totalCoins), Integer.valueOf(userId)});
               }

               world.db.jdbc.run("DELETE FROM guilds_inventory WHERE GuildID = ? AND ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(guildId), Integer.valueOf(itemId), Integer.valueOf(userId)});
               world.send(guildhall, user);
            }

            userResult.close();
         } else {
            world.users.log(user, "Packet Edit [GuildSellItem]", "Attempted to sell an item not in possession");
         }
      } catch (JdbcException var20) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in guild sell item transaction: " + var20.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
