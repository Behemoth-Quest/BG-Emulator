/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import jdbchelper.ResultSetMapper;
/*  7:   */ 
/*  8:   */ public class AuraEffects
/*  9:   */ {
/* 10:   */   private int id;
/* 11:   */   private String stat;
/* 12:   */   private String type;
/* 13:   */   private double value;
/* 14:22 */   public static final ResultSetMapper<Integer, AuraEffects> resultSetMapper = new ResultSetMapper()
/* 15:   */   {
/* 16:   */     public AbstractMap.SimpleEntry<Integer, AuraEffects> mapRow(ResultSet rs)
/* 17:   */       throws SQLException
/* 18:   */     {
/* 19:26 */       AuraEffects ae = new AuraEffects();
/* 20:   */       
/* 21:28 */       ae.id = rs.getInt("id");
/* 22:29 */       ae.stat = rs.getString("Stat");
/* 23:30 */       ae.type = rs.getString("Type");
/* 24:31 */       ae.value = rs.getDouble("Value");
/* 25:   */       
/* 26:33 */       return new AbstractMap.SimpleEntry(Integer.valueOf(ae.id), ae);
/* 27:   */     }
/* 28:   */   };
/* 29:   */   
/* 30:   */   public int getId()
/* 31:   */   {
/* 32:38 */     return this.id;
/* 33:   */   }
/* 34:   */   
/* 35:   */   public String getStat()
/* 36:   */   {
/* 37:42 */     return this.stat;
/* 38:   */   }
/* 39:   */   
/* 40:   */   public String getType()
/* 41:   */   {
/* 42:46 */     return this.type;
/* 43:   */   }
/* 44:   */   
/* 45:   */   public double getValue()
/* 46:   */   {
/* 47:50 */     return this.value;
/* 48:   */   }
/* 49:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.AuraEffects

 * JD-Core Version:    0.7.0.1

 */