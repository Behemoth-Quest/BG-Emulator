package world.behemoth.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleLogFormat extends Formatter {
   private static final String nl = System.getProperty("line.separator");

   public SimpleLogFormat() {
      super();
   }

   public String format(LogRecord record) {
      String s = "[ " + record.getLevel() + " ][ " + this.formatLocation(record) + " ] " + record.getMessage() + nl;
      Throwable t = record.getThrown();
      if(t == null) {
         return s;
      } else {
         StackTraceElement[] elements = t.getStackTrace();
         StringBuilder sb = new StringBuilder(s);
         sb.append(" ").append(t.toString()).append(nl);
         StackTraceElement[] arr$ = elements;
         int len$ = elements.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            StackTraceElement element = arr$[i$];
            sb.append("\t").append(element.toString()).append(nl);
         }

         return sb.toString();
      }
   }

   private String formatLocation(LogRecord record) {
      String className = record.getSourceClassName();
      int idx = className.lastIndexOf(".");
      if(idx != -1) {
         className = className.substring(idx + 1);
      }

      return className + "." + record.getSourceMethodName();
   }
}
