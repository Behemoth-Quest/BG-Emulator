package world.behemoth.tasks;

import world.behemoth.ai.MonsterAI;
import world.behemoth.db.objects.Area;
import world.behemoth.db.objects.Monster;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DamageOverTime implements Runnable, CancellableTask {
   private static final Random rand = new Random();
   private String fromTarget;
   private World world;
   private ScheduledFuture<?> running;
   private User user;
   private MonsterAI ai;
   private int damage;

   public DamageOverTime(World world, User user, int damage, String cInf) {
      super();
      this.fromTarget = cInf;
      this.world = world;
      this.user = user;
      this.damage = damage;
      rand.setSeed((long)damage);
   }

   public DamageOverTime(World world, MonsterAI ai, int damage, String cInf) {
      super();
      this.fromTarget = cInf;
      this.world = world;
      this.ai = ai;
      this.damage = damage;
      rand.setSeed((long)damage);
   }

   public void run() {
      if(this.damage == 0) {
         throw new RuntimeException("damage is 0, pointless to continue.");
      } else {
         int dotDamage = rand.nextInt(Math.abs(this.damage));
         dotDamage = this.damage < 0?dotDamage * -1:dotDamage;
         JSONObject ct = new JSONObject();
         JSONArray sara = new JSONArray();
         JSONObject saraObj = new JSONObject();
         JSONObject actionResult = new JSONObject();
         JSONObject tgtInfo = new JSONObject();
         actionResult.put("hp", Integer.valueOf(dotDamage));
         actionResult.put("cInf", this.fromTarget);
         actionResult.put("typ", "d");
         if(this.user != null) {
            actionResult.put("tInf", "p:" + this.user.getUserId());
         } else if(this.ai != null) {
            actionResult.put("tInf", "m:" + this.ai.getMapId());
         }

         saraObj.put("actionResult", actionResult);
         saraObj.put("iRes", Integer.valueOf(1));
         sara.add(saraObj);
         ct.put("cmd", "ct");
         ct.put("sara", sara);
         JSONObject p;
         int monTargetsList;
         if(this.user != null) {
            Room m = this.world.zone.getRoom(this.user.getRoom());
            p = new JSONObject();
            monTargetsList = ((Integer)this.user.properties.get("hp")).intValue() - dotDamage;
            monTargetsList = monTargetsList <= 0?0:monTargetsList;
            monTargetsList = monTargetsList >= ((Integer)this.user.properties.get("hpmax")).intValue()?((Integer)this.user.properties.get("hpmax")).intValue():monTargetsList;
            this.user.properties.put("hp", Integer.valueOf(monTargetsList));
            if(((Integer)this.user.properties.get("state")).intValue() == 0) {
               this.running.cancel(true);
               return;
            }

            tgtInfo.put("intHP", Integer.valueOf(monTargetsList));
            if(monTargetsList <= 0 && ((Integer)this.user.properties.get("state")).intValue() != 0) {
               this.running.cancel(false);
               this.world.users.die(this.user);
               tgtInfo.put("intState", (Integer)this.user.properties.get("state"));
               tgtInfo.put("intMP", Integer.valueOf(0));
               if(((Area)this.world.areas.get(m.getName().split("-")[0])).isPvP()) {
                  int monTargets = ((Integer)this.user.properties.get("pvpteam")).intValue() == 0?1:0;
                  if(m.getName().split("-")[0].equals("deadlock")) {
                     this.world.rooms.addPvPScore(m, 1000, monTargets);
                  } else {
                     this.world.rooms.addPvPScore(m, ((Integer)this.user.properties.get("level")).intValue(), monTargets);
                  }

                  ct.put("pvp", this.world.rooms.getPvPResult(m));
               }
            }

            p.put(this.user.getName(), tgtInfo);
            ct.put("p", p);
            this.world.sendToRoom(ct, this.user, m);
         }

         if(this.ai != null) {
            JSONObject m1 = new JSONObject();
            p = new JSONObject();
            this.ai.setHealth(this.ai.getHealth() - dotDamage);
            if(this.ai.getState() == 0) {
               this.running.cancel(true);
               return;
            }

            if(this.ai.getHealth() <= 0 && this.ai.getState() != 0) {
               this.running.cancel(false);
               this.ai.die();
               if(((Area)this.world.areas.get(this.ai.getRoom().getName().split("-")[0])).isPvP()) {
                  monTargetsList = ((Monster)this.world.monsters.get(Integer.valueOf(this.ai.getMonsterId()))).getTeamId() == 1?0:1;
                  this.world.rooms.relayPvPEvent(this.ai, monTargetsList);
                  ct.put("pvp", this.world.rooms.getPvPResult(this.ai.getRoom()));
               }

               JSONArray monTargetsList1 = new JSONArray();
               Set monTargets1 = this.ai.getTargets();
               Iterator i$ = monTargets1.iterator();

               while(i$.hasNext()) {
                  int userId = ((Integer)i$.next()).intValue();
                  User userTgt = ExtensionHelper.instance().getUserById(userId);
                  if(userTgt != null) {
                     userTgt.properties.put("state", Integer.valueOf(1));
                     this.world.users.regen(this.user);
                     monTargetsList1.add(userTgt.getName());
                     JSONObject userData = new JSONObject();
                     userData.put("intState", (Integer)userTgt.properties.get("state"));
                     p.put(userTgt.getName(), userData);
                  }
               }

               tgtInfo.put("targets", monTargetsList1);
            }

            tgtInfo.put("intHP", Integer.valueOf(this.ai.getHealth()));
            if(this.ai.getState() == 0) {
               tgtInfo.put("intMP", Integer.valueOf(this.ai.getMana()));
               tgtInfo.put("intState", Integer.valueOf(this.ai.getState()));
            }

            m1.put(String.valueOf(this.ai.getMapId()), tgtInfo);
            ct.put("m", m1);
            if(!p.isEmpty()) {
               ct.put("p", p);
            }

            this.world.send(ct, this.ai.getRoom().getChannellList());
         }

      }
   }

   public void cancel() {
      this.running.cancel(false);
   }

   public void setRunning(ScheduledFuture<?> running) {
      this.running = running;
   }
}
