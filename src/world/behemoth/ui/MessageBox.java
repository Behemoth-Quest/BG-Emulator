package world.behemoth.ui;

import java.awt.Component;
import javax.swing.JOptionPane;

public class MessageBox {
   public static void showMessage(String message, String title) {
      JOptionPane.showMessageDialog((Component)null, message, title, 1);
   }

   public static int showConfirm(String message, String title, int type) {
      return JOptionPane.showConfirmDialog((Component)null, message, title, type);
   }

   private MessageBox() {
      super();
   }
}
