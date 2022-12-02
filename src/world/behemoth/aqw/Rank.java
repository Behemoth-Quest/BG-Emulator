package world.behemoth.aqw;

public class Rank {
   private static final int[] arrRanks = new int[10];

   private Rank() {
      super();
      throw new UnsupportedOperationException("not allowed to have an instance of this class");
   }

   public static int getRankFromPoints(int cp) {
      for(int i = 1; i < arrRanks.length; ++i) {
         if(cp < arrRanks[i]) {
            return i;
         }
      }

      return 10;
   }

   static {
      for(int i = 1; i < arrRanks.length; ++i) {
         int rankExp = (int)(Math.pow((double)(i + 1), 3.0D) * 100.0D);
         if(i > 1) {
            arrRanks[i] = rankExp + arrRanks[i - 1];
         } else {
            arrRanks[i] = rankExp + 100;
         }
      }

   }
}
