package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class GuildRename implements IRequest {
   public GuildRename() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(((Integer)user.properties.get("guildid")).intValue() <= 0) {
         throw new RequestException("You do not belong to a guild.", "server");
      } else if(((Integer)user.properties.get("guildrank")).intValue() < 3) {
         throw new RequestException("Invalid /rename request.");
      } else {
         world.db.jdbc.beginTransaction();

         try {
            String je = params[1].trim();
            int Coins = world.db.jdbc.queryForInt("SELECT Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
            if(je.length() > 25) {
               world.db.jdbc.rollbackTransaction();
               throw new RequestException("Guild names must be 25 characters or less.", "server");
            }

            if(je.length() <= 0) {
               world.db.jdbc.rollbackTransaction();
               throw new RequestException("Please specify a name for your guild.", "server");
            }

            if(!je.matches("[a-zA-Z ]+")) {
               throw new RequestException("Illegal characters is forbidden.", "warning");
            }

            if(Coins <= 0) {
               world.db.jdbc.rollbackTransaction();
               throw new RequestException("You do not have enough ACs.", "server");
            }

            world.db.jdbc.run("UPDATE users SET Coins = (Coins - 1000) WHERE id = ?", new Object[]{user.properties.get("dbId")});
            int rowcount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM guilds WHERE Name = ?", new Object[]{je});
            if(rowcount > 0) {
               world.db.jdbc.rollbackTransaction();
               throw new RequestException("Guild name is already in use.");
            }

            JSONObject guildData = (JSONObject)user.properties.get("guildobj");
            guildData.put("Name", params[1].trim());
            world.db.jdbc.run("UPDATE guilds SET Name = ? WHERE id = ?", new Object[]{params[1].trim(), user.properties.get("guildid")});
            world.send(new String[]{"gRename"}, user);
            world.sendGuildUpdate(guildData);
            world.sendToGuild(new String[]{"server", "Guild has been renamed to " + je + "."}, guildData);
         } catch (JdbcException var12) {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.rollbackTransaction();
            }

            SmartFoxServer.log.severe("Error in rename guild transaction: " + var12.getMessage());
         } finally {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.commitTransaction();
            }

         }

      }
   }
}
