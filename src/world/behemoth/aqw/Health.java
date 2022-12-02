package world.behemoth.aqw;

public class Health {
   private static final int[] arrHP = new int[100];

   public static int getHealthByLevel(int level) {
      return arrHP[level - 1];
   }

   private Health() {
      super();
      throw new UnsupportedOperationException("not allowed to have an instance of this class");
   }

   static {
      double intLevelCap = 100.0D;
      double intHPBase3 = 550.0D;
      double intHPConst3 = 20000.0D;
      double intScaling3 = 1.3D;

      for(int i = 0; (double)i < intLevelCap; ++i) {
         arrHP[i] = (int)Math.round(intHPBase3 + Math.pow((double)i / intLevelCap, intScaling3) * intHPConst3);
      }

   }
}
