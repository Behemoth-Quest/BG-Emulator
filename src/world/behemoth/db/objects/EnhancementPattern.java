/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import java.util.Collections;
/*  7:   */ import java.util.HashMap;
/*  8:   */ import java.util.Map;
/*  9:   */ import jdbchelper.ResultSetMapper;
/* 10:   */ 
/* 11:   */ public class EnhancementPattern
/* 12:   */ {
/* 13:   */   private int id;
/* 14:   */   private String name;
/* 15:   */   private String description;
/* 16:   */   private Map<String, Integer> stats;
/* 17:25 */   public static final ResultSetMapper<Integer, EnhancementPattern> resultSetMapper = new ResultSetMapper()
/* 18:   */   {
/* 19:   */     public AbstractMap.SimpleEntry<Integer, EnhancementPattern> mapRow(ResultSet rs)
/* 20:   */       throws SQLException
/* 21:   */     {
/* 22:29 */       EnhancementPattern ep = new EnhancementPattern();
/* 23:   */       
/* 24:31 */       ep.id = rs.getInt("id");
/* 25:32 */       ep.name = rs.getString("Name");
/* 26:33 */       ep.description = rs.getString("Desc");
/* 27:   */       
/* 28:35 */       ep.stats = new HashMap();
/* 29:   */       
/* 30:37 */       ep.stats.put("WIS", Integer.valueOf(rs.getInt("Wisdom")));
/* 31:38 */       ep.stats.put("END", Integer.valueOf(rs.getInt("Endurance")));
/* 32:39 */       ep.stats.put("LCK", Integer.valueOf(rs.getInt("Luck")));
/* 33:40 */       ep.stats.put("STR", Integer.valueOf(rs.getInt("Strength")));
/* 34:41 */       ep.stats.put("DEX", Integer.valueOf(rs.getInt("Dexterity")));
/* 35:42 */       ep.stats.put("INT", Integer.valueOf(rs.getInt("Intelligence")));
/* 36:   */       
/* 37:44 */       return new AbstractMap.SimpleEntry(Integer.valueOf(ep.getId()), ep);
/* 38:   */     }
/* 39:   */   };
/* 40:   */   
/* 41:   */   public int getId()
/* 42:   */   {
/* 43:49 */     return this.id;
/* 44:   */   }
/* 45:   */   
/* 46:   */   public String getName()
/* 47:   */   {
/* 48:53 */     return this.name;
/* 49:   */   }
/* 50:   */   
/* 51:   */   public String getDescription()
/* 52:   */   {
/* 53:57 */     return this.description;
/* 54:   */   }
/* 55:   */   
/* 56:   */   public Map<String, Integer> getStats()
/* 57:   */   {
/* 58:61 */     return Collections.unmodifiableMap(this.stats);
/* 59:   */   }
/* 60:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.EnhancementPattern

 * JD-Core Version:    0.7.0.1

 */