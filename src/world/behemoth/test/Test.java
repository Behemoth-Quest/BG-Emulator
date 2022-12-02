package world.behemoth.test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {
   public Test() {
      super();
   }

   public static void main(String[] args) {
      System.out.println(generateSHA("salt", "salty"));
      System.out.println("What!is-dis=trim".trim());
   }

   public static BigInteger generateSHA(String originalsalt, String originalpassword) {
      byte[] salt = originalsalt.getBytes();
      byte[] password = originalpassword.getBytes();

      try {
         MessageDigest e = MessageDigest.getInstance("SHA-512");
         byte[] output = e.digest(password);
         e.update(salt);
         e.update(output);
         return new BigInteger(1, e.digest());
      } catch (NoSuchAlgorithmException var6) {
         throw new UnsupportedOperationException(var6);
      }
   }
}
