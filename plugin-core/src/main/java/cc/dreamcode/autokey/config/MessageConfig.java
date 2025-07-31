package cc.dreamcode.autokey.config;

import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Headers;
import org.bukkit.Bukkit;

@Configuration(child = "message.yml")
@Headers({
        @Header("## Dream-Autokey (Message-Config) ##"),
        @Header("Dostepne type: (DO_NOT_SEND, CHAT, ACTION_BAR, SUBTITLE, TITLE, TITLE_SUBTITLE)")
})
public class MessageConfig extends OkaeriConfig {

    @CustomKey("command-usage")
    public BukkitNotice usage = BukkitNotice.chat("&7Przyklady uzycia komendy: &c{label}");
    @CustomKey("command-usage-help")
    public BukkitNotice usagePath = BukkitNotice.chat("&f{usage} &8- &7{description}");

    @CustomKey("command-usage-not-found")
    public BukkitNotice usageNotFound = BukkitNotice.chat("&cNie znaleziono pasujacych do kryteriow komendy.");
    @CustomKey("command-path-not-found")
    public BukkitNotice pathNotFound = BukkitNotice.chat("&cTa komenda jest pusta lub nie posiadasz dostepu do niej.");
    @CustomKey("command-no-permission")
    public BukkitNotice noPermission = BukkitNotice.chat("&cNie posiadasz uprawnien.");
    @CustomKey("command-not-player")
    public BukkitNotice notPlayer = BukkitNotice.chat("&cTa komende mozna tylko wykonac z poziomu gracza.");
    @CustomKey("command-not-console")
    public BukkitNotice notConsole = BukkitNotice.chat("&cTa komende mozna tylko wykonac z poziomu konsoli.");
    @CustomKey("command-invalid-format")
    public BukkitNotice invalidFormat = BukkitNotice.chat("&cPodano nieprawidlowy format argumentu komendy. ({input})");

    @CustomKey("player-not-found")
    public BukkitNotice playerNotFound = BukkitNotice.chat("&cPodanego gracza nie znaleziono.");
    @CustomKey("world-not-found")
    public BukkitNotice worldNotFound = BukkitNotice.chat("&cPodanego swiata nie znaleziono.");
    @CustomKey("cannot-do-at-my-self")
    public BukkitNotice cannotDoAtMySelf = BukkitNotice.chat("&cNie mozesz tego zrobic na sobie.");
    @CustomKey("number-is-not-valid")
    public BukkitNotice numberIsNotValid = BukkitNotice.chat("&cPodana liczba nie jest cyfra.");

    @CustomKey("config-reloaded")
    public BukkitNotice reloaded = BukkitNotice.chat("&aPrzeladowano! &7({time})");
    @CustomKey("config-reload-error")
    public BukkitNotice reloadError = BukkitNotice.chat("&cZnaleziono problem w konfiguracji: &6{error}");

    @Comment
    @Comment("Wiadomość po rozdaniu kluczy.")
    @CustomKey("autokey-arozdaj")
    public BukkitNotice arozdaj = BukkitNotice.chat("&7Rozdano &6{amount} &7kluczy &6{type}");

    @Comment
    @Comment("Wiadomość po rozdaniu kluczy osobom z permisją z configu.")
    @CustomKey("autokey-rozdaj")
    public BukkitNotice rozdaj = BukkitNotice.chat("&7Rozdano &6klucze &7rangom!");

    @Comment
    @CustomKey("invalid-case-type")
    public BukkitNotice invalidCaseType = BukkitNotice.chat("&cNie znaleziono skrzynki o nazwie &4{type}&c. Dostępne typy to: &7{available_types}&c.");

    @Comment
    @CustomKey("perms-distributed")
    public BukkitNotice permsDistributed = BukkitNotice.chat("&7Wykonano komendy dla wszystkich graczy &6z permisjami &7z &6configu!");
}
