package world.behemoth.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import world.behemoth.config.ConfigData;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.db.DbManager;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import jdbchelper.ConnectionPool;
import jdbchelper.JdbcHelper;
import jdbchelper.PooledDataSource;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

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
      this.maxPoolSize = maxPoolSize;
      Hikari();

//      this.source = new MysqlConnectionPoolDataSource();
//      this.source.setServerName(ConfigData.DB_HOST);
//      this.source.setPort(ConfigData.DB_PORT);
//      this.source.setUser(ConfigData.DB_USERNAME);
//      this.source.setPassword(ConfigData.DB_PASSWORD);
//      this.source.setDatabaseName(ConfigData.DB_NAME);
//      this.source.setAutoReconnectForConnectionPools(true);
//      this.pool = new ConnectionPool(this.source, maxPoolSize);
//      this.jdbc = new JdbcHelper(new PooledDataSource(this.pool));
//      SmartFoxServer.log.info("Database connections initialized.");
   }

   public void Hikari() {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl("jdbc:mysql://" + ConfigData.DB_HOST + ":" + ConfigData.DB_PORT + "/" + ConfigData.DB_NAME);
      config.setUsername(ConfigData.DB_USERNAME);
      config.setPassword(ConfigData.DB_PASSWORD);
      config.setIdleTimeout(300000);
      config.setRegisterMbeans(true);
      config.setAllowPoolSuspension(true);
      config.setMaximumPoolSize(this.maxPoolSize);

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

   public int getActiveConnections() {
      return this.pool.getActiveConnections();
   }

   public void destroy() {
      this.dataSource.close();
      SmartFoxServer.log.info("Database connections destroyed.");
//      try {
//         this.pool.dispose();
//         this.pool = null;
//         this.jdbc = null;
//         SmartFoxServer.log.info("Database connections destroyed.");
//      } catch (SQLException var2) {
//         SmartFoxServer.log.severe("Error diposing connection pool: " + var2.getMessage());
//      }
   }
}