/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import java.util.Collections;
/*  7:   */ import java.util.HashSet;
/*  8:   */ import java.util.Set;
/*  9:   */ import jdbchelper.BeanCreator;
/* 10:   */ import jdbchelper.ResultSetMapper;
/* 11:   */ 
/* 12:   */ public class Hairshop
/* 13:   */ {
/* 14:   */   private int id;
/* 15:   */   public Set<Integer> male;
/* 16:   */   public Set<Integer> female;
/* 17:27 */   public static final BeanCreator<Set<Integer>> beanHairshopItems = new BeanCreator()
/* 18:   */   {
/* 19:   */     public Set<Integer> createBean(ResultSet rs)
/* 20:   */       throws SQLException
/* 21:   */     {
/* 22:30 */       Set<Integer> set = new HashSet();
/* 23:   */       
/* 24:32 */       set.add(Integer.valueOf(rs.getInt("HairID")));
/* 25:34 */       while (rs.next()) {
/* 26:35 */         set.add(Integer.valueOf(rs.getInt("HairID")));
/* 27:   */       }
/* 28:37 */       return set;
/* 29:   */     }
/* 30:   */   };
/* 31:41 */   public static final ResultSetMapper<Integer, Hairshop> resultSetMapper = new ResultSetMapper()
/* 32:   */   {
/* 33:   */     public AbstractMap.SimpleEntry<Integer, Hairshop> mapRow(ResultSet rs)
/* 34:   */       throws SQLException
/* 35:   */     {
/* 36:45 */       Hairshop hairshop = new Hairshop();
/* 37:46 */       hairshop.id = rs.getInt("id");
/* 38:47 */       return new AbstractMap.SimpleEntry(Integer.valueOf(hairshop.getId()), hairshop);
/* 39:   */     }
/* 40:   */   };
/* 41:   */   
/* 42:   */   public Set<Integer> getShopItems(String gender)
/* 43:   */   {
/* 44:52 */     if (gender.equals("M")) {
/* 45:53 */       return Collections.unmodifiableSet(this.male);
/* 46:   */     }
/* 47:55 */     return Collections.unmodifiableSet(this.female);
/* 48:   */   }
/* 49:   */   
/* 50:   */   public int getId()
/* 51:   */   {
/* 52:59 */     return this.id;
/* 53:   */   }
/* 54:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Hairshop

 * JD-Core Version:    0.7.0.1

 */