package world.behemoth.requests.guild;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class BuyPlot implements IRequest {
   public BuyPlot() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int guildRank = ((Integer)user.properties.get("guildrank")).intValue();
      if(guildRank >= 2) {
         world.db.jdbc.beginTransaction();

         try {
            int je = world.db.jdbc.queryForInt("SELECT Gold FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
            int hallSize = world.db.jdbc.queryForInt("SELECT HallSize FROM guilds WHERE id = ? FOR UPDATE", new Object[]{Integer.valueOf(guildId)});
            if(je >= 1000) {
               if(hallSize < 16) {
                  int deltaGold = je - 1000;
                  world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id = ?", new Object[]{Integer.valueOf(deltaGold), user.properties.get("dbId")});
                  ++hallSize;
                  world.db.jdbc.run("UPDATE guilds SET HallSize = ? WHERE id = ?", new Object[]{Integer.valueOf(hallSize), Integer.valueOf(guildId)});
                  JSONObject guildhall = new JSONObject();
                  guildhall.put("cmd", "guildhall");
                  guildhall.put("gCmd", "buyPlot");
                  guildhall.put("bitSuccess", Integer.valueOf(1));
                  world.send(guildhall, user);
               }

               return;
            }
         } catch (JdbcException var14) {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.rollbackTransaction();
            }

            SmartFoxServer.log.severe("Error in buy plot transaction: " + var14.getMessage());
            return;
         } finally {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.commitTransaction();
            }

         }

      }
   }
}
