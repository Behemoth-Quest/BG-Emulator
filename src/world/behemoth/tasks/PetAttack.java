package world.behemoth.tasks;

import world.behemoth.db.objects.Skill;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class PetAttack implements Runnable {
   private final Skill skill;
   private final User user;
   private final World world;
   private final Room room;

   public PetAttack(User user, Skill skill, World world, Room room) {
      super();
      this.skill = skill;
      this.user = user;
      this.world = world;
      this.room = room;
   }

   public void run() {
   }
}
