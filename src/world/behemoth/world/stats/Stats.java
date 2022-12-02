package world.behemoth.world.stats;

import world.behemoth.db.objects.AuraEffects;
import world.behemoth.db.objects.Enhancement;
import world.behemoth.db.objects.Item;
import world.behemoth.db.objects.Skill;
import world.behemoth.world.World;
import it.gotoandplay.smartfoxserver.SmartFoxServer;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;

public class Stats {
   public static final Map<String, List<Double>> classCatMap;
   public static final Map<String, Double> ratioByEquipment;
   private double $cai = 1.0D;
   private double $cao = 1.0D;
   private double $cdi = 1.0D;
   private double $cdo = 1.0D;
   private double $chi = 1.0D;
   private double $cho = 1.0D;
   private double $cmc = 1.0D;
   private double $cmi = 1.0D;
   private double $cmo = 1.0D;
   private double $cpi = 1.0D;
   private double $cpo = 1.0D;
   private double $sbm = 0.7D;
   private double $scm = 1.5D;
   private double $sem = 0.05D;
   private double $shb = 0.0D;
   private double $smb = 0.0D;
   private double $srm = 0.7D;
   private double attackPower = 0.0D;
   private double magicPower = 0.0D;
   private double block = 0.0D;
   private double criticalHit = 0.05D;
   private double evasion = 0.04D;
   private double haste = 0.0D;
   private double hit = 0.0D;
   private double parry = 0.03D;
   private double resist = 0.7D;
   private double _ap = 0.0D;
   private double _cai = 1.0D;
   private double _cao = 1.0D;
   private double _cdi = 1.0D;
   private double _cdo = 1.0D;
   private double _chi = 1.0D;
   private double _cho = 1.0D;
   private double _cmc = 1.0D;
   private double _cmi = 1.0D;
   private double _cmo = 1.0D;
   private double _cpi = 1.0D;
   private double _cpo = 1.0D;
   private double _sbm = 0.7D;
   private double _scm = 1.5D;
   private double _sem = 0.05D;
   private double _shb = 0.0D;
   private double _smb = 0.0D;
   private double _sp = 0.0D;
   private double _srm = 0.7D;
   private double _tbl = 0.0D;
   private double _tcr = 0.0D;
   private double _tdo = 0.0D;
   private double _tha = 0.0D;
   private double _thi = 0.0D;
   private double _tpa = 0.0D;
   private double _tre = 0.0D;
   private int minDmg = 0;
   private int maxDmg = 0;
   public int wDPS = 0;
   public Map<String, Double> innate = new LinkedHashMap(6);
   public Map<String, Double> weapon = new LinkedHashMap(6);
   public Map<String, Double> helm = new LinkedHashMap(6);
   public Map<String, Double> armor = new LinkedHashMap(6);
   public Map<String, Double> cape = new LinkedHashMap(6);
   public Set<AuraEffects> effects = new LinkedHashSet();
   private User user;
   private World world;

   public Stats(User user, World world) {
      super();
      this.innate.put("STR", Double.valueOf(0.0D));
      this.innate.put("END", Double.valueOf(0.0D));
      this.innate.put("DEX", Double.valueOf(0.0D));
      this.innate.put("INT", Double.valueOf(0.0D));
      this.innate.put("WIS", Double.valueOf(0.0D));
      this.innate.put("LCK", Double.valueOf(0.0D));
      this.weapon.put("STR", Double.valueOf(0.0D));
      this.weapon.put("END", Double.valueOf(0.0D));
      this.weapon.put("DEX", Double.valueOf(0.0D));
      this.weapon.put("INT", Double.valueOf(0.0D));
      this.weapon.put("WIS", Double.valueOf(0.0D));
      this.weapon.put("LCK", Double.valueOf(0.0D));
      this.helm.put("STR", Double.valueOf(0.0D));
      this.helm.put("END", Double.valueOf(0.0D));
      this.helm.put("DEX", Double.valueOf(0.0D));
      this.helm.put("INT", Double.valueOf(0.0D));
      this.helm.put("WIS", Double.valueOf(0.0D));
      this.helm.put("LCK", Double.valueOf(0.0D));
      this.armor.put("STR", Double.valueOf(0.0D));
      this.armor.put("END", Double.valueOf(0.0D));
      this.armor.put("DEX", Double.valueOf(0.0D));
      this.armor.put("INT", Double.valueOf(0.0D));
      this.armor.put("WIS", Double.valueOf(0.0D));
      this.armor.put("LCK", Double.valueOf(0.0D));
      this.cape.put("STR", Double.valueOf(0.0D));
      this.cape.put("END", Double.valueOf(0.0D));
      this.cape.put("DEX", Double.valueOf(0.0D));
      this.cape.put("INT", Double.valueOf(0.0D));
      this.cape.put("WIS", Double.valueOf(0.0D));
      this.cape.put("LCK", Double.valueOf(0.0D));
      this.user = user;
      this.world = world;
   }

   public void sendStatChanges(Stats stat, Set<AuraEffects> effects) {
      JSONObject stu = new JSONObject();
      JSONObject sta = new JSONObject();
      Iterator i$ = effects.iterator();

      while(i$.hasNext()) {
         AuraEffects ae = (AuraEffects)i$.next();
         if(ae.getStat().equals("tha")) {
            sta.put("$tha", Double.valueOf(this.haste));
         } else if(ae.getStat().equals("tdo")) {
            sta.put("$tdo", Double.valueOf(this.evasion));
         } else if(ae.getStat().equals("thi")) {
            sta.put("$thi", Double.valueOf(this.hit));
         } else if(ae.getStat().equals("tcr")) {
            sta.put("$tcr", Double.valueOf(this.criticalHit));
         }
      }

      stu.put("cmd", "stu");
      stu.put("sta", sta);
      this.world.send(stu, this.user);
   }

   public void update() {
      this.initInnateStats();
      this.applyCoreStatRatings();
      this.applyAuraEffects();
      this.initDamage();
   }

   private void applyAuraEffects() {
      Iterator i$ = this.effects.iterator();

      while(i$.hasNext()) {
         AuraEffects ae = (AuraEffects)i$.next();
         if(ae.getStat().equals("tha")) {
            if(ae.getType().equals("+")) {
               this.haste += ae.getValue();
            } else if(ae.getType().equals("-")) {
               this.haste -= ae.getValue();
            } else {
               this.haste *= ae.getValue();
            }
         } else if(ae.getStat().equals("tdo")) {
            if(ae.getType().equals("+")) {
               this.evasion += ae.getValue();
            } else if(ae.getType().equals("-")) {
               this.evasion -= ae.getValue();
            } else {
               this.evasion *= ae.getValue();
            }
         } else if(ae.getStat().equals("thi")) {
            if(ae.getType().equals("+")) {
               this.hit += ae.getValue();
            } else if(ae.getType().equals("-")) {
               this.hit -= ae.getValue();
            } else {
               this.hit *= ae.getValue();
            }
         } else if(ae.getStat().equals("tcr")) {
            if(ae.getType().equals("+")) {
               this.criticalHit += ae.getValue();
            } else if(ae.getType().equals("-")) {
               this.criticalHit -= ae.getValue();
            } else {
               this.criticalHit *= ae.getValue();
            }
         }
      }

   }

   private void initInnateStats() {
      int level = ((Integer)this.user.properties.get("level")).intValue();
      String cat = (String)this.user.properties.get("classcat");
      int innateStat = this.world.getInnateStats(level);
      List ratios = (List)classCatMap.get(cat);
      Set keyEntry = this.innate.keySet();
      int i = 0;

      for(Iterator i$ = keyEntry.iterator(); i$.hasNext(); ++i) {
         String key = (String)i$.next();
         double stat = (double)Math.round(((Double)ratios.get(i)).doubleValue() * (double)innateStat);
         this.innate.put(key, Double.valueOf(stat));
      }

   }

   private void resetValues() {
      this._ap = 0.0D;
      this.attackPower = 0.0D;
      this._sp = 0.0D;
      this.magicPower = 0.0D;
      this._tbl = 0.0D;
      this._tpa = 0.0D;
      this._tdo = 0.0D;
      this._tcr = 0.0D;
      this._thi = 0.0D;
      this._tha = 0.0D;
      this._tre = 0.0D;
      this.block = ((Double)this.world.coreValues.get("baseBlock")).doubleValue();
      this.parry = ((Double)this.world.coreValues.get("baseParry")).doubleValue();
      this.evasion = ((Double)this.world.coreValues.get("baseDodge")).doubleValue();
      this.criticalHit = ((Double)this.world.coreValues.get("baseCrit")).doubleValue();
      this.hit = ((Double)this.world.coreValues.get("baseHit")).doubleValue();
      this.haste = ((Double)this.world.coreValues.get("baseHaste")).doubleValue();
      this.resist = 0.0D;
      this._cpo = 1.0D;
      this._cpi = 1.0D;
      this._cao = 1.0D;
      this._cai = 1.0D;
      this._cmo = 1.0D;
      this._cmi = 1.0D;
      this._cdo = 1.0D;
      this._cdi = 1.0D;
      this._cho = 1.0D;
      this._chi = 1.0D;
      this._cmc = 1.0D;
      this.$cpo = 1.0D;
      this.$cpi = 1.0D;
      this.$cao = 1.0D;
      this.$cai = 1.0D;
      this.$cmo = 1.0D;
      this.$cmi = 1.0D;
      this.$cdo = 1.0D;
      this.$cdi = 1.0D;
      this.$cho = 1.0D;
      this.$chi = 1.0D;
      this.$cmc = 1.0D;
      this._scm = ((Double)this.world.coreValues.get("baseCritValue")).doubleValue();
      this._sbm = ((Double)this.world.coreValues.get("baseBlockValue")).doubleValue();
      this._srm = ((Double)this.world.coreValues.get("baseResistValue")).doubleValue();
      this._sem = ((Double)this.world.coreValues.get("baseEventValue")).doubleValue();
      this.$scm = ((Double)this.world.coreValues.get("baseCritValue")).doubleValue();
      this.$sbm = ((Double)this.world.coreValues.get("baseBlockValue")).doubleValue();
      this.$srm = ((Double)this.world.coreValues.get("baseResistValue")).doubleValue();
      this.$sem = ((Double)this.world.coreValues.get("baseEventValue")).doubleValue();
      this._shb = 0.0D;
      this._smb = 0.0D;
      this.$shb = 0.0D;
      this.$smb = 0.0D;
   }

   private void applyCoreStatRatings() {
      String cat = (String)this.user.properties.get("classcat");
      Enhancement enhancement = (Enhancement)this.user.properties.get("weaponitemenhancement");
      int level = ((Integer)this.user.properties.get("level")).intValue();
      double wLvl = enhancement != null?(double)enhancement.getLevel():1.0D;
      double iDPS = enhancement != null?(double)enhancement.getDPS():100.0D;
      iDPS = iDPS == 0.0D?100.0D:iDPS;
      iDPS /= 100.0D;
      double intAPtoDPS = (double)((Double)this.world.coreValues.get("intAPtoDPS")).intValue();
      double PCDPSMod = ((Double)this.world.coreValues.get("PCDPSMod")).doubleValue();
      double hpTgt = (double)this.world.getBaseHPByLevel(level);
      double TTD = 20.0D;
      double tDPS = hpTgt / 20.0D * 0.7D;
      double sp1pc = 2.25D * tDPS / (100.0D / intAPtoDPS) / 2.0D;
      this.resetValues();
      Set keyEntry = this.innate.keySet();
      Iterator i$ = keyEntry.iterator();

      while(true) {
         double val;
         label187:
         do {
            while(i$.hasNext()) {
               String key = (String)i$.next();
               val = ((Double)this.innate.get(key)).doubleValue() + ((Double)this.armor.get(key)).doubleValue() + ((Double)this.weapon.get(key)).doubleValue() + ((Double)this.helm.get(key)).doubleValue() + ((Double)this.cape.get(key)).doubleValue();
               if(key.equals("STR")) {
                  if(cat.equals("M1")) {
                     this.$sbm -= val / sp1pc / 100.0D * 0.3D;
                  }

                  if(cat.equals("S1")) {
                     this.attackPower += (double)Math.round(val * 1.4D);
                  } else {
                     this.attackPower += val * 2.0D;
                  }
                  continue label187;
               }

               if(key.equals("INT")) {
                  this.$cmi -= val / sp1pc / 100.0D;
                  if(cat.substring(0, 1).equals("C") || cat.equals("M3")) {
                     this.$cmo += val / sp1pc / 100.0D;
                  }

                  if(cat.equals("S1")) {
                     this.magicPower += (double)Math.round(val * 1.4D);
                  } else {
                     this.magicPower += val * 2.0D;
                  }

                  if(cat.equals("C1") || cat.equals("C2") || cat.equals("C3") || cat.equals("M3") || cat.equals("S1")) {
                     if(cat.equals("C2")) {
                        this.haste += val / sp1pc / 100.0D * 0.5D;
                     } else {
                        this.haste += val / sp1pc / 100.0D * 0.3D;
                     }
                  }
               } else if(key.equals("DEX")) {
                  if(cat.equals("M1") || cat.equals("M2") || cat.equals("M3") || cat.equals("M4") || cat.equals("S1")) {
                     if(!cat.substring(0, 1).equals("C")) {
                        this.hit += val / sp1pc / 100.0D * 0.2D;
                     }

                     if(!cat.equals("M2") && !cat.equals("M4")) {
                        this.haste += val / sp1pc / 100.0D * 0.3D;
                     } else {
                        this.haste += val / sp1pc / 100.0D * 0.5D;
                     }

                     if(cat.equals("M1") && this._tbl > 0.01D) {
                        this.block += val / sp1pc / 100.0D * 0.5D;
                     }
                  }

                  if(!cat.equals("M2") && !cat.equals("M3")) {
                     this.evasion += val / sp1pc / 100.0D * 0.3D;
                  } else {
                     this.evasion += val / sp1pc / 100.0D * 0.5D;
                  }
               } else if(key.equals("WIS")) {
                  if(cat.equals("C1") || cat.equals("C2") || cat.equals("C3") || cat.equals("S1")) {
                     if(cat.equals("C1")) {
                        this.criticalHit += val / sp1pc / 100.0D * 0.7D;
                     } else {
                        this.criticalHit += val / sp1pc / 100.0D * 0.4D;
                     }

                     this.hit += val / sp1pc / 100.0D * 0.2D;
                  }

                  this.evasion += val / sp1pc / 100.0D * 0.3D;
               } else if(key.equals("LCK")) {
                  this.$sem += val / sp1pc / 100.0D * 2.0D;
                  if(cat.equals("S1")) {
                     this.attackPower += (double)Math.round(val * 1.0D);
                     this.magicPower += (double)Math.round(val * 1.0D);
                     this.criticalHit += val / sp1pc / 100.0D * 0.3D;
                     this.hit += val / sp1pc / 100.0D * 0.1D;
                     this.haste += val / sp1pc / 100.0D * 0.3D;
                     this.evasion += val / sp1pc / 100.0D * 0.25D;
                     this.$scm += val / sp1pc / 100.0D * 2.5D;
                  } else {
                     if(cat.equals("M1") || cat.equals("M2") || cat.equals("M3") || cat.equals("M4")) {
                        this.attackPower += (double)Math.round(val * 0.7D);
                     }

                     if(cat.equals("C1") || cat.equals("C2") || cat.equals("C3") || cat.equals("M3")) {
                        this.magicPower += (double)Math.round(val * 0.7D);
                     }

                     this.criticalHit += val / sp1pc / 100.0D * 0.2D;
                     this.hit += val / sp1pc / 100.0D * 0.1D;
                     this.haste += val / sp1pc / 100.0D * 0.1D;
                     this.evasion += val / sp1pc / 100.0D * 0.1D;
                     this.$scm += val / sp1pc / 100.0D * 5.0D;
                  }
               }
            }

            this.wDPS = (int)(Math.round((double)this.world.getBaseHPByLevel((int)wLvl) / TTD * iDPS * PCDPSMod) + Math.round(this.attackPower / intAPtoDPS));
            return;
         } while(!cat.equals("M1") && !cat.equals("M2") && !cat.equals("M3") && !cat.equals("M4") && !cat.equals("S1"));

         if(cat.equals("M4")) {
            this.criticalHit += val / sp1pc / 100.0D * 0.7D;
         } else {
            this.criticalHit += val / sp1pc / 100.0D * 0.4D;
         }
      }
   }

   private void initDamage() {
      Map userSkills = (Map)this.user.properties.get("skills");
      Item weaponItem = (Item)this.user.properties.get("weaponitem");
      if(userSkills != null && weaponItem != null) {
         Skill autoAttack = (Skill)this.world.skills.get(userSkills.get("aa"));
         double wSPD = 2.0D;
         double wDMG = (double)this.wDPS * wSPD;
         double wepRng = (double)weaponItem.getRange();
         double iRNG = wepRng / 100.0D;
         double tDMG = wDMG * autoAttack.getDamage();
         this.minDmg = (int)Math.floor(tDMG - tDMG * iRNG);
         this.maxDmg = (int)Math.ceil(tDMG + tDMG * iRNG);
         SmartFoxServer.log.fine("wSPD : " + wSPD);
         SmartFoxServer.log.fine("wDMG : " + wDMG);
         SmartFoxServer.log.fine("iRNG : " + iRNG);
         SmartFoxServer.log.fine("tDMG : " + tDMG);
      }

   }

   public int get$DEX() {
      return (int)(((Double)this.weapon.get("DEX")).doubleValue() + ((Double)this.armor.get("DEX")).doubleValue() + ((Double)this.helm.get("DEX")).doubleValue() + ((Double)this.cape.get("DEX")).doubleValue());
   }

   public int get$END() {
      return (int)(((Double)this.weapon.get("END")).doubleValue() + ((Double)this.armor.get("END")).doubleValue() + ((Double)this.helm.get("END")).doubleValue() + ((Double)this.cape.get("END")).doubleValue());
   }

   public int get$INT() {
      return (int)(((Double)this.weapon.get("INT")).doubleValue() + ((Double)this.armor.get("INT")).doubleValue() + ((Double)this.helm.get("INT")).doubleValue() + ((Double)this.cape.get("INT")).doubleValue());
   }

   public int get$LCK() {
      return (int)(((Double)this.weapon.get("LCK")).doubleValue() + ((Double)this.armor.get("LCK")).doubleValue() + ((Double)this.helm.get("LCK")).doubleValue() + ((Double)this.cape.get("LCK")).doubleValue());
   }

   public int get$STR() {
      return (int)(((Double)this.weapon.get("STR")).doubleValue() + ((Double)this.armor.get("STR")).doubleValue() + ((Double)this.helm.get("STR")).doubleValue() + ((Double)this.cape.get("STR")).doubleValue());
   }

   public int get$WIS() {
      return (int)(((Double)this.weapon.get("WIS")).doubleValue() + ((Double)this.armor.get("WIS")).doubleValue() + ((Double)this.helm.get("WIS")).doubleValue() + ((Double)this.cape.get("WIS")).doubleValue());
   }

   public double get$ap() {
      return this.attackPower;
   }

   public double get$cai() {
      return this.$cai;
   }

   public double get$cao() {
      return this.$cao;
   }

   public double get$cdi() {
      return this.$cdi;
   }

   public double get$cdo() {
      return this.$cdo;
   }

   public double get$chi() {
      return this.$chi;
   }

   public double get$cho() {
      return this.$cho;
   }

   public double get$cmc() {
      return this.$cmc;
   }

   public double get$cmi() {
      return this.$cmi;
   }

   public double get$cmo() {
      return this.$cmo;
   }

   public double get$cpi() {
      return this.$cpi;
   }

   public double get$cpo() {
      return this.$cpo;
   }

   public double get$sbm() {
      return this.$sbm;
   }

   public double get$scm() {
      return this.$scm;
   }

   public double get$sem() {
      return this.$sem;
   }

   public double get$shb() {
      return this.$shb;
   }

   public double get$smb() {
      return this.$smb;
   }

   public double get$sp() {
      return this.magicPower;
   }

   public double get$srm() {
      return this.$srm;
   }

   public double get$tbl() {
      return this.block;
   }

   public double get$tcr() {
      return this.criticalHit;
   }

   public double get$tdo() {
      return this.evasion;
   }

   public double get$tha() {
      return this.haste;
   }

   public double get$thi() {
      return this.hit;
   }

   public double get$tpa() {
      return this.parry;
   }

   public double get$tre() {
      return this.resist;
   }

   public double get_DEX() {
      return ((Double)this.innate.get("DEX")).doubleValue();
   }

   public double get_END() {
      return ((Double)this.innate.get("END")).doubleValue();
   }

   public double get_INT() {
      return ((Double)this.innate.get("INT")).doubleValue();
   }

   public double get_LCK() {
      return ((Double)this.innate.get("LCK")).doubleValue();
   }

   public double get_STR() {
      return ((Double)this.innate.get("STR")).doubleValue();
   }

   public double get_WIS() {
      return ((Double)this.innate.get("WIS")).doubleValue();
   }

   public double get_ap() {
      return this._ap;
   }

   public double get_cai() {
      return this._cai;
   }

   public double get_cao() {
      return this._cao;
   }

   public double get_cdi() {
      return this._cdi;
   }

   public double get_cdo() {
      return this._cdo;
   }

   public double get_chi() {
      return this._chi;
   }

   public double get_cho() {
      return this._cho;
   }

   public double get_cmc() {
      return this._cmc;
   }

   public double get_cmi() {
      return this._cmi;
   }

   public double get_cmo() {
      return this._cmo;
   }

   public double get_cpi() {
      return this._cpi;
   }

   public double get_cpo() {
      return this._cpo;
   }

   public double get_sbm() {
      return this._sbm;
   }

   public double get_scm() {
      return this._scm;
   }

   public double get_sem() {
      return this._sem;
   }

   public double get_shb() {
      return this._shb;
   }

   public double get_smb() {
      return this._smb;
   }

   public double get_sp() {
      return this._sp;
   }

   public double get_srm() {
      return this._srm;
   }

   public double get_tbl() {
      return this._tbl;
   }

   public double get_tcr() {
      return this._tcr;
   }

   public double get_tdo() {
      return this._tdo;
   }

   public double get_tha() {
      return this._tha;
   }

   public double get_thi() {
      return this._thi;
   }

   public double get_tpa() {
      return this._tpa;
   }

   public double get_tre() {
      return this._tre;
   }

   public int getMinDmg() {
      return this.minDmg;
   }

   public int getMaxDmg() {
      return this.maxDmg;
   }

   static {
      List M1 = Arrays.asList(new Double[]{Double.valueOf(0.27D), Double.valueOf(0.3D), Double.valueOf(0.22D), Double.valueOf(0.05D), Double.valueOf(0.1D), Double.valueOf(0.06D)});
      List M2 = Arrays.asList(new Double[]{Double.valueOf(0.2D), Double.valueOf(0.22D), Double.valueOf(0.33D), Double.valueOf(0.05D), Double.valueOf(0.1D), Double.valueOf(0.1D)});
      List M3 = Arrays.asList(new Double[]{Double.valueOf(0.24D), Double.valueOf(0.2D), Double.valueOf(0.2D), Double.valueOf(0.24D), Double.valueOf(0.07D), Double.valueOf(0.05D)});
      List M4 = Arrays.asList(new Double[]{Double.valueOf(0.3D), Double.valueOf(0.18D), Double.valueOf(0.3D), Double.valueOf(0.02D), Double.valueOf(0.06D), Double.valueOf(0.14D)});
      List C1 = Arrays.asList(new Double[]{Double.valueOf(0.06D), Double.valueOf(0.2D), Double.valueOf(0.11D), Double.valueOf(0.33D), Double.valueOf(0.15D), Double.valueOf(0.15D)});
      List C2 = Arrays.asList(new Double[]{Double.valueOf(0.08D), Double.valueOf(0.27D), Double.valueOf(0.1D), Double.valueOf(0.3D), Double.valueOf(0.1D), Double.valueOf(0.15D)});
      List C3 = Arrays.asList(new Double[]{Double.valueOf(0.06D), Double.valueOf(0.23D), Double.valueOf(0.05D), Double.valueOf(0.28D), Double.valueOf(0.28D), Double.valueOf(0.1D)});
      List S1 = Arrays.asList(new Double[]{Double.valueOf(0.22D), Double.valueOf(0.18D), Double.valueOf(0.21D), Double.valueOf(0.08D), Double.valueOf(0.08D), Double.valueOf(0.23D)});
      HashMap catMap = new HashMap(8);
      catMap.put("M1", M1);
      catMap.put("M2", M2);
      catMap.put("M3", M3);
      catMap.put("M4", M4);
      catMap.put("C1", C1);
      catMap.put("C2", C2);
      catMap.put("C3", C3);
      catMap.put("S1", S1);
      classCatMap = catMap;
      HashMap ratioEquip = new HashMap(4);
      ratioEquip.put("he", Double.valueOf(0.25D));
      ratioEquip.put("ar", Double.valueOf(0.25D));
      ratioEquip.put("ba", Double.valueOf(0.2D));
      ratioEquip.put("Weapon", Double.valueOf(0.33D));
      ratioByEquipment = ratioEquip;
   }
}
