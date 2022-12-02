package world.behemoth.requests.customfunctions;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class RedeemCode implements IRequest {
   public RedeemCode() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String codeInserted = params[0].trim().toLowerCase();
      QueryResult redeemResults = world.db.jdbc.query("SELECT * FROM redeem_codes WHERE Code = ?", new Object[]{codeInserted});
      if(redeemResults.next()) {
         int codeTimeLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(SECOND, NOW(), ?)", new Object[]{redeemResults.getString("DateExpiry")});
         codeTimeLeft = codeTimeLeft >= 0?codeTimeLeft:0;
         if(codeTimeLeft > 0) {
            int userRedeemCount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_redeems WHERE UserID = ? AND RedeemID = ?", new Object[]{user.properties.get("dbId"), Integer.valueOf(redeemResults.getInt("id"))});
            if(userRedeemCount < 1) {
               if(redeemResults.getInt("Coins") > 0) {
                  JSONObject KeKTopZ = new JSONObject();
                  KeKTopZ.put("cmd", "sellItem");
                  KeKTopZ.put("intAmount", Integer.valueOf(redeemResults.getInt("Coins")));
                  KeKTopZ.put("CharItemID", Integer.valueOf(user.hashCode()));
                  KeKTopZ.put("bCoins", Integer.valueOf(1));
                  world.send(KeKTopZ, user);
                  world.db.jdbc.run("UPDATE users SET Coins = (Coins + ?) WHERE id=?", new Object[]{Integer.valueOf(redeemResults.getInt("Coins")), user.properties.get("dbId")});
               }

               if(redeemResults.getInt("Gold") > 0) {
                  world.users.giveRewards(user, 0, redeemResults.getInt("Gold"), 0, 0, -1, user.getUserId(), "p");
               }

               if(redeemResults.getInt("Exp") > 0) {
                  world.users.giveRewards(user, redeemResults.getInt("Exp"), 0, 0, 0, -1, user.getUserId(), "p");
               }

               if(redeemResults.getInt("ClassPoints") > 0) {
                  world.users.giveRewards(user, 0, 0, redeemResults.getInt("ClassPoints"), 0, -1, user.getUserId(), "p");
               }

               if(redeemResults.getInt("ItemID") > 0) {
                  world.users.dropItem(user, redeemResults.getInt("ItemID"));
               }

               if(redeemResults.getInt("UpgradeDaysInc") > 0) {
                  int KeKTopZ1 = redeemResults.getInt("UpgradeDaysInc") * 24 * 60;
                  QueryResult upgradeExpire = world.db.jdbc.query("SELECT UpgradeExpire FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
                  if(upgradeExpire.next()) {
                     int k = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(MINUTE, NOW(), ?)", new Object[]{upgradeExpire.getString("UpgradeExpire")});
                     k = k >= 0?k:0;
                     world.db.jdbc.run("UPDATE users SET UpgradeExpire = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id = ?", new Object[]{Integer.valueOf(KeKTopZ1 + k), user.properties.get("dbId")});
                  }

                  upgradeExpire.close();
               }

               world.send(new String[]{"server", "You successfully earned " + redeemResults.getInt("Coins") + " Coins," + redeemResults.getInt("Gold") + " Gold," + redeemResults.getInt("Exp") + " Experience," + redeemResults.getInt("ClassPoints") + " Class Points,and " + redeemResults.getInt("UpgradeDaysInc") + " Upgrade Days from the code!"}, user);
               world.db.jdbc.run("INSERT INTO users_redeems (RedeemID, UserID, Date) VALUES (?, ?, NOW())", new Object[]{Integer.valueOf(redeemResults.getInt("id")), user.properties.get("dbId")});
               redeemResults.close();
            } else {
               redeemResults.close();
               throw new RequestException("You already redeemed this code!");
            }
         } else {
            redeemResults.close();
            throw new RequestException("The code you\'re trying to redeem is already expired!");
         }
      } else {
         redeemResults.close();
         throw new RequestException("The code you\'re trying to redeem is invalid!");
      }
   }
}
