/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
/*  5:   */ import java.util.HashSet;
/*  6:   */ import java.util.Set;
/*  7:   */ import jdbchelper.BeanCreator;
/*  8:   */ 
/*  9:   */ public class Class
/* 10:   */ {
/* 11:   */   private String category;
/* 12:   */   private String description;
/* 13:   */   private String manaRegenerationMethods;
/* 14:   */   private String statsDescription;
/* 15:   */   public Set<Integer> skills;
/* 16:22 */   public static final BeanCreator<Set<Integer>> beanSkills = new BeanCreator()
/* 17:   */   {
/* 18:   */     public Set<Integer> createBean(ResultSet rs)
/* 19:   */       throws SQLException
/* 20:   */     {
/* 21:25 */       Set<Integer> set = new HashSet();
/* 22:   */       
/* 23:27 */       set.add(Integer.valueOf(rs.getInt("id")));
/* 24:29 */       while (rs.next()) {
/* 25:30 */         set.add(Integer.valueOf(rs.getInt("id")));
/* 26:   */       }
/* 27:32 */       return set;
/* 28:   */     }
/* 29:   */   };
/* 30:36 */   public static final BeanCreator<Class> beanCreator = new BeanCreator()
/* 31:   */   {
/* 32:   */     public Class createBean(ResultSet rs)
/* 33:   */       throws SQLException
/* 34:   */     {
/* 35:40 */       Class oClass = new Class();
/* 36:   */       
/* 37:42 */       oClass.category = rs.getString("Category");
/* 38:43 */       oClass.description = rs.getString("Description");
/* 39:44 */       oClass.manaRegenerationMethods = rs.getString("ManaRegenerationMethods");
/* 40:45 */       oClass.statsDescription = rs.getString("StatsDescription");
/* 41:   */       
/* 42:47 */       return oClass;
/* 43:   */     }
/* 44:   */   };
/* 45:   */   
/* 46:   */   public String getCategory()
/* 47:   */   {
/* 48:52 */     return this.category;
/* 49:   */   }
/* 50:   */   
/* 51:   */   public String getDescription()
/* 52:   */   {
/* 53:56 */     return this.description;
/* 54:   */   }
/* 55:   */   
/* 56:   */   public String getManaRegenerationMethods()
/* 57:   */   {
/* 58:60 */     return this.manaRegenerationMethods;
/* 59:   */   }
/* 60:   */   
/* 61:   */   public String getStatsDescription()
/* 62:   */   {
/* 63:64 */     return this.statsDescription;
/* 64:   */   }
/* 65:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Class

 * JD-Core Version:    0.7.0.1

 */