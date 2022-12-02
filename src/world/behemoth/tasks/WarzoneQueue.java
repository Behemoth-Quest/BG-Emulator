package world.behemoth.tasks;

import world.behemoth.db.objects.Area;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import net.sf.json.JSONObject;

public class WarzoneQueue implements Runnable {
   public ConcurrentHashMap<String, LinkedBlockingQueue<Integer>> warzoneQueues = new ConcurrentHashMap();
   private World world;

   public WarzoneQueue(World world) {
      super();
      this.world = world;
      SmartFoxServer.log.info("WarzoneQueue intialized.");
   }

   public void run() {
      try {
         Iterator e = this.warzoneQueues.entrySet().iterator();

         while(true) {
            LinkedBlockingQueue pq;
            String warzone;
            Area area;
            do {
               do {
                  if(!e.hasNext()) {
                     return;
                  }

                  Entry e1 = (Entry)e.next();
                  pq = (LinkedBlockingQueue)e1.getValue();
                  warzone = (String)e1.getKey();
                  area = (Area)this.world.areas.get(warzone);
               } while(area == null);

               Iterator PVPI = pq.iterator();

               while(PVPI.hasNext()) {
                  Integer warzoneRoom = (Integer)PVPI.next();
                  User i = ExtensionHelper.instance().getUserById(warzoneRoom.intValue());
                  if(i == null) {
                     this.removeUserFromQueues(warzoneRoom);
                  }
               }
            } while(pq.size() < area.getMaxPlayers());

            JSONObject var12 = new JSONObject();
            var12.put("cmd", "PVPI");
            var12.put("warzone", warzone);
            Room var13 = this.world.rooms.createRoom(warzone + "-" + (new Random()).nextInt(99999));

            for(int var14 = 0; var14 < area.getMaxPlayers(); ++var14) {
               Integer userid = (Integer)pq.take();
               this.removeUserFromQueues(userid);
               User user = ExtensionHelper.instance().getUserById(userid.intValue());
               if(user != null) {
                  user.properties.put("roomqueued", var13);
                  if(var14 % 2 == 0) {
                     user.properties.put("pvpteam", Integer.valueOf(0));
                  } else {
                     user.properties.put("pvpteam", Integer.valueOf(1));
                  }

                  this.world.send(new String[]{"server", "A new Warzone battle has started!"}, user);
                  this.world.send(var12, user);
               }
            }
         }
      } catch (Exception var11) {
         SmartFoxServer.log.warning("WarzoneQueue interrupted, reinitializing..");
         SmartFoxServer.log.warning("Error message: " + var11.getMessage());
         this.warzoneQueues = new ConcurrentHashMap();
      }
   }

   private LinkedBlockingQueue<Integer> getWarzoneQueue(String warzone) {
      if(this.warzoneQueues.containsKey(warzone)) {
         return (LinkedBlockingQueue)this.warzoneQueues.get(warzone);
      } else {
         this.warzoneQueues.putIfAbsent(warzone, new LinkedBlockingQueue());
         return (LinkedBlockingQueue)this.warzoneQueues.get(warzone);
      }
   }

   public void removeUserFromQueues(Integer id) {
      Iterator i$ = this.warzoneQueues.values().iterator();

      while(i$.hasNext()) {
         LinkedBlockingQueue pq = (LinkedBlockingQueue)i$.next();
         pq.remove(id);
      }

   }

   public void queueUser(String warzone, User user) {
      this.removeUserFromQueues(Integer.valueOf(user.getUserId()));
      LinkedBlockingQueue pq = this.getWarzoneQueue(warzone);
      pq.offer(Integer.valueOf(user.getUserId()));
   }
}
