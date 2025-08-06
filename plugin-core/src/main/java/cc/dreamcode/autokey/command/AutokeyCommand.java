package cc.dreamcode.autokey.command;

import cc.dreamcode.autokey.bossbar.BossBarService;
import cc.dreamcode.autokey.config.CaseConfig;
import cc.dreamcode.autokey.config.MessageConfig;
import cc.dreamcode.autokey.config.PluginConfig;
import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.DreamSender;
import cc.dreamcode.command.annotation.*;
import cc.dreamcode.notice.NoticeType;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.autokey.utils.TimeUtil;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Command(name = "autokey")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AutokeyCommand implements CommandBase {

    private final PluginConfig pluginConfig;
    private final MessageConfig messageConfig;
    private final BossBarService bossBarService;
    private final Plugin plugin;

    @Async
    @Permission("dream-autokey.reload")
    @Executor(path = "reload", description = "Przeładowuje konfiguracje.")
    public BukkitNotice reload(CommandSender sender) {
        final long startTime = System.currentTimeMillis();

        try {
            this.messageConfig.load();
            this.pluginConfig.load();

            long reloadTime = System.currentTimeMillis() - startTime;
            String formattedReloadTime = TimeUtil.convertSecondsToDisplay(reloadTime / 1000);

            this.messageConfig.reloaded
                    .with("time", formattedReloadTime)
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

        final int initialCountdown = caseConfig.getCount();
        final AtomicInteger currentCountdown = new AtomicInteger(initialCountdown + 1);

        final BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            int remainingSeconds = currentCountdown.decrementAndGet();

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (remainingSeconds > 0) {
                    sendCountdownNotice(player, caseConfig, remainingSeconds);
                } else {
                    sendExecutedNotice(player, caseConfig);
                    String commandToExecute = caseConfig.getCommand()
                            .replace("%player%", player.getName())
                            .replace("{AMOUNT}", String.valueOf(amount))
                            .replace("%amount%", String.valueOf(amount));

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
                }
            });

            if (remainingSeconds <= 0) {
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> commandsToDispatch = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> {
                pluginConfig.perms.forEach(permConfigEntry -> {
                    if (player.hasPermission(permConfigEntry.getPerm())) {
                        permConfigEntry.getCommands().forEach(command -> {
                            String parsedCommand = command.replace("%player%", player.getName());
                            commandsToDispatch.add(parsedCommand);
                        });
                    }
                });
            });
            if (!commandsToDispatch.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    commandsToDispatch.forEach(command -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    });
                });
            }
        });

        return messageConfig.permsDistributed;
    }

    private void sendCountdownNotice(Player player, CaseConfig config, int seconds) {
        String formattedTime = TimeUtil.convertSecondsToDisplay(seconds);
        String text = config.getText().replace("%seconds%", formattedTime);
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