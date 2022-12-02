/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import jdbchelper.ResultSetMapper;
/*  7:   */ 
/*  8:   */ public class Hair
/*  9:   */ {
/* 10:   */   private int id;
/* 11:   */   private String name;
/* 12:   */   private String file;
/* 13:   */   private String gender;
/* 14:21 */   public static final ResultSetMapper<Integer, Hair> resultSetMapper = new ResultSetMapper()
/* 15:   */   {
/* 16:   */     public AbstractMap.SimpleEntry<Integer, Hair> mapRow(ResultSet rs)
/* 17:   */       throws SQLException
/* 18:   */     {
/* 19:25 */       Hair hair = new Hair();
/* 20:   */       
/* 21:27 */       hair.id = rs.getInt("id");
/* 22:   */       
/* 23:29 */       hair.name = rs.getString("Name");
/* 24:30 */       hair.file = rs.getString("File");
/* 25:31 */       hair.gender = rs.getString("Gender");
/* 26:   */       
/* 27:33 */       return new AbstractMap.SimpleEntry(Integer.valueOf(hair.id), hair);
/* 28:   */     }
/* 29:   */   };
/* 30:   */   
/* 31:   */   public String getName()
/* 32:   */   {
/* 33:38 */     return this.name;
/* 34:   */   }
/* 35:   */   
/* 36:   */   public String getFile()
/* 37:   */   {
/* 38:42 */     return this.file;
/* 39:   */   }
/* 40:   */   
/* 41:   */   public String getGender()
/* 42:   */   {
/* 43:46 */     return this.gender;
/* 44:   */   }
/* 45:   */   
/* 46:   */   public int getId()
/* 47:   */   {
/* 48:50 */     return this.id;
/* 49:   */   }
/* 50:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Hair

 * JD-Core Version:    0.7.0.1

 */