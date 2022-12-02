package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class SaveInterior implements IRequest {
   public SaveInterior() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String interior = params[1];
      String cell = params[2];
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int hallId = world.db.jdbc.queryForInt("SELECT id FROM guilds_halls WHERE Cell = ? AND GuildID = ?", new Object[]{cell, Integer.valueOf(guildId)});
      world.db.jdbc.run("UPDATE guilds_halls SET Interior = ? WHERE id = ?", new Object[]{interior, Integer.valueOf(hallId)});
      JSONObject guildhall = new JSONObject();
      guildhall.put("cmd", "guildhall");
      guildhall.put("gCmd", "updateInterior");
      guildhall.put("interior", interior);
      world.sendToRoom(guildhall, user, room);
   }
}
