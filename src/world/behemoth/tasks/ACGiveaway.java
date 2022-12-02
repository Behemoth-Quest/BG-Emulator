package world.behemoth.tasks;

import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.List;
import java.util.Random;
import net.sf.json.JSONObject;

public class ACGiveaway implements Runnable {
   private World world;
   private Random rand;

   public ACGiveaway(World world) {
      super();
      this.world = world;
      this.rand = new Random(System.currentTimeMillis());
      SmartFoxServer.log.info("ACGiveaway event initialized.");
   }

   private User getRandomUser() {
      List users = this.world.zone.getUserList();
      User user = (User)users.get(this.rand.nextInt(users.size()));
      return !user.isAdmin() && !user.isModerator()?user:this.getRandomUser();
   }

   public void run() {
      int total = this.world.zone.getUserCount();
      if(total > 0) {
         User target = this.getRandomUser();
         this.world.sendServerMessage("Congratulations! <font color=\"#ffffff\"><a href=\"http://www.aldmor.org/player/" + target.getName() + "\" target=\"_blank\">" + target.properties.get("username") + "</a></font> has won <font color=\"#ffffff\">500</font> AdventureCoins!");
         this.world.send(new String[]{"administrator", "Congratulations! You just won 500 AdventureCoins!"}, target);
         this.world.sendToUsers(new String[]{"administrator", "Congratulations! <font color=\"#ffffff\">" + target.properties.get("username") + "</font> has won 500 AdventureCoins!"});
         JSONObject sell = new JSONObject();
         sell.put("cmd", "sellItem");
         sell.put("intAmount", Integer.valueOf(500));
         sell.put("CharItemID", Integer.valueOf(target.hashCode()));
         sell.put("bCoins", Integer.valueOf(1));
         this.world.send(sell, target);
         this.world.db.jdbc.run("UPDATE users SET Coins = (Coins + ?) WHERE id = ?", new Object[]{Integer.valueOf(500), target.properties.get("dbId")});
         SmartFoxServer.log.info("User [ " + target.getName() + " ] won 500 AdventureCoins.");

         try {
            Thread.sleep(5000L);
            this.world.sendServerMessage("The next lucky winner will be selected randomly in the next 30 minutes.");
         } catch (InterruptedException var5) {
            ;
         }

      }
   }
}
