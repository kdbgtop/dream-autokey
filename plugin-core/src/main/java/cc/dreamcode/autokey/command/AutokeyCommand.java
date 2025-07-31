package cc.dreamcode.autokey.command;

import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.DreamSender;
import cc.dreamcode.command.annotation.*;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.autokey.config.MessageConfig;
import cc.dreamcode.autokey.config.PluginConfig;
import cc.dreamcode.autokey.config.CaseConfig;
import cc.dreamcode.autokey.config.PermConfig;
import cc.dreamcode.utilities.TimeUtil;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.command.CommandSender;
import cc.dreamcode.notice.NoticeType;
import java.util.concurrent.atomic.AtomicInteger;

@Command(name = "autokey")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AutokeyCommand implements CommandBase {

    private final PluginConfig pluginConfig;
    private final MessageConfig messageConfig;
    private final Plugin plugin;

    @Async
    @Permission("dream-autokey.reload")
    @Executor(path = "reload", description = "Przeładowuje konfiguracje.")
    public BukkitNotice reload(CommandSender sender) {
        final long time = System.currentTimeMillis();

        try {
            this.messageConfig.load();
            this.pluginConfig.load();

            this.messageConfig.reloaded
                    .with("time", TimeUtil.format(System.currentTimeMillis() - time))
                    .send(sender);
            return null;
        }
        catch (NullPointerException | OkaeriException e) {
            e.printStackTrace();
            this.messageConfig.reloadError
                    .with("error", e.getMessage())
                    .send(sender);
            return null;
        }
    }

    @Permission("dream-autokey.arozdaj")
    @Executor(path = "arozdaj", description = "Rozdaje podane klucze z configu.")
    @Sender(DreamSender.Type.CLIENT)
    public BukkitNotice arozdaj(
            Player sender,
            @Arg("amount") int amount,
            @Arg("type") String type
    ) {
        CaseConfig caseConfig = pluginConfig.cases.get(type);

        if (caseConfig == null) {
            String availableTypes = String.join(", ", pluginConfig.cases.keySet());
            return messageConfig.invalidCaseType
                    .with("type", type)
                    .with("available_types", availableTypes);
        }

        int initialCountdown = caseConfig.getCount();
        final AtomicInteger currentCountdown = new AtomicInteger(initialCountdown + 1);

        final BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            int remainingSeconds = currentCountdown.decrementAndGet();

            if (remainingSeconds > 0) {
                sendCountdownNotice(sender, caseConfig, remainingSeconds);
            } else {
                sendExecutedNotice(sender, caseConfig);

                String commandToExecute = caseConfig.getCommand()
                        .replace("%player%", sender.getName())
                        .replace("{AMOUNT}", String.valueOf(amount))
                        .replace("%amount%", String.valueOf(amount));

                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute)
                );
                task[0].cancel();
            }
        }, 0L, 20L);
        return null;
    }

    @Async
    @Permission("dream-autokey.rozdaj")
    @Executor(path = "rozdaj", description = "Wykonuje komendy z configu 'perms' dla graczy posiadających odpowiednie permisje.")
    @Sender(DreamSender.Type.CLIENT)
    public BukkitNotice rozdaj() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            pluginConfig.perms.forEach(permConfigEntry -> {
                if (player.hasPermission(permConfigEntry.getPerm())) {
                    permConfigEntry.getCommands().forEach(command -> {
                        String parsedCommand = command.replace("%player%", player.getName());

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
                        });
                    });
                }
            });
        });
        return messageConfig.permsDistributed;
    }

    private void sendCountdownNotice(Player player, CaseConfig config, int seconds) {
        String text = config.getText().replace("%seconds%", String.valueOf(seconds));
        sendNoticeByType(player, config.getTextType(), text);
    }

    private void sendExecutedNotice(Player player, CaseConfig config) {
        String text = config.getTextExecuted();
        sendNoticeByType(player, config.getTextType(), text);
    }

    private void sendNoticeByType(Player player, CaseConfig.TextType type, String text) {
        NoticeType dreamCodeNoticeType;
        switch (type) {
            case CHAT:
                dreamCodeNoticeType = NoticeType.CHAT;
                break;
            case ACTIONBAR:
                dreamCodeNoticeType = NoticeType.ACTION_BAR;
                break;
            case TITLE:
                dreamCodeNoticeType = NoticeType.TITLE;
                break;
            case SUBTITLE:
                dreamCodeNoticeType = NoticeType.SUBTITLE;
                break;
            default:
                BukkitNotice.chat("Błąd konfiguracji typu powiadomienia! (" + type.name() + "): " + text + " - Użyto CHAT.").send(player);
                return;
        }

        switch (dreamCodeNoticeType) {
            case CHAT:
                BukkitNotice.chat(text).send(player);
                break;
            case ACTION_BAR:
                BukkitNotice.actionBar(text).send(player);
                break;
            case TITLE:
                BukkitNotice.title(text).send(player);
                break;
            case SUBTITLE:
                BukkitNotice.subtitle(text).send(player);
                break;
        }
    }
}