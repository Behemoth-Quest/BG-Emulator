package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class GuildMOTD implements IRequest {
   public GuildMOTD() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      if(((Integer)user.properties.get("guildid")).intValue() > 0 && ((Integer)user.properties.get("guildrank")).intValue() >= 2) {
         String message = params[1].trim();
         if(message.contains(">") || message.contains("<")) {
            throw new RequestException("You can\'t input illegal chararacters.");
         }

         world.db.jdbc.run("UPDATE guilds SET MessageOfTheDay = ? WHERE id = ?", new Object[]{message, user.properties.get("guildid")});
         JSONObject object = new JSONObject();
         object.put("cmd", "gMOTD");
         object.put("unm", user.properties.get("username"));
         object.put("msg", message);
         JSONObject guildObj = (JSONObject)user.properties.get("guildobj");
         guildObj.put("MOTD", message);
         world.sendToGuild(object, guildObj);
         world.sendToGuild(new String[]{"server", "Guild message has been changed."}, guildObj);
         world.sendGuildUpdate(guildObj);
      }

   }
}
