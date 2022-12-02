package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class GuildAccept implements IRequest {
   public GuildAccept() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[2].toLowerCase());
      if(client != null) {
         int guildId = Integer.parseInt(params[1]);
         int clientGuildID = ((Integer)client.properties.get("guildid")).intValue();
         if(clientGuildID != guildId) {
            world.users.kick(user);
            world.users.log(user, "Packet Edit [GuildAccept]", "Guild id does not match with requesting client");
         } else {
            Set requestedGuild = (Set)user.properties.get("requestedguild");
            if(requestedGuild.contains(Integer.valueOf(clientGuildID))) {
               requestedGuild.remove(Integer.valueOf(clientGuildID));
               world.db.jdbc.beginTransaction();

               try {
                  QueryResult object = world.db.jdbc.query("SELECT * FROM users_guilds WHERE UserID = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
                  if(object.next()) {
                     world.db.jdbc.run("UPDATE users_guilds SET GuildID = ?, Rank = 0 WHERE UserID = ?", new Object[]{Integer.valueOf(guildId), user.properties.get("dbId")});
                  } else {
                     world.db.jdbc.run("INSERT INTO users_guilds (`GuildID`, `UserID`, `Rank`) VALUES (?, ?, 0)", new Object[]{Integer.valueOf(guildId), user.properties.get("dbId")});
                  }

                  object.close();
               } catch (JdbcException var13) {
                  if(world.db.jdbc.isInTransaction()) {
                     world.db.jdbc.rollbackTransaction();
                  }

                  SmartFoxServer.log.severe("Error in guild accept transaction: " + var13.getMessage());
               } finally {
                  if(world.db.jdbc.isInTransaction()) {
                     world.db.jdbc.commitTransaction();
                  }

               }

               user.properties.put("guildid", Integer.valueOf(guildId));
               user.properties.put("guildrank", Integer.valueOf(0));
               user.properties.put("guildobj", world.users.getGuildObject(guildId));
               client.properties.put("guildobj", (JSONObject)user.properties.get("guildobj"));
               JSONObject object1 = new JSONObject();
               object1.put("cmd", params[0]);
               object1.put("unm", user.properties.get("username"));
               object1.put("guild", user.properties.get("guildobj"));
               world.sendToRoom(object1, user, room);
               world.sendGuildUpdate((JSONObject)user.properties.get("guildobj"));
            } else {
               world.users.kick(user);
               world.users.log(user, "Packet Edit [GuildAccept]", "Forcing guild accept");
            }

         }
      }
   }
}
