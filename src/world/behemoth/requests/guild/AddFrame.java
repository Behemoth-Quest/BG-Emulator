package world.behemoth.requests.guild;

import world.behemoth.aqw.Pad;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class AddFrame implements IRequest {
   public AddFrame() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String newCell = params[1];
      String linkage = params[2];
      String curCell = params[3];
      String toPad = params[4];
      int purchase = Integer.parseInt(params[5]);
      int cost = Integer.parseInt(params[6]);
      boolean isCoins = Boolean.parseBoolean(params[7]);
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int hallId = world.db.jdbc.queryForInt("SELECT id FROM guilds_halls WHERE Cell = ? AND GuildID = ?", new Object[]{curCell, Integer.valueOf(guildId)});
      world.db.jdbc.beginTransaction();

      try {
         if(purchase > 0) {
            QueryResult guildhall = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
            if(guildhall.next()) {
               int coins = guildhall.getInt("Coins");
               int gold = guildhall.getInt("Gold");
               guildhall.close();
               boolean valid = isCoins && cost <= coins?true:cost <= gold;
               if(valid) {
                  int newHallId;
                  if(!isCoins) {
                     newHallId = gold - cost;
                     world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id=?", new Object[]{Integer.valueOf(newHallId), user.properties.get("dbId")});
                  } else {
                     newHallId = coins - cost;
                     world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id=?", new Object[]{Integer.valueOf(newHallId), user.properties.get("dbId")});
                  }

                  world.db.jdbc.run("INSERT INTO guilds_halls (GuildID, Linkage, Cell, Interior) VALUES (?, ?, ?, ?)", new Object[]{Integer.valueOf(guildId), linkage, newCell, "|||"});
                  newHallId = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
                  world.db.jdbc.run("INSERT INTO guilds_halls_connections (HallID, Pad, Cell, PadPosition) VALUES (? ,? ,? ,?)", new Object[]{Integer.valueOf(hallId), toPad, newCell, Pad.getPad(toPad)});
                  world.db.jdbc.run("INSERT INTO guilds_halls_connections (HallID, Pad, Cell, PadPosition) VALUES (? ,? ,? ,?)", new Object[]{Integer.valueOf(newHallId), Pad.getPair(toPad), curCell, Pad.getPad(Pad.getPair(toPad))});
               }
            }

            guildhall.close();
         } else {
            world.db.jdbc.run("INSERT INTO guilds_halls (GuildID, Linkage, Cell) VALUES (?, ?, ?)", new Object[]{Integer.valueOf(guildId), linkage, newCell});
            int guildhall1 = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
            world.db.jdbc.run("INSERT INTO guilds_halls_connections (HallID, Pad, Cell, PadPosition) VALUES (? ,? ,? ,?)", new Object[]{Integer.valueOf(hallId), toPad, newCell, Pad.getPad(toPad)});
            world.db.jdbc.run("INSERT INTO guilds_halls_connections (HallID, Pad, Cell, PadPosition) VALUES (? ,? ,? ,?)", new Object[]{Integer.valueOf(guildhall1), Pad.getPair(toPad), curCell, Pad.getPad(Pad.getPair(toPad))});
         }
      } catch (JdbcException var22) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in buy frame transaction: " + var22.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

      JSONObject guildhall2 = new JSONObject();
      guildhall2.put("cmd", "guildhall");
      guildhall2.put("gCmd", "addframe");
      guildhall2.put("guildHall", world.users.getGuildHallData(guildId));
      guildhall2.put("bitSuccess", Integer.valueOf(1));
      world.sendToRoom(guildhall2, user, room);
   }
}
