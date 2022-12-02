package world.behemoth.db.objects;

import com.google.common.collect.Multimap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jdbchelper.BeanCreator;
import jdbchelper.ResultSetMapper;

public class Quest {
   private int id;
   private int factionId;
   private int reqReputation;
   private int reqClassId;
   private int reqClassPoints;
   private int experience;
   private int gold;
   private int reputation;
   private int classPoints;
   private int level;
   private int slot;
   private int value;
   private int index;
   private String name;
   private String description;
   private String endText;
   private String rewardType;
   private String field;
   private boolean upgrade;
   private boolean once;
   public Multimap<Integer, QuestReward> rewards;
   public Map<Integer, Integer> reqd;
   public Map<Integer, Integer> requirements;
   public Set<Integer> locations;
   
   public static final BeanCreator<Set<Integer>> beanLocations = new BeanCreator()
/*  38:    */   {
/*  39:    */     public Set<Integer> createBean(ResultSet rs)
/*  40:    */       throws SQLException
/*  41:    */     {
/*  42: 35 */       Set<Integer> set = new HashSet();
/*  43:    */       
/*  44: 37 */       set.add(Integer.valueOf(rs.getInt("MapID")));
/*  45: 39 */       while (rs.next()) {
/*  46: 40 */         set.add(Integer.valueOf(rs.getInt("MapID")));
/*  47:    */       }
/*  48: 42 */       return set;
/*  49:    */     }
/*  50:    */   };
   public static final ResultSetMapper<Integer, Integer> requirementsRewardsMapper = new ResultSetMapper()
/*  52:    */   {
/*  53:    */     public AbstractMap.SimpleEntry<Integer, Integer> mapRow(ResultSet rs)
/*  54:    */       throws SQLException
/*  55:    */     {
/*  56: 50 */       return new AbstractMap.SimpleEntry(Integer.valueOf(rs.getInt("ItemID")), Integer.valueOf(rs.getInt("Quantity")));
/*  57:    */     }
/*  58:    */   };
/*  59: 53 */   public static final ResultSetMapper<Integer, Quest> resultSetMapper = new ResultSetMapper()
/*  60:    */   {
/*  61:    */     public AbstractMap.SimpleEntry<Integer, Quest> mapRow(ResultSet rs)
/*  62:    */       throws SQLException
/*  63:    */     {
/*  64: 57 */       Quest quest = new Quest();
/*  65:    */       
/*  66: 59 */       quest.id = rs.getInt("id");
/*  67: 60 */       quest.factionId = rs.getInt("FactionID");
/*  68: 61 */       quest.reqReputation = rs.getInt("ReqReputation");
/*  69: 62 */       quest.reqClassId = rs.getInt("ReqClassID");
/*  70: 63 */       quest.reqClassPoints = rs.getInt("ReqClassPoints");
/*  71: 64 */       quest.experience = rs.getInt("Experience");
/*  72: 65 */       quest.gold = rs.getInt("Gold");
/*  73: 66 */       quest.reputation = rs.getInt("Reputation");
/*  74: 67 */       quest.classPoints = rs.getInt("ClassPoints");
/*  75: 68 */       quest.level = rs.getInt("Level");
/*  76: 69 */       quest.slot = rs.getInt("Slot");
/*  77: 70 */       quest.value = rs.getInt("Value");
/*  78: 71 */       quest.index = rs.getInt("Index");
/*  79:    */       
/*  80: 73 */       quest.name = rs.getString("Name");
/*  81: 74 */       quest.description = rs.getString("Description");
/*  82: 75 */       quest.endText = rs.getString("EndText");
/*  83: 76 */       quest.rewardType = rs.getString("RewardType");
/*  84: 77 */       quest.field = rs.getString("Field");
/*  85:    */       
/*  86: 79 */       quest.once = rs.getBoolean("Once");
/*  87: 80 */       quest.upgrade = rs.getBoolean("Upgrade");
/*  88:    */       
/*  89: 82 */       return new AbstractMap.SimpleEntry(Integer.valueOf(quest.getId()), quest);
/*  90:    */     }
/*  91:    */   };

   public Quest() {
      super();
   }

   public int getId() {
      return this.id;
   }

   public int getFactionId() {
      return this.factionId;
   }

   public int getReqReputation() {
      return this.reqReputation;
   }

   public int getReqClassId() {
      return this.reqClassId;
   }

   public int getReqClassPoints() {
      return this.reqClassPoints;
   }

   public int getExperience() {
      return this.experience;
   }

   public int getGold() {
      return this.gold;
   }

   public int getReputation() {
      return this.reputation;
   }

   public int getClassPoints() {
      return this.classPoints;
   }

   public int getLevel() {
      return this.level;
   }

   public int getSlot() {
      return this.slot;
   }

   public int getValue() {
      return this.value;
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getEndText() {
      return this.endText;
   }

   public String getRewardType() {
      return this.rewardType;
   }

   public String getField() {
      return this.field;
   }

   public boolean isUpgrade() {
      return this.upgrade;
   }

   public boolean isOnce() {
      return this.once;
   }
}
