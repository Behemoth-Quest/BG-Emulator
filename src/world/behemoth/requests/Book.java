package world.behemoth.requests;

import world.behemoth.dispatcher.IRequest;
import world.behemoth.dispatcher.RequestException;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Book implements IRequest {
   public Book() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      JSONObject BookData = new JSONObject();
      JSONObject BookInfo = new JSONObject();
      JSONObject OBadge = new JSONObject();
      JSONArray OBadgeAdd = new JSONArray();
      JSONObject HMBadge = new JSONObject();
      JSONArray HMBadgeAdd = new JSONArray();
      JSONObject AchBadge = new JSONObject();
      JSONArray AchBadgeAdd = new JSONArray();
      JSONArray Quests = new JSONArray();
      QueryResult bResult = world.db.jdbc.query("SELECT * FROM book ORDER BY id ASC", new Object[0]);

      while(bResult.next()) {
         if(null != bResult.getString("strType")) {
            String qResult = bResult.getString("strType");
            byte quest = -1;
            switch(qResult.hashCode()) {
            case -1971520803:
               if(qResult.equals("AchBadge")) {
                  quest = 2;
               }
               break;
            case -1969322860:
               if(qResult.equals("OBadge")) {
                  quest = 0;
               }
               break;
            case 1744141726:
               if(qResult.equals("HMBadge")) {
                  quest = 1;
               }
            }

            switch(quest) {
            case 0:
               OBadge.element("strFile", bResult.getString("strFile"));
               OBadge.element("strName", bResult.getString("strName"));
               OBadge.element("strLinkage", bResult.getString("strLinkage"));
               if(!bResult.getString("sLock").isEmpty()) {
                  OBadge.element("sLock", bResult.getString("sLock"));
               }

               OBadge.element("sDesc", bResult.getString("sDesc"));
               if(!bResult.getString("strMap").isEmpty()) {
                  OBadge.element("strMap", bResult.getString("strMap"));
               }

               OBadge.element("strType", bResult.getString("strType"));
               OBadge.element("bitHide", bResult.getInt("bitHide"));
               if(!bResult.getString("strLabel").isEmpty()) {
                  OBadge.element("strLabel", bResult.getString("strLabel"));
               }

               if(bResult.getInt("strShop") > 0) {
                  OBadge.element("strShop", bResult.getInt("strShop"));
               }

               if(bResult.getString("strField").equals("QS")) {
                  OBadge.element("strField", bResult.getString("strField"));
                  OBadge.element("intIndex", bResult.getInt("intIndex"));
                  OBadge.element("intValue", bResult.getInt("intValue"));
               } else if(bResult.getString("strField").equals("ia0")) {
                  HMBadge.element("strField", bResult.getString("strField"));
                  OBadge.element("intValue", bResult.getInt("intValue"));
               }

               OBadgeAdd.add(OBadge);
               break;
            case 1:
               HMBadge.element("strFile", bResult.getString("strFile"));
               HMBadge.element("strName", bResult.getString("strName"));
               HMBadge.element("strLinkage", bResult.getString("strLinkage"));
               if(!bResult.getString("sLock").isEmpty()) {
                  HMBadge.element("sLock", bResult.getString("sLock"));
               }

               HMBadge.element("sDesc", bResult.getString("sDesc"));
               if(!bResult.getString("strMap").isEmpty()) {
                  HMBadge.element("strMap", bResult.getString("strMap"));
               }

               HMBadge.element("strType", bResult.getString("strType"));
               HMBadge.element("bitHide", bResult.getInt("bitHide"));
               if(!bResult.getString("strLabel").isEmpty()) {
                  HMBadge.element("strLabel", bResult.getString("strLabel"));
               }

               if(bResult.getInt("strShop") > 0) {
                  HMBadge.element("strShop", bResult.getInt("strShop"));
               }

               if(bResult.getString("strField").equals("QS")) {
                  HMBadge.element("strField", bResult.getString("strField"));
                  HMBadge.element("intIndex", bResult.getInt("intIndex"));
                  HMBadge.element("intValue", bResult.getInt("intValue"));
               } else if(bResult.getString("strField").equals("ia0")) {
                  HMBadge.element("strField", bResult.getString("strField"));
                  HMBadge.element("intValue", bResult.getInt("intValue"));
               }

               HMBadgeAdd.add(HMBadge);
               break;
            case 2:
               AchBadge.element("strFile", bResult.getString("strFile"));
               AchBadge.element("strName", bResult.getString("strName"));
               if(bResult.getInt("intLevel") > 0) {
                  AchBadge.element("intLevel", bResult.getInt("intLevel"));
               }

               AchBadge.element("strLinkage", bResult.getString("strLinkage"));
               if(!bResult.getString("sLock").isEmpty()) {
                  AchBadge.element("sLock", bResult.getString("sLock"));
               }

               AchBadge.element("sDesc", bResult.getString("sDesc"));
               if(!bResult.getString("strMap").isEmpty()) {
                  AchBadge.element("strMap", bResult.getString("strMap"));
               }

               AchBadge.element("strType", bResult.getString("strType"));
               AchBadge.element("bitHide", bResult.getInt("bitHide"));
               if(!bResult.getString("strLabel").isEmpty()) {
                  AchBadge.element("strLabel", bResult.getString("strLabel"));
               }

               if(bResult.getString("strField").equals("QS")) {
                  AchBadge.element("strField", bResult.getString("strField"));
                  AchBadge.element("intIndex", bResult.getInt("intIndex"));
                  AchBadge.element("intValue", bResult.getInt("intValue"));
               } else if(bResult.getString("strField").equals("ia0")) {
                  AchBadge.element("strField", bResult.getString("strField"));
                  AchBadge.element("intValue", bResult.getInt("intValue"));
               }

               AchBadgeAdd.add(AchBadge);
            }
         }
      }

      bResult.close();
      QueryResult qResult1 = world.db.jdbc.query("SELECT * FROM book_quest ORDER BY id DESC", new Object[0]);

      while(qResult1.next()) {
         JSONObject quest1 = new JSONObject();
         quest1.element("intIndex", qResult1.getInt("intIndex"));
         quest1.element("strName", qResult1.getString("strName"));
         quest1.element("strField", qResult1.getString("strField"));
         quest1.element("sLock", qResult1.getString("sLock"));
         quest1.element("strMap", qResult1.getString("strMap"));
         quest1.element("strType", qResult1.getString("strType"));
         quest1.element("bitHide", qResult1.getInt("bitHide"));
         quest1.element("intValue", qResult1.getInt("intValue"));
         Quests.add(quest1);
      }

      qResult1.close();
      BookData.element("OBadge", OBadgeAdd);
      BookData.element("HMBadge", HMBadgeAdd);
      BookData.element("AchBadge", AchBadgeAdd);
      BookData.element("quests", Quests);
      BookInfo.element("bookData", BookData);
      BookInfo.element("cmd", "bookInfo");
      world.send(BookInfo, user);
   }
}
