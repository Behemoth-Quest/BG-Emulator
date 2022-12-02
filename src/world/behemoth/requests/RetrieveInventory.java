package world.behemoth.requests;

import world.behemoth.config.ConfigData;
import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.EnhancementPattern;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.sql.Date;
import java.util.Iterator;
import java.util.Map;
import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RetrieveInventory implements IRequest {
   private JSONArray hitems = new JSONArray();
   private JSONArray items = new JSONArray();

   public RetrieveInventory() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      this.retrieveInventory(user, world, room);
      this.sendEnhancementPatterns(user, world);
      this.loadInventoryBig(user, world);
      world.users.sendStats(user);
   }

   private void loadInventoryBig(User user, World world) {
      JSONObject lib = new JSONObject();
      lib.put("bankCount", Integer.valueOf(world.users.getBankCount(user)));
      lib.put("cmd", "loadInventoryBig");
      lib.put("items", this.items);
      lib.put("hitems", this.hitems);
      lib.put("factions", this.getFactions(user, world));
      if(((Integer)user.properties.get("guildid")).intValue() > 0) {
         lib.put("guild", user.properties.get("guildobj"));
      }

      world.send(lib, user);
      if(user.properties.get("language").equals("BR")) {
         world.send(new String[]{"server", "Personagem carga completa."}, user);
      } else {
         world.send(new String[]{"server", "Character load complete."}, user);
      }

      JSONObject updateFriend = new JSONObject();
      JSONObject friendInfo = new JSONObject();
      updateFriend.put("cmd", "updateFriend");
      friendInfo.put("iLvl", (Integer)user.properties.get("level"));
      friendInfo.put("ID", user.properties.get("dbId"));
      friendInfo.put("sName", user.properties.get("username"));
      friendInfo.put("sServer", ConfigData.SERVER_NAME);
      updateFriend.put("friend", friendInfo);
      QueryResult result = world.db.jdbc.query("SELECT Name FROM users LEFT JOIN users_friends ON FriendID = id WHERE UserID = ?", new Object[]{user.properties.get("dbId")});

      while(result.next()) {
         User guildId = world.zone.getUserByName(result.getString("Name").toLowerCase());
         if(guildId != null) {
            world.send(updateFriend, guildId);
            if(user.properties.get("language").equals("BR")) {
               world.send(new String[]{"server", user.getName() + " tem logado."}, guildId);
            } else {
               world.send(new String[]{"server", user.getName() + " has logged in."}, guildId);
            }
         }
      }

      result.close();
      world.db.jdbc.run("UPDATE users SET CurrentServer = ? WHERE id = ?", new Object[]{ConfigData.SERVER_NAME, user.properties.get("dbId")});
      int guildId1 = ((Integer)user.properties.get("guildid")).intValue();
      if(guildId1 > 0) {
         world.sendGuildUpdate(world.users.getGuildObject(guildId1));
      }

      this.retrieveBoosts(user, world);
   }

   private JSONArray getFactions(User user, World world) {
      JSONArray factions = new JSONArray();
      QueryResult result = world.db.jdbc.query("SELECT * FROM users_factions WHERE UserID = ?", new Object[]{user.properties.get("dbId")});
      Map userfactions = (Map)user.properties.get("factions");

      while(result.next()) {
         JSONObject faction = new JSONObject();
         faction.put("FactionID", result.getString("FactionID"));
         faction.put("CharFactionID", result.getString("id"));
         faction.put("sName", world.factions.get(Integer.valueOf(result.getInt("FactionID"))));
         faction.put("iRep", Integer.valueOf(result.getInt("Reputation")));
         factions.add(faction);
         userfactions.put(Integer.valueOf(result.getInt("FactionID")), Integer.valueOf(result.getInt("Reputation")));
      }

      result.close();
      return factions;
   }

   private void sendEnhancementPatterns(User user, World world) {
      JSONObject enhp = new JSONObject();
      JSONObject o = new JSONObject();
      Iterator i$ = world.patterns.values().iterator();

      while(i$.hasNext()) {
         EnhancementPattern ep = (EnhancementPattern)i$.next();
         JSONObject pattern = new JSONObject();
         Map stats = ep.getStats();
         pattern.put("ID", String.valueOf(ep.getId()));
         pattern.put("sName", ep.getName());
         pattern.put("sDesc", ep.getDescription());
         pattern.put("iWIS", String.valueOf(stats.get("WIS")));
         pattern.put("iEND", String.valueOf(stats.get("END")));
         pattern.put("iLCK", String.valueOf(stats.get("LCK")));
         pattern.put("iSTR", String.valueOf(stats.get("STR")));
         pattern.put("iDEX", String.valueOf(stats.get("DEX")));
         pattern.put("iINT", String.valueOf(stats.get("INT")));
         o.put(String.valueOf(ep.getId()), pattern);
      }

      enhp.put("cmd", "enhp");
      enhp.put("o", o);
      world.send(enhp, user);
   }

   private void retrieveInventory(User user, World world, Room room) {
      JSONObject eqp = new JSONObject();
      this.items = new JSONArray();
      this.hitems = new JSONArray();
      QueryResult result = world.db.jdbc.query("SELECT * FROM users_items WHERE Bank = 0 AND UserID = ?", new Object[]{user.properties.get("dbId")});

      while(true) {
         while(result.next()) {
            int eqpClass = result.getInt("id");
            int weaponItem = result.getInt("Quantity");
            int itemId = result.getInt("ItemID");
            int enhId = result.getInt("EnhID");
            boolean equipped = result.getBoolean("Equipped");
            Item itemObj = (Item)world.items.get(Integer.valueOf(itemId));
            Enhancement enhancement = (Enhancement)world.enhancements.get(Integer.valueOf(enhId));
            JSONObject item = Item.getItemJSON(itemObj, enhancement);
            item.put("bBank", "0");
            item.put("CharItemID", Integer.valueOf(eqpClass));
            item.put("iQty", Integer.valueOf(weaponItem));
            if(((Item)world.items.get(Integer.valueOf(itemId))).isCoins()) {
               Date eqpObj = result.getDate("DatePurchased");
               java.util.Date ei = new java.util.Date();
               long diff = ei.getTime() - eqpObj.getTime();
               long diffHours = diff / 3600000L;
               item.put("iHrs", Long.valueOf(diffHours));
               item.put("dPurchase", result.getString("DatePurchased").replaceAll(" ", "T"));
            }

            if(equipped) {
               item.put("bEquip", result.getString("Equipped"));
               JSONObject eqpObj1 = new JSONObject();
               eqpObj1.put("ItemID", Integer.valueOf(itemId));
               eqpObj1.put("sFile", itemObj.getFile());
               eqpObj1.put("sLink", itemObj.getLink());
               JSONObject ei1 = new JSONObject();
               ei1.put("uid", Integer.valueOf(user.getUserId()));
               ei1.put("cmd", "equipItem");
               ei1.put("ItemID", Integer.valueOf(itemId));
               ei1.put("strES", itemObj.getEquipment());
               ei1.put("sFile", itemObj.getFile());
               ei1.put("sLink", itemObj.getLink());
               ei1.put("sMeta", itemObj.getMeta());
               if(itemObj.getEquipment().equals("Weapon")) {
                  eqpObj1.put("sType", itemObj.getType());
                  ei1.put("sType", itemObj.getType());
                  user.properties.put("weaponitem", itemObj);
                  if(enhancement != null) {
                     user.properties.put("weaponitemenhancement", enhancement);
                  }
               }

               if(!eqp.has(itemObj.getEquipment())) {
                  world.sendToRoom(ei1, user, room);
                  if(itemObj.getEquipment().equals("ar") || itemObj.getEquipment().equals("ba") || itemObj.getEquipment().equals("he") || itemObj.getEquipment().equals("Weapon")) {
                     if(itemObj.getEquipment().equals("ar")) {
                        world.users.updateClass(user, itemObj, weaponItem);
                     }

                     world.users.updateStats(user, enhancement, itemObj.getEquipment());
                  }

                  eqp.put(itemObj.getEquipment(), eqpObj1);
               }
            }

            if(!itemObj.getEquipment().equals("ho") && !itemObj.getEquipment().equals("hi")) {
               this.items.add(item);
            } else {
               this.hitems.add(item);
            }
         }

         result.close();
         user.properties.put("equipment", eqp);
         JSONObject eqpClass1 = eqp.getJSONObject("ar");
         Item weaponItem1 = (Item)user.properties.get("weaponitem");
         if(weaponItem1 != null && world.specialskills.containsKey(Integer.valueOf(weaponItem1.getId()))) {
            world.users.loadSkills(user, (Item)world.items.get(Integer.valueOf(eqpClass1.getInt("ItemID"))), ((Integer)user.properties.get("cp")).intValue());
         }

         return;
      }
   }

   private void retrieveBoosts(User user, World world) {
      QueryResult boosts = world.db.jdbc.query("SELECT ExpBoostExpire, CpBoostExpire, GoldBoostExpire, RepBoostExpire FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
      if(boosts.next()) {
         JSONObject boost = new JSONObject();
         boost.put("bShowShop", "undefined");
         boost.put("op", "+");
         int xpSecsLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(SECOND, NOW(), ?)", new Object[]{boosts.getString("ExpBoostExpire")});
         if(xpSecsLeft > 0) {
            boost.put("cmd", "xpboost");
            boost.put("iSecsLeft", Integer.valueOf(xpSecsLeft));
            world.send(boost, user);
            user.properties.put("xpboost", Boolean.valueOf(true));
         }

         int cpSecsLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(SECOND,NOW(),?)", new Object[]{boosts.getString("CpBoostExpire")});
         if(cpSecsLeft > 0) {
            boost.put("cmd", "cpboost");
            boost.put("iSecsLeft", Integer.valueOf(cpSecsLeft));
            world.send(boost, user);
            user.properties.put("cpboost", Boolean.valueOf(true));
         }

         int repSecsLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(SECOND,NOW(),?)", new Object[]{boosts.getString("RepBoostExpire")});
         if(repSecsLeft > 0) {
            boost.put("cmd", "repboost");
            boost.put("iSecsLeft", Integer.valueOf(repSecsLeft));
            world.send(boost, user);
            user.properties.put("repboost", Boolean.valueOf(true));
         }

         int goldSecsLeft = world.db.jdbc.queryForInt("SELECT TIMESTAMPDIFF(SECOND,NOW(),?)", new Object[]{boosts.getString("GoldBoostExpire")});
         if(goldSecsLeft > 0) {
            boost.put("cmd", "gboost");
            boost.put("iSecsLeft", Integer.valueOf(goldSecsLeft));
            world.send(boost, user);
            user.properties.put("goldboost", Boolean.valueOf(true));
         }
      }

      boosts.close();
   }
}
