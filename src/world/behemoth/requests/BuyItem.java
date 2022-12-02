package world.behemoth.requests;

import world.behemoth.db.objects.Item;
import world.behemoth.db.objects.Shop;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import jdbchelper.JdbcException;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class BuyItem implements IRequest {
   public BuyItem() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int itemId = Integer.parseInt(params[0]);
      int shopId = Integer.parseInt(params[1]);
      Item item = (Item)world.items.get(Integer.valueOf(itemId));
      Shop shop = (Shop)world.shops.get(Integer.valueOf(shopId));
      if(!shop.items.containsValue(Integer.valueOf(itemId))) {
         world.users.log(user, "Packet Edit [BuyItem]", "Attempted to purchase an item from wrong shop");
      } else if(shop.isStaff() && !user.isAdmin() && !user.isModerator()) {
         world.users.log(user, "Packet Edit [BuyItem]", "Attempted to purchase from staff shop");
      } else {
         if(item.getFactionId() > 1) {
            Map cost = (Map)user.properties.get("factions");
            if(!cost.containsKey(Integer.valueOf(item.getFactionId()))) {
               world.users.log(user, "Packet Edit [EquipItem]", "Attempted to equip an item without required reputation.");
               return;
            }

            if(((Integer)cost.get(Integer.valueOf(item.getFactionId()))).intValue() < item.getReqReputation()) {
               world.users.log(user, "Packet Edit [EquipItem]", "Attempted to equip an item without required reputation.");
            }
         }

         int cost1 = item.getCost() * item.getQuantity();
         int userLevel = ((Integer)user.properties.get("level")).intValue();
         int houseCount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{user.properties.get("dbId")});
         int inventoryCount = world.db.jdbc.queryForInt("SELECT COUNT(*) AS rowcount FROM users_items LEFT JOIN items ON items.id = users_items.ItemID WHERE Equipment NOT IN (\'ho\',\'hi\') AND Bank = 0 AND UserID = ?", new Object[]{user.properties.get("dbId")});
         boolean upgrade = ((Integer)user.properties.get("upgdays")).intValue() > 0;
         JSONObject buy = new JSONObject();
         buy.put("cmd", "buyItem");
         buy.put("bitSuccess", Integer.valueOf(0));
         buy.put("CharItemID", Integer.valueOf(-1));
         if(item.isUpgrade() && !upgrade) {
            if(user.properties.get("language").equals("BR")) {
               buy.put("strMessage", "Este item \u00e9 \u00fanico membro!");
            } else {
               buy.put("strMessage", "This item is member only!");
            }
         } else if(item.getLevel() > userLevel) {
            if(user.properties.get("language").equals("BR")) {
               buy.put("strMessage", "Exig\u00eancia de n\u00edvel n\u00e3o conheci!");
            } else {
               buy.put("strMessage", "Level requirement not met!");
            }
         } else if(item.isStaff() && !user.isAdmin() && !user.isModerator()) {
            if(user.properties.get("language").equals("BR")) {
               buy.put("strMessage", "Teste Item: N\u00e3o pode ser comprado ainda!");
            } else {
               buy.put("strMessage", "Test Item: Cannot be purchased yet!");
            }

            world.users.log(user, "Packet Edit [BuyItem]", "Attempted to purchase a staff only item");
         } else if(!shop.isHouse() && inventoryCount >= ((Integer)user.properties.get("bagslots")).intValue()) {
            if(user.properties.get("language").equals("BR")) {
               buy.put("strMessage", "Inventory Full!");
            } else {
               buy.put("strMessage", "Invent\u00e1rio completo!");
            }
         } else if(shop.isHouse() && houseCount >= ((Integer)user.properties.get("houseslots")).intValue()) {
            if(user.properties.get("language").equals("BR")) {
               buy.put("strMessage", "Casa invent\u00e1rio completo!");
            } else {
               buy.put("strMessage", "House Inventory Full!");
            }
         } else {
            if(shop.isLimited()) {
               int je = world.db.jdbc.queryForInt("SELECT QuantityRemain FROM shops_items WHERE ShopID = ? AND ItemID = ?", new Object[]{Integer.valueOf(shopId), Integer.valueOf(itemId)});
               if(je <= 0) {
                  buy.put("strMessage", item.getName());
                  buy.put("bSoldOut", Integer.valueOf(1));
                  world.send(buy, user);
                  return;
               }
            }

            int gold;
            int coinsLeft;
            if(!item.requirements.isEmpty()) {
               Iterator je1 = item.requirements.entrySet().iterator();

               while(je1.hasNext()) {
                  Entry coins = (Entry)je1.next();
                  gold = ((Integer)coins.getKey()).intValue();
                  Item valid = (Item)world.items.get(Integer.valueOf(gold));
                  int itemResult = ((Integer)coins.getValue()).intValue() >= valid.getStack()?valid.getStack():((Integer)coins.getValue()).intValue();
                  QueryResult charItemId = world.db.jdbc.query("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(gold), user.properties.get("dbId")});
                  if(!charItemId.next()) {
                     charItemId.close();
                     if(user.properties.get("language").equals("BR")) {
                        buy.put("strMessage", "Voc\u00ea n\u00e3o cumprem os requisitos para comprar este item.");
                     } else {
                        buy.put("strMessage", "You do not meet the requirements to buy this item.");
                     }

                     world.send(buy, user);
                     return;
                  }

                  coinsLeft = charItemId.getInt("Quantity");
                  charItemId.close();
                  if(coinsLeft < itemResult) {
                     if(user.properties.get("language").equals("BR")) {
                        buy.put("strMessage", "Voc\u00ea n\u00e3o cumprem os requisitos para comprar este item.");
                     } else {
                        buy.put("strMessage", "You do not meet the requirements to buy this item.");
                     }

                     world.send(buy, user);
                     return;
                  }

                  charItemId.close();
               }

               world.users.turnInItems(user, item.requirements);
            }

            world.db.jdbc.beginTransaction();

            try {
               QueryResult je2 = world.db.jdbc.query("SELECT Gold, Coins FROM users WHERE id = ? FOR UPDATE", new Object[]{user.properties.get("dbId")});
               if(je2.next()) {
                  int coins1 = je2.getInt("Coins");
                  gold = je2.getInt("Gold");
                  je2.close();
                  boolean valid1 = item.isCoins() && cost1 <= coins1?true:cost1 <= gold;
                  if(valid1) {
                     QueryResult itemResult1 = world.db.jdbc.query("SELECT id FROM users_items WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
                     int charItemId1;
                     if(itemResult1.next()) {
                        charItemId1 = itemResult1.getInt("id");
                        itemResult1.close();
                        if(item.getStack() > 1) {
                           coinsLeft = world.db.jdbc.queryForInt("SELECT Quantity FROM users_items WHERE ItemID = ? AND UserID = ? FOR UPDATE", new Object[]{Integer.valueOf(itemId), user.properties.get("dbId")});
                           if(coinsLeft >= item.getStack()) {
                              world.db.jdbc.rollbackTransaction();
                              if(user.properties.get("language").equals("BR")) {
                                 buy.put("strMessage", "Voc\u00ea n\u00e3o pode ter mais do que " + item.getStack() + " desse item!");
                              } else {
                                 buy.put("strMessage", "You cannot have more than " + item.getStack() + " of that item!");
                              }

                              world.send(buy, user);
                              world.users.log(user, "Packet Edit [BuyItem]", "Attempted to purchase more than stack value");
                              return;
                           }

                           world.db.jdbc.run("UPDATE users_items SET Quantity = ? WHERE ItemID = ? AND UserID = ?", new Object[]{Integer.valueOf(coinsLeft + item.getQuantity()), Integer.valueOf(itemId), user.properties.get("dbId")});
                        } else if(item.getStack() == 1) {
                           world.db.jdbc.rollbackTransaction();
                           if(user.properties.get("language").equals("BR")) {
                              buy.put("strMessage", "Voc\u00ea n\u00e3o pode ter mais do que " + item.getStack() + " desse item!");
                           } else {
                              buy.put("strMessage", "You cannot have more than " + item.getStack() + " of that item!");
                           }

                           world.send(buy, user);
                           world.users.log(user, "Packet Edit [BuyItem]", "Attempted to purchase more than stack value");
                           return;
                        }
                     } else {
                        world.db.jdbc.run("INSERT INTO users_items (UserID, ItemID, EnhID, Equipped, Quantity, Bank, DatePurchased) VALUES (?, ?, ?, 0, ?, 0, NOW())", new Object[]{user.properties.get("dbId"), Integer.valueOf(itemId), Integer.valueOf(item.getEnhId()), Integer.valueOf(item.getQuantity())});
                        charItemId1 = Long.valueOf(world.db.jdbc.getLastInsertId()).intValue();
                     }

                     itemResult1.close();
                     if(charItemId1 > 0) {
                        if(!item.isCoins()) {
                           coinsLeft = gold - cost1;
                           world.db.jdbc.run("UPDATE users SET Gold = ? WHERE id=?", new Object[]{Integer.valueOf(coinsLeft), user.properties.get("dbId")});
                        } else {
                           coinsLeft = coins1 - cost1;
                           world.db.jdbc.run("UPDATE users SET Coins = ? WHERE id=?", new Object[]{Integer.valueOf(coinsLeft), user.properties.get("dbId")});
                        }

                        if(shop.isLimited()) {
                           world.db.jdbc.run("UPDATE shops_items SET QuantityRemain = (QuantityRemain - 1) WHERE ShopID = ? AND ItemID = ?", new Object[]{Integer.valueOf(shopId), Integer.valueOf(itemId)});
                        }

                        buy.put("bitSuccess", Integer.valueOf(1));
                        buy.put("CharItemID", Integer.valueOf(charItemId1));
                     } else {
                        world.db.jdbc.rollbackTransaction();
                        buy.put("strMessage", "An error occured while purchasing the item!");
                     }
                  } else if(user.properties.get("language").equals("BR")) {
                     buy.put("strMessage", "Fundos insuficientes!");
                  } else {
                     buy.put("strMessage", "Insufficient funds!");
                  }
               }

               je2.close();
            } catch (JdbcException var25) {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.rollbackTransaction();
               }

               SmartFoxServer.log.severe("Error in buy item transaction: " + var25.getMessage());
            } finally {
               if(world.db.jdbc.isInTransaction()) {
                  world.db.jdbc.commitTransaction();
               }

            }
         }

         world.send(buy, user);
      }
   }
}
