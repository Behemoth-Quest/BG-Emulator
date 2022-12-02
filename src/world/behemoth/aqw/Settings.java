package world.behemoth.aqw;

import it.gotoandplay.smartfoxserver.data.User;

public class Settings {
   public static final String ANIMATION = "bWAnim";
   public static final String CLOAK = "bCloak";
   public static final String DUEL = "bDuel";
   public static final String FBSHARE = "bFBShare";
   public static final String FRIEND = "bFriend";
   public static final String GOTO = "bGoto";
   public static final String GUILD = "bGuild";
   public static final String HELM = "bHelm";
   public static final String MUSIC = "bMusicOn";
   public static final String PARTY = "bParty";
   public static final String PET = "bPet";
   public static final String SOUND = "bSoundOn";
   public static final String TOOLTIPS = "bTT";
   public static final String WHISPER = "bWhisper";
   public static final String TRADE = "bTrade";
   public static final String DUEL_MESSAGE_OFF = "Ignoring duel invites.";
   public static final String DUEL_MESSAGE_ON = "Accepting duel invites.";
   public static final String FRIEND_MESSAGE_OFF = "Ignoring Friend requests.";
   public static final String FRIEND_MESSAGE_ON = "Accepting Friend requests.";
   public static final String GOTO_MESSAGE_OFF = "Blocking goto requests.";
   public static final String GOTO_MESSAGE_ON = "Accepting goto requests.";
   public static final String GUILD_MESSAGE_OFF = "Ignoring guild invites.";
   public static final String GUILD_MESSAGE_ON = "Accepting guild invites.";
   public static final String PARTY_MESSAGE_OFF = "Ignoring party invites.";
   public static final String PARTY_MESSAGE_ON = "Accepting party invites.";
   public static final String TOOLTIPS_MESSAGE_OFF = "Ability ToolTips will not show on mouseover during combat.";
   public static final String TOOLTIPS_MESSAGE_ON = "Ability ToolTips will always show on mouseover.";
   public static final String WHISPER_MESSAGE_OFF = "Ignoring PMs.";
   public static final String WHISPER_MESSAGE_ON = "Accepting PMs.";
   public static final String TRADE_MESSAGE_OFF = "Ignoring trade invites.";
   public static final String TRADE_MESSAGE_ON = "Accepting trade invites.";
   public static final String BR_DUEL_MESSAGE_OFF = "Ignorando convida duelo.";
   public static final String BR_DUEL_MESSAGE_ON = "Aceitando convida duelo";
   public static final String BR_FRIEND_MESSAGE_OFF = "Ignorando Pedidos de amizade.";
   public static final String BR_FRIEND_MESSAGE_ON = "Aceitando Pedidos de amizade.";
   public static final String BR_GOTO_MESSAGE_OFF = "Ignorando solicitacoes de Goto.";
   public static final String BR_GOTO_MESSAGE_ON = "Aceitando solicitacoes de Goto.";
   public static final String BR_GUILD_MESSAGE_OFF = "Ignorando alianca convida.";
   public static final String BR_GUILD_MESSAGE_ON = "Aceitando alianca convida.";
   public static final String BR_PARTY_MESSAGE_OFF = "Ignorando convites para festas.";
   public static final String BR_PARTY_MESSAGE_ON = "Aceitando convites para festas.";
   public static final String BR_TOOLTIPS_MESSAGE_OFF = "Dicas de ferramentas de habilidade nao serao exibidos em mouseover durante o combate.";
   public static final String BR_TOOLTIPS_MESSAGE_ON = "Dicas de ferramentas de habilidade ira mostrar sempre no mouseover.";
   public static final String BR_WHISPER_MESSAGE_OFF = "Ignorando PMs.";
   public static final String BR_WHISPER_MESSAGE_ON = "Aceitando PMs.";
   public static final String BR_TRADE_MESSAGE_OFF = "Ignorando trade invites.";
   public static final String BR_TRADE_MESSAGE_ON = "Aceitando trade invites.";

   private Settings() {
      super();
      throw new UnsupportedOperationException("not allowed to have an instance of this class");
   }

   public static boolean isAllowed(String pref, User user, User client) {
      return getPreferences(pref, client.properties.get("settings")) || user.isAdmin() || user.isModerator();
   }

   public static boolean getPreferences(String pref, Object setting) {
      return getPreferences(pref, Integer.parseInt(setting.toString()));
   }

   public static boolean getPreferences(String pref, int setting) {
      boolean value = false;
      if(pref.equals("bCloak")) {
         value = Achievement.get(setting, 0) == 0;
      } else if(pref.equals("bHelm")) {
         value = Achievement.get(setting, 1) == 0;
      } else if(pref.equals("bPet")) {
         value = Achievement.get(setting, 2) == 0;
      } else if(pref.equals("bWAnim")) {
         value = Achievement.get(setting, 3) == 0;
      } else if(pref.equals("bGoto")) {
         value = Achievement.get(setting, 4) == 0;
      } else if(pref.equals("bSoundOn")) {
         value = Achievement.get(setting, 5) == 0;
      } else if(pref.equals("bMusicOn")) {
         value = Achievement.get(setting, 6) == 0;
      } else if(pref.equals("bFriend")) {
         value = Achievement.get(setting, 7) == 0;
      } else if(pref.equals("bParty")) {
         value = Achievement.get(setting, 8) == 0;
      } else if(pref.equals("bGuild")) {
         value = Achievement.get(setting, 9) == 0;
      } else if(pref.equals("bWhisper")) {
         value = Achievement.get(setting, 10) == 0;
      } else if(pref.equals("bTT")) {
         value = Achievement.get(setting, 11) == 0;
      } else if(pref.equals("bFBShare")) {
         value = Achievement.get(setting, 12) == 0;
      } else if(pref.equals("bDuel")) {
         value = Achievement.get(setting, 13) == 0;
      } else if(pref.equals("bTrade")) {
         value = Achievement.get(setting, 15) == 0;
      } else if(pref.equals("bFBShard")) {
         value = false;
      }

      return value;
   }

   public static int setPreferences(String pref, Object test, boolean value) {
      return setPreferences(pref, Integer.parseInt(test.toString()), value);
   }

   public static int setPreferences(String pref, int intValue, boolean value) {
      int setting = value?0:1;
      int newInt = 0;
      if(pref.equals("bCloak")) {
         newInt = Achievement.update(intValue, 0, setting);
      } else if(pref.equals("bHelm")) {
         newInt = Achievement.update(intValue, 1, setting);
      } else if(pref.equals("bPet")) {
         newInt = Achievement.update(intValue, 2, setting);
      } else if(pref.equals("bWAnim")) {
         newInt = Achievement.update(intValue, 3, setting);
      } else if(pref.equals("bGoto")) {
         newInt = Achievement.update(intValue, 4, setting);
      } else if(pref.equals("bSoundOn")) {
         newInt = Achievement.update(intValue, 5, setting);
      } else if(pref.equals("bMusicOn")) {
         newInt = Achievement.update(intValue, 6, setting);
      } else if(pref.equals("bFriend")) {
         newInt = Achievement.update(intValue, 7, setting);
      } else if(pref.equals("bParty")) {
         newInt = Achievement.update(intValue, 8, setting);
      } else if(pref.equals("bGuild")) {
         newInt = Achievement.update(intValue, 9, setting);
      } else if(pref.equals("bWhisper")) {
         newInt = Achievement.update(intValue, 10, setting);
      } else if(pref.equals("bTT")) {
         newInt = Achievement.update(intValue, 11, setting);
      } else if(pref.equals("bFBShare")) {
         newInt = Achievement.update(intValue, 12, setting);
      } else if(pref.equals("bDuel")) {
         newInt = Achievement.update(intValue, 13, setting);
      } else if(pref.equals("bTrade")) {
         newInt = Achievement.update(intValue, 15, setting);
      } else if(pref.equals("bFBShard")) {
         newInt = 1;
      }

      return newInt;
   }
}
