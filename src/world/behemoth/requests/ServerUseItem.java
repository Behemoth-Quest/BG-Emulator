package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class ServerUseItem implements IRequest {
   public ServerUseItem() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String option = params[0];
      if(option.equals("+")) {
         int type = Integer.parseInt(params[1]);
         Item boost = (Item)world.items.get(Integer.valueOf(type));
         if(boost != null && boost.getType().equals("ServerUse")) {
            if(!boost.getLink().contains("::")) {
               throw new RequestException("This feature is not yet available.", "server");
            }

            String[] itemParams = boost.getLink().split("::");
            String type1 = itemParams[0];
            int minutes = Integer.parseInt(itemParams[1]);
            boolean showShop = Boolean.parseBoolean(itemParams[2]);
            if(world.users.turnInItem(user, type, 1)) {
               JSONObject boost1 = new JSONObject();
               boost1.put("cmd", type1);
               boost1.put("bShowShop", Boolean.valueOf(showShop));
               boost1.put("op", option);
               QueryResult boosts = world.db.jdbc.query("SELECT ExpBoostExpire, CpBoostExpire, GoldBoostExpire, RepBoostExpire FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
               if(boosts.next()) {
                  int repMinLeft;
                  if(type1.equals("xpboost")) {
                     repMinLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(MINUTE, NOW(), ?)", new Object[]{boosts.getString("ExpBoostExpire")});
                     repMinLeft = repMinLeft >= 0?repMinLeft:0;
                     world.db.jdbc.run("UPDATE users SET ExpBoostExpire = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id = ?", new Object[]{Integer.valueOf(minutes + repMinLeft), user.properties.get("dbId")});
                     user.properties.put("xpboost", Boolean.valueOf(true));
                     boost1.put("iSecsLeft", Integer.valueOf((minutes + repMinLeft) * 60));
                  } else if(type1.equals("gboost")) {
                     repMinLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(MINUTE,NOW(),?)", new Object[]{boosts.getString("GoldBoostExpire")});
                     repMinLeft = repMinLeft >= 0?repMinLeft:0;
                     world.db.jdbc.run("UPDATE users SET GoldBoostExpire = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id = ?", new Object[]{Integer.valueOf(minutes + repMinLeft), user.properties.get("dbId")});
                     user.properties.put("goldboost", Boolean.valueOf(true));
                     boost1.put("iSecsLeft", Integer.valueOf((minutes + repMinLeft) * 60));
                  } else if(type1.equals("cpboost")) {
                     repMinLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(MINUTE, NOW(), ?)", new Object[]{boosts.getString("CpBoostExpire")});
                     repMinLeft = repMinLeft >= 0?repMinLeft:0;
                     world.db.jdbc.run("UPDATE users SET CpBoostExpire = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id = ?", new Object[]{Integer.valueOf(minutes + repMinLeft), user.properties.get("dbId")});
                     user.properties.put("cpboost", Boolean.valueOf(true));
                     boost1.put("iSecsLeft", Integer.valueOf((minutes + repMinLeft) * 60));
                  } else if(type1.equals("repboost")) {
                     repMinLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(MINUTE, NOW() , ?)", new Object[]{boosts.getString("RepBoostExpire")});
                     repMinLeft = repMinLeft >= 0?repMinLeft:0;
                     world.db.jdbc.run("UPDATE users SET RepBoostExpire = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id = ?", new Object[]{Integer.valueOf(minutes + repMinLeft), user.properties.get("dbId")});
                     user.properties.put("repboost", Boolean.valueOf(true));
                     boost1.put("iSecsLeft", Integer.valueOf((minutes + repMinLeft) * 60));
                  }

                  world.send(boost1, user);
               }

               boosts.close();
            } else {
               world.users.log(user, "Suspicious Request [ServerUseItem]", "Failed to pass turn-in validation, might be a duplicate request.");
            }
         }
      } else {
         String type2 = params[1];
         JSONObject boost2 = new JSONObject();
         boost2.put("cmd", type2);
         boost2.put("op", option);
         if(type2.equals("xpboost")) {
            user.properties.put("xpboost", Boolean.valueOf(false));
         } else if(type2.equals("gboost")) {
            user.properties.put("goldboost", Boolean.valueOf(false));
         } else if(type2.equals("cpboost")) {
            user.properties.put("cpboost", Boolean.valueOf(false));
         } else if(type2.equals("repboost")) {
            user.properties.put("repboost", Boolean.valueOf(false));
         }

         world.send(boost2, user);
      }

   }
}
