package world.behemoth.ai;

import world.behemoth.db.objects.Aura;
import world.behemoth.db.objects.AuraEffects;
import world.behemoth.db.objects.MapMonster;
import world.behemoth.db.objects.Monster;
import world.behemoth.db.objects.MonsterDrop;
import world.behemoth.db.objects.MonsterSkill;
import world.behemoth.db.objects.Skill;
import world.behemoth.tasks.DamageOverTime;
import world.behemoth.tasks.MonsterAttack;
import world.behemoth.tasks.MonsterAttackCooldown;
import world.behemoth.tasks.MonsterRespawn;
import world.behemoth.tasks.RemoveAura;
import world.behemoth.world.World;
import world.behemoth.world.stats.Stats;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MonsterAI implements Runnable {
   public ScheduledFuture<?> attacking;
   private World world;
   public volatile int monsterId;
   public volatile int mapId;
   public volatile int state;
   public volatile int health;
   public volatile int mana;
   private String frame;
   public Set<Integer> targets;
   public Set<RemoveAura> auras;
   private Random rand;
   private Room room;

   public MonsterAI(MapMonster mapMon, World world, Room room) {
      super();
      this.world = world;
      this.monsterId = mapMon.getMonsterId();
      this.mapId = mapMon.getMonMapId();
      this.frame = mapMon.getFrame();
      this.room = room;
      this.rand = new Random((long)(mapMon.getMonMapId() * mapMon.getMonsterId()));
      this.targets = Collections.newSetFromMap(new ConcurrentHashMap());
      this.auras = Collections.newSetFromMap(new ConcurrentHashMap());
      this.state = 1;
      this.health = ((Monster)world.monsters.get(Integer.valueOf(this.monsterId))).getHealth();
      this.mana = ((Monster)world.monsters.get(Integer.valueOf(this.monsterId))).getMana();
   }

   public void cancel() {
      if(this.targets.isEmpty() && this.state > 0) {
         this.restore();
         this.attacking.cancel(false);
      }

   }

   public void run() {
      if(this.state == 0) {
         this.attacking.cancel(false);
      } else {
         int userId = this.getRandomTarget();
         User user = SmartFoxServer.getInstance().getUserById(Integer.valueOf(userId));
         if(user != null && this.room.getId() == user.getRoom() && this.frame.equals((String)user.properties.get("frame"))) {
            int monDmg = ((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getDPS();
            int minDmg = (int)Math.floor((double)monDmg - (double)monDmg * 0.1D);
            int maxDmg = (int)Math.ceil((double)monDmg + (double)monDmg * 0.1D);
            int damage = this.rand.nextInt(maxDmg - minDmg) + minDmg;
            Stats stats = (Stats)user.properties.get("stats");
            boolean crit = Math.random() < 0.2D;
            boolean dodge = Math.random() < stats.get$tdo();
            damage = (int)(dodge?0.0D:(crit?(double)damage * 1.25D:(double)damage));
            if(((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getElement().equals(user.properties.get("none"))) {
               damage = (int)((double)damage - (double)damage * 0.2D);
            }

            Iterator userAuras = this.auras.iterator();

            Aura anims;
            do {
               if(!userAuras.hasNext()) {
                  Set userAuras1 = (Set)user.properties.get("auras");
                  Iterator userHp1 = userAuras1.iterator();

                  while(userHp1.hasNext()) {
                     RemoveAura anims1 = (RemoveAura)userHp1.next();
                     Aura anim = anims1.getAura();
                     if(!anim.getCategory().equals("d")) {
                        damage = (int)((double)damage * (1.0D - anim.getDamageTakenDecrease()));
                     }
                  }

                  int userHp2 = ((Integer)user.properties.get("hp")).intValue() - damage;
                  userHp2 = userHp2 <= 0?0:userHp2;
                  user.properties.put("hp", Integer.valueOf(userHp2));
                  user.properties.put("state", Integer.valueOf(2));
                  if(((Integer)user.properties.get("hp")).intValue() <= 0) {
                     this.world.users.die(user);
                     this.removeTarget(userId);
                     this.cancel();
                  }

                  JSONArray anims2 = new JSONArray();
                  JSONObject anim1 = new JSONObject();
                  JSONObject action = new JSONObject();
                  JSONObject p = new JSONObject();
                  JSONObject userData = new JSONObject();
                  JSONArray sara = new JSONArray();
                  JSONObject saraObj = new JSONObject();
                  JSONObject ct = new JSONObject();
                  anim1.put("strFrame", user.properties.get("frame"));
                  anim1.put("cInf", "m:" + this.mapId);
                  anim1.put("fx", "m");
                  anim1.put("tInf", "p:" + Integer.toString(userId));
                  anim1.put("animStr", "Attack1,Attack2");
                  anims2.add(anim1);
                  action.put("hp", Integer.valueOf(damage));
                  action.put("cInf", "m:" + this.mapId);
                  action.put("tInf", "p:" + Integer.toString(userId));
                  action.put("type", dodge?"dodge":(crit?"crit":"hit"));
                  userData.put("intMP", user.properties.get("mp"));
                  userData.put("intHP", user.properties.get("hp"));
                  userData.put("intState", user.properties.get("state"));
                  p.put(user.getName(), userData);
                  saraObj.put("actionResult", action);
                  saraObj.put("iRes", Integer.valueOf(1));
                  sara.add(saraObj);
                  JSONObject m = new JSONObject();
                  JSONObject monData = new JSONObject();
                  this.mana += (int)((double)((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getMana() * 0.02D);
                  this.mana = this.mana > ((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getMana()?((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getMana():this.mana;
                  monData.put("intMP", Integer.valueOf(this.mana));
                  m.put(String.valueOf(this.mapId), monData);
                  ct.put("anims", anims2);
                  ct.put("p", p);
                  ct.put("m", m);
                  ct.put("cmd", "ct");
                  this.world.send(ct, this.room.getChannellList());
                  ct.put("sara", sara);
                  this.world.send(ct, user);
                  if(!((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).skills.isEmpty()) {
                     MonsterSkill skill = (MonsterSkill)((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).skills.toArray()[(new Random()).nextInt(((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).skills.size())];
                     Skill skillData = (Skill)this.world.skills.get(Integer.valueOf(skill.skillId));
                     if(!skill.cooldown) {
                        skill.cooldown = true;
                        this.world.scheduleTask(new MonsterAttackCooldown(skill), (long)skillData.getCooldown(), TimeUnit.MILLISECONDS, false);
                        this.world.scheduleTask(new MonsterAttack(this, skillData, this.world, this.room), 1800L, TimeUnit.MILLISECONDS, false);
                     }
                  }

                  return;
               }

               RemoveAura userHp = (RemoveAura)userAuras.next();
               anims = userHp.getAura();
            } while(!anims.getCategory().equals("stun") && !anims.getCategory().equals("freeze") && !anims.getCategory().equals("stone") && !anims.getCategory().equals("disabled"));

         } else {
            this.removeTarget(userId);
            this.cancel();
         }
      }
   }

   public void restore() {
      this.state = 1;
      this.health = ((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getHealth();
      this.mana = ((Monster)this.world.monsters.get(Integer.valueOf(this.monsterId))).getMana();
      this.targets.clear();
      JSONObject monInfo = new JSONObject();
      monInfo.put("intHP", Integer.valueOf(this.health));
      monInfo.put("intMP", Integer.valueOf(this.mana));
      monInfo.put("intState", Integer.valueOf(this.state));
      JSONObject mtls = new JSONObject();
      mtls.put("cmd", "mtls");
      mtls.put("id", Integer.valueOf(this.mapId));
      mtls.put("o", monInfo);
      this.world.send(mtls, this.room.getChannellList());
   }

   public void die() {
      if(this.state != 0) {
         if(this.attacking != null) {
            this.attacking.cancel(true);
         }

         Iterator mon = this.auras.iterator();

         while(mon.hasNext()) {
            RemoveAura drops = (RemoveAura)mon.next();
            drops.run();
            drops.cancel();
         }

         this.auras.clear();
         this.health = 0;
         this.mana = 0;
         this.state = 0;
         Monster mon1 = (Monster)this.world.monsters.get(Integer.valueOf(this.monsterId));
         this.world.scheduleTask(new MonsterRespawn(this.world, this), 20L, TimeUnit.SECONDS);
         HashSet drops1 = new HashSet();
         Iterator i$ = mon1.drops.iterator();

         while(i$.hasNext()) {
            MonsterDrop userId = (MonsterDrop)i$.next();
            if(Math.random() <= userId.chance * (double)this.world.DROP_RATE) {
               drops1.add(userId);
            }
         }

         i$ = this.targets.iterator();

         while(true) {
            User user;
            do {
               if(!i$.hasNext()) {
                  return;
               }

               int userId1 = ((Integer)i$.next()).intValue();
               user = ExtensionHelper.instance().getUserById(userId1);
            } while(user == null);

            Iterator i$1 = drops1.iterator();

            while(i$1.hasNext()) {
               MonsterDrop md = (MonsterDrop)i$1.next();
               this.world.users.dropItem(user, md.itemId, md.quantity);
            }

            this.world.users.giveRewards(user, mon1.getExperience(), mon1.getGold(), mon1.getReputation(), 0, -1, this.mapId, "m");
         }
      }
   }

   public boolean hasAura(int auraId) {
      Iterator i$ = this.auras.iterator();

      Aura aura;
      do {
         if(!i$.hasNext()) {
            return false;
         }

         RemoveAura ra = (RemoveAura)i$.next();
         aura = ra.getAura();
      } while(aura.getId() != auraId);

      return true;
   }

   public void removeAura(RemoveAura ra) {
      this.auras.remove(ra);
   }

   public RemoveAura applyAura(Aura aura) {
      RemoveAura ra = new RemoveAura(this.world, aura, this);
      ra.setRunning(this.world.scheduleTask(ra, (long)aura.getDuration(), TimeUnit.SECONDS));
      this.auras.add(ra);
      return ra;
   }

   public JSONObject applyAura(World world, User user, int auraId, String fromTarget, int damage) {
      JSONObject aInfo = new JSONObject();
      Aura aura = (Aura)world.auras.get(Integer.valueOf(auraId));
      boolean auraExists = world.users.hasAura(user, aura.getId());
      aInfo.put("cInf", fromTarget);
      aInfo.put("cmd", "aura+");
      aInfo.put("auras", aura.getAuraArray(!auraExists));
      aInfo.put("tInf", "p:" + user.getUserId());
      if(auraExists) {
         return aInfo;
      } else {
         RemoveAura ra = world.users.applyAura(user, aura);
         if(!aura.effects.isEmpty()) {
            Stats dot = (Stats)user.properties.get("stats");
            HashSet auraEffects = new HashSet();
            Iterator iterator = aura.effects.iterator();

            while(iterator.hasNext()) {
               int effectId = ((Integer)iterator.next()).intValue();
               AuraEffects ae = (AuraEffects)world.effects.get(Integer.valueOf(effectId));
               dot.effects.add(ae);
               auraEffects.add(ae);
            }

            dot.update();
            dot.sendStatChanges(dot, auraEffects);
         }

         if(aura.getCategory().equals("d")) {
            DamageOverTime dot1 = new DamageOverTime(world, user, damage, fromTarget);
            dot1.setRunning(world.scheduleTask(dot1, 2L, TimeUnit.SECONDS, true));
            ra.setDot(dot1);
         }

         return aInfo;
      }
   }

   public int getRandomTarget() {
      Integer[] setArray = (Integer[])this.targets.toArray(new Integer[this.targets.size()]);
      return setArray[this.rand.nextInt(setArray.length)].intValue();
   }

   public Set<Integer> getTargets() {
      return Collections.unmodifiableSet(this.targets);
   }

   public void addTarget(int userId) {
      if(!this.targets.contains(Integer.valueOf(userId))) {
         this.targets.add(Integer.valueOf(userId));
      }

   }

   public void removeTarget(int userId) {
      this.targets.remove(Integer.valueOf(userId));
   }

   public int getState() {
      return this.state;
   }

   public int getMapId() {
      return this.mapId;
   }

   public String getFrame() {
      return this.frame;
   }

   public int getMonsterId() {
      return this.monsterId;
   }

   public int getHealth() {
      return this.health;
   }

   public int getMana() {
      return this.mana;
   }

   public Room getRoom() {
      return this.room;
   }

   public synchronized void setAttacking(ScheduledFuture<?> attacking) {
      if(this.attacking != null && !this.attacking.isDone()) {
         attacking.cancel(true);
      } else {
         this.state = 2;
         this.attacking = attacking;
      }

   }

   public void setState(int state) {
      this.state = state;
   }

   public void setHealth(int health) {
      this.health = health;
      if(this.health < 0) {
         this.health = 0;
      }

   }

   public void setMana(int mana) {
      this.mana = mana;
      if(this.mana < 0) {
         this.mana = 0;
      }

   }

   public Set<RemoveAura> getAuras() {
      return Collections.unmodifiableSet(this.auras);
   }
}
