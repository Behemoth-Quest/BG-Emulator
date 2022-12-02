package world.behemoth.tasks;

import world.behemoth.ai.MonsterAI;
import world.behemoth.db.objects.Aura;
import world.behemoth.db.objects.Monster;
import world.behemoth.db.objects.Skill;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MonsterAttack implements Runnable {
   private final Skill skill;
   private final MonsterAI monster;
   private final World world;
   private final Room room;

   public MonsterAttack(MonsterAI monster, Skill skill, World world, Room room) {
      super();
      this.skill = skill;
      this.monster = monster;
      this.world = world;
      this.room = room;
   }

   public void run() {
      if(this.monster.attacking == null) {
         throw new RuntimeException("attacking handle is null, unable to continue.");
      } else {
         if(this.monster.state == 0) {
            this.monster.attacking.cancel(false);
         }

         if(this.skill.getMana() <= this.monster.getMana()) {
            String frame = "";
            String targetStr = this.getRandomTargets(this.skill.getHitTargets());
            String cInf = "m:" + this.monster.mapId;
            String[] arrTargets = targetStr.split(",");
            Monster mon = (Monster)this.world.monsters.get(Integer.valueOf(this.monster.monsterId));
            JSONObject p = new JSONObject();
            JSONArray a = new JSONArray();
            JSONArray auras = new JSONArray();
            int monDmg = mon.getDPS();
            int minDmg = (int)Math.floor((double)monDmg - (double)monDmg * 0.1D);
            int maxDmg = (int)Math.ceil((double)monDmg + (double)monDmg * 0.1D);
            String[] anims = arrTargets;
            int anim = arrTargets.length;

            for(int sarsa = 0; sarsa < anim; ++sarsa) {
               String sarsaObj = anims[sarsa];
               int ct = Integer.parseInt(sarsaObj.split(":")[1]);
               User m = SmartFoxServer.getInstance().getUserById(Integer.valueOf(ct));
               if(m != null && this.room.getId() == m.getRoom()) {
                  frame = (String)m.properties.get("frame");
                  int monData = (new Random()).nextInt(maxDmg - minDmg) + minDmg;
                  boolean crit = Math.random() < 0.2D;
                  boolean dodge = Math.random() < 0.05D;
                  monData = (int)(dodge?0.0D:(crit?(double)monData * this.skill.getDamage() * 1.25D:(double)monData * this.skill.getDamage()));
                  Iterator userAuras = this.monster.auras.iterator();

                  while(userAuras.hasNext()) {
                     RemoveAura userHp = (RemoveAura)userAuras.next();
                     Aura damageResult = userHp.getAura();
                     if(damageResult.getCategory().equals("stun") || damageResult.getCategory().equals("freeze") || damageResult.getCategory().equals("stone") || damageResult.getCategory().equals("disabled")) {
                        return;
                     }

                     if(!damageResult.getCategory().equals("d")) {
                        monData *= (int)(1.0D - damageResult.getDamageTakenDecrease());
                        monData = monData < 0?0:monData;
                     }
                  }

                  Set var32 = (Set)m.properties.get("auras");
                  Iterator var33 = var32.iterator();

                  while(var33.hasNext()) {
                     RemoveAura var35 = (RemoveAura)var33.next();
                     Aura userData = var35.getAura();
                     if(!userData.getCategory().equals("d")) {
                        if(userData.selfCast) {
                           monData *= (int)(1.0D - userData.getDamageTakenDecrease());
                           monData = monData < 0?0:monData;
                        } else {
                           monData *= (int)(1.0D + userData.getDamageIncrease());
                        }
                     }
                  }

                  int var34 = ((Integer)m.properties.get("hp")).intValue() - monData;
                  var34 = var34 <= 0?0:var34;
                  m.properties.put("hp", Integer.valueOf(var34));
                  m.properties.put("state", Integer.valueOf(2));
                  if(((Integer)m.properties.get("hp")).intValue() <= 0) {
                     this.world.users.die(m);
                     this.monster.removeTarget(ct);
                     this.monster.cancel();
                  }

                  if(this.skill.hasAuraId()) {
                     auras.add(this.monster.applyAura(this.world, m, this.skill.getAuraId(), cInf, monData));
                  }

                  JSONObject var36 = new JSONObject();
                  var36.put("hp", Integer.valueOf(monData));
                  var36.put("tInf", sarsaObj);
                  var36.put("type", dodge?"dodge":(crit?"crit":"hit"));
                  a.add(var36);
                  JSONObject var37 = new JSONObject();
                  var37.put("intMP", m.properties.get("mp"));
                  var37.put("intHP", m.properties.get("hp"));
                  var37.put("intState", m.properties.get("state"));
                  p.put(m.getName(), var37);
               } else {
                  if(this.monster.targets.contains(Integer.valueOf(ct))) {
                     this.monster.targets.remove(Integer.valueOf(ct));
                  }

                  if(this.monster.targets.isEmpty() && this.monster.state > 0) {
                     this.monster.restore();
                  }

                  this.monster.attacking.cancel(false);
               }
            }

            JSONArray var25 = new JSONArray();
            JSONObject var26 = new JSONObject();
            JSONArray var27 = new JSONArray();
            JSONObject var28 = new JSONObject();
            JSONObject var29 = new JSONObject();
            var26.put("strFrame", frame);
            var26.put("cInf", cInf);
            var26.put("fx", this.skill.getEffects());
            var26.put("tInf", targetStr);
            var26.put("animStr", this.skill.getAnimation());
            if(!this.skill.getStrl().isEmpty()) {
               var26.put("strl", this.skill.getStrl());
            }

            var25.add(var26);
            var28.put("cInf", cInf);
            var28.put("a", a);
            var28.put("iRes", Integer.valueOf(1));
            var27.add(var28);
            if(!auras.isEmpty()) {
               var29.put("a", auras);
            }

            JSONObject var30 = new JSONObject();
            JSONObject var31 = new JSONObject();
            this.monster.mana -= this.skill.getMana();
            this.monster.mana = this.monster.mana <= 0?0:this.monster.mana;
            var31.put("intMP", Integer.valueOf(this.monster.mana));
            var30.put(Integer.valueOf(this.monster.mapId), var31);
            var29.put("anims", var25);
            var29.put("p", p);
            var29.put("m", var30);
            var29.put("cmd", "ct");
            var29.put("sarsa", var27);
            this.world.send(var29, this.room.getChannellList());
            if(this.monster.targets.isEmpty() && this.monster.state > 0) {
               this.monster.restore();
               this.monster.attacking.cancel(false);
            }

         }
      }
   }

   private String getRandomTargets(int maxTargets) {
      StringBuilder sb = new StringBuilder();

      for(maxTargets = maxTargets < 1?1:maxTargets; maxTargets > 0; --maxTargets) {
         int userId = this.monster.getRandomTarget();
         if(!sb.toString().contains(String.valueOf(userId))) {
            sb.append(",");
            sb.append("p:");
            sb.append(userId);
         }
      }

      sb.deleteCharAt(0);
      return sb.toString();
   }
}
