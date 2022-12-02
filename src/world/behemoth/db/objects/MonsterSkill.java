package world.behemoth.db.objects;

public class MonsterSkill {
   public int skillId;
   public boolean cooldown = false;

   public MonsterSkill() {
      super();
   }

   public void cooldown() {
      this.cooldown = true;
   }

   public boolean onCooldown() {
      return this.cooldown;
   }
}
