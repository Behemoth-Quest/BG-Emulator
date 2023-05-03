package world.behemoth.requests;

import world.behemoth.db.objects.Area;
import world.behemoth.db.objects.Item;
import world.behemoth.db.objects.Quest;
import world.behemoth.db.objects.QuestReward;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.Users;
import world.behemoth.world.World;

import com.google.common.collect.HashMultimap;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class TryQuestComplete implements IRequest
{
   private static final Random rand = new Random();
   private static final List<Integer> doom = Arrays.asList(3073, 3074, 3075, 3076);
   private static final List<Integer> destiny = Arrays.asList(3128, 3129, 3130, 3131);
   private static final int boost = 19189;
   private static final int potion = 18927;

   @Override
   public void process(String[] params, User user, World world, Room room) throws RequestException
   {
      int questId = Integer.parseInt(params[0]);
      int itemId = Integer.parseInt(params[1]);
//      Set userQuests = (Set)user.properties.get("quests");
      Set<Integer> userQuests = (Set<Integer>)user.properties.get("quests");
      Quest quest = (Quest)world.quests.get(Integer.valueOf(questId));
      if(!quest.locations.isEmpty()) {
         int ccqr = ((Area)world.areas.get(room.getName().split("-")[0])).getId();
         if(!quest.locations.contains(Integer.valueOf(ccqr))) {
            world.users.log(user, "Invalid Quest Complete", "Quest complete triggered at different location.");
            return;
         }
      }

      JSONObject ccqr1 = new JSONObject();
      ccqr1.put("cmd", "ccqr");
      ccqr1.put("QuestID", Integer.valueOf(questId));
      if(!userQuests.contains(Integer.valueOf(questId)) && !doom.contains(Integer.valueOf(questId)) && !destiny.contains(Integer.valueOf(questId))) {
         ccqr1.put("bSuccess", Integer.valueOf(0));
         world.users.log(user, "Packet Edit [TryQuestComplete]", "Attempted to complete an unaccepted quest: " + quest.getName());
      } else {
         if(!quest.getField().isEmpty() && world.users.getAchievement(quest.getField(), quest.getIndex(), user) != 0) {
            ccqr1.put("bSuccess", Integer.valueOf(0));
            world.send(ccqr1, user);
            world.users.log(user, "Packet Edit [TryQuestComplete]", "Failed to pass achievement validation while attempting to complete quest: " + quest.getName());
            return;
         }
         if (!quest.getField().isEmpty() && world.users.getAchievement(quest.getField(), quest.getIndex(), user) != 0) {
            ccqr1.put("bSuccess", 0);
            world.send(ccqr1, user);
            world.users.log(user, "Packet Edit [TryQuestComplete]", "Failed to pass achievement validation while attempting to complete quest: " + quest.getName());
            world.send(new String[]{"server", "Quest daily/monthly limit has been reached. Please try again later."}, user);
            return;
         }

         if(world.users.turnInItems(user, quest.requirements)) {
            if(doom.contains(Integer.valueOf(questId))) {
               this.doWheel(user, world, "doom");
            } else if(destiny.contains(Integer.valueOf(questId))) {
               this.doWheel(user, world, "destiny");
            } else if(quest.rewards.size() > 0) {
               HashMultimap rewardObj = HashMultimap.create();
               HashMultimap itemsPercentage = HashMultimap.create();
               Iterator keys = quest.rewards.entries().iterator();

               while(keys.hasNext()) {
                  Entry randomKey = (Entry)keys.next();
                  QuestReward qty = (QuestReward)randomKey.getValue();
                  String randomValue = qty.type;
                  byte rate = -1;
                  switch(randomValue.hashCode()) {
                     case 67:
                        if(randomValue.equals("C")) {
                           rate = 0;
                        }
                        break;
                     case 82:
                        if(randomValue.equals("R")) {
                           rate = 1;
                        }
                        break;
                     case 83:
                        if(randomValue.equals("S")) {
                           rate = 3;
                        }
                        break;
                     case 3492901:
                        if(randomValue.equals("rand")) {
                           rate = 2;
                        }
                  }

                  switch(rate) {
                     case 0:
                        if(itemId == qty.itemId) {
                           world.users.dropItem(user, qty.itemId, qty.quantity);
                        }
                        break;
                     case 1:
                     case 2:
                        rewardObj.put(Integer.valueOf(qty.itemId), Integer.valueOf(qty.quantity));
                        itemsPercentage.put(Integer.valueOf(qty.itemId), Double.valueOf(qty.rate));
                        break;
                     case 3:
                     default:
                        world.users.dropItem(user, qty.itemId, qty.quantity);
                  }
               }

               if(rewardObj.size() > 0) {
                  ArrayList keys1 = new ArrayList(rewardObj.keySet());
                  int randomKey1 = ((Integer)keys1.get(rand.nextInt(keys1.size()))).intValue();
                  Object[] qty1 = rewardObj.get(Integer.valueOf(randomKey1)).toArray();
                  int randomValue1 = ((Integer)qty1[rand.nextInt(qty1.length)]).intValue();
                  Object[] rate1 = itemsPercentage.get(Integer.valueOf(randomKey1)).toArray();
                  Double rateValue = (Double)rate1[rand.nextInt(rate1.length)];
                  if(Math.random() > rateValue.doubleValue()) {
                     SmartFoxServer.log.severe("User chance test: " + rateValue);
                  } else {
                     world.users.dropItem(user, randomKey1, randomValue1);
                  }
               }
            }

            int gold = ((Integer) user.properties.get(Users.GOLD)).intValue() >= (world.coreValues.get("intGoldMax")).intValue() ? 0 : quest.getGold();
            int coins = ((Integer) user.properties.get(Users.COINS)).intValue() >= (world.coreValues.get("intCoinsMax")).intValue() ? 0 : quest.getCoins();
            world.users.giveRewards(user, quest.getExperience(), gold, coins, quest.getClassPoints(), quest.getReputation(), quest.getFactionId(), user.getUserId(), "p");
//            world.users.giveRewards(user, quest.getExperience(), quest.getGold(), 1, quest.getClassPoints(), quest.getReputation(), quest.getFactionId(), "p");
//            if(quest.getCoins() > 0){
//               JSONObject var16 = new JSONObject();
//               var16.put("cmd", "sellItem");
//               var16.put("intAmount", quest.getCoins());
//               var16.put("CharItemID", Integer.valueOf(user.hashCode()));
//               var16.put("bCoins", Integer.valueOf(1));
//               world.send(var16, user);
//               world.db.jdbc.run("UPDATE users SET Coins = (Coins + ?) WHERE id=?", new Object[]{Integer.valueOf(quest.getCoins()), user.properties.get("dbId")});
//               world.send(new String[]{"server", quest.getCoins() + "ACs has been added to your account."}, user);
//            }

            JSONObject rewardObj1 = new JSONObject();
            rewardObj1.put("intGold", quest.getGold());
//            rewardObj1.put("intCoins", quest.getCoins());
            rewardObj1.put("intExp", quest.getExperience());
            rewardObj1.put("iCP", quest.getClassPoints());

            if (quest.getFactionId() > 0) {
               rewardObj1.put("iRep", quest.getReputation());
            }

            ccqr1.put("rewardObj", rewardObj1);
            ccqr1.put("sName", quest.getName());

            if (quest.getSlot() > 0) {
               if (world.users.getQuestValue(user, quest.getSlot()) >= quest.getValue()) {
                  //mencegah Quest Chain yang mengulang save pointnya
               } else {
                  world.users.setQuestValue(user, quest.getSlot(), quest.getValue());
               }
            }

            if(!quest.getField().isEmpty()) {
               world.users.setAchievement(quest.getField(), quest.getIndex(), 1, user);
            }
//            QueryResult achvalueResults = world.db.jdbc.query("SELECT * FROM achievements WHERE QuestsValue = ? AND QuestsSlot = ?", new Object[]{quest.getValue(), quest.getSlot()});
//            if (achvalueResults.next()) {
//               int achCheck = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_achievements WHERE UserID = ? AND AchID = ?", new Object[]{user.properties.get("dbId"), achvalueResults.getInt("id")});
//               if (achCheck < 1) {
//                  world.db.jdbc.run("INSERT INTO users_achievements (UserID, AchID) VALUES (?, ?)", new Object[]{user.properties.get("dbId"), achvalueResults.getInt("id")});
//               }
//            }
//            achvalueResults.close();
//
//
//            QueryResult titlelist = world.db.jdbc.query("SELECT * FROM titles WHERE QuestsValue = ? AND QuestsSlot = ?", new Object[]{quest.getValue(), quest.getSlot()});
//            if (titlelist.next()) {
//               int titlecheck = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_titles WHERE UserID = ? AND TitleID = ?", new Object[]{user.properties.get("dbId"), titlelist.getInt("titleid")});
//               if (titlecheck < 1) {
//                  world.db.jdbc.run("INSERT INTO users_titles (UserID, TitleID) VALUES (?, ?)", new Object[]{user.properties.get("dbId"), titlelist.getInt("titleid")});
//               }
//            }
//            titlelist.close();
            userQuests.remove(Integer.valueOf(questId));
         } else {
            ccqr1.put("bSuccess", Integer.valueOf(0));
            world.users.log(user, "Packet Edit [TryQuestComplete]", "Failed to pass turn in validation while attempting to complete quest: " + quest.getName());
         }
      }

      world.send(ccqr1, user);
   }

   private void doWheel(User user, World world, String wheelType) throws RequestException {
      JSONObject wheel = new JSONObject();
      ArrayList keys = new ArrayList(world.wheelsItems.keySet());
      int itemId = ((Integer)keys.get(rand.nextInt(keys.size()))).intValue();
      Double chance = (Double)world.wheelsItems.get(Integer.valueOf(itemId));
      Item item = (Item)world.items.get(Integer.valueOf(itemId));
      if(Math.random() > chance.doubleValue()) {
         this.doWheel(user, world, wheelType);
      } else {
         int charItemId = -1;
         int quantity1 = 0;
         int quantity2 = 0;
         int charItemId1 = -1;
         int charItemId2 = -1;
         world.db.jdbc.beginTransaction();

         try {
            QueryResult itemJSON = world.db.jdbc.query("SELECT * FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
            if(!itemJSON.next()) {
               world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, NOW())", new Object[]{user.properties.get("dbId"), Integer.valueOf(itemId), Integer.valueOf(item.getEnhId()), Integer.valueOf(item.getQuantity())});
               charItemId = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
            }
            itemJSON.close();
            QueryResult dropItems = world.db.jdbc.query("SELECT id, Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(19189), user.properties.get("dbId")});
            if(dropItems.next()) {
               charItemId1 = dropItems.getInt("id");
               quantity1 = dropItems.getInt("Quantity");
               world.db.jdbc.run("UPDATE users_items SET Quantity = (Quantity + 1) WHERE id = ?", new Object[]{Integer.valueOf(charItemId1)});
               dropItems.close();
            } else {
               world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, NOW())", new Object[]{user.properties.get("dbId"), Integer.valueOf(19189), Integer.valueOf(0), Integer.valueOf(1)});
               charItemId1 = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
               quantity1 = 1;
               dropItems.close();
            }
            dropItems.close();
            QueryResult potionResult = world.db.jdbc.query("SELECT id, Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(18927), user.properties.get("dbId")});
            if(potionResult.next()) {
               charItemId2 = potionResult.getInt("id");
               quantity2 = potionResult.getInt("Quantity");
               world.db.jdbc.run("UPDATE users_items SET Quantity = (Quantity + 1) WHERE id = ?", new Object[]{Integer.valueOf(charItemId2)});
               potionResult.close();
            } else {
               world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, NOW())", new Object[]{user.properties.get("dbId"), Integer.valueOf(18927), Integer.valueOf(0), Integer.valueOf(1)});
               charItemId2 = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
               quantity2 = 1;
               potionResult.close();
            }
            potionResult.close();
         } catch (JdbcException var20) {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.rollbackTransaction();
            }

            SmartFoxServer.log.severe("Error in wheel transaction: " + var20.getMessage());
         } finally {
            if(world.db.jdbc.isInTransaction()) {
               world.db.jdbc.commitTransaction();
            }

         }

         JSONObject itemJSON1 = Item.getItemJSON(item);
         itemJSON1.put("iQty", Integer.valueOf(item.getQuantity()));
         wheel.put("cmd", "Wheel");
         if(charItemId > 0) {
            wheel.put("Item", itemJSON1);
            world.send(new String[]{"wheel", "You won " + item.getName()}, user);
            if(item.getRarity() >= 30) {
               world.sendToUsers(new String[]{"wheel", "Player <font color=\"#ffffff\">" + user.properties.get("username") + "</font> has received " + item.getName() + " from the wheel of " + wheelType});
            }
         } else {
            world.send(new String[]{"wheel", "You have already won \'" + item.getName() + "\' before. Try your luck next time."}, user);
         }

         JSONObject dropItems1 = new JSONObject();
         dropItems1.put(String.valueOf(19189), Item.getItemJSON((Item)world.items.get(Integer.valueOf(19189))));
         dropItems1.put(String.valueOf(18927), Item.getItemJSON((Item)world.items.get(Integer.valueOf(18927))));
         wheel.put("dropItems", dropItems1);
         wheel.put("CharItemID", Integer.valueOf(charItemId));
         wheel.put("charItem1", Integer.valueOf(charItemId1));
         wheel.put("charItem2", Integer.valueOf(charItemId2));
         wheel.put("iQty1", Integer.valueOf(quantity1));
         wheel.put("iQty2", Integer.valueOf(quantity2));
         world.send(wheel, user);
      }
   }
}
