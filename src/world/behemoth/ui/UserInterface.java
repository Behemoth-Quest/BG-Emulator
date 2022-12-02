/*   1:    */ package world.behemoth.ui;
/*   2:    */ 
/*   3:    */

/*   4:    */
/*   5:    */ import world.behemoth.world.World;
/*   6:    */ import it.gotoandplay.smartfoxserver.SmartFoxServer;
/*   7:    */ import it.gotoandplay.smartfoxserver.config.ConfigData;
/*   8:    */ import it.gotoandplay.smartfoxserver.data.Zone;
/*   9:    */ import it.gotoandplay.smartfoxserver.extensions.ExtensionHelper;
/*  10:    */ import it.gotoandplay.smartfoxserver.extensions.ExtensionManager;
/*  11:    */
        /*  12:    */ import java.awt.Dimension;
/*  13:    */ import java.awt.Font;
/*  14:    */ import java.awt.Image;
/*  15:    */ import java.awt.event.ActionEvent;
/*  16:    */ import java.awt.event.ActionListener;
/*  17:    */ import java.awt.event.ItemEvent;
/*  18:    */ import java.awt.event.ItemListener;
/*  19:    */ import java.io.IOException;
/*  20:    */
        /*  21:    */
        /*  22:    */ import java.util.ResourceBundle;
/*  23:    */ import java.util.concurrent.TimeUnit;
/*  24:    */ import javax.imageio.ImageIO;
/*  25:    */ import javax.swing.GroupLayout;
/*  26:    */
        /*  27:    */
        /*  28:    */
        /*  29:    */ import javax.swing.JButton;
/*  30:    */ import javax.swing.JCheckBox;
/*  31:    */ import javax.swing.JFrame;
/*  32:    */ import javax.swing.JLabel;
/*  33:    */ import javax.swing.JPanel;
/*  34:    */ import javax.swing.JProgressBar;
/*  35:    */ import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
/*  36:    */
        /*  37:    */ import javax.swing.Timer;
/*  38:    */ import org.apache.commons.io.FileUtils;

/*  39:    */
/*  40:    */ public class UserInterface
/*  41:    */   extends JFrame
/*  42:    */ {
/*  43:    */   private static final long serialVersionUID = 1L;
/*  44:    */   private World world;
/*  45:    */   private Timer refreshTimer;
/*  46:    */   private JLabel activeThreads;
/*  47:    */   private JLabel aurasCount;
/*  48:    */   private JButton btnAbout;
/*  49:    */   private JButton btnClear;
/*  50:    */   private JButton btnRefresh;
/*  51:    */   private JButton btnReload;
/*  52:    */   private JButton btnRestart;
/*  53:    */   private JButton btnShutdown;
/*  54:    */   private JCheckBox chkAuto;
/*  55:    */   private JLabel dataIn;
/*  56:    */   private JLabel dataOut;
/*  57:    */   private JLabel dataTotal;
/*  58:    */   private JLabel dbConnections;
/*  59:    */   private JLabel effectsCount;
/*  60:    */   private JLabel enhancementsCount;
/*  61:    */   private JLabel factionsCount;
/*  62:    */   private JLabel hairsCount;
/*  63:    */   private JLabel hairshopsCount;
/*  64:    */   private JLabel highestUserCount;
/*  65:    */   private JLabel itemCount;
/*  66:    */   private JLabel jLabel1;
/*  67:    */   private JLabel jLabel10;
/*  68:    */   private JLabel jLabel11;
/*  69:    */   private JLabel jLabel12;
/*  70:    */   private JLabel jLabel13;
/*  71:    */   private JLabel jLabel14;
/*  72:    */   private JLabel jLabel15;
/*  73:    */   private JLabel jLabel16;
/*  74:    */   private JLabel jLabel17;
/*  75:    */   private JLabel jLabel18;
/*  76:    */   private JLabel jLabel19;
/*  77:    */   private JLabel jLabel2;
/*  78:    */   private JLabel jLabel20;
/*  79:    */   private JLabel jLabel21;
/*  80:    */   private JLabel jLabel22;
/*  81:    */   private JLabel jLabel23;
/*  82:    */   private JLabel jLabel24;
/*  83:    */   private JLabel jLabel25;
/*  84:    */   private JLabel jLabel26;
/*  85:    */   private JLabel jLabel27;
/*  86:    */   private JLabel jLabel28;
/*  87:    */   private JLabel jLabel29;
/*  88:    */   private JLabel jLabel3;
/*  89:    */   private JLabel jLabel32;
/*  90:    */   private JLabel jLabel33;
/*  91:    */   private JLabel jLabel4;
/*  92:    */   private JLabel jLabel44;
/*  93:    */   private JLabel jLabel5;
/*  94:    */   private JLabel jLabel6;
/*  95:    */   private JLabel jLabel7;
/*  96:    */   private JLabel jLabel8;
/*  97:    */   private JLabel jLabel9;
/*  98:    */   private JPanel jPanel1;
/*  99:    */   private JPanel jPanel2;
/* 100:    */   private JPanel jPanel3;
/* 101:    */   private JPanel jPanel4;
/* 102:    */   private JPanel jPanel6;
/* 103:    */   private JPanel jPanel7;
/* 104:    */   private JTabbedPane mainTabPane;
/* 105:    */   private JLabel mapsCount;
/* 106:    */   private JLabel memoryFree;
/* 107:    */   private JLabel memoryPercent;
/* 108:    */   private JProgressBar memoryProgress;
/* 109:    */   private JLabel memoryTotal;
/* 110:    */   private JLabel memoryUsed;
/* 111:    */   private JLabel monstersCount;
/* 112:    */   private JLabel numOfRestarts;
/* 113:    */   private JPanel panelStatus;
/* 114:    */   private JLabel partyCount;
/* 115:    */   private JLabel questsCount;
/* 116:    */   private JLabel rooms;
/* 117:    */   private JLabel serverRates;
/* 118:    */   private JLabel shopsCount;
/* 119:    */   private JLabel skillsCount;
/* 120:    */   private JLabel socketsConnected;
/* 121:    */   private JLabel upTime;
/* 122:    */   private JLabel users;
/* 123:    */   
/* 124:    */   public UserInterface(World world)
/* 125:    */     throws IOException
/* 126:    */   {
/* 127: 37 */     initComponents();
/* 128: 38 */     setLocationRelativeTo(null);
/* 129: 39 */     Image i = ImageIO.read(getClass().getResource("/pie/ui/pie.ico"));
/* 130: 40 */     setIconImage(i);
/* 131:    */     
/* 132: 42 */     this.world = world;
/* 133: 43 */     this.refreshTimer = new Timer(1000, new ActionListener()
/* 134:    */     {
/* 135:    */       public void actionPerformed(ActionEvent evt)
/* 136:    */       {
/* 137: 46 */         UserInterface.this.refreshTimerActionPerformed(evt);
/* 138:    */       }
/* 139: 48 */     });
/* 140: 49 */     this.refreshTimer.setRepeats(true);
/* 141:    */     
/* 142: 51 */     refresh();
/* 143: 52 */     this.chkAuto.setSelected(true);
/* 144:    */   }
/* 145:    */   
/* 146:    */   private void refreshTimerActionPerformed(ActionEvent evt)
/* 147:    */   {
/* 148: 56 */     refresh();
/* 149:    */   }
/* 150:    */   
/* 151:    */   private String getUptime()
/* 152:    */   {
/* 153: 60 */     StringBuilder result = new StringBuilder();
/* 154:    */     
/* 155: 62 */     long now = System.currentTimeMillis();
/* 156: 63 */     long start = SmartFoxServer.getInstance().getServerStartTime();
/* 157:    */     
/* 158: 65 */     long elapsed = now - start;
/* 159: 66 */     int days = (int)Math.floor(elapsed / 86400000L);
/* 160:    */     
/* 161: 68 */     long temp = 86400000L * days;
/* 162:    */     
/* 163: 70 */     elapsed -= temp;
/* 164: 71 */     int hours = (int)Math.floor(elapsed / 3600000L);
/* 165:    */     
/* 166: 73 */     temp = 3600000 * hours;
/* 167: 74 */     elapsed -= temp;
/* 168: 75 */     int minutes = (int)Math.floor(elapsed / 60000L);
/* 169:    */     
/* 170: 77 */     String s_days = String.valueOf(days);
/* 171: 78 */     for (int i = 0; i < 4 - s_days.length(); i++) {
/* 172: 79 */       result.append("0");
/* 173:    */     }
/* 174: 80 */     result.append(s_days);
/* 175: 81 */     result.append(":");
/* 176: 82 */     if (hours < 10) {
/* 177: 83 */       result.append("0");
/* 178:    */     }
/* 179: 84 */     result.append(hours);
/* 180: 85 */     result.append(":");
/* 181: 86 */     if (minutes < 10) {
/* 182: 87 */       result.append("0");
/* 183:    */     }
/* 184: 88 */     result.append(minutes);
/* 185:    */     
/* 186: 90 */     return result.toString();
/* 187:    */   }
/* 188:    */   
/* 189:    */   private void refresh()
/* 190:    */   {
/* 191: 94 */     if ((this.world == null) || (this.world.db == null)) {
/* 192: 94 */       return;
/* 193:    */     }
/* 194: 95 */     this.upTime.setText(getUptime());
/* 195: 96 */     this.users.setText(String.valueOf(SmartFoxServer.getInstance().getGlobalUserCount()));
/* 196: 97 */     this.rooms.setText(String.valueOf(SmartFoxServer.getInstance().getRoomNumber()));
/* 197: 98 */     this.highestUserCount.setText(String.valueOf(ConfigData.maxSimultanousConnections));
/* 198: 99 */     this.socketsConnected.setText(String.valueOf(SmartFoxServer.getInstance().getChannels().size()));
/* 199:100 */     this.activeThreads.setText(String.valueOf(Thread.activeCount()));
/* 200:101 */     this.numOfRestarts.setText(String.valueOf(ConfigData.restartCount));
/* 201:    */     
/* 202:103 */     this.dbConnections.setText(String.valueOf(this.world.db.getActiveConnections()));
/* 203:    */     
/* 204:105 */     this.dataOut.setText(FileUtils.byteCountToDisplaySize(ConfigData.dataOUT));
/* 205:106 */     this.dataIn.setText(FileUtils.byteCountToDisplaySize(ConfigData.dataIN));
/* 206:107 */     this.dataTotal.setText(FileUtils.byteCountToDisplaySize(ConfigData.dataIN + ConfigData.dataOUT));
/* 207:    */     
/* 208:109 */     Runtime rt = Runtime.getRuntime();
/* 209:    */     
/* 210:111 */     this.memoryUsed.setText(FileUtils.byteCountToDisplaySize(rt.totalMemory() - rt.freeMemory()));
/* 211:112 */     this.memoryTotal.setText(FileUtils.byteCountToDisplaySize(rt.totalMemory()));
/* 212:113 */     this.memoryFree.setText(FileUtils.byteCountToDisplaySize(rt.freeMemory()));
/* 213:    */     
/* 214:115 */     this.memoryProgress.setMaximum(Long.valueOf(rt.totalMemory()).intValue());
/* 215:116 */     this.memoryProgress.setValue(Long.valueOf(rt.totalMemory() - rt.freeMemory()).intValue());
/* 216:    */     
/* 217:118 */     int percentage = (int)((rt.totalMemory() - rt.freeMemory()) * 100.0D / rt.totalMemory() + 0.5D);
/* 218:119 */     this.memoryPercent.setText(percentage + "%");
/* 219:    */     
/* 220:121 */     this.itemCount.setText(String.valueOf(this.world.items.size()));
/* 221:122 */     this.skillsCount.setText(String.valueOf(this.world.skills.size()));
/* 222:123 */     this.mapsCount.setText(String.valueOf(this.world.areas.size()));
/* 223:124 */     this.shopsCount.setText(String.valueOf(this.world.shops.size()));
/* 224:125 */     this.aurasCount.setText(String.valueOf(this.world.auras.size()));
/* 225:126 */     this.monstersCount.setText(String.valueOf(this.world.monsters.size()));
/* 226:127 */     this.questsCount.setText(String.valueOf(this.world.quests.size()));
/* 227:128 */     this.factionsCount.setText(String.valueOf(this.world.factions.size()));
/* 228:129 */     this.enhancementsCount.setText(String.valueOf(this.world.enhancements.size()));
/* 229:130 */     this.hairshopsCount.setText(String.valueOf(this.world.hairshops.size()));
/* 230:131 */     this.hairsCount.setText(String.valueOf(this.world.hairs.size()));
/* 231:132 */     this.effectsCount.setText(String.valueOf(this.world.effects.size()));
/* 232:133 */     this.partyCount.setText(String.valueOf(this.world.parties.size()));
/* 233:    */     
/* 234:135 */     this.serverRates.setText(String.format("Server Rates: %dx EXP, %dx Gold, %dx Rep, %dx CP", new Object[] { Integer.valueOf(this.world.EXP_RATE), Integer.valueOf(this.world.GOLD_RATE), Integer.valueOf(this.world.REP_RATE), Integer.valueOf(this.world.CP_RATE) }));
/* 235:    */   }
/* 236:    */   
/* 237:    */   private void initComponents()
/* 238:    */   {
/* 239:147 */     this.mainTabPane = new JTabbedPane();
/* 240:148 */     this.panelStatus = new JPanel();
/* 241:149 */     this.btnRefresh = new JButton();
/* 242:150 */     this.chkAuto = new JCheckBox();
/* 243:151 */     this.jPanel1 = new JPanel();
/* 244:152 */     this.jLabel1 = new JLabel();
/* 245:153 */     this.jLabel2 = new JLabel();
/* 246:154 */     this.jLabel3 = new JLabel();
/* 247:155 */     this.jLabel4 = new JLabel();
/* 248:156 */     this.jLabel5 = new JLabel();
/* 249:157 */     this.jLabel6 = new JLabel();
/* 250:158 */     this.jLabel7 = new JLabel();
/* 251:159 */     this.numOfRestarts = new JLabel();
/* 252:160 */     this.activeThreads = new JLabel();
/* 253:161 */     this.socketsConnected = new JLabel();
/* 254:162 */     this.highestUserCount = new JLabel();
/* 255:163 */     this.users = new JLabel();
/* 256:164 */     this.rooms = new JLabel();
/* 257:165 */     this.upTime = new JLabel();
/* 258:166 */     this.jLabel17 = new JLabel();
/* 259:167 */     this.jLabel27 = new JLabel();
/* 260:168 */     this.partyCount = new JLabel();
/* 261:169 */     this.jPanel2 = new JPanel();
/* 262:170 */     this.jLabel18 = new JLabel();
/* 263:171 */     this.dbConnections = new JLabel();
/* 264:172 */     this.jLabel32 = new JLabel();
/* 265:173 */     this.jPanel3 = new JPanel();
/* 266:174 */     this.jLabel19 = new JLabel();
/* 267:175 */     this.dataIn = new JLabel();
/* 268:176 */     this.jLabel33 = new JLabel();
/* 269:177 */     this.jLabel20 = new JLabel();
/* 270:178 */     this.dataOut = new JLabel();
/* 271:179 */     this.jLabel21 = new JLabel();
/* 272:180 */     this.dataTotal = new JLabel();
/* 273:181 */     this.jPanel4 = new JPanel();
/* 274:182 */     this.jLabel8 = new JLabel();
/* 275:183 */     this.memoryProgress = new JProgressBar();
/* 276:184 */     this.memoryPercent = new JLabel();
/* 277:185 */     this.jLabel10 = new JLabel();
/* 278:186 */     this.jLabel11 = new JLabel();
/* 279:187 */     this.memoryTotal = new JLabel();
/* 280:188 */     this.memoryUsed = new JLabel();
/* 281:189 */     this.jLabel12 = new JLabel();
/* 282:190 */     this.memoryFree = new JLabel();
/* 283:191 */     this.jPanel6 = new JPanel();
/* 284:192 */     this.jLabel9 = new JLabel();
/* 285:193 */     this.jLabel13 = new JLabel();
/* 286:194 */     this.jLabel14 = new JLabel();
/* 287:195 */     this.jLabel15 = new JLabel();
/* 288:196 */     this.jLabel16 = new JLabel();
/* 289:197 */     this.jLabel22 = new JLabel();
/* 290:198 */     this.jLabel23 = new JLabel();
/* 291:199 */     this.itemCount = new JLabel();
/* 292:200 */     this.effectsCount = new JLabel();
/* 293:201 */     this.skillsCount = new JLabel();
/* 294:202 */     this.mapsCount = new JLabel();
/* 295:203 */     this.hairsCount = new JLabel();
/* 296:204 */     this.shopsCount = new JLabel();
/* 297:205 */     this.jPanel7 = new JPanel();
/* 298:206 */     this.jLabel24 = new JLabel();
/* 299:207 */     this.jLabel25 = new JLabel();
/* 300:208 */     this.jLabel26 = new JLabel();
/* 301:209 */     this.jLabel28 = new JLabel();
/* 302:210 */     this.jLabel29 = new JLabel();
/* 303:211 */     this.questsCount = new JLabel();
/* 304:212 */     this.enhancementsCount = new JLabel();
/* 305:213 */     this.monstersCount = new JLabel();
/* 306:214 */     this.hairshopsCount = new JLabel();
/* 307:215 */     this.aurasCount = new JLabel();
/* 308:216 */     this.jLabel44 = new JLabel();
/* 309:217 */     this.factionsCount = new JLabel();
/* 310:218 */     this.btnRestart = new JButton();
/* 311:219 */     this.btnClear = new JButton();
/* 312:220 */     this.btnShutdown = new JButton();
/* 313:221 */     this.btnAbout = new JButton();
/* 314:222 */     this.serverRates = new JLabel();
/* 315:223 */     this.btnReload = new JButton();
/* 316:    */     
/* 317:225 */     setDefaultCloseOperation(0);
/* 318:226 */     ResourceBundle bundle = ResourceBundle.getBundle("pie/ui/Bundle");
/* 319:227 */     setTitle(bundle.getString("UserInterface.title"));
/* 320:228 */     setMaximumSize(getPreferredSize());
/* 321:229 */     setMinimumSize(getPreferredSize());
/* 322:230 */     setResizable(false);
/* 323:    */     
/* 324:232 */     this.btnRefresh.setText(bundle.getString("UserInterface.btnRefresh.text"));
/* 325:233 */     this.btnRefresh.addActionListener(new ActionListener()
/* 326:    */     {
/* 327:    */       public void actionPerformed(ActionEvent evt)
/* 328:    */       {
/* 329:235 */         UserInterface.this.btnRefreshActionPerformed(evt);
/* 330:    */       }
/* 331:238 */     });
/* 332:239 */     this.chkAuto.setText(bundle.getString("UserInterface.chkAuto.text"));
/* 333:240 */     this.chkAuto.addItemListener(new ItemListener()
/* 334:    */     {
/* 335:    */       public void itemStateChanged(ItemEvent evt)
/* 336:    */       {
/* 337:242 */         UserInterface.this.chkAutoItemStateChanged(evt);
/* 338:    */       }
/* 339:245 */     });
/* 340:246 */     this.jPanel1.setMaximumSize(new Dimension(250, 200));
/* 341:247 */     this.jPanel1.setMinimumSize(new Dimension(250, 200));
/* 342:248 */     this.jPanel1.setPreferredSize(new Dimension(250, 200));
/* 343:    */     
/* 344:250 */     this.jLabel1.setText(bundle.getString("UserInterface.jLabel1.text"));
/* 345:    */     
/* 346:252 */     this.jLabel2.setText(bundle.getString("UserInterface.jLabel2.text"));
/* 347:    */     
/* 348:254 */     this.jLabel3.setText(bundle.getString("UserInterface.jLabel3.text"));
/* 349:    */     
/* 350:256 */     this.jLabel4.setText(bundle.getString("UserInterface.jLabel4.text"));
/* 351:    */     
/* 352:258 */     this.jLabel5.setText(bundle.getString("UserInterface.jLabel5.text"));
/* 353:    */     
/* 354:260 */     this.jLabel6.setText(bundle.getString("UserInterface.jLabel6.text"));
/* 355:    */     
/* 356:262 */     this.jLabel7.setText(bundle.getString("UserInterface.jLabel7.text"));
/* 357:    */     
/* 358:264 */     this.numOfRestarts.setText(bundle.getString("UserInterface.numOfRestarts.text"));
/* 359:    */     
/* 360:266 */     this.activeThreads.setText(bundle.getString("UserInterface.activeThreads.text"));
/* 361:    */     
/* 362:268 */     this.socketsConnected.setText(bundle.getString("UserInterface.socketsConnected.text"));
/* 363:    */     
/* 364:270 */     this.highestUserCount.setText(bundle.getString("UserInterface.highestUserCount.text"));
/* 365:    */     
/* 366:272 */     this.users.setText(bundle.getString("UserInterface.users.text"));
/* 367:    */     
/* 368:274 */     this.rooms.setText(bundle.getString("UserInterface.rooms.text"));
/* 369:    */     
/* 370:276 */     this.upTime.setText(bundle.getString("UserInterface.upTime.text"));
/* 371:    */     
/* 372:278 */     this.jLabel17.setFont(new Font("Tahoma", 1, 11));
/* 373:279 */     this.jLabel17.setText(bundle.getString("UserInterface.jLabel17.text"));
/* 374:    */     
/* 375:281 */     this.jLabel27.setFont(new Font("Tahoma", 1, 11));
/* 376:282 */     this.jLabel27.setText(bundle.getString("UserInterface.jLabel27.text"));
/* 377:    */     
/* 378:284 */     this.partyCount.setText(bundle.getString("UserInterface.partyCount.text"));
/* 379:    */     
/* 380:286 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/* 381:287 */     this.jPanel1.setLayout(jPanel1Layout);
/* 382:288 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel7, GroupLayout.Alignment.TRAILING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel5).addComponent(this.jLabel6).addComponent(this.jLabel3).addComponent(this.jLabel4).addComponent(this.jLabel1).addComponent(this.jLabel2)).addGap(1, 1, 1))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 126, 32767).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.numOfRestarts, GroupLayout.Alignment.TRAILING).addComponent(this.rooms, GroupLayout.Alignment.TRAILING).addComponent(this.upTime, GroupLayout.Alignment.TRAILING).addComponent(this.highestUserCount, GroupLayout.Alignment.TRAILING).addComponent(this.users, GroupLayout.Alignment.TRAILING).addComponent(this.socketsConnected, GroupLayout.Alignment.TRAILING).addComponent(this.activeThreads, GroupLayout.Alignment.TRAILING))).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jLabel17).addGap(0, 0, 32767)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jLabel27).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.partyCount))).addContainerGap()));
/* 383:    */     
/* 384:    */ 
/* 385:    */ 
/* 386:    */ 
/* 387:    */ 
/* 388:    */ 
/* 389:    */ 
/* 390:    */ 
/* 391:    */ 
/* 392:    */ 
/* 393:    */ 
/* 394:    */ 
/* 395:    */ 
/* 396:    */ 
/* 397:    */ 
/* 398:    */ 
/* 399:    */ 
/* 400:    */ 
/* 401:    */ 
/* 402:    */ 
/* 403:    */ 
/* 404:    */ 
/* 405:    */ 
/* 406:    */ 
/* 407:    */ 
/* 408:    */ 
/* 409:    */ 
/* 410:    */ 
/* 411:    */ 
/* 412:    */ 
/* 413:    */ 
/* 414:    */ 
/* 415:    */ 
/* 416:    */ 
/* 417:323 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel17).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.upTime)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.rooms)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.users)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.highestUserCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.socketsConnected)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel6).addComponent(this.activeThreads)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel7).addComponent(this.numOfRestarts)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel27).addComponent(this.partyCount)).addContainerGap(15, 32767)));
/* 418:    */     
/* 419:    */ 
/* 420:    */ 
/* 421:    */ 
/* 422:    */ 
/* 423:    */ 
/* 424:    */ 
/* 425:    */ 
/* 426:    */ 
/* 427:    */ 
/* 428:    */ 
/* 429:    */ 
/* 430:    */ 
/* 431:    */ 
/* 432:    */ 
/* 433:    */ 
/* 434:    */ 
/* 435:    */ 
/* 436:    */ 
/* 437:    */ 
/* 438:    */ 
/* 439:    */ 
/* 440:    */ 
/* 441:    */ 
/* 442:    */ 
/* 443:    */ 
/* 444:    */ 
/* 445:    */ 
/* 446:    */ 
/* 447:    */ 
/* 448:    */ 
/* 449:    */ 
/* 450:    */ 
/* 451:    */ 
/* 452:    */ 
/* 453:    */ 
/* 454:    */ 
/* 455:    */ 
/* 456:    */ 
/* 457:363 */     this.jPanel2.setMaximumSize(new Dimension(250, 200));
/* 458:364 */     this.jPanel2.setMinimumSize(new Dimension(250, 200));
/* 459:365 */     this.jPanel2.setPreferredSize(new Dimension(250, 100));
/* 460:    */     
/* 461:367 */     this.jLabel18.setText(bundle.getString("UserInterface.jLabel18.text"));
/* 462:    */     
/* 463:369 */     this.dbConnections.setText(bundle.getString("UserInterface.dbConnections.text"));
/* 464:    */     
/* 465:371 */     this.jLabel32.setFont(new Font("Tahoma", 1, 11));
/* 466:372 */     this.jLabel32.setText(bundle.getString("UserInterface.jLabel32.text"));
/* 467:    */     
/* 468:374 */     GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
/* 469:375 */     this.jPanel2.setLayout(jPanel2Layout);
/* 470:376 */     jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.jLabel18).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 128, 32767).addComponent(this.dbConnections)).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.jLabel32).addGap(0, 0, 32767))).addContainerGap()));
/* 471:    */     
/* 472:    */ 
/* 473:    */ 
/* 474:    */ 
/* 475:    */ 
/* 476:    */ 
/* 477:    */ 
/* 478:    */ 
/* 479:    */ 
/* 480:    */ 
/* 481:    */ 
/* 482:    */ 
/* 483:    */ 
/* 484:390 */     jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addGap(6, 6, 6).addComponent(this.jLabel32).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.dbConnections).addComponent(this.jLabel18)).addContainerGap(-1, 32767)));
/* 485:    */     
/* 486:    */ 
/* 487:    */ 
/* 488:    */ 
/* 489:    */ 
/* 490:    */ 
/* 491:    */ 
/* 492:    */ 
/* 493:    */ 
/* 494:    */ 
/* 495:    */ 
/* 496:402 */     this.jPanel3.setMaximumSize(new Dimension(250, 200));
/* 497:403 */     this.jPanel3.setMinimumSize(new Dimension(250, 200));
/* 498:404 */     this.jPanel3.setPreferredSize(new Dimension(250, 100));
/* 499:    */     
/* 500:406 */     this.jLabel19.setText(bundle.getString("UserInterface.jLabel19.text"));
/* 501:    */     
/* 502:408 */     this.dataIn.setText(bundle.getString("UserInterface.dataIn.text"));
/* 503:    */     
/* 504:410 */     this.jLabel33.setFont(new Font("Tahoma", 1, 11));
/* 505:411 */     this.jLabel33.setText(bundle.getString("UserInterface.jLabel33.text"));
/* 506:    */     
/* 507:413 */     this.jLabel20.setText(bundle.getString("UserInterface.jLabel20.text"));
/* 508:    */     
/* 509:415 */     this.dataOut.setText(bundle.getString("UserInterface.dataOut.text"));
/* 510:    */     
/* 511:417 */     this.jLabel21.setText(bundle.getString("UserInterface.jLabel21.text"));
/* 512:    */     
/* 513:419 */     this.dataTotal.setText(bundle.getString("UserInterface.dataTotal.text"));
/* 514:    */     
/* 515:421 */     GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
/* 516:422 */     this.jPanel3.setLayout(jPanel3Layout);
/* 517:423 */     jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel19).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.dataIn)).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel33).addGap(0, 0, 32767)).addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup().addComponent(this.jLabel20).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 176, 32767).addComponent(this.dataOut)).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel21).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.dataTotal))).addContainerGap()));
/* 518:    */     
/* 519:    */ 
/* 520:    */ 
/* 521:    */ 
/* 522:    */ 
/* 523:    */ 
/* 524:    */ 
/* 525:    */ 
/* 526:    */ 
/* 527:    */ 
/* 528:    */ 
/* 529:    */ 
/* 530:    */ 
/* 531:    */ 
/* 532:    */ 
/* 533:    */ 
/* 534:    */ 
/* 535:    */ 
/* 536:    */ 
/* 537:    */ 
/* 538:    */ 
/* 539:445 */     jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel33).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel19).addComponent(this.dataIn)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel20).addComponent(this.dataOut)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel21).addComponent(this.dataTotal)).addContainerGap(-1, 32767)));
/* 540:    */     
/* 541:    */ 
/* 542:    */ 
/* 543:    */ 
/* 544:    */ 
/* 545:    */ 
/* 546:    */ 
/* 547:    */ 
/* 548:    */ 
/* 549:    */ 
/* 550:    */ 
/* 551:    */ 
/* 552:    */ 
/* 553:    */ 
/* 554:    */ 
/* 555:    */ 
/* 556:    */ 
/* 557:    */ 
/* 558:    */ 
/* 559:465 */     this.jLabel8.setFont(new Font("Tahoma", 1, 11));
/* 560:466 */     this.jLabel8.setText(bundle.getString("UserInterface.jLabel8.text"));
/* 561:    */     
/* 562:468 */     this.memoryPercent.setLabelFor(this.memoryProgress);
/* 563:469 */     this.memoryPercent.setText(bundle.getString("UserInterface.memoryPercent.text"));
/* 564:    */     
/* 565:471 */     this.jLabel10.setText(bundle.getString("UserInterface.jLabel10.text"));
/* 566:    */     
/* 567:473 */     this.jLabel11.setText(bundle.getString("UserInterface.jLabel11.text"));
/* 568:    */     
/* 569:475 */     this.memoryTotal.setText(bundle.getString("UserInterface.memoryTotal.text"));
/* 570:    */     
/* 571:477 */     this.memoryUsed.setText(bundle.getString("UserInterface.memoryUsed.text"));
/* 572:    */     
/* 573:479 */     this.jLabel12.setText(bundle.getString("UserInterface.jLabel12.text"));
/* 574:    */     
/* 575:481 */     this.memoryFree.setText(bundle.getString("UserInterface.memoryFree.text"));
/* 576:    */     
/* 577:483 */     GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
/* 578:484 */     this.jPanel4.setLayout(jPanel4Layout);
/* 579:485 */     jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addComponent(this.jLabel10).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.memoryTotal)).addGroup(jPanel4Layout.createSequentialGroup().addComponent(this.jLabel11).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.memoryUsed)).addGroup(jPanel4Layout.createSequentialGroup().addComponent(this.jLabel8).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.memoryProgress, -1, 147, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.memoryPercent)).addGroup(jPanel4Layout.createSequentialGroup().addComponent(this.jLabel12).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.memoryFree))).addContainerGap()));
/* 580:    */     
/* 581:    */ 
/* 582:    */ 
/* 583:    */ 
/* 584:    */ 
/* 585:    */ 
/* 586:    */ 
/* 587:    */ 
/* 588:    */ 
/* 589:    */ 
/* 590:    */ 
/* 591:    */ 
/* 592:    */ 
/* 593:    */ 
/* 594:    */ 
/* 595:    */ 
/* 596:    */ 
/* 597:    */ 
/* 598:    */ 
/* 599:    */ 
/* 600:    */ 
/* 601:    */ 
/* 602:    */ 
/* 603:    */ 
/* 604:510 */     jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel8).addComponent(this.memoryProgress, -2, -1, -2).addComponent(this.memoryPercent)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel10).addComponent(this.memoryTotal)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel11).addComponent(this.memoryUsed)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel12).addComponent(this.memoryFree)).addContainerGap(15, 32767)));
/* 605:    */     
/* 606:    */ 
/* 607:    */ 
/* 608:    */ 
/* 609:    */ 
/* 610:    */ 
/* 611:    */ 
/* 612:    */ 
/* 613:    */ 
/* 614:    */ 
/* 615:    */ 
/* 616:    */ 
/* 617:    */ 
/* 618:    */ 
/* 619:    */ 
/* 620:    */ 
/* 621:    */ 
/* 622:    */ 
/* 623:    */ 
/* 624:    */ 
/* 625:    */ 
/* 626:    */ 
/* 627:533 */     this.jLabel9.setFont(new Font("Tahoma", 1, 11));
/* 628:534 */     this.jLabel9.setText(bundle.getString("UserInterface.jLabel9.text"));
/* 629:    */     
/* 630:536 */     this.jLabel13.setText(bundle.getString("UserInterface.jLabel13.text"));
/* 631:    */     
/* 632:538 */     this.jLabel14.setText(bundle.getString("UserInterface.jLabel14.text"));
/* 633:    */     
/* 634:540 */     this.jLabel15.setText(bundle.getString("UserInterface.jLabel15.text"));
/* 635:    */     
/* 636:542 */     this.jLabel16.setText(bundle.getString("UserInterface.jLabel16.text"));
/* 637:    */     
/* 638:544 */     this.jLabel22.setText(bundle.getString("UserInterface.jLabel22.text"));
/* 639:    */     
/* 640:546 */     this.jLabel23.setText(bundle.getString("UserInterface.jLabel23.text"));
/* 641:    */     
/* 642:548 */     this.itemCount.setText(bundle.getString("UserInterface.itemCount.text"));
/* 643:    */     
/* 644:550 */     this.effectsCount.setText(bundle.getString("UserInterface.effectsCount.text"));
/* 645:    */     
/* 646:552 */     this.skillsCount.setText(bundle.getString("UserInterface.skillsCount.text"));
/* 647:    */     
/* 648:554 */     this.mapsCount.setText(bundle.getString("UserInterface.mapsCount.text"));
/* 649:    */     
/* 650:556 */     this.hairsCount.setText(bundle.getString("UserInterface.hairsCount.text"));
/* 651:    */     
/* 652:558 */     this.shopsCount.setText(bundle.getString("UserInterface.shopsCount.text"));
/* 653:    */     
/* 654:560 */     GroupLayout jPanel6Layout = new GroupLayout(this.jPanel6);
/* 655:561 */     this.jPanel6.setLayout(jPanel6Layout);
/* 656:562 */     jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel14).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.effectsCount)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel9).addGap(0, 0, 32767)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel13).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.itemCount)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel15).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.skillsCount)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel16).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.mapsCount)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel22).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.hairsCount)).addGroup(jPanel6Layout.createSequentialGroup().addComponent(this.jLabel23).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.shopsCount))).addContainerGap()));
/* 657:    */     
/* 658:    */ 
/* 659:    */ 
/* 660:    */ 
/* 661:    */ 
/* 662:    */ 
/* 663:    */ 
/* 664:    */ 
/* 665:    */ 
/* 666:    */ 
/* 667:    */ 
/* 668:    */ 
/* 669:    */ 
/* 670:    */ 
/* 671:    */ 
/* 672:    */ 
/* 673:    */ 
/* 674:    */ 
/* 675:    */ 
/* 676:    */ 
/* 677:    */ 
/* 678:    */ 
/* 679:    */ 
/* 680:    */ 
/* 681:    */ 
/* 682:    */ 
/* 683:    */ 
/* 684:    */ 
/* 685:    */ 
/* 686:    */ 
/* 687:    */ 
/* 688:    */ 
/* 689:    */ 
/* 690:596 */     jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel9).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel13).addComponent(this.itemCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel14).addComponent(this.effectsCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel15).addComponent(this.skillsCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel16).addComponent(this.mapsCount)).addGap(7, 7, 7).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel22).addComponent(this.hairsCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel23).addComponent(this.shopsCount)).addContainerGap(-1, 32767)));
/* 691:    */     
/* 692:    */ 
/* 693:    */ 
/* 694:    */ 
/* 695:    */ 
/* 696:    */ 
/* 697:    */ 
/* 698:    */ 
/* 699:    */ 
/* 700:    */ 
/* 701:    */ 
/* 702:    */ 
/* 703:    */ 
/* 704:    */ 
/* 705:    */ 
/* 706:    */ 
/* 707:    */ 
/* 708:    */ 
/* 709:    */ 
/* 710:    */ 
/* 711:    */ 
/* 712:    */ 
/* 713:    */ 
/* 714:    */ 
/* 715:    */ 
/* 716:    */ 
/* 717:    */ 
/* 718:    */ 
/* 719:    */ 
/* 720:    */ 
/* 721:    */ 
/* 722:628 */     this.jLabel24.setText(bundle.getString("UserInterface.jLabel24.text"));
/* 723:    */     
/* 724:630 */     this.jLabel25.setText(bundle.getString("UserInterface.jLabel25.text"));
/* 725:    */     
/* 726:632 */     this.jLabel26.setText(bundle.getString("UserInterface.jLabel26.text"));
/* 727:    */     
/* 728:634 */     this.jLabel28.setText(bundle.getString("UserInterface.jLabel28.text"));
/* 729:    */     
/* 730:636 */     this.jLabel29.setText(bundle.getString("UserInterface.jLabel29.text"));
/* 731:    */     
/* 732:638 */     this.questsCount.setText(bundle.getString("UserInterface.questsCount.text"));
/* 733:    */     
/* 734:640 */     this.enhancementsCount.setText(bundle.getString("UserInterface.enhancementsCount.text"));
/* 735:    */     
/* 736:642 */     this.monstersCount.setText(bundle.getString("UserInterface.monstersCount.text"));
/* 737:    */     
/* 738:644 */     this.hairshopsCount.setText(bundle.getString("UserInterface.hairshopsCount.text"));
/* 739:    */     
/* 740:646 */     this.aurasCount.setText(bundle.getString("UserInterface.aurasCount.text"));
/* 741:    */     
/* 742:648 */     this.jLabel44.setText(bundle.getString("UserInterface.jLabel44.text"));
/* 743:    */     
/* 744:650 */     this.factionsCount.setText(bundle.getString("UserInterface.factionsCount.text"));
/* 745:    */     
/* 746:652 */     GroupLayout jPanel7Layout = new GroupLayout(this.jPanel7);
/* 747:653 */     this.jPanel7.setLayout(jPanel7Layout);
/* 748:654 */     jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addContainerGap().addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel24).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.questsCount)).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel25).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.enhancementsCount)).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel26).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.monstersCount)).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel28).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.hairshopsCount)).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel29).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.aurasCount)).addGroup(jPanel7Layout.createSequentialGroup().addComponent(this.jLabel44).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.factionsCount))).addContainerGap()));
/* 749:    */     
/* 750:    */ 
/* 751:    */ 
/* 752:    */ 
/* 753:    */ 
/* 754:    */ 
/* 755:    */ 
/* 756:    */ 
/* 757:    */ 
/* 758:    */ 
/* 759:    */ 
/* 760:    */ 
/* 761:    */ 
/* 762:    */ 
/* 763:    */ 
/* 764:    */ 
/* 765:    */ 
/* 766:    */ 
/* 767:    */ 
/* 768:    */ 
/* 769:    */ 
/* 770:    */ 
/* 771:    */ 
/* 772:    */ 
/* 773:    */ 
/* 774:    */ 
/* 775:    */ 
/* 776:    */ 
/* 777:    */ 
/* 778:    */ 
/* 779:685 */     jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel7Layout.createSequentialGroup().addGap(31, 31, 31).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel25).addComponent(this.enhancementsCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel26).addComponent(this.monstersCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel28).addComponent(this.hairshopsCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel29).addComponent(this.aurasCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel44).addComponent(this.factionsCount)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel24).addComponent(this.questsCount)).addContainerGap(12, 32767)));
/* 780:    */     
/* 781:    */ 
/* 782:    */ 
/* 783:    */ 
/* 784:    */ 
/* 785:    */ 
/* 786:    */ 
/* 787:    */ 
/* 788:    */ 
/* 789:    */ 
/* 790:    */ 
/* 791:    */ 
/* 792:    */ 
/* 793:    */ 
/* 794:    */ 
/* 795:    */ 
/* 796:    */ 
/* 797:    */ 
/* 798:    */ 
/* 799:    */ 
/* 800:    */ 
/* 801:    */ 
/* 802:    */ 
/* 803:    */ 
/* 804:    */ 
/* 805:    */ 
/* 806:    */ 
/* 807:    */ 
/* 808:    */ 
/* 809:715 */     this.btnRestart.setText(bundle.getString("UserInterface.btnRestart.text"));
/* 810:716 */     this.btnRestart.addActionListener(new ActionListener()
/* 811:    */     {
/* 812:    */       public void actionPerformed(ActionEvent evt)
/* 813:    */       {
/* 814:718 */         UserInterface.this.btnRestartActionPerformed(evt);
/* 815:    */       }
/* 816:721 */     });
/* 817:722 */     this.btnClear.setText(bundle.getString("UserInterface.btnClear.text"));
/* 818:723 */     this.btnClear.addActionListener(new ActionListener()
/* 819:    */     {
/* 820:    */       public void actionPerformed(ActionEvent evt)
/* 821:    */       {
/* 822:725 */         UserInterface.this.btnClearActionPerformed(evt);
/* 823:    */       }
/* 824:728 */     });
/* 825:729 */     this.btnShutdown.setText(bundle.getString("UserInterface.btnShutdown.text"));
/* 826:730 */     this.btnShutdown.addActionListener(new ActionListener()
/* 827:    */     {
/* 828:    */       public void actionPerformed(ActionEvent evt)
/* 829:    */       {
/* 830:732 */         UserInterface.this.btnShutdownActionPerformed(evt);
/* 831:    */       }
/* 832:735 */     });
/* 833:736 */     this.btnAbout.setText(bundle.getString("UserInterface.btnAbout.text"));
/* 834:737 */     this.btnAbout.addActionListener(new ActionListener()
/* 835:    */     {
/* 836:    */       public void actionPerformed(ActionEvent evt)
/* 837:    */       {
/* 838:739 */         UserInterface.this.btnAboutActionPerformed(evt);
/* 839:    */       }
/* 840:742 */     });
/* 841:743 */     this.serverRates.setText(bundle.getString("UserInterface.serverRates.text"));
/* 842:    */     
/* 843:745 */     this.btnReload.setText(bundle.getString("UserInterface.btnReload.text"));
/* 844:746 */     this.btnReload.addActionListener(new ActionListener()
/* 845:    */     {
/* 846:    */       public void actionPerformed(ActionEvent evt)
/* 847:    */       {
/* 848:748 */         UserInterface.this.btnReloadActionPerformed(evt);
/* 849:    */       }
/* 850:751 */     });
/* 851:752 */     GroupLayout panelStatusLayout = new GroupLayout(this.panelStatus);
/* 852:753 */     this.panelStatus.setLayout(panelStatusLayout);
/* 853:754 */     panelStatusLayout.setHorizontalGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(panelStatusLayout.createSequentialGroup().addContainerGap().addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(panelStatusLayout.createSequentialGroup().addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel1, -2, -1, -2).addComponent(this.jPanel2, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(panelStatusLayout.createSequentialGroup().addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jPanel3, -2, -1, -2).addComponent(this.jPanel6, -1, -1, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel4, -1, -1, 32767).addComponent(this.jPanel7, -1, -1, 32767))).addGroup(panelStatusLayout.createSequentialGroup().addGap(0, 0, 32767).addComponent(this.chkAuto).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btnRefresh)))).addGroup(panelStatusLayout.createSequentialGroup().addComponent(this.btnShutdown).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.btnRestart, -2, 76, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.btnAbout, -2, 73, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.btnClear).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.btnReload).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.serverRates))).addContainerGap()));
/* 854:    */     
/* 855:    */ 
/* 856:    */ 
/* 857:    */ 
/* 858:    */ 
/* 859:    */ 
/* 860:    */ 
/* 861:    */ 
/* 862:    */ 
/* 863:    */ 
/* 864:    */ 
/* 865:    */ 
/* 866:    */ 
/* 867:    */ 
/* 868:    */ 
/* 869:    */ 
/* 870:    */ 
/* 871:    */ 
/* 872:    */ 
/* 873:    */ 
/* 874:    */ 
/* 875:    */ 
/* 876:    */ 
/* 877:    */ 
/* 878:    */ 
/* 879:    */ 
/* 880:    */ 
/* 881:    */ 
/* 882:    */ 
/* 883:    */ 
/* 884:    */ 
/* 885:    */ 
/* 886:    */ 
/* 887:    */ 
/* 888:    */ 
/* 889:    */ 
/* 890:    */ 
/* 891:792 */     panelStatusLayout.setVerticalGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(panelStatusLayout.createSequentialGroup().addContainerGap().addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(panelStatusLayout.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jPanel2, -2, 0, 32767)).addGroup(panelStatusLayout.createSequentialGroup().addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel3, -2, -1, -2).addComponent(this.jPanel4, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jPanel7, -2, -1, -2).addComponent(this.jPanel6, -2, -1, -2)))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnRefresh).addComponent(this.chkAuto)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addGroup(panelStatusLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.btnShutdown).addComponent(this.btnRestart).addComponent(this.btnAbout).addComponent(this.btnClear).addComponent(this.serverRates).addComponent(this.btnReload)).addContainerGap()));
/* 892:    */     
/* 893:    */ 
/* 894:    */ 
/* 895:    */ 
/* 896:    */ 
/* 897:    */ 
/* 898:    */ 
/* 899:    */ 
/* 900:    */ 
/* 901:    */ 
/* 902:    */ 
/* 903:    */ 
/* 904:    */ 
/* 905:    */ 
/* 906:    */ 
/* 907:    */ 
/* 908:    */ 
/* 909:    */ 
/* 910:    */ 
/* 911:    */ 
/* 912:    */ 
/* 913:    */ 
/* 914:    */ 
/* 915:    */ 
/* 916:    */ 
/* 917:    */ 
/* 918:    */ 
/* 919:    */ 
/* 920:    */ 
/* 921:    */ 
/* 922:    */ 
/* 923:824 */     this.mainTabPane.addTab(bundle.getString("UserInterface.panelStatus.TabConstraints.tabTitle"), this.panelStatus);
/* 924:    */     
/* 925:826 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 926:827 */     getContentPane().setLayout(layout);
/* 927:828 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.mainTabPane));
/* 928:    */     
/* 929:    */ 
/* 930:    */ 
/* 931:832 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.mainTabPane));
/* 932:    */     
/* 933:    */ 
/* 934:    */ 
/* 935:    */ 
/* 936:837 */     pack();
/* 937:    */   }
/* 938:    */   
/* 939:    */   private void btnShutdownActionPerformed(ActionEvent evt)
/* 940:    */   {
/* 941:841 */     int result = MessageBox.showConfirm("Continue shutdown operation? Players will rage.", "Confirmation", 0);
/* 942:843 */     if (result == 0)
/* 943:    */     {
/* 944:844 */       this.world.send(new String[] { "logoutWarning", "", "60" }, this.world.zone.getChannelList());
/* 945:845 */       this.world.shutdown();
/* 946:    */       try
/* 947:    */       {
/* 948:847 */         Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
/* 949:    */       }
/* 950:    */       catch (InterruptedException ex) {}
/* 951:850 */       System.exit(0);
/* 952:    */     }
/* 953:    */   }
/* 954:    */   
/* 955:    */   private void btnClearActionPerformed(ActionEvent evt)
/* 956:    */   {
/* 957:855 */     if (this.world.retrieveDatabaseObject("all")) {
/* 958:856 */       MessageBox.showMessage("Data Memory Cleared!", "Operation Successful");
/* 959:    */     }
/* 960:    */   }
/* 961:    */   
/* 962:    */   private void btnRestartActionPerformed(ActionEvent evt)
/* 963:    */   {
/* 964:860 */     int result = MessageBox.showConfirm("Continue restart operation? Players will rage.", "Confirmation", 0);
/* 965:862 */     if (result == 0)
/* 966:    */     {
/* 967:863 */       this.world.send(new String[] { "logoutWarning", "", "60" }, this.world.zone.getChannelList());
/* 968:864 */       this.world.shutdown();
/* 969:    */       try
/* 970:    */       {
/* 971:866 */         Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
/* 972:    */       }
/* 973:    */       catch (InterruptedException ex) {}
/* 974:869 */       ExtensionHelper.instance().rebootServer();
/* 975:    */     }
/* 976:    */   }
/* 977:    */   
/* 978:    */   private void chkAutoItemStateChanged(ItemEvent evt)
/* 979:    */   {
/* 980:874 */     if (evt.getStateChange() == 2) {
/* 981:875 */       this.refreshTimer.stop();
/* 982:876 */     } else if (evt.getStateChange() == 1) {
/* 983:877 */       this.refreshTimer.start();
/* 984:    */     }
/* 985:    */   }
/* 986:    */   
/* 987:    */   private void btnRefreshActionPerformed(ActionEvent evt)
/* 988:    */   {
/* 989:881 */     refresh();
/* 990:    */   }
/* 991:    */   
/* 992:    */   private void btnAboutActionPerformed(ActionEvent evt)
/* 993:    */   {
/* 994:885 */     MessageBox.showMessage("HP Trolling Sphere (MExt v3) by Mystical" + System.getProperty("line.separator") + "(c) 2013 InfinityArts", "About");
/* 995:    */   }
/* 996:    */   
/* 997:    */   private void btnReloadActionPerformed(ActionEvent evt)
/* 998:    */   {
/* 999:892 */     String zoneName = "zone_master";
/* :00:893 */     String extName = "zm";
/* :01:894 */     Zone zone = SmartFoxServer.getInstance().getZone(zoneName);
/* :02:895 */     if (zone != null)
/* :03:    */     {
/* :04:896 */       ExtensionManager em = zone.getExtManager();
/* :05:897 */       if (em != null) {
/* :06:898 */         em.reloadExtension(extName);
/* :07:    */       }
/* :08:    */     }
/* :09:    */   }
/* :10:    */ }



/* Location:           F:\HP\pie.jar

 * Qualified Name:     pie.ui.UserInterface

 * JD-Core Version:    0.7.0.1

 */