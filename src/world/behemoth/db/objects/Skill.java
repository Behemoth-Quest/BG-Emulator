/*   1:    */ package world.behemoth.db.objects;
/*   2:    */ 
/*   3:    */ import java.sql.ResultSet;
/*   4:    */ import java.sql.SQLException;
import java.util.AbstractMap;
/*   5:    */
/*   6:    */ import jdbchelper.ResultSetMapper;
/*   7:    */ 
/*   8:    */ public class Skill
/*   9:    */ {
/*  10:    */   private String name;
/*  11:    */   private String animation;
/*  12:    */   private String description;
/*  13:    */   private String icon;
/*  14:    */   private String dsrc;
/*  15:    */   private String reference;
/*  16:    */   private String target;
/*  17:    */   private String effects;
/*  18:    */   private String type;
/*  19:    */   private String strl;
/*  20:    */   private double damage;
/*  21:    */   private int id;
/*  22:    */   private int mana;
/*  23:    */   private int range;
/*  24:    */   private int hitTargets;
/*  25:    */   private int cooldown;
/*  26:    */   private int auraId;
/*  27: 22 */   public static final ResultSetMapper<Integer, Skill> resultSetMapper = new ResultSetMapper()
/*  28:    */   {
/*  29:    */     public AbstractMap.SimpleEntry<Integer, Skill> mapRow(ResultSet rs)
/*  30:    */       throws SQLException
/*  31:    */     {
/*  32: 26 */       Skill skill = new Skill();
/*  33:    */       
/*  34: 28 */       skill.id = rs.getInt("id");
/*  35:    */       
/*  36: 30 */       skill.name = rs.getString("Name");
/*  37: 31 */       skill.animation = rs.getString("Animation");
/*  38: 32 */       skill.description = rs.getString("Description");
/*  39: 33 */       skill.icon = rs.getString("Icon");
/*  40: 34 */       skill.dsrc = rs.getString("Dsrc");
/*  41: 35 */       skill.reference = rs.getString("Reference");
/*  42: 36 */       skill.target = rs.getString("Target");
/*  43: 37 */       skill.effects = rs.getString("Effects");
/*  44: 38 */       skill.type = rs.getString("Type");
/*  45: 39 */       skill.strl = rs.getString("Strl");
/*  46:    */       
/*  47: 41 */       skill.damage = rs.getDouble("Damage");
/*  48:    */       
/*  49: 43 */       skill.mana = rs.getInt("Mana");
/*  50: 44 */       skill.range = rs.getInt("Range");
/*  51: 45 */       skill.hitTargets = rs.getInt("HitTargets");
/*  52: 46 */       skill.cooldown = rs.getInt("Cooldown");
/*  53: 47 */       skill.auraId = rs.getInt("AuraID");
/*  54:    */       
/*  55: 49 */       return new AbstractMap.SimpleEntry(Integer.valueOf(skill.id), skill);
/*  56:    */     }
/*  57:    */   };
/*  58:    */   
/*  59:    */   public boolean hasAuraId()
/*  60:    */   {
/*  61: 54 */     return this.auraId > 0;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String getName()
/*  65:    */   {
/*  66: 58 */     return this.name;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String getAnimation()
/*  70:    */   {
/*  71: 62 */     return this.animation;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String getDescription()
/*  75:    */   {
/*  76: 66 */     return this.description;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String getIcon()
/*  80:    */   {
/*  81: 70 */     return this.icon;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String getDsrc()
/*  85:    */   {
/*  86: 74 */     return this.dsrc;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String getReference()
/*  90:    */   {
/*  91: 78 */     return this.reference;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getTarget()
/*  95:    */   {
/*  96: 82 */     return this.target;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String getEffects()
/* 100:    */   {
/* 101: 86 */     return this.effects;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String getType()
/* 105:    */   {
/* 106: 90 */     return this.type;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String getStrl()
/* 110:    */   {
/* 111: 94 */     return this.strl;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double getDamage()
/* 115:    */   {
/* 116: 98 */     return this.damage;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public int getMana()
/* 120:    */   {
/* 121:102 */     return this.mana;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public int getRange()
/* 125:    */   {
/* 126:106 */     return this.range;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public int getHitTargets()
/* 130:    */   {
/* 131:110 */     return this.hitTargets;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public int getCooldown()
/* 135:    */   {
/* 136:114 */     return this.cooldown;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public int getId()
/* 140:    */   {
/* 141:118 */     return this.id;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public int getAuraId()
/* 145:    */   {
/* 146:122 */     return this.auraId;
/* 147:    */   }
/* 148:    */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Skill

 * JD-Core Version:    0.7.0.1

 */