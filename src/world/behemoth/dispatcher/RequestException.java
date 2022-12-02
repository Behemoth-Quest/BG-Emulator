package world.behemoth.dispatcher;

public class RequestException extends Exception {
   private static final long serialVersionUID = 1L;
   private String type = "warning";

   public RequestException() {
      super();
   }

   public RequestException(String msg) {
      super(msg);
   }

   public RequestException(String msg, String type) {
      super(msg);
      this.type = type;
   }

   public String getType() {
      return this.type;
   }
}
