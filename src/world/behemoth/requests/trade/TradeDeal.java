package world.behemoth.requests.trade;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.requests.trade.TradeCancel;
import world.behemoth.world.Users;
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
      } else if(client.getUserId() != ((Integer)user.properties.get(Users.TRADE_TARGET)).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getUserId() != ((Integer)client.properties.get(Users.TRADE_TARGET)).intValue()) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else if(user.getName().equals(client.getName())) {
         (new TradeCancel()).process(new String[]{Integer.toString(-1)}, user, world, room);
         throw new RequestException(client.getName() + " has canceled the trade.");
      } else {
         JSONObject tr = new JSONObject();
         tr.element("cmd", "tradeDeal");
         tr.element("bitSuccess", 0);
         user.properties.put(Users.TRADE_DEAL, Boolean.valueOf(true));
         if(!((Boolean)client.properties.get(Users.TRADE_DEAL)).booleanValue()) {
            tr.element("bitSuccess", 1);
            tr.element("onHold", 1);
            world.send(tr, user);
         } else if(((Boolean)client.properties.get(Users.TRADE_LOCK)).booleanValue() && ((Boolean)user.properties.get(Users.TRADE_LOCK)).booleanValue()) {
            Map offers1 = (Map)user.properties.get(Users.TRADE_OFFERS);
            Map offers2 = (Map)client.properties.get(Users.TRADE_OFFERS);
            boolean currencyLimit1 = false;
            boolean currencyLimit2 = false;

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
            int inventoryCount1 = world.db.getJdbc().queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment NOT IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{user.properties.get(Users.DATABASE_ID)});
            int inventoryCount2 = world.db.getJdbc().queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment NOT IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{client.properties.get(Users.DATABASE_ID)});
            QueryResult userResult = world.db.getJdbc().query("SELECT Gold, Coins FROM users WHERE id = ?", new Object[]{user.properties.get(Users.DATABASE_ID)});
            if(userResult.next()) {
               coins1 = userResult.getInt("Coins");
               gold1 = userResult.getInt("Gold");
               if(coins1 >= ((Integer)user.properties.get(Users.TRADE_COINS)).intValue() && gold1 >= ((Integer)user.properties.get(Users.TRADE_GOLD)).intValue()) {
                  currencyCheck1 = true;
               }
               if (((Integer)client.properties.get(Users.TRADE_GOLD)).intValue() > 0) {
                  if(gold1 - ((Integer)user.properties.get(Users.TRADE_GOLD)).intValue() + ((Integer)client.properties.get(Users.TRADE_GOLD)).intValue() > (this.world.coreValues.get("intGoldMax")).intValue()) {
                     currencyLimit1 = true;
                  }
               }
               if (((Integer)client.properties.get(Users.TRADE_COINS)).intValue() > 0) {
                  if(coins1 - ((Integer)user.properties.get(Users.TRADE_COINS)).intValue() + ((Integer)client.properties.get(Users.TRADE_COINS)).intValue() > (this.world.coreValues.get("intCoinsMax")).intValue()) {
                     currencyLimit1 = true;
                  }
               }
            }

            userResult.close();
            userResult = world.db.getJdbc().query("SELECT Gold, Coins FROM users WHERE id = ?", new Object[]{client.properties.get(Users.DATABASE_ID)});
            if(userResult.next()) {
               coins2 = userResult.getInt("Coins");
               gold2 = userResult.getInt("Gold");
               if(coins2 >= ((Integer)client.properties.get(Users.TRADE_COINS)).intValue() && gold2 >= ((Integer)client.properties.get(Users.TRADE_GOLD)).intValue()) {
                  currencyCheck2 = true;
               }
               if (((Integer)user.properties.get(Users.TRADE_GOLD)).intValue() > 0) {
                  if(gold2 - ((Integer)client.properties.get(Users.TRADE_GOLD)).intValue() + ((Integer)user.properties.get(Users.TRADE_GOLD)).intValue() > (this.world.coreValues.get("intGoldMax")).intValue()) {
                     currencyLimit2 = true;
                  }
               }
               if (((Integer)user.properties.get(Users.TRADE_COINS)).intValue() > 0) {
                  if(coins2 - ((Integer)client.properties.get(Users.TRADE_COINS)).intValue() + ((Integer)user.properties.get(Users.TRADE_COINS)).intValue() > (this.world.coreValues.get("intCoinsMax")).intValue()) {
                     currencyLimit2 = true;
                  }
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
                  itemObj = world.db.getJdbc().queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), client.properties.get(Users.DATABASE_ID)});
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
                  itemObj = world.db.getJdbc().queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
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

            if(inventoryCount1 >= ((Integer)user.properties.get(Users.SLOTS_BAG)).intValue()) {
               tr.element("msg", "Your inventory is full!");
               world.send(tr, user);
               tr.element("msg", user.getName() + "\'s inventory is full!");
               world.send(tr, client);
            } else if(inventoryCount2 >= ((Integer)client.properties.get(Users.SLOTS_BAG)).intValue()) {
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
            } else if(currencyLimit1) {
               tr.element("msg", "You have reach max gold/coins!");
               world.send(tr, user);
               tr.element("msg", user.getName() + " have reach max gold/coins!");
               world.send(tr, client);
            } else if(currencyLimit2) {
               tr.element("msg", "You have reach max gold/coins!");
               world.send(tr, client);
               tr.element("msg", client.getName() + " have reach max gold/coins!");
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
               user.properties.put(Users.TRADE_TARGET, Integer.valueOf(-1));
               user.properties.put(Users.TRADE_LOCK, Boolean.valueOf(false));
               user.properties.put(Users.TRADE_DEAL, Boolean.valueOf(false));
               client.properties.put(Users.TRADE_TARGET, Integer.valueOf(-1));
               client.properties.put(Users.TRADE_LOCK, Boolean.valueOf(false));
               client.properties.put(Users.TRADE_DEAL, Boolean.valueOf(false));
               i$ = offers1.entrySet().iterator();

               Map enhances;
               Item itemObj1;
               while(i$.hasNext()) {
                  entry = (Entry)i$.next();
                  itemId = ((Integer)entry.getKey()).intValue();
                  quantity = ((Integer)entry.getValue()).intValue();
                  itemObj1 = (Item)world.items.get(Integer.valueOf(itemId));
                  if(itemObj1 != null) {
                     enhances = (Map)user.properties.get(Users.TRADE_OFFERS_ENHID);
                     this.sendItem(client, user, itemObj1, quantity, ((Integer)enhances.get(Integer.valueOf(itemId))).intValue());
                  }
               }

               i$ = offers2.entrySet().iterator();

               while(i$.hasNext()) {
                  entry = (Entry)i$.next();
                  itemId = ((Integer)entry.getKey()).intValue();
                  quantity = ((Integer)entry.getValue()).intValue();
                  itemObj1 = (Item)world.items.get(Integer.valueOf(itemId));
                  if(itemObj1 != null) {
                     enhances = (Map)client.properties.get(Users.TRADE_OFFERS_ENHID);
                     this.sendItem(user, client, itemObj1, quantity, ((Integer)enhances.get(Integer.valueOf(itemId))).intValue());
                  }
               }

               user.properties.put(Users.TRADE_OFFERS, new HashMap());
               user.properties.put(Users.TRADE_OFFERS_ENHID, new HashMap());
               client.properties.put(Users.TRADE_OFFERS, new HashMap());
               client.properties.put(Users.TRADE_OFFERS_ENHID, new HashMap());
               this.updateGoldCoins(user, client, gold1 - ((Integer)user.properties.get(Users.TRADE_GOLD)).intValue() + ((Integer)client.properties.get(Users.TRADE_GOLD)).intValue(), coins1 - ((Integer)user.properties.get(Users.TRADE_COINS)).intValue() + ((Integer)client.properties.get(Users.TRADE_COINS)).intValue());
               this.updateGoldCoins(client, user, gold2 - ((Integer)client.properties.get(Users.TRADE_GOLD)).intValue() + ((Integer)user.properties.get(Users.TRADE_GOLD)).intValue(), coins2 - ((Integer)client.properties.get(Users.TRADE_COINS)).intValue() + ((Integer)user.properties.get(Users.TRADE_COINS)).intValue());
               user.properties.put(Users.TRADE_GOLD, Integer.valueOf(0));
               user.properties.put(Users.TRADE_COINS, Integer.valueOf(0));
               client.properties.put(Users.TRADE_GOLD, Integer.valueOf(0));
               client.properties.put(Users.TRADE_COINS, Integer.valueOf(0));
               tr.element("bitSuccess", 1);
               world.send(tr, user);
               world.send(tr, client);
               world.users.log(user, "Complete Transaction[Trade]", "User: " + user.getName() + " completed transaction with User: " + client.getName(), (world.areas.get(room.getName().split("-")[0])).getId());
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

   private void updateGoldCoins(User user, User client, int gold, int coins) {
      JSONObject tr = new JSONObject();
      tr.element("cmd", "updateGoldCoins");
      tr.element("intGold", gold);
      tr.element("intCoins", coins);
      this.world.send(tr, user);
      this.world.db.getJdbc().run("INSERT INTO users_logs_trades (FromUserID, ToUserID, Gold, Coins, Date) VALUES (?, ?, ?, ?, NOW())", new Object[]{user.properties.get(Users.DATABASE_ID), client.properties.get(Users.DATABASE_ID), ((Integer) user.properties.get(Users.TRADE_GOLD)).intValue(), ((Integer) user.properties.get(Users.TRADE_COINS)).intValue()});
      this.world.db.getJdbc().run("UPDATE users SET Coins = ?, Gold = ? WHERE id = ?", new Object[]{Integer.valueOf(coins), Integer.valueOf(gold), user.properties.get(Users.DATABASE_ID)});
      user.properties.put(Users.GOLD, gold);
      user.properties.put(Users.COINS, coins);
   }

   private void sendItem(User user, User client, Item itemObj, int quantity, int enhId) throws RequestException {
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
      this.world.db.getJdbc().beginTransaction();

      try {
         QueryResult je = this.world.db.getJdbc().query("SELECT id FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
         int charItemId;
         if(je.next()) {
            charItemId = je.getInt("id");
            je.close();
            if(itemObj.getStack() > 1) {
               int itemQty = this.world.db.getJdbc().queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
               if(itemQty >= itemObj.getStack()) {
                  this.world.db.getJdbc().rollbackTransaction();
                  this.world.send(gd, user);
                  return;
               }

               this.world.db.getJdbc().run("INSERT INTO users_logs_trades_items (ToUserID, FromUserID, ItemID, EnhID, Quantity, Date) VALUES (?, ?, ?, ?, ?, NOW())", new Object[]{user.properties.get(Users.DATABASE_ID), client.properties.get(Users.DATABASE_ID), Integer.valueOf(itemId), Integer.valueOf(enhId), Integer.valueOf(quantity)});
               this.world.db.getJdbc().run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemQty + quantity), Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
            } else if(itemObj.getStack() == 1) {
               this.world.db.getJdbc().rollbackTransaction();
               this.world.send(gd, user);
            }
         } else {
            je.close();
            this.world.db.getJdbc().run("INSERT INTO users_logs_trades_items (ToUserID, FromUserID, ItemID, EnhID, Quantity, Date) VALUES (?, ?, ?, ?, ?, NOW())", new Object[]{user.properties.get(Users.DATABASE_ID), client.properties.get(Users.DATABASE_ID), Integer.valueOf(itemId), Integer.valueOf(enhId), Integer.valueOf(quantity)});
            this.world.db.getJdbc().run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, \'2012-12-12 01:00:00\')", new Object[]{user.properties.get(Users.DATABASE_ID), Integer.valueOf(itemId), Integer.valueOf(enhId), Integer.valueOf(quantity)});
            charItemId = Long.valueOf(this.world.db.getJdbc().getLastInsertId()).intValue();
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
            this.world.db.getJdbc().rollbackTransaction();
         }
      } catch (JdbcException var17) {
         if(this.world.db.getJdbc().isInTransaction()) {
            this.world.db.getJdbc().rollbackTransaction();
         }
      } finally {
         if(this.world.db.getJdbc().isInTransaction()) {
            this.world.db.getJdbc().commitTransaction();
         }

      }

   }

   private boolean turnInItems(User user, Map<Integer, Integer> items, User user2, Map<Integer, Integer> items2) {
      boolean valid = true;
      this.world.db.getJdbc().beginTransaction();

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
            itemResult = this.world.db.getJdbc().query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
            if(!itemResult.next()) {
               valid = false;
               itemResult.close();
               this.world.db.getJdbc().rollbackTransaction();
               this.world.users.log(user, "Suspicous TurnIn", "Item to turn in not found in database.");
               break;
            }

            quantity = itemResult.getInt("Quantity");
            quantityLeft = quantity - quantityRequirement;
            itemResult.close();
            if(quantityLeft > 0) {
               valid = true;
               this.world.db.getJdbc().run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(quantityLeft), Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
            } else {
               if(quantityLeft < 0) {
                  valid = false;
                  this.world.db.getJdbc().rollbackTransaction();
                  this.world.users.log(user, "Suspicous TurnIn", "Quantity requirement for turning in item is lacking.");
                  break;
               }

               valid = true;
               this.world.db.getJdbc().run("DELETE FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get(Users.DATABASE_ID)});
            }
         }

         if(this.world.db.getJdbc().isInTransaction()) {
            for(e = items2.entrySet().iterator(); e.hasNext(); itemResult.close()) {
               entry = (Entry)e.next();
               itemId = ((Integer)entry.getKey()).intValue();
               quantityRequirement = ((Integer)entry.getValue()).intValue();
               itemResult = this.world.db.getJdbc().query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user2.properties.get(Users.DATABASE_ID)});
               if(!itemResult.next()) {
                  valid = false;
                  itemResult.close();
                  this.world.db.getJdbc().rollbackTransaction();
                  this.world.users.log(user, "Suspicous TurnIn", "Item to turn in not found in database.");
                  break;
               }

               quantity = itemResult.getInt("Quantity");
               quantityLeft = quantity - quantityRequirement;
               itemResult.close();
               if(quantityLeft > 0) {
                  valid = true;
                  this.world.db.getJdbc().run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(quantityLeft), Integer.valueOf(itemId), user2.properties.get(Users.DATABASE_ID)});
               } else {
                  if(quantityLeft < 0) {
                     valid = false;
                     this.world.db.getJdbc().rollbackTransaction();
                     this.world.users.log(user, "Suspicous TurnIn", "Quantity requirement for turning in item is lacking.");
                     break;
                  }

                  valid = true;
                  this.world.db.getJdbc().run("DELETE FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user2.properties.get(Users.DATABASE_ID)});
               }
            }
         }
      } catch (Exception var16) {
         if(this.world.db.getJdbc().isInTransaction()) {
            this.world.db.getJdbc().rollbackTransaction();
         }
      } finally {
         if(this.world.db.getJdbc().isInTransaction()) {
            this.world.db.getJdbc().commitTransaction();
         }

      }

      return valid;
   }
}
