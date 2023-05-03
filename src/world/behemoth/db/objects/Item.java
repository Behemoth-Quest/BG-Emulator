/*   1:    */ package world.behemoth.db.objects;
/*   2:    */ 
/*   3:    */ import java.sql.ResultSet;
/*   4:    */ import java.sql.SQLException;
import java.util.AbstractMap;
/*   5:    */
/*   6:    */ import java.util.Map;
/*   7:    */ import jdbchelper.ResultSetMapper;
/*   8:    */ import net.sf.json.JSONObject;
/*   9:    */ 
/*  10:    */ public class Item
/*  11:    */ {
/*  12:    */   public static final String EQUIPMENT_CLASS = "ar";
/*  13:    */   public static final String EQUIPMENT_ARMOR = "co";
/*  14:    */   public static final String EQUIPMENT_PET = "pe";
/*  15:    */   public static final String EQUIPMENT_HELM = "he";
/*  16:    */   public static final String EQUIPMENT_CAPE = "ba";
/*  17:    */   public static final String EQUIPMENT_WEAPON = "Weapon";
/*  18:    */   public static final String EQUIPMENT_AMULET = "am";
/*  19:    */   public static final String EQUIPMENT_HOUSE = "ho";
/*  20:    */   public static final String EQUIPMENT_HOUSE_ITEM = "hi";
/*  21:    */   private String name;
/*  22:    */   private String description;
/*  23:    */   private String type;
/*  24:    */   private String element;
/*  25:    */   private String file;
/*  26:    */   private String link;
/*  27:    */   private String icon;
/*  28:    */   private String equipment;
/*  29:    */   private String reqQuests;
/*  30:    */   private String meta;
/*  31:    */   private int id;
/*  32:    */   private int level;
/*  33:    */   private int DPS;
/*  34:    */   private int range;
/*  35:    */   private int rarity;
/*  36:    */   private int cost;
/*  37:    */   private int quantity;
/*  38:    */   private int stack;
/*  39:    */   private int enhId;
/*  40:    */   private int factionId;
/*  41:    */   private int reqReputation;
/*  42:    */   private int reqClassId;
/*  43:    */   private int reqClassPoints;
/*  44:    */   private int questStringIndex;
/*  45:    */   private int questStringValue;
/*  46:    */   private boolean coins;
/*  47:    */   private boolean upgrade;
/*  48:    */   private boolean staff;
/*  49:    */   private boolean temporary;
/*  50:    */   private boolean sell, market;
/*  51:    */   public Class classObj;
/*  52:    */   public Map<Integer, Integer> requirements;
/*  53: 38 */   public static final ResultSetMapper<Integer, Integer> requirementMapper = new ResultSetMapper()
/*  54:    */   {
/*  55:    */     public AbstractMap.SimpleEntry<Integer, Integer> mapRow(ResultSet rs)
/*  56:    */       throws SQLException
/*  57:    */     {
/*  58: 42 */       return new AbstractMap.SimpleEntry(Integer.valueOf(rs.getInt("ReqItemID")), Integer.valueOf(rs.getInt("Quantity")));
/*  59:    */     }
/*  60:    */   };
/*  61: 46 */   public static final ResultSetMapper<Integer, Item> resultSetMapper = new ResultSetMapper()
/*  62:    */   {
/*  63:    */     public AbstractMap.SimpleEntry<Integer, Item> mapRow(ResultSet rs)
/*  64:    */       throws SQLException
/*  65:    */     {
/*  66: 49 */       Item item = new Item();
/*  67:    */       
/*  68: 51 */       item.id = rs.getInt("id");
/*  69:    */       
/*  70:    */ 
/*  71: 54 */       item.name = rs.getString("Name");
/*  72: 55 */       item.description = rs.getString("Description");
/*  73: 56 */       item.type = rs.getString("Type");
/*  74: 57 */       item.element = rs.getString("Element");
/*  75: 58 */       item.file = rs.getString("File");
/*  76: 59 */       item.link = rs.getString("Link");
/*  77: 60 */       item.icon = rs.getString("Icon");
/*  78: 61 */       item.equipment = rs.getString("Equipment");
/*  79: 62 */       item.reqQuests = rs.getString("ReqQuests");
/*  80: 63 */       item.meta = rs.getString("Meta");
/*  81:    */       
/*  82:    */ 
/*  83: 66 */       item.level = rs.getInt("Level");
/*  84: 67 */       item.DPS = rs.getInt("DPS");
/*  85: 68 */       item.range = rs.getInt("Range");
/*  86: 69 */       item.rarity = rs.getInt("Rarity");
/*  87: 70 */       item.cost = rs.getInt("Cost");
/*  88: 71 */       item.quantity = rs.getInt("Quantity");
/*  89: 72 */       item.stack = rs.getInt("Stack");
/*  90: 73 */       item.enhId = rs.getInt("EnhID");
/*  91: 74 */       item.factionId = rs.getInt("FactionID");
/*  92: 75 */       item.reqReputation = rs.getInt("ReqReputation");
/*  93: 76 */       item.reqClassId = rs.getInt("ReqClassID");
/*  94: 77 */       item.reqClassPoints = rs.getInt("ReqClassPoints");
/*  95: 78 */       item.questStringIndex = rs.getInt("QuestStringIndex");
/*  96: 79 */       item.questStringValue = rs.getInt("QuestStringValue");
/*  97:    */       
/*  98:    */ 
/*  99: 82 */       item.coins = rs.getBoolean("Coins");
/* 100: 83 */       item.upgrade = rs.getBoolean("Upgrade");
/* 101: 84 */       item.staff = rs.getBoolean("Staff");
/* 102: 85 */       item.temporary = rs.getBoolean("Temporary");
/* 103: 86 */       item.sell = rs.getBoolean("Sell");
                    item.market = rs.getBoolean("Market");

/* 104:    */       
/* 105: 88 */       return new AbstractMap.SimpleEntry(Integer.valueOf(item.getId()), item);
/* 106:    */     }
/* 107:    */   };
/* 108:    */   
/* 109:    */   public static JSONObject getItemJSON(Item itemObj)
/* 110:    */   {
/* 111: 93 */     return getItemJSON(itemObj, null);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static JSONObject getItemJSON(Item itemObj, Enhancement enhancement)
/* 115:    */   {
/* 116: 97 */     if (itemObj == null) {
/* 117: 98 */       throw new NullPointerException("itemObj is null");
/* 118:    */     }
/* 119:100 */     JSONObject item = new JSONObject();
/* 120:    */     
/* 121:102 */     item.put("ItemID", Integer.valueOf(itemObj.getId()));
/* 122:103 */     item.put("bCoins", Integer.valueOf(itemObj.isCoins() ? 1 : 0));
/* 123:104 */     item.put("bHouse", Integer.valueOf(itemObj.isHouse() ? 1 : 0));
                    item.put("bSell", Integer.valueOf(itemObj.isSellable() ? 1 : 0));
                    item.put("bMarket", Integer.valueOf(itemObj.isMarketable() ? 1 : 0));
/* 124:105 */     item.put("bPTR", Integer.valueOf(0));
/* 125:106 */     item.put("bStaff", Integer.valueOf(itemObj.isStaff() ? 1 : 0));
/* 126:107 */     item.put("bTemp", Integer.valueOf(itemObj.isTemporary() ? 1 : 0));
/* 127:108 */     item.put("bUpg", Integer.valueOf(itemObj.isUpgrade() ? 1 : 0));
/* 128:109 */     item.put("iCost", Integer.valueOf(itemObj.getCost()));
/* 129:110 */     item.put("iDPS", Integer.valueOf(itemObj.getDPS()));
/* 130:111 */     item.put("iLvl", Integer.valueOf(itemObj.getLevel()));
/* 131:112 */     item.put("iQSindex", Integer.valueOf(itemObj.getQuestStringIndex()));
/* 132:113 */     item.put("iQSvalue", Integer.valueOf(itemObj.getQuestStringValue()));
/* 133:114 */     item.put("iRng", Integer.valueOf(itemObj.getRange()));
/* 134:115 */     item.put("iRty", Integer.valueOf(itemObj.getRarity()));
/* 135:116 */     item.put("iStk", Integer.valueOf(itemObj.getStack()));
/* 136:117 */     item.put("sDesc", itemObj.getDescription());
/* 137:118 */     item.put("sES", itemObj.getEquipment());
/* 138:119 */     item.put("sElmt", itemObj.getElement());
/* 139:120 */     item.put("sFile", itemObj.getFile());
/* 140:121 */     item.put("sIcon", itemObj.getIcon());
/* 141:122 */     item.put("sLink", itemObj.getLink());
/* 142:123 */     item.put("sMeta", itemObj.getMeta());
/* 143:124 */     item.put("sName", itemObj.getName());
/* 144:125 */     item.put("sReqQuests", itemObj.getReqQuests());
/* 145:126 */     item.put("sType", itemObj.getType());
/* 146:128 */     if (enhancement != null) {
/* 147:129 */       if (itemObj.getType().equals("Enhancement"))
/* 148:    */       {
/* 149:130 */         item.put("PatternID", Integer.valueOf(enhancement.getPatternId()));
/* 150:131 */         item.put("iDPS", Integer.valueOf(enhancement.getDPS()));
/* 151:132 */         item.put("iLvl", Integer.valueOf(enhancement.getLevel()));
/* 152:133 */         item.put("iRty", Integer.valueOf(enhancement.getRarity()));
/* 153:134 */         item.put("EnhID", Integer.valueOf(0));
/* 154:135 */         item.remove("sFile");
/* 155:    */       }
/* 156:    */       else
/* 157:    */       {
/* 158:137 */         item.put("EnhID", Integer.valueOf(enhancement.getId()));
/* 159:138 */         item.put("EnhLvl", Integer.valueOf(enhancement.getLevel()));
/* 160:139 */         item.put("EnhPatternID", Integer.valueOf(enhancement.getPatternId()));
/* 161:140 */         item.put("EnhRty", Integer.valueOf(enhancement.getRarity()));
/* 162:141 */         item.put("iRng", Integer.valueOf(itemObj.getRange()));
/* 163:142 */         item.put("EnhRng", Integer.valueOf(itemObj.getRange()));
/* 164:143 */         item.put("InvEnhPatternID", Integer.valueOf(enhancement.getPatternId()));
/* 165:144 */         item.put("EnhDPS", Integer.valueOf(enhancement.getDPS()));
/* 166:    */       }
/* 167:    */     }
/* 168:146 */     return item;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String getName()
/* 172:    */   {
/* 173:150 */     return this.name;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String getDescription()
/* 177:    */   {
/* 178:154 */     return this.description;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String getType()
/* 182:    */   {
/* 183:158 */     return this.type;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public String getElement()
/* 187:    */   {
/* 188:162 */     return this.element;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String getFile()
/* 192:    */   {
/* 193:166 */     return this.file;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public String getLink()
/* 197:    */   {
/* 198:170 */     return this.link;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public String getIcon()
/* 202:    */   {
/* 203:174 */     return this.icon;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String getEquipment()
/* 207:    */   {
/* 208:178 */     return this.equipment;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public int getLevel()
/* 212:    */   {
/* 213:182 */     return this.level;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public int getDPS()
/* 217:    */   {
/* 218:186 */     return this.DPS;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public int getRange()
/* 222:    */   {
/* 223:190 */     return this.range;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public int getRarity()
/* 227:    */   {
/* 228:194 */     return this.rarity;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public int getCost()
/* 232:    */   {
/* 233:198 */     return this.cost;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public int getQuantity()
/* 237:    */   {
/* 238:202 */     return this.quantity;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public int getStack()
/* 242:    */   {
/* 243:206 */     return this.stack;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public int getEnhId()
/* 247:    */   {
/* 248:210 */     return this.enhId;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public int getFactionId()
/* 252:    */   {
/* 253:214 */     return this.factionId;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public int getReqReputation()
/* 257:    */   {
/* 258:218 */     return this.reqReputation;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public int getReqClassId()
/* 262:    */   {
/* 263:222 */     return this.reqClassId;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public int getReqClassPoints()
/* 267:    */   {
/* 268:226 */     return this.reqClassPoints;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public int getQuestStringIndex()
/* 272:    */   {
/* 273:230 */     return this.questStringIndex;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public int getQuestStringValue()
/* 277:    */   {
/* 278:234 */     return this.questStringValue;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public boolean isCoins()
/* 282:    */   {
/* 283:238 */     return this.coins;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public boolean isSellable()
/* 287:    */   {
/* 288:242 */     return this.sell;
/* 289:    */   }

    public boolean isMarketable() {
        return this.market;
    }
/* 290:    */
/* 291:    */   public boolean isUpgrade()
/* 292:    */   {
/* 293:246 */     return this.upgrade;
/* 294:    */   }
/* 295:    */   
/* 296:    */   public boolean isHouse()
/* 297:    */   {
/* 298:250 */     return (getEquipment().equals("ho")) || (getEquipment().equals("hi"));
/* 299:    */   }
/* 300:    */   
/* 301:    */   public boolean isStaff()
/* 302:    */   {
/* 303:254 */     return this.staff;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public int getId()
/* 307:    */   {
/* 308:258 */     return this.id;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public boolean isTemporary()
/* 312:    */   {
/* 313:262 */     return this.temporary;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String getReqQuests()
/* 317:    */   {
/* 318:266 */     return this.reqQuests;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public String getMeta()
/* 322:    */   {
/* 323:270 */     return this.meta;
/* 324:    */   }
/* 325:    */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.db.objects.Item

 * JD-Core Version:    0.7.0.1

 */