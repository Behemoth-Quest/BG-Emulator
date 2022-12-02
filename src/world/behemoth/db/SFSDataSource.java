package world.behemoth.db;

import it.gotoandplay.smartfoxserver.db.DbManager;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/** @deprecated */
@Deprecated
public class SFSDataSource implements DataSource {
   private final DbManager db;

   public SFSDataSource(DbManager db) {
      super();
      this.db = db;
   }

   public Connection getConnection() throws SQLException {
      return this.db.getConnection();
   }

   public Connection getConnection(String username, String password) throws SQLException {
      return this.db.getConnection();
   }

   public PrintWriter getLogWriter() throws SQLException {
      return null;
   }

   public void setLogWriter(PrintWriter out) throws SQLException {
   }

   public void setLoginTimeout(int seconds) throws SQLException {
   }

   public int getLoginTimeout() throws SQLException {
      return 0;
   }

   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return null;
   }

   public Object unwrap(Class iface) throws SQLException {
      if(!DataSource.class.equals(iface)) {
         throw new SQLException("DataSource of type [" + this.getClass().getName() + "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
      } else {
         return this;
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return DataSource.class.equals(iface);
   }
}
