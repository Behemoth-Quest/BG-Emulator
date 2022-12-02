package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class GuildRemove implements IRequest {
   public GuildRemove() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int userRank = ((Integer)user.properties.get("guildrank")).intValue();
      QueryResult result = world.db.jdbc.query("SELECT users.id, users.Name, users_guilds.GuildID, users_guilds.Rank FROM users LEFT JOIN users_guilds ON UserID = id WHERE Name = ?", new Object[]{params[1]});
      if(!result.next()) {
         result.close();
         throw new RequestException("Player \"" + params[1].toLowerCase() + "\" could not be found.");
      } else {
         String username = result.getString("Name");
         int clientId = result.getInt("id");
         int clientRank = result.getInt("Rank");
         int clientGuildID = result.getInt("GuildID");
         result.close();
         if(clientGuildID <= 0) {
            throw new RequestException(username + " does belong to a guild!");
         } else if(clientGuildID != guildId) {
            throw new RequestException(username + " is not in your guild!");
         } else if((clientRank > userRank || userRank < 2) && !user.getName().equals(username.toLowerCase())) {
            throw new RequestException("Invalid /gr request.");
         } else {
            int remainingMembers = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_guilds WHERE GuildID = ?", new Object[]{Integer.valueOf(guildId)});
            if(clientRank == 3 && remainingMembers > 1) {
               throw new RequestException("Invalid /gr request.");
            } else {
               world.db.jdbc.run("DELETE FROM users_guilds WHERE GuildID = ? AND UserID = ?", new Object[]{Integer.valueOf(guildId), Integer.valueOf(clientId)});
               if(clientRank == 3 && remainingMembers == 1) {
                  world.db.jdbc.run("DELETE FROM guilds WHERE id = ?", new Object[]{Integer.valueOf(guildId)});
               }

               JSONObject guildObj = world.users.getGuildObject(guildId);
               world.sendGuildUpdate(guildObj);
               if(user.getName().equals(username.toLowerCase())) {
                  world.sendToGuild(new String[]{"server", username.toLowerCase() + " has left the guild."}, guildObj);
               } else {
                  world.sendToGuild(new String[]{"server", username.toLowerCase() + " has been kicked."}, guildObj);
               }

               User client = world.zone.getUserByName(username.toLowerCase());
               if(client != null) {
                  JSONObject object = new JSONObject();
                  object.put("cmd", params[0]);
                  object.put("unm", username);
                  object.put("guild", guildObj);
                  client.properties.put("guildid", Integer.valueOf(0));
                  client.properties.put("guildrank", Integer.valueOf(0));
                  client.properties.put("guildobj", new JSONObject());
                  world.send(object, world.zone.getRoom(client.getRoom()).getChannellList());
               }

               result.close();
            }
         }
      }
   }
}
