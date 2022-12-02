/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import jdbchelper.ResultSetMapper;
/*  7:   */ 
/*  8:   */ public class Enhancement
/*  9:   */ {
/* 10:   */   private String name;
/* 11:   */   private int id;
/* 12:   */   private int patternId;
/* 13:   */   private int rarity;
/* 14:   */   private int DPS;
/* 15:   */   private int level;
/* 16:21 */   public static final ResultSetMapper<Integer, Enhancement> resultSetMapper = new ResultSetMapper()
/* 17:   */   {
/* 18:   */     public AbstractMap.SimpleEntry<Integer, Enhancement> mapRow(ResultSet rs)
/* 19:   */       throws SQLException
/* 20:   */     {
/* 21:25 */       Enhancement enhancement = new Enhancement();
/* 22:   */       
/* 23:27 */       enhancement.id = rs.getInt("id");
/* 24:28 */       enhancement.name = rs.getString("Name");
/* 25:29 */       enhancement.patternId = rs.getInt("PatternID");
/* 26:30 */       enhancement.rarity = rs.getInt("Rarity");
/* 27:31 */       enhancement.DPS = rs.getInt("DPS");
/* 28:32 */       enhancement.level = rs.getInt("Level");
/* 29:   */       
/* 30:34 */       return new AbstractMap.SimpleEntry(Integer.valueOf(enhancement.id), enhancement);
/* 31:   */     }
/* 32:   */   };
/* 33:   */   
/* 34:   */   public String getName()
/* 35:   */   {
/* 36:39 */     return this.name;
/* 37:   */   }
/* 38:   */   
/* 39:   */   public int getPatternId()
/* 40:   */   {
/* 41:43 */     return this.patternId;
/* 42:   */   }
/* 43:   */   
/* 44:   */   public int getRarity()
/* 45:   */   {
/* 46:47 */     return this.rarity;
/* 47:   */   }
/* 48:   */   
/* 49:   */   public int getDPS()
/* 50:   */   {
/* 51:51 */     return this.DPS;
/* 52:   */   }
/* 53:   */   
/* 54:   */   public int getLevel()
/* 55:   */   {
/* 56:55 */     return this.level;
/* 57:   */   }
/* 58:   */   
/* 59:   */   public int getId()
/* 60:   */   {
/* 61:59 */     return this.id;
/* 62:   */   }
/* 63:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Enhancement

 * JD-Core Version:    0.7.0.1

 */