/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import java.util.Collections;
/*  7:   */ import java.util.HashSet;
/*  8:   */ import java.util.Map;
/*  9:   */ import java.util.Set;
/* 10:   */ import jdbchelper.BeanCreator;
/* 11:   */ import jdbchelper.ResultSetMapper;
/* 12:   */ 
/* 13:   */ public class Area
/* 14:   */ {
/* 15:   */   protected String name;
/* 16:   */   protected String file;
/* 17:   */   protected int id;
/* 18:   */   protected int maxPlayers;
/* 19:   */   protected int reqLevel;
/* 20:   */   private boolean upgrade;
/* 21:   */   private boolean staff;
/* 22:   */   private boolean PvP;
/* 23:26 */   public Set<MapMonster> monsters = Collections.EMPTY_SET;
/* 24:27 */   public Map<Integer, Cell> cells = Collections.EMPTY_MAP;
/* 25:28 */   public Set<Integer> items = Collections.EMPTY_SET;
/* 26:30 */   public static final BeanCreator<Set<Integer>> beanItems = new BeanCreator()
/* 27:   */   {
/* 28:   */     public Set<Integer> createBean(ResultSet rs)
/* 29:   */       throws SQLException
/* 30:   */     {
/* 31:34 */       Set<Integer> set = new HashSet();
/* 32:   */       
/* 33:36 */       set.add(Integer.valueOf(rs.getInt("ItemID")));
/* 34:38 */       while (rs.next()) {
/* 35:39 */         set.add(Integer.valueOf(rs.getInt("ItemID")));
/* 36:   */       }
/* 37:41 */       return set;
/* 38:   */     }
/* 39:   */   };
/* 40:45 */   public static final ResultSetMapper<String, Area> resultSetMapper = new ResultSetMapper()
/* 41:   */   {
/* 42:   */     public AbstractMap.SimpleEntry<String, Area> mapRow(ResultSet rs)
/* 43:   */       throws SQLException
/* 44:   */     {
/* 45:49 */       Area area = new Area();
/* 46:   */       
/* 47:51 */       area.name = rs.getString("Name").toLowerCase();
/* 48:52 */       area.file = rs.getString("File");
/* 49:   */       
/* 50:54 */       area.id = rs.getInt("id");
/* 51:55 */       area.maxPlayers = rs.getInt("MaxPlayers");
/* 52:56 */       area.reqLevel = rs.getInt("ReqLevel");
/* 53:   */       
/* 54:58 */       area.upgrade = rs.getBoolean("Upgrade");
/* 55:59 */       area.staff = rs.getBoolean("Staff");
/* 56:60 */       area.PvP = rs.getBoolean("PvP");
/* 57:   */       
/* 58:62 */       return new AbstractMap.SimpleEntry(area.getName(), area);
/* 59:   */     }
/* 60:   */   };
/* 61:   */   
/* 62:   */   public String getName()
/* 63:   */   {
/* 64:67 */     return this.name;
/* 65:   */   }
/* 66:   */   
/* 67:   */   public String getFile()
/* 68:   */   {
/* 69:71 */     return this.file;
/* 70:   */   }
/* 71:   */   
/* 72:   */   public int getMaxPlayers()
/* 73:   */   {
/* 74:75 */     return this.maxPlayers;
/* 75:   */   }
/* 76:   */   
/* 77:   */   public int getReqLevel()
/* 78:   */   {
/* 79:79 */     return this.reqLevel;
/* 80:   */   }
/* 81:   */   
/* 82:   */   public boolean isUpgrade()
/* 83:   */   {
/* 84:83 */     return this.upgrade;
/* 85:   */   }
/* 86:   */   
/* 87:   */   public boolean isStaff()
/* 88:   */   {
/* 89:87 */     return this.staff;
/* 90:   */   }
/* 91:   */   
/* 92:   */   public int getId()
/* 93:   */   {
/* 94:91 */     return this.id;
/* 95:   */   }
/* 96:   */   
/* 97:   */   public boolean isPvP()
/* 98:   */   {
/* 99:95 */     return this.PvP;
/* :0:   */   }
/* :1:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Area

 * JD-Core Version:    0.7.0.1

 */