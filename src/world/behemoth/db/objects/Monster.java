package world.behemoth.db.objects;

import java.sql.ResultSet;
import java.util.AbstractMap;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import jdbchelper.BeanCreator;
import jdbchelper.ResultSetMapper;

public class Monster {
   private String name;
   private String race;
   private String file;
   private String linkage;
   private String element;
   private int id;
   private int health;
   private int mana;
   private int level;
   private int gold;
   private int experience;
   private int reputation;
   private int DPS;
   private int teamId;
   public Set<MonsterDrop> drops;
   public Set<MonsterSkill> skills;
  public Monster() {
      super();
   }
  public static final BeanCreator<Set<MonsterDrop>> beanDrops = new BeanCreator() {

    public Set<MonsterDrop> createBean(ResultSet rs)
      throws SQLException
    {
      Set<MonsterDrop> drops = new HashSet();
      MonsterDrop md = new MonsterDrop();
      
      md.itemId = rs.getInt("ItemID");
      md.quantity = rs.getInt("Quantity");
      md.chance = rs.getDouble("Chance");
      
       drops.add(md);
       while (rs.next())
      {
        MonsterDrop md2 = new MonsterDrop();
        
       md2.itemId = rs.getInt("ItemID");
        md2.quantity = rs.getInt("Quantity");
        md2.chance = rs.getDouble("Chance");
        
         drops.add(md2);
       }
      return drops;
    }
  };
   public static final ResultSetMapper<Integer, Monster> resultSetMapper = new ResultSetMapper()
   {
    public AbstractMap.SimpleEntry<Integer, Monster> mapRow(ResultSet rs)
       throws SQLException
     {
      Monster monster = new Monster();
      
      monster.id = rs.getInt("id");
       monster.name = rs.getString("Name");
       monster.race = rs.getString("Race");
       monster.file = rs.getString("File");
       monster.linkage = rs.getString("Linkage");
       monster.element = rs.getString("Element");
       
       monster.health = rs.getInt("Health");
       monster.mana = rs.getInt("Mana");
       monster.level = rs.getInt("Level");
       monster.gold = rs.getInt("Gold");
       monster.experience = rs.getInt("Experience");
       monster.reputation = rs.getInt("Reputation");
       monster.DPS = rs.getInt("DPS");
      monster.teamId = rs.getInt("TeamID");
       
       return new AbstractMap.SimpleEntry(Integer.valueOf(monster.id), monster);
    }
  };
   public static final BeanCreator<Set<MonsterSkill>> beanSkills = new BeanCreator() {
       public Set<MonsterSkill> createBean(ResultSet rs) throws SQLException {
      HashSet skills = new HashSet();
      MonsterSkill md = new MonsterSkill();
      md.skillId = rs.getInt("SkillID");
      md.cooldown = false;
      skills.add(md);

      while(rs.next()) {
         MonsterSkill md2 = new MonsterSkill();
         md2.skillId = rs.getInt("SkillID");
         md2.cooldown = false;
         skills.add(md2);
      }

      return skills;
   }
   };
 
  

   public String getName() {
      return this.name;
   }

   public String getRace() {
      return this.race;
   }

   public String getFile() {
      return this.file;
   }

   public String getLinkage() {
      return this.linkage;
   }

   public String getElement() {
      return this.element;
   }

   public int getHealth() {
      return this.health;
   }

   public int getMana() {
      return this.mana;
   }

   public int getLevel() {
      return this.level;
   }

   public int getGold() {
      return this.gold;
   }

   public int getExperience() {
      return this.experience;
   }

   public int getReputation() {
      return this.reputation;
   }

   public int getDPS() {
      return this.DPS;
   }

   public int getId() {
      return this.id;
   }

   public int getTeamId() {
      return this.teamId;
   }  
}
