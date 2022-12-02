package world.behemoth.requests.guild;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import net.sf.json.JSONObject;

public class AddBuilding implements IRequest {
   public AddBuilding() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String curCell = params[1];
      int slot = Integer.parseInt(params[2]);
      int itemId = Integer.parseInt(params[3]);
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int size = ((Item)world.items.get(Integer.valueOf(itemId))).getStack();
      int hallId = world.db.jdbc.queryForInt("SELECT id FROM guilds_halls WHERE Cell = ? AND GuildID = ?", new Object[]{curCell, Integer.valueOf(guildId)});
      world.db.jdbc.run("INSERT INTO guilds_halls_buildings (HallID, ItemID, Slot, Size) VALUES (?, ?, ?, ?)", new Object[]{Integer.valueOf(hallId), Integer.valueOf(itemId), Integer.valueOf(slot), Integer.valueOf(size)});
      JSONObject guildhall = new JSONObject();
      guildhall.put("cmd", "guildhall");
      guildhall.put("gCmd", "addbuilding");
      guildhall.put("Buildings", world.users.getBuildingString(hallId));
      guildhall.put("Cell", curCell);
      world.sendToRoom(guildhall, user, room);
   }
}
