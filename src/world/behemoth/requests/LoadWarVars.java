package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class LoadWarVars implements IRequest {
   public LoadWarVars() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      try {
         QueryResult je = world.db.jdbc.query("SELECT * FROM events_wars", new Object[0]);

         while(je.next()) {
            JSONObject warVarsObj = (new JSONObject()).element("cmd", "loadWarVars").element("intWar1", je.getInt("War1")).element("intWar2", je.getInt("War2")).element("intWar3", je.getInt("War3")).element("intWar4", je.getInt("War4")).element("intWar5", je.getInt("War5")).element("intWarTotal", je.getInt("WarTotal"));
            world.send(warVarsObj, user);
         }

         je.close();
      } catch (JdbcException var7) {
         SmartFoxServer.log.severe("Error in loading war vars: " + var7.getMessage());
      }

   }
}
