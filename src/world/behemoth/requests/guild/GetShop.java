package world.behemoth.requests.guild;

import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.db.objects.Shop;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Map.Entry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetShop implements IRequest {
   public GetShop() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int shopId = Integer.parseInt(params[1]);
      if(world.shops.containsKey(Integer.valueOf(shopId))) {
         Shop shopObj = (Shop)world.shops.get(Integer.valueOf(shopId));
         JSONObject guildhall = new JSONObject();
         JSONObject shopinfo = new JSONObject();
         JSONArray items = new JSONArray();
         Iterator i$ = shopObj.items.entrySet().iterator();

         while(true) {
            int shopItemId;
            Item itemObj;
            do {
               if(!i$.hasNext()) {
                  shopinfo.put("bHouse", Integer.valueOf(shopObj.isHouse()?1:0));
                  shopinfo.put("bStaff", Integer.valueOf(shopObj.isStaff()?1:0));
                  shopinfo.put("bUpgrd", Integer.valueOf(shopObj.isUpgrade()?1:0));
                  shopinfo.put("bLimited", Integer.valueOf(shopObj.isLimited()?1:0));
                  shopinfo.put("iIndex", "-1");
                  shopinfo.put("items", items);
                  shopinfo.put("ShopID", Integer.valueOf(shopId));
                  shopinfo.put("sField", shopObj.getField());
                  shopinfo.put("sName", shopObj.getName());
                  guildhall.put("shopinfo", shopinfo);
                  guildhall.put("cmd", "guildhall");
                  guildhall.put("gCmd", "guildShop");
                  world.send(guildhall, user);
                  return;
               }

               Entry entry = (Entry)i$.next();
               shopItemId = ((Integer)entry.getKey()).intValue();
               int itemId = ((Integer)entry.getValue()).intValue();
               itemObj = (Item)world.items.get(Integer.valueOf(itemId));
            } while(itemObj == null);

            Enhancement enhancement = (Enhancement)world.enhancements.get(Integer.valueOf(itemObj.getEnhId()));
            JSONObject item;
            if(!user.isAdmin() && !user.isModerator() && shopObj.isStaff()) {
               item = Item.getItemJSON(itemObj, (Enhancement)world.enhancements.get(Integer.valueOf(1957)));
               item.put("ItemID", Integer.valueOf(item.hashCode()));
               item.put("sName", itemObj.getName() + " Preview");
               item.put("bStaff", Integer.valueOf(1));
            } else {
               item = Item.getItemJSON(itemObj, enhancement);
            }

            if(shopObj.isLimited()) {
               int turnInArr = world.db.jdbc.queryForInt("SELECT QuantityRemain FROM shops_items WHERE id = ?", new Object[]{Integer.valueOf(shopItemId)});
               item.put("iQtyRemain", Integer.valueOf(turnInArr));
            } else {
               item.put("iQtyRemain", Integer.valueOf(-1));
            }

            item.put("ShopItemID", Integer.valueOf(shopItemId));
            item.put("iQty", Integer.valueOf(itemObj.getQuantity()));
            item.put("iQSindex", Integer.valueOf(itemObj.getQuestStringIndex()));
            item.put("iQSvalue", Integer.valueOf(itemObj.getQuestStringValue()));
            item.put("iReqCP", Integer.valueOf(itemObj.getReqClassPoints()));
            item.put("iReqRep", Integer.valueOf(itemObj.getReqReputation()));
            item.put("FactionID", Integer.valueOf(itemObj.getFactionId()));
            item.put("sFaction", world.factions.get(Integer.valueOf(itemObj.getFactionId())));
            item.put("iCost", Integer.valueOf(itemObj.getCost() * itemObj.getQuantity()));
            if(itemObj.getReqClassId() > 0) {
               item.put("iClass", Integer.valueOf(itemObj.getReqClassId()));
               item.put("sClass", ((Item)world.items.get(Integer.valueOf(itemObj.getReqClassId()))).getName());
            }

            if(!itemObj.requirements.isEmpty()) {
               JSONArray turnInArr1 = new JSONArray();
               Iterator i$1 = itemObj.requirements.entrySet().iterator();

               while(i$1.hasNext()) {
                  Entry require = (Entry)i$1.next();
                  int reqItemId = ((Integer)require.getKey()).intValue();
                  Item reqItemObj = (Item)world.items.get(Integer.valueOf(reqItemId));
                  int quantityNeeded = ((Integer)require.getValue()).intValue() >= reqItemObj.getStack()?reqItemObj.getStack():((Integer)require.getValue()).intValue();
                  JSONObject wObj = new JSONObject();
                  wObj.put("ItemID", Integer.valueOf(reqItemId));
                  wObj.put("iQty", Integer.valueOf(quantityNeeded));
                  wObj.put("sName", reqItemObj.getName());
                  turnInArr1.add(wObj);
               }

               item.put("turnin", turnInArr1);
            }

            items.add(item);
         }
      }
   }
}
