package world.behemoth.aqw;

import java.util.HashMap;
import java.util.Map;

public class Pad {
   private static final Map<String, String> pads = new HashMap();
   private static final Map<String, String> pairs;

   public static String getPair(String pad) {
      return (String)pairs.get(pad);
   }

   public static String getPad(String pad) {
      return (String)pads.get(pad);
   }

   private Pad() {
      super();
      throw new UnsupportedOperationException("not allowed to have an instance of this class");
   }

   static {
      pads.put("Pad1", "Right");
      pads.put("Pad2", "Down1");
      pads.put("Pad3", "Down1");
      pads.put("Pad4", "Down2");
      pads.put("Pad5", "Left");
      pads.put("Pad6", "Top1");
      pads.put("Pad7", "Top3");
      pads.put("Pad8", "Top2");
      pairs = new HashMap();
      pairs.put("Pad3", "Pad7");
      pairs.put("Pad5", "Pad1");
      pairs.put("Pad2", "Pad6");
      pairs.put("Pad1", "Pad5");
      pairs.put("Pad7", "Pad3");
      pairs.put("Pad6", "Pad2");
      pairs.put("Pad4", "Pad8");
      pairs.put("Pad8", "Pad4");
   }
}
