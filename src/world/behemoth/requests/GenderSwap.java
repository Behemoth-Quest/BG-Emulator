package world.behemoth.requests;

import world.behemoth.db.objects.Hair;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.JdbcException;
import net.sf.json.JSONObject;

public class GenderSwap implements IRequest {
   public GenderSwap() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      world.db.jdbc.beginTransaction();

      try {
         int je = world.db.jdbc.queryForInt("SELECT Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
         int deltaCoins = je - 1000;
         if(deltaCoins <= 0) {
            world.db.jdbc.rollbackTransaction();
            throw new RequestException("You don\'t have enough coins!");
         }

         String gender = (String)user.properties.get("gender");
         Hair hair;
         String newGender;
         if(gender.equals("M")) {
            hair = (Hair)world.hairs.get(Integer.valueOf(83));
            newGender = "F";
         } else {
            hair = (Hair)world.hairs.get(Integer.valueOf(52));
            newGender = "M";
         }

         world.db.jdbc.run("UPDATE users SET Gender = ?, Coins = ?, HairID = ? WHERE id = ?", new Object[]{newGender, Integer.valueOf(deltaCoins), Integer.valueOf(hair.getId()), user.properties.get("dbId")});
         user.properties.put("gender", newGender);
         user.properties.put("hairId", Integer.valueOf(hair.getId()));
         JSONObject genderSwap = new JSONObject();
         genderSwap.put("uid", Integer.valueOf(user.getUserId()));
         genderSwap.put("strHairFilename", hair.getFile());
         genderSwap.put("cmd", "genderSwap");
         genderSwap.put("bitSuccess", Integer.valueOf(1));
         genderSwap.put("HairID", Integer.valueOf(hair.getId()));
         genderSwap.put("strHairName", hair.getName());
         genderSwap.put("gender", newGender);
         genderSwap.put("intCoins", Integer.valueOf(1000));
         world.sendToRoom(genderSwap, user, room);
      } catch (JdbcException var14) {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.rollbackTransaction();
         }

         SmartFoxServer.log.severe("Error in gender swap transaction: " + var14.getMessage());
      } finally {
         if(world.db.jdbc.isInTransaction()) {
            world.db.jdbc.commitTransaction();
         }

      }

   }
}
