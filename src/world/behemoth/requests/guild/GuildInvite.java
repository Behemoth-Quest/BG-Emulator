package world.behemoth.requests.guild;

import world.behemoth.aqw.Settings;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GuildInvite implements IRequest {
   public GuildInvite() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(((Integer)user.properties.get("guildrank")).intValue() < 2) {
         throw new RequestException("Invalid /gi request.");
      } else {
         String username = params[1].toLowerCase();
         User client = world.zone.getUserByName(username);
         if(client == null) {
            throw new RequestException("Player \"" + username + "\" could not be found.");
         } else {
            int clientGuildID = ((Integer)client.properties.get("guildid")).intValue();
            int userGuildID = ((Integer)user.properties.get("guildid")).intValue();
            if(userGuildID <= 0) {
               throw new RequestException("You are not in a guild!");
            } else if(clientGuildID > 0) {
               throw new RequestException(client.getName() + " already belongs to a guild.");
            } else if(!Settings.isAllowed("bGuild", user, client)) {
               throw new RequestException("Player " + client.getName() + " is not accepting guild invites.", "server");
            } else if(((Integer)client.properties.get("state")).intValue() == 2) {
               throw new RequestException(client.getName() + " is currently busy.");
            } else {
               JSONObject guildData = (JSONObject)user.properties.get("guildobj");
               JSONArray members = (JSONArray)guildData.get("ul");
               if(members.size() > 0) {
                  Iterator gi = members.iterator();

                  while(gi.hasNext()) {
                     JSONObject requestedGuild = (JSONObject)gi.next();
                     if(requestedGuild.get("ID").toString().equals(client.properties.get("dbId").toString())) {
                        throw new RequestException(client.getName() + " is already in your guild!");
                     }
                  }
               }

               if(members.size() >= ((Integer)guildData.get("MaxMembers")).intValue()) {
                  throw new RequestException("Your guild has reached the maximum number of members.");
               } else {
                  JSONObject gi1 = new JSONObject();
                  gi1.put("cmd", params[0]);
                  gi1.put("owner", user.getName());
                  gi1.put("gName", guildData.get("Name"));
                  gi1.put("guildID", Integer.valueOf(userGuildID));
                  Set requestedGuild1 = (Set)client.properties.get("requestedguild");
                  requestedGuild1.add(Integer.valueOf(userGuildID));
                  world.send(new String[]{"server", "You have invited " + client.getName() + " to join your guild."}, user);
                  world.send(gi1, client);
               }
            }
         }
      }
   }
}
