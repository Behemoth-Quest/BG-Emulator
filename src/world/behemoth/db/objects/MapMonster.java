/*  1:   */ package world.behemoth.db.objects;
/*  2:   */ 
/*  3:   */ import java.sql.ResultSet;
/*  4:   */ import java.sql.SQLException;
/*  5:   */ import java.util.HashSet;
/*  6:   */ import java.util.Set;
/*  7:   */ import jdbchelper.BeanCreator;
/*  8:   */ 
/*  9:   */ public class MapMonster
/* 10:   */ {
/* 11:   */   private int monMapId;
/* 12:   */   private int monsterId;
/* 13:   */   private String frame;
/* 14:22 */   public static final BeanCreator<Set<MapMonster>> setCreator = new BeanCreator()
/* 15:   */   {
/* 16:   */     public Set<MapMonster> createBean(ResultSet rs)
/* 17:   */       throws SQLException
/* 18:   */     {
/* 19:26 */       Set<MapMonster> monsters = new HashSet();
/* 20:   */       
/* 21:28 */       MapMonster mapMonster = new MapMonster();
/* 22:   */       
/* 23:30 */       mapMonster.frame = rs.getString("Frame");
/* 24:31 */       mapMonster.monsterId = rs.getInt("MonsterID");
/* 25:32 */       mapMonster.monMapId = rs.getInt("MonMapID");
/* 26:   */       
/* 27:34 */       monsters.add(mapMonster);
/* 28:36 */       while (rs.next())
/* 29:   */       {
/* 30:37 */         mapMonster = new MapMonster();
/* 31:   */         
/* 32:39 */         mapMonster.frame = rs.getString("Frame");
/* 33:40 */         mapMonster.monsterId = rs.getInt("MonsterID");
/* 34:41 */         mapMonster.monMapId = rs.getInt("MonMapID");
/* 35:   */         
/* 36:43 */         monsters.add(mapMonster);
/* 37:   */       }
/* 38:46 */       return monsters;
/* 39:   */     }
/* 40:   */   };
/* 41:   */   
/* 42:   */   public int getMonMapId()
/* 43:   */   {
/* 44:52 */     return this.monMapId;
/* 45:   */   }
/* 46:   */   
/* 47:   */   public int getMonsterId()
/* 48:   */   {
/* 49:56 */     return this.monsterId;
/* 50:   */   }
/* 51:   */   
/* 52:   */   public String getFrame()
/* 53:   */   {
/* 54:60 */     return this.frame;
/* 55:   */   }
/* 56:   */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.MapMonster

 * JD-Core Version:    0.7.0.1

 */