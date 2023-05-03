package world.behemoth.requests;

import world.behemoth.db.objects.Area;
import world.behemoth.db.objects.Item;
import world.behemoth.db.objects.Quest;
import world.behemoth.db.objects.QuestReward;
import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Map.Entry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetQuests implements IRequest {
   public GetQuests() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject q = new JSONObject();
      JSONObject arrQuests = new JSONObject();
      String[] arr$ = params;
      int len$ = params.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String strId = arr$[i$];
         int questId = Integer.parseInt(strId);
         Quest questObj = (Quest)world.quests.get(Integer.valueOf(questId));
         if(questObj != null) {
            if(!questObj.locations.isEmpty()) {
               int quest = ((Area)world.areas.get(room.getName().split("-")[0])).getId();
               if(!questObj.locations.contains(Integer.valueOf(quest))) {
                  world.users.log(user, "Invalid Quest Load", "Quest load triggered at different location.");
                  continue;
               }
            }

            JSONObject var29 = new JSONObject();
            JSONObject oRewards = new JSONObject();
            JSONObject rewArray = new JSONObject();
            JSONArray rewards = new JSONArray();
            JSONObject reqdArray = new JSONObject();
            JSONArray reqd = new JSONArray();
            Object types = null;
            Iterator turnin;
            Entry oItems;
            int entry;
            int itemId;
            int quantity;
            Item itemObj;
            JSONObject item;
            JSONObject turnInObj;
            if(questObj.rewards.size() > 0) {
               turnin = questObj.rewards.entries().iterator();

               while(turnin.hasNext()) {
                  oItems = (Entry)turnin.next();
                  QuestReward i$1 = (QuestReward)oItems.getValue();
                  entry = i$1.itemId;
                  itemId = i$1.quantity;
                  quantity = (int)i$1.rate;
                  itemObj = (Item)world.items.get(Integer.valueOf(entry));
                  if(itemObj != null) {
                     item = Item.getItemJSON(itemObj);
                     item.put("iQty", Integer.valueOf(itemId));
                     turnInObj = new JSONObject();
                     turnInObj.put("ItemID", Integer.valueOf(entry));
                     turnInObj.put("QuestID", Integer.valueOf(questId));
                     turnInObj.put("iRate", Integer.valueOf(quantity));
                     turnInObj.put("iType", Integer.valueOf(0));
                     turnInObj.put("iQty", Integer.valueOf(itemId));
                     rewards.add(turnInObj);
                     if(oRewards.get("items" + i$1.type) != null) {
                        rewArray = (JSONObject)oRewards.get("items" + i$1.type);
                     }

                     rewArray.put(Integer.valueOf(oItems.hashCode()), item);
                     oRewards.put("items" + i$1.type, rewArray);
                  }
               }
            }

            if(questObj.reqd.size() > 0) {
               turnin = questObj.reqd.entrySet().iterator();

               while(turnin.hasNext()) {
                  oItems = (Entry)turnin.next();
                  int var32 = ((Integer)oItems.getKey()).intValue();
                  entry = ((Integer)oItems.getValue()).intValue();
                  Item var35 = (Item)world.items.get(Integer.valueOf(var32));
                  if(var35 != null) {
                     JSONObject var36 = Item.getItemJSON(var35);
                     reqdArray.put(String.valueOf(var32), var36);
                     JSONObject var37 = new JSONObject();
                     var37.put("ItemID", Integer.valueOf(var32));
                     var37.put("QuestID", Integer.valueOf(questId));
                     var37.put("iQty", Integer.valueOf(entry));
                     reqd.add(var37);
                  }
               }
            }

            JSONArray var30 = new JSONArray();
            JSONObject var31 = new JSONObject();
            Iterator var33 = questObj.requirements.entrySet().iterator();

            while(var33.hasNext()) {
               Entry var34 = (Entry)var33.next();
               itemId = ((Integer)var34.getKey()).intValue();
               quantity = ((Integer)var34.getValue()).intValue();
               itemObj = (Item)world.items.get(Integer.valueOf(itemId));
               if(itemObj != null) {
                  item = Item.getItemJSON(itemObj);
                  var31.put(Integer.valueOf(itemId), item);
                  turnInObj = new JSONObject();
                  turnInObj.put("ItemID", String.valueOf(itemId));
                  turnInObj.put("QuestID", Integer.valueOf(questId));
                  turnInObj.put("iQty", Integer.valueOf(quantity));
                  var30.add(turnInObj);
               }
            }

            var29.put("FactionID", Integer.valueOf(questObj.getFactionId()));
            if(questObj.getFactionId() > 1) {
               var29.put("sFaction", world.factions.get(Integer.valueOf(questObj.getFactionId())));
            }

            var29.put("QuestID", Integer.valueOf(questId));
            var29.put("bOnce", Integer.valueOf(questObj.isOnce()?1:0));
            var29.put("bStaff", Integer.valueOf(0));
            var29.put("bUpg", Integer.valueOf(questObj.isUpgrade()?1:0));
            if(questObj.getReqClassId() > 0) {
               var29.put("sClass", ((Item)world.items.get(Integer.valueOf(questObj.getReqClassId()))).getName());
            }

            var29.put("iClass", Integer.valueOf(questObj.getReqClassId()));
            var29.put("iExp", Integer.valueOf(questObj.getExperience()));
            var29.put("iGold", Integer.valueOf(questObj.getGold()));
            var29.put("iCoins", Integer.valueOf(questObj.getCoins()));
            var29.put("iLvl", Integer.valueOf(questObj.getLevel()));
            var29.put("iRep", Integer.valueOf(questObj.getReputation()));
            var29.put("iReqCP", Integer.valueOf(questObj.getReqClassPoints()));
            var29.put("iReqRep", Integer.valueOf(questObj.getReqReputation()));
            var29.put("iSlot", Integer.valueOf(questObj.getSlot()));
            var29.put("iValue", Integer.valueOf(questObj.getValue()));
            var29.put("iWar", Integer.valueOf(0));
            if(questObj.reqd.size() > 0) {
               var29.put("reqd", reqd);
            }

            var29.put("oReqd", reqdArray);
            var29.put("oItems", var31);
            var29.put("oRewards", oRewards);
            var29.put("reward", rewards);
            var29.put("sDesc", questObj.getDescription());
            var29.put("sEndText", questObj.getEndText());
            var29.put("sName", questObj.getName());
            var29.put("turnin", var30);
            if(!questObj.getField().isEmpty()) {
               var29.put("sField", questObj.getField());
               var29.put("iIndex", Integer.valueOf(questObj.getIndex()));
            }

            arrQuests.put(strId, var29);
         }
      }

      q.put("cmd", "getQuests");
      q.put("quests", arrQuests);
      world.send(q, user);
   }
}
