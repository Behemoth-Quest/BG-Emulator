package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class GuildCreate implements IRequest {
   public GuildCreate() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      world.db.jdbc.beginTransaction();

      try {
         String je = params[1].trim();
         int rowcount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_guilds WHERE UserID = ?", new Object[]{user.properties.get("dbId")});
         if(rowcount > 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("You already have a guild!");
         }

         int Coins = world.db.jdbc.queryForInt("SELECT Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
         int UpgradeDays = ((Integer)user.properties.get("upgdays")).intValue();
         if(UpgradeDays < 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("Only members may create guilds.", "server");
         }

         if(je.length() > 25) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("Guild names must be 25 characters or less.", "server");
         }

         if(je.length() <= 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("Please specify a name for your guild.", "server");
         }

         if(!je.matches("[a-zA-Z ]+")) {
            throw new RequestException("Cannot use illegal characters.", "warning");
         }

         if(Coins <= 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("You do not have enough ACs.", "server");
         }

         world.db.jdbc.run("UPDATE users SET Coins = (Coins - 1000) WHERE id = ?", new Object[]{user.properties.get("dbId")});
         rowcount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM guilds WHERE Name = ?", new Object[]{je});
         if(rowcount > 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("Guild name is already in use.");
         }

         world.db.jdbc.run("INSERT INTO guilds (Name, MessageOfTheDay) VALUES (?, \'Hello World!\')", new Object[]{je});
         int guildId = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
         if(guildId <= 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("There was an error during guild creation!");
         }

         world.db.jdbc.run("INSERT INTO users_guilds (`GuildID`, `UserID`, `Rank`) VALUES (?, ?, 3)", new Object[]{Integer.valueOf(guildId), user.properties.get("dbId")});
         world.db.jdbc.commitTransaction();
         JSONObject guildData = world.users.getGuildObject(guildId);
         user.properties.put("guildid", Integer.valueOf(guildId));
         user.properties.put("guildobj", guildData);
         user.properties.put("guildrank", Integer.valueOf(3));
         JSONObject object = new JSONObject();
         object.put("cmd", params[0]);
         object.put("uid", Integer.valueOf(user.getUserId()));
         object.put("guild", guildData);
         world.sendToRoom(object, user, room);
         world.send(new String[]{"server", "Guild " + je + " successfuly created."}, user);
      } catch (JdbcException var15) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in create guild transaction: " + var15.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
