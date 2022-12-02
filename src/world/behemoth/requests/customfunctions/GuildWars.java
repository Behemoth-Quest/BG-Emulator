package world.behemoth.requests.customfunctions;

import world.behemoth.aqw.Settings;
import world.behemoth.db.objects.Area;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Set;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class GuildWars implements IRequest {
   public GuildWars() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String targetGuildName = params[0].toLowerCase();
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int gUserRank = ((Integer)user.properties.get("guildrank")).intValue();
      if(guildId <= 0) {
         throw new RequestException("You\'re not in a guild!");
      } else if(gUserRank < 2) {
         throw new RequestException("You\'re guild rank is not high enough to use this command!");
      } else {
         QueryResult gResult = world.db.jdbc.query("SELECT id FROM guilds WHERE Name = ?", new Object[]{targetGuildName});
         if(gResult.next()) {
            int targetGuildId = gResult.getInt("id");
            gResult.close();
            String targetGuildOwner = world.db.jdbc.queryForString("SELECT Name FROM users WHERE id = (SELECT UserID FROM users_guilds WHERE GuildID = ? AND Rank > 2)", new Object[]{Integer.valueOf(targetGuildId)}).toLowerCase();
            User client = world.zone.getUserByName(targetGuildOwner);
            if(client == null) {
               throw new RequestException("Guild Leader " + targetGuildOwner + " of the opposite guild is currently offline.");
            } else if((Integer)user.properties.get("dbId") == (Integer)client.properties.get("dbId")) {
               throw new RequestException("You can\'t challenge your own guild to a guild battle");
            } else if(!Settings.isAllowed("bDuel", user, client)) {
               throw new RequestException("Player \"" + targetGuildOwner + "\" is not accepting duel invites.");
            } else if(((Integer)user.properties.get("state")).intValue() != 2 && ((Integer)user.properties.get("state")).intValue() != 0) {
               Area area = (Area)world.areas.get(room.getName().split("-")[0]);
               Room clientRoom = world.zone.getRoom(client.getRoom());
               Area clientArea = (Area)world.areas.get(clientRoom.getName().split("-")[0]);
               if(area.isPvP()) {
                  throw new RequestException("One does not simply send duel request while on a pvp map.");
               } else if(clientArea.isPvP()) {
                  throw new RequestException(client.getName() + " is currently busy.");
               } else {
                  Set requestedDuel = (Set)client.properties.get("requestedduel");
                  requestedDuel.add(Integer.valueOf(user.getUserId()));
                  JSONObject gdi = new JSONObject();
                  gdi.put("owner", user.getName());
                  gdi.put("cmd", "gdi");
                  world.send(gdi, client);
                  world.send(new String[]{"server", "You have challenged " + targetGuildName + " to a guild battle."}, user);
               }
            } else {
               throw new RequestException(client.getName() + " is currently busy.");
            }
         } else {
            gResult.close();
            throw new RequestException("Guild \"" + targetGuildName + "\" could not be found.");
         }
      }
   }
}
