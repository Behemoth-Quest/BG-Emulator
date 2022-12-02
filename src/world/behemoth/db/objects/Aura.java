package world.behemoth.db.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import jdbchelper.BeanCreator;
import jdbchelper.ResultSetMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Aura {
   private int id;
   private int duration;
   private String name;
   private String category;
   private double damageIncrease;
   private double damageTakenDecrease;
   public Set<Integer> effects;
   public boolean selfCast = false;

   public Aura() {
      super();
   }
public static final BeanCreator<Set<Integer>> beanEffects = new BeanCreator()
   {
     public Set<Integer> createBean(ResultSet rs)
       throws SQLException
     {
      Set<Integer> set = new HashSet();
       set.add(Integer.valueOf(rs.getInt("id")));
      while (rs.next()) {
         set.add(Integer.valueOf(rs.getInt("id")));
       }
       return set;
     }
   };
public static final ResultSetMapper<Integer, Aura> resultSetMapper = new ResultSetMapper()
   {
     public AbstractMap.SimpleEntry<Integer, Aura> mapRow(ResultSet rs)
       throws SQLException
     {
      Aura aura = new Aura();
      
       aura.id = rs.getInt("id");
       aura.duration = rs.getInt("Duration");
       
       aura.name = rs.getString("Name");
       aura.category = rs.getString("Category");
       
       aura.damageIncrease = rs.getDouble("DamageIncrease");
     aura.damageTakenDecrease = rs.getDouble("DamageTakenDecrease");
      
      return new AbstractMap.SimpleEntry(Integer.valueOf(aura.id), aura);
    }
   };   
   public JSONArray getAuraArray(boolean isNew) {
      JSONArray auras = new JSONArray();
      JSONObject auraInfo = new JSONObject();
      if(!this.getCategory().isEmpty() && !this.getCategory().equals("d")) {
         auraInfo.put("cat", this.getCategory());
         if(this.getCategory().equals("stun")) {
            auraInfo.put("s", "s");
         }
      }

      auraInfo.put("nam", this.getName());
      auraInfo.put("t", "s");
      auraInfo.put("dur", String.valueOf(this.getDuration()));
      auraInfo.put("isNew", Boolean.valueOf(isNew));
      auras.add(auraInfo);
      return auras;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getCategory() {
      return this.category;
   }

   public double getDamageIncrease() {
      return this.damageIncrease;
   }

   public double getDamageTakenDecrease() {
      return this.damageTakenDecrease;
   }

   public int getDuration() {
      return this.duration;
   }
}
