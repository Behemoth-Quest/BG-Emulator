package world.behemoth.db;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.db.DbManager;
import jdbchelper.ConnectionPool;
import jdbchelper.JdbcHelper;
import world.behemoth.config.ConfigData;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Database {
   public JdbcHelper jdbc;
   private MysqlConnectionPoolDataSource source;
   private ConnectionPool pool;

   private HikariDataSource dataSource;
   private static HikariPoolMXBean poolProxy;
   private int maxPoolSize;

   public Database() {
      this(50);
   }

   public Database(int maxPoolSize) {
      super();
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl("jdbc:mysql://" + ConfigData.DB_HOST + ":" + ConfigData.DB_PORT + "/" + ConfigData.DB_NAME);
      config.setUsername(ConfigData.DB_USERNAME);
      config.setPassword(ConfigData.DB_PASSWORD);
      config.setIdleTimeout(300000);
      config.setRegisterMbeans(true);
      config.setAllowPoolSuspension(true);
      config.setMaximumPoolSize(maxPoolSize);

      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.addDataSourceProperty("useSSL", "false");
      this.dataSource = new HikariDataSource(config);
      this.jdbc = new JdbcHelper(dataSource);

      try {
         MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
         ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (HikariPool-1)");
         poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);
      } catch (MalformedObjectNameException e) {
         e.printStackTrace();
      }
      SmartFoxServer.log.info("Database connections initialized.");
   }

   public void freeIdleConnections() {
      this.pool.freeIdleConnections();
   }

   public int getIdleConnections() {
      return poolProxy.getIdleConnections();
   }

   public int getActiveConnections() {
      return poolProxy.getActiveConnections();
   }

   public JdbcHelper getJdbc() {
      return this.jdbc;
   }

   public void destroy() {
      this.dataSource.close();
      SmartFoxServer.log.info("Database connections destroyed.");
   }
}