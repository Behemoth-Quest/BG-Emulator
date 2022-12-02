package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class GuildDeclineInvite implements IRequest {
   public GuildDeclineInvite() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      User client = world.zone.getUserByName(params[2].toLowerCase());
      if(client != null) {
         JSONObject gd = new JSONObject();
         gd.put("cmd", "gd");
         gd.put("unm", user.getName());
         world.send(new String[]{"server", "You declined the invitation."}, user);
         world.send(gd, client);
      }
   }
}
