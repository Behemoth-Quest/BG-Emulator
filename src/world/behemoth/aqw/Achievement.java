package world.behemoth.aqw;

public class Achievement {
   private Achievement() {
      super();
      throw new UnsupportedOperationException("not allowed to have an instance of this class");
   }

   public static int get(int value, int index) {
      return index >= 0 && index <= 31?((value & (int)Math.pow(2.0D, (double)index)) == 0?0:1):-1;
   }

   public static int update(int valueToSet, int index, int value) {
      int newValue = 0;
      if(value == 0) {
         newValue = valueToSet & ~((int)Math.pow(2.0D, (double)index));
      } else if(value == 1) {
         newValue = valueToSet | (int)Math.pow(2.0D, (double)index);
      }

      return newValue;
   }
}
