package cc.dreamcode.autokey.config;

import cc.dreamcode.notice.NoticeType;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import cc.dreamcode.platform.persistence.StorageConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import lombok.Getter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.*;

@Configuration(child = "config.yml")
@Header("## Dream-Autokey (Main-Config) ##")
public class PluginConfig extends OkaeriConfig {

    @Comment
    @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wylaczyc. :P")
    @CustomKey("debug")
    public boolean debug = true;

    @Comment
    @Comment("Ponizej znajduja sie dane do logowania bazy danych:")
    @CustomKey("storage-config")
    public StorageConfig storageConfig = new StorageConfig("dreamautokey");

    @Comment
    @Comment("Konfiguracja skrzynek do rozdawania kluczy.")
    @Comment("Dostepne typy tekstu: TITLE, SUBTITLE, CHAT, ACTIONBAR")
    public Map<String, CaseConfig> cases = new HashMap<String, CaseConfig>() {{
        put("epicka", new CaseConfig(
                10,
                "case give %player% epicka {AMOUNT}",
                "Rozdanie kluczy za %seconds%",
                "Rozdano klucze!",
                CaseConfig.TextType.TITLE
        ));
    }};

    @Comment
    @Comment("Konfiguracja komend dla danych permisji. Jesli gracz posiada dana permisje, wykona sie lista komend.")
    public List<PermConfig> perms = new ArrayList<>(Arrays.asList(
            new PermConfig(
                    "rank.vip",
                    Arrays.asList("komenda1", "komenda 2", "komenda 3 itp.")
            )
    ));

    @Comment
    @Comment("Automatyczne rozdawanie kluczy o określonych godzinach.")
    @Comment("Można dodać kilka godzin, np. '12:00', '18:30'.")
    @CustomKey("autokey")
    public List<AutokeyTimeConfig> autokeyTimes = new ArrayList<>(Arrays.asList(
            new AutokeyTimeConfig(
                    "16:00",
                    "case give %player% epicka 10",
                    "Klucze zostaną rozdane za &e{remaining_time} sekund!"
            )
    ));

    @Comment
    @Comment("Ile godzin przed rozdaniem kluczy ma sie pojawic bossbar")
    @Comment("Mozesz ustawic 0 aby bossbar pokazal sie 5 minut przed rozdaniem :D")
    public int bossBarCountdownHours = 1;

    @Comment
    @Comment("Kolor bossbara po angielsku")
    public BarColor bossBarColor = BarColor.RED;

    @Comment
    @Comment("Styl bossbara")
    public BarStyle bossBarStyle = BarStyle.SOLID;

    @Comment
    @Comment("Tutaj mozesz ustawic ilosc graczy do rozdania kluczy")
    @CustomKey("players-online")
    public PlayersOnlineConfig playersOnline = new PlayersOnlineConfig(
            90,
            100,
            "Brakuje &e{players_needed}&7 graczy do rozdania kluczy!",
            "xdxd",
            "case give %player% epicka 1"
    );

    public int getBossBarCountdownHours() {
        return bossBarCountdownHours;
    }

    public List<AutokeyTimeConfig> getAutokeyTimes() {
        return autokeyTimes;
    }

    public BarColor getBossBarColor() {
        return bossBarColor;
    }

    public BarStyle getBossBarStyle() {
        return bossBarStyle;
    }

    public PlayersOnlineConfig getPlayersOnline() {
        return playersOnline;
    }
}