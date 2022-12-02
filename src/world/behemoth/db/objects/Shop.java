/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import java.util.HashSet;
/*  7:   */ import java.util.Map;
/*  8:   */ import java.util.Set;
/*  9:   */ import jdbchelper.BeanCreator;
/* 10:   */ import jdbchelper.ResultSetMapper;
/* 11:   */ 
/* 12:   */ public class Shop
/* 13:   */ {
/* 14:   */   private int id;
/* 15:   */   private String name;
/* 16:   */   private String field;
/* 17:   */   private boolean house;
/* 18:   */   private boolean upgrade;
/* 19:   */   private boolean staff;
/* 20:   */   private boolean limited;
/* 21:   */   public Map<Integer, Integer> items;
/* 22:   */   public Set<Integer> locations;
/* 23:28 */   public static final BeanCreator<Set<Integer>> beanLocations = new BeanCreator()
/* 24:   */   {
/* 25:   */     public Set<Integer> createBean(ResultSet rs)
/* 26:   */       throws SQLException
/* 27:   */     {
/* 28:32 */       Set<Integer> set = new HashSet();
/* 29:   */       
/* 30:34 */       set.add(Integer.valueOf(rs.getInt("MapID")));
/* 31:36 */       while (rs.next()) {
/* 32:37 */         set.add(Integer.valueOf(rs.getInt("MapID")));
/* 33:   */       }
/* 34:39 */       return set;
/* 35:   */     }
/* 36:   */   };
/* 37:43 */   public static final ResultSetMapper<Integer, Integer> shopItemsMapper = new ResultSetMapper()
/* 38:   */   {
/* 39:   */     public AbstractMap.SimpleEntry<Integer, Integer> mapRow(ResultSet rs)
/* 40:   */       throws SQLException
/* 41:   */     {
/* 42:47 */       return new AbstractMap.SimpleEntry(Integer.valueOf(rs.getInt("id")), Integer.valueOf(rs.getInt("ItemID")));
/* 43:   */     }
/* 44:   */   };
/* 45:51 */   public static final ResultSetMapper<Integer, Shop> resultSetMapper = new ResultSetMapper()
/* 46:   */   {
/* 47:   */     public AbstractMap.SimpleEntry<Integer, Shop> mapRow(ResultSet rs)
/* 48:   */       throws SQLException
/* 49:   */     {
/* 50:55 */       Shop shop = new Shop();
/* 51:   */       
/* 52:57 */       shop.id = rs.getInt("id");
/* 53:   */       
/* 54:59 */       shop.name = rs.getString("Name");
/* 55:60 */       shop.field = rs.getString("Field");
/* 56:   */       
/* 57:62 */       shop.house = rs.getBoolean("House");
/* 58:63 */       shop.upgrade = rs.getBoolean("Upgrade");
/* 59:64 */       shop.staff = rs.getBoolean("Staff");
/* 60:65 */       shop.limited = rs.getBoolean("Limited");
/* 61:   */       
/* 62:67 */       return new AbstractMap.SimpleEntry(Integer.valueOf(shop.getId()), shop);
/* 63:   */     }
/* 64:   */   };
/* 65:   */   
/* 66:   */   public String getName()
/* 67:   */   {
/* 68:72 */     return this.name;
/* 69:   */   }
/* 70:   */   
/* 71:   */   public String getField()
/* 72:   */   {
/* 73:76 */     return this.field;
/* 74:   */   }
/* 75:   */   
/* 76:   */   public boolean isHouse()
/* 77:   */   {
/* 78:80 */     return this.house;
/* 79:   */   }
/* 80:   */   
/* 81:   */   public boolean isUpgrade()
/* 82:   */   {
/* 83:84 */     return this.upgrade;
/* 84:   */   }
/* 85:   */   
/* 86:   */   public boolean isStaff()
/* 87:   */   {
/* 88:88 */     return this.staff;
/* 89:   */   }
/* 90:   */   
/* 91:   */   public boolean isLimited()
/* 92:   */   {
/* 93:92 */     return this.limited;
/* 94:   */   }
/* 95:   */   
/* 96:   */   public int getId()
/* 97:   */   {
/* 98:96 */     return this.id;
/* 99:   */   }
/* :0:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Shop

 * JD-Core Version:    0.7.0.1

 */