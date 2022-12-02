package world.behemoth.tasks;

import world.behemoth.ai.MonsterAI;
import world.behemoth.world.World;

public class MonsterRespawn implements Runnable {
   private MonsterAI ai;
   private World world;

   public MonsterRespawn(World world, MonsterAI ai) {
      super();
      this.ai = ai;
      this.world = world;
   }

   public void run() {
      this.ai.restore();
      this.world.send(new String[]{"respawnMon", Integer.toString(this.ai.getMapId())}, this.ai.getRoom().getChannellList());
   }
}
