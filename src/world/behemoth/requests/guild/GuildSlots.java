package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class GuildSlots implements IRequest {
   public GuildSlots() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int slotsToPurchase = Integer.parseInt(params[1]);
      if(slotsToPurchase <= 0) {
         SmartFoxServer.log.warning("Kicking for Invalid slotsToPurchase input: " + user.properties.get("username"));
         world.users.log(user, "Packet Edit [GuildSlots]", "Invalid slotsToPurchase input.");
         world.db.jdbc.run("UPDATE users SET Access = 0 WHERE id = ?", new Object[]{user.properties.get("dbId")});
         world.users.kick(user);
      } else {
         if(((Integer)user.properties.get("guildid")).intValue() > 0 && ((Integer)user.properties.get("guildrank")).intValue() == 3) {
            world.db.jdbc.beginTransaction();

            try {
               int je = world.db.jdbc.queryForInt("SELECT MaxMembers FROM guilds WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("guildid")}) + slotsToPurchase;
               int totalCost = slotsToPurchase * 200;
               if(je > 50) {
                  throw new RequestException("You have already reached the maximum amount of guild member slots.");
               }

               int coins = world.db.jdbc.queryForInt("SELECT Coins FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
               int coinsLeft = coins - totalCost;
               if(coinsLeft < 0) {
                  throw new RequestException("You don\'t have enough coins!");
               }

               world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id = ?", new Object[]{Integer.valueOf(coinsLeft), user.properties.get("dbId")});
               world.db.jdbc.run("UPDATE guilds SET MaxMembers = ? WHERE id = ?", new Object[]{Integer.valueOf(je), user.properties.get("guildid")});
               world.db.jdbc.commitTransaction();
               user.properties.put("guildobj", world.users.getGuildObject(((Integer)user.properties.get("guildid")).intValue()));
               world.sendGuildUpdate((JSONObject)user.properties.get("guildobj"));
               world.send(new String[]{"buyGSlots", params[1]}, user);
            } catch (JdbcException var13) {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.rollbackTransaction();
               }

               SmartFoxServer.log.severe("Error in guild slots transaction: " + var13.getMessage());
            } finally {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.commitTransaction();
               }

            }
         }

      }
   }
}
