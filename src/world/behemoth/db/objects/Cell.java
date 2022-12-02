/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
import java.util.AbstractMap;
/*  5:   */
/*  6:   */ import jdbchelper.ResultSetMapper;
/*  7:   */ 
/*  8:   */ public class Cell
/*  9:   */ {
/* 10:   */   private String frame;
/* 11:   */   private String pad;
/* 12:   */   
/* 13:   */   public Cell(String frame, String pad)
/* 14:   */   {
/* 15:21 */     this.frame = frame;
/* 16:22 */     this.pad = pad;
/* 17:   */   }
/* 18:   */   
/* 19:25 */   public static final ResultSetMapper<Integer, Cell> resultSetMapper = new ResultSetMapper()
/* 20:   */   {
/* 21:   */     public AbstractMap.SimpleEntry<Integer, Cell> mapRow(ResultSet rs)
/* 22:   */       throws SQLException
/* 23:   */     {
/* 24:29 */       return new AbstractMap.SimpleEntry(Integer.valueOf(rs.getInt("id")), new Cell(rs.getString("Frame"), rs.getString("Pad")));
/* 25:   */     }
/* 26:   */   };
/* 27:   */   
/* 28:   */   public String getFrame()
/* 29:   */   {
/* 30:34 */     return this.frame;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public String getPad()
/* 34:   */   {
/* 35:38 */     return this.pad;
/* 36:   */   }
/* 37:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Cell

 * JD-Core Version:    0.7.0.1

 */