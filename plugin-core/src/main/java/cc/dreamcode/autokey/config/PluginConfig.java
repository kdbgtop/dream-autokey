package cc.dreamcode.autokey.config;

import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import cc.dreamcode.platform.persistence.StorageConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;

import java.util.*;

import static javax.swing.UIManager.put;

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
    public StorageConfig storageConfig = new StorageConfig("dreamtemplate");

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
    public List<PermConfig> perms = Collections.singletonList(
            new PermConfig(
                    "rank.vip",
                    Arrays.asList("komenda1", "komenda 2", "komenda 3 itp.")
            )
    );

    @Comment
    @Comment("Automatyczne rozdawanie kluczy o okreslonych godzinach.")
    @Comment("Mozna dodac kilka godzin, np. '12:00', '18:30'.")
    @CustomKey("autokey")
    public List<AutokeyTimeConfig> autokeyTimes = Collections.singletonList(
            new AutokeyTimeConfig(
                    "16:00",
                    "case give %player% epicka 10",
                    "Klucze zostana rozdane za %remaining_time%!"
            )
    );

    @Comment
    @Comment("Konfiguracja powiadomien o liczbie graczy online.")
    @CustomKey("players-online")
    public PlayersOnlineConfig playersOnline = new PlayersOnlineConfig(
            90,
            100,
            "Brakuje %players% do rozdania kluczy!",
            "xdxd"
    );
}
