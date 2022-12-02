package world.behemoth.requests.trade;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import jdbchelper.JdbcException;
import jdbchelper.NoResultException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class TradeDeal implements IRequest {
   private World world;

   public TradeDeal() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      this.world = world;
      User client = SmartFoxServer.getInstance().getUserById(Integer.valueOf(Integer.parseInt(params[0])));
      if(client == null) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException("Trade has been canceled due to other player can\'t be found!");
      } else if(client.getUserId() != ((Integer)user.properties.get("tradetgt")).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getUserId() != ((Integer)client.properties.get("tradetgt")).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getName().equals(client.getName())) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else {
         JSONObject tr = new JSONObject();
         tr.element("cmd", "tradeDeal");
         tr.element("bitSuccess", 0);
         user.properties.put("tradedeal", Boolean.valueOf(true));
         if(!((Boolean)client.properties.get("tradedeal")).booleanValue()) {
            tr.element("bitSuccess", 1);
            tr.element("onHold", 1);
            world.send(tr, user);
         } else if(((Boolean)client.properties.get("tradelock")).booleanValue() && ((Boolean)user.properties.get("tradelock")).booleanValue()) {
            Map offers1 = (Map)user.properties.get("offer");
            Map offers2 = (Map)client.properties.get("offer");
            boolean currencyCheck1 = false;
            boolean currencyCheck2 = false;
            boolean stackCheck1 = true;
            boolean stackCheck2 = true;
            Item item1 = null;
            Item item2 = null;
            int coins1 = 0;
            int gold1 = 0;
            int coins2 = 0;
            int gold2 = 0;
            int inventoryCount1 = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment NOT IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{user.properties.get("dbId")});
            int inventoryCount2 = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment NOT IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{client.properties.get("dbId")});
            QueryResult userResult = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ?", new Object[]{user.properties.get("dbId")});
            if(userResult.next()) {
               coins1 = userResult.getInt("Coins");
               gold1 = userResult.getInt("Gold");
               if(coins1 >= ((Integer)user.properties.get("tradecoins")).intValue() && gold1 >= ((Integer)user.properties.get("tradegold")).intValue()) {
                  currencyCheck1 = true;
               }
            }

            userResult.close();
            userResult = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ?", new Object[]{client.properties.get("dbId")});
            if(userResult.next()) {
               coins2 = userResult.getInt("Coins");
               gold2 = userResult.getInt("Gold");
               if(coins2 >= ((Integer)client.properties.get("tradecoins")).intValue() && gold2 >= ((Integer)client.properties.get("tradegold")).intValue()) {
                  currencyCheck2 = true;
               }
            }

            userResult.close();
            Iterator i$ = offers1.entrySet().iterator();

            Entry entry;
            int itemId;
            int quantity;
            int itemObj;
            while(i$.hasNext()) {
               entry = (Entry)i$.next();
               itemId = ((Integer)entry.getKey()).intValue();
               quantity = ((Integer)entry.getValue()).intValue();
               item1 = (Item)world.items.get(Integer.valueOf(itemId));

               try {
                  itemObj = world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), client.properties.get("dbId")});
                  if(item1.getStack() <= 1) {
                     stackCheck1 = false;
                     break;
                  }

                  if(itemObj + quantity > item1.getStack()) {
                     stackCheck1 = false;
                     break;
                  }
               } catch (NoResultException var29) {
                  ;
               }
            }

            i$ = offers2.entrySet().iterator();

            while(i$.hasNext()) {
               entry = (Entry)i$.next();
               itemId = ((Integer)entry.getKey()).intValue();
               quantity = ((Integer)entry.getValue()).intValue();
               item2 = (Item)world.items.get(Integer.valueOf(itemId));

               try {
                  itemObj = world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
                  if(item2.getStack() <= 1) {
                     stackCheck2 = false;
                     break;
                  }

                  if(itemObj + quantity > item2.getStack()) {
                     stackCheck2 = false;
                     break;
                  }
               } catch (NoResultException var28) {
                  ;
               }
            }

            if(inventoryCount1 >= ((Integer)user.properties.get("bagslots")).intValue()) {
               tr.element("msg", "Your inventory is full!");
               world.send(tr, user);
               tr.element("msg", user.getName() + "\'s inventory is full!");
               world.send(tr, client);
            } else if(inventoryCount2 >= ((Integer)client.properties.get("bagslots")).intValue()) {
               tr.element("msg", "Your inventory is full!");
               world.send(tr, client);
               tr.element("msg", client.getName() + "\'s inventory is full!");
               world.send(tr, user);
            } else if(!currencyCheck1) {
               tr.element("msg", "You do not have enough gold/coins!");
               world.send(tr, user);
               tr.element("msg", user.getName() + " does not have enough gold/coins!");
               world.send(tr, client);
            } else if(!currencyCheck2) {
               tr.element("msg", "You do not have enough gold/coins!");
               world.send(tr, client);
               tr.element("msg", client.getName() + " does not have enough gold/coins!");
               world.send(tr, user);
            } else if(!stackCheck1) {
               tr.element("msg", "You cannot have more than " + item1.getStack() + " of " + item1.getName() + "!");
               world.send(tr, client);
               tr.element("msg", client.getName() + " cannot have more than " + item1.getStack() + " of " + item1.getName() + "!");
               world.send(tr, user);
            } else if(!stackCheck2) {
               tr.element("msg", "You cannot have more than " + item2.getStack() + " of " + item2.getName() + "!");
               world.send(tr, user);
               tr.element("msg", user.getName() + " cannot have more than " + item2.getStack() + " of " + item2.getName() + "!");
               world.send(tr, client);
            } else if(this.turnInItems(user, offers1, client, offers2)) {
               user.properties.put("tradetgt", Integer.valueOf(-1));
               user.properties.put("tradelock", Boolean.valueOf(false));
               user.properties.put("tradedeal", Boolean.valueOf(false));
               client.properties.put("tradetgt", Integer.valueOf(-1));
               client.properties.put("tradelock", Boolean.valueOf(false));
               client.properties.put("tradedeal", Boolean.valueOf(false));
               i$ = offers1.entrySet().iterator();

               Map enhances;
               Item itemObj1;
               while(i$.hasNext()) {
                  entry = (Entry)i$.next();
                  itemId = ((Integer)entry.getKey()).intValue();
                  quantity = ((Integer)entry.getValue()).intValue();
                  itemObj1 = (Item)world.items.get(Integer.valueOf(itemId));
                  if(itemObj1 != null) {
                     enhances = (Map)user.properties.get("offerenh");
                     this.sendItem(client, itemObj1, quantity, ((Integer)enhances.get(Integer.valueOf(itemId))).intValue());
                  }
               }

               i$ = offers2.entrySet().iterator();

               while(i$.hasNext()) {
                  entry = (Entry)i$.next();
                  itemId = ((Integer)entry.getKey()).intValue();
                  quantity = ((Integer)entry.getValue()).intValue();
                  itemObj1 = (Item)world.items.get(Integer.valueOf(itemId));
                  if(itemObj1 != null) {
                     enhances = (Map)client.properties.get("offerenh");
                     this.sendItem(user, itemObj1, quantity, ((Integer)enhances.get(Integer.valueOf(itemId))).intValue());
                  }
               }

               user.properties.put("offer", new HashMap());
               user.properties.put("offerenh", new HashMap());
               client.properties.put("offer", new HashMap());
               client.properties.put("offerenh", new HashMap());
               this.updateGoldCoins(user, gold1 - ((Integer)user.properties.get("tradegold")).intValue() + ((Integer)client.properties.get("tradegold")).intValue(), coins1 - ((Integer)user.properties.get("tradecoins")).intValue() + ((Integer)client.properties.get("tradecoins")).intValue());
               this.updateGoldCoins(client, gold2 - ((Integer)client.properties.get("tradegold")).intValue() + ((Integer)user.properties.get("tradegold")).intValue(), coins2 - ((Integer)client.properties.get("tradecoins")).intValue() + ((Integer)user.properties.get("tradecoins")).intValue());
               user.properties.put("tradegold", Integer.valueOf(0));
               user.properties.put("tradecoins", Integer.valueOf(0));
               client.properties.put("tradegold", Integer.valueOf(0));
               client.properties.put("tradecoins", Integer.valueOf(0));
               tr.element("bitSuccess", 1);
               world.send(tr, user);
               world.send(tr, client);
               world.users.log(user, "Complete Transaction[Trade]", "User: " + user.getName() + " completed transaction with User: " + client.getName());
               world.send(new String[]{"server", "Trade success!"}, user);
               world.send(new String[]{"server", "Trade success!"}, client);
            } else {
               tr.element("msg", "You\'re experiencing technical difficulties.. Please relog!");
               world.send(tr, client);
               world.send(tr, user);
            }
         } else {
            tr.element("msg", "Your/His offer/s is not yet confirmed!");
            world.send(tr, user);
            world.send(tr, client);
         }

      }
   }

   private void updateGoldCoins(User user, int gold, int coins) {
      JSONObject tr = new JSONObject();
      tr.element("cmd", "updateGoldCoins");
      tr.element("gold", gold);
      tr.element("coins", coins);
      this.world.send(tr, user);
      this.world.db.jdbc.run("UPDATE users SET Coins = ?, Gold = ? WHERE id = ?", new Object[]{Integer.valueOf(coins), Integer.valueOf(gold), user.properties.get("dbId")});
   }

   private void sendItem(User user, Item itemObj, int quantity, int enhId) throws RequestException {
      int itemId = itemObj.getId();
      JSONObject di = new JSONObject();
      JSONObject arrItems = new JSONObject();
      Enhancement enhancement = (Enhancement)this.world.enhancements.get(Integer.valueOf(enhId));
      JSONObject item = Item.getItemJSON(itemObj, enhancement);
      item.element("iQty", quantity);
      item.element("iReqCP", itemObj.getReqClassPoints());
      item.element("iReqRep", itemObj.getReqReputation());
      item.element("FactionID", itemObj.getFactionId());
      item.element("sFaction", this.world.factions.get(Integer.valueOf(itemObj.getFactionId())));
      arrItems.put(Integer.valueOf(itemId), item);
      di.element("items", arrItems);
      di.element("addItem", 1);
      di.element("cmd", "dropItem");
      this.world.send(di, user);
      JSONObject gd = new JSONObject();
      gd.element("cmd", "getDrop");
      gd.element("ItemID", itemId);
      gd.element("bSuccess", "0");
      this.world.db.jdbc.beginTransaction();

      try {
         QueryResult je = this.world.db.jdbc.query("SELECT id FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
         int charItemId;
         if(je.next()) {
            charItemId = je.getInt("id");
            je.close();
            if(itemObj.getStack() > 1) {
               int itemQty = this.world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
               if(itemQty >= itemObj.getStack()) {
                  this.world.db.jdbc.rollbackTransaction();
                  this.world.send(gd, user);
                  return;
               }

               this.world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemQty + quantity), Integer.valueOf(itemId), user.properties.get("dbId")});
            } else if(itemObj.getStack() == 1) {
               this.world.db.jdbc.rollbackTransaction();
               this.world.send(gd, user);
            }
         } else {
            je.close();
            this.world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, \'2012-12-12 01:00:00\')", new Object[]{user.properties.get("dbId"), Integer.valueOf(itemId), Integer.valueOf(enhId), Integer.valueOf(quantity)});
            charItemId = Long.valueOf(this.world.db.jdbc.getLastInsertId()).intValue();
         }

         je.close();
         if(charItemId > 0) {
            gd.element("CharItemID", charItemId);
            gd.element("bBank", false);
            gd.element("iQty", quantity);
            gd.element("bSuccess", "1");
            if(enhancement.getId() > 0) {
               gd.element("EnhID", enhancement.getId());
               gd.element("EnhLvl", enhancement.getLevel());
               gd.element("EnhPatternID", enhancement.getPatternId());
               gd.element("EnhRty", enhancement.getRarity());
               gd.element("iRng", itemObj.getRange());
               gd.element("EnhRng", itemObj.getRange());
               gd.element("InvEnhPatternID", enhancement.getPatternId());
               gd.element("EnhDPS", enhancement.getDPS());
            }

            this.world.send(gd, user);
         } else {
            this.world.db.jdbc.rollbackTransaction();
         }
      } catch (JdbcException var17) {
         if(this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.rollbackTransaction();
         }
      } finally {
         if(this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.commitTransaction();
         }

      }

   }

   private boolean turnInItems(User user, Map<Integer, Integer> items, User user2, Map<Integer, Integer> items2) {
      boolean valid = true;
      this.world.db.jdbc.beginTransaction();

      try {
         Iterator e;
         Entry entry;
         int itemId;
         int quantityRequirement;
         QueryResult itemResult;
         int quantity;
         int quantityLeft;
         for(e = items.entrySet().iterator(); e.hasNext(); itemResult.close()) {
            entry = (Entry)e.next();
            itemId = ((Integer)entry.getKey()).intValue();
            quantityRequirement = ((Integer)entry.getValue()).intValue();
            itemResult = this.world.db.jdbc.query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
            if(!itemResult.next()) {
               valid = false;
               itemResult.close();
               this.world.db.jdbc.rollbackTransaction();
               this.world.users.log(user, "Suspicous TurnIn", "Item to turn in not found in database.");
               break;
            }

            quantity = itemResult.getInt("Quantity");
            quantityLeft = quantity - quantityRequirement;
            itemResult.close();
            if(quantityLeft > 0) {
               valid = true;
               this.world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(quantityLeft), Integer.valueOf(itemId), user.properties.get("dbId")});
            } else {
               if(quantityLeft < 0) {
                  valid = false;
                  this.world.db.jdbc.rollbackTransaction();
                  this.world.users.log(user, "Suspicous TurnIn", "Quantity requirement for turning in item is lacking.");
                  break;
               }

               valid = true;
               this.world.db.jdbc.run("DELETE FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
            }
         }

         if(this.world.db.jdbc.isInTransaction()) {
            for(e = items2.entrySet().iterator(); e.hasNext(); itemResult.close()) {
               entry = (Entry)e.next();
               itemId = ((Integer)entry.getKey()).intValue();
               quantityRequirement = ((Integer)entry.getValue()).intValue();
               itemResult = this.world.db.jdbc.query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user2.properties.get("dbId")});
               if(!itemResult.next()) {
                  valid = false;
                  itemResult.close();
                  this.world.db.jdbc.rollbackTransaction();
                  this.world.users.log(user, "Suspicous TurnIn", "Item to turn in not found in database.");
                  break;
               }

               quantity = itemResult.getInt("Quantity");
               quantityLeft = quantity - quantityRequirement;
               itemResult.close();
               if(quantityLeft > 0) {
                  valid = true;
                  this.world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(quantityLeft), Integer.valueOf(itemId), user2.properties.get("dbId")});
               } else {
                  if(quantityLeft < 0) {
                     valid = false;
                     this.world.db.jdbc.rollbackTransaction();
                     this.world.users.log(user, "Suspicous TurnIn", "Quantity requirement for turning in item is lacking.");
                     break;
                  }

                  valid = true;
                  this.world.db.jdbc.run("DELETE FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user2.properties.get("dbId")});
               }
            }
         }
      } catch (Exception var16) {
         if(this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.rollbackTransaction();
         }
      } finally {
         if(this.world.db.jdbc.isInTransaction()) {
            this.world.db.jdbc.commitTransaction();
         }

      }

      return valid;
   }
}
