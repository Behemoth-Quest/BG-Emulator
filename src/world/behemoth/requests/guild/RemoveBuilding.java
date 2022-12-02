package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class RemoveBuilding implements IRequest {
   public RemoveBuilding() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String curCell = params[1];
      int slot = Integer.parseInt(params[2]);
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int hallId = world.db.jdbc.queryForInt("SELECT id FROM guilds_halls WHERE Cell = ? AND GuildID = ?", new Object[]{curCell, Integer.valueOf(guildId)});
      world.db.jdbc.run("DELETE FROM guilds_halls_buildings WHERE HallID = ? AND Slot = ?", new Object[]{Integer.valueOf(hallId), Integer.valueOf(slot)});
      JSONObject guildhall = new JSONObject();
      guildhall.put("cmd", "guildhall");
      guildhall.put("gCmd", "removeBuilding");
      guildhall.put("Lot", Integer.valueOf(slot));
      guildhall.put("Cell", curCell);
      world.sendToRoom(guildhall, user, room);
   }
}
