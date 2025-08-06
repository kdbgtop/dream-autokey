package cc.dreamcode.autokey.autokey;

import cc.dreamcode.autokey.Main;
import cc.dreamcode.autokey.bossbar.BossBarService;
import cc.dreamcode.autokey.config.AutokeyTimeConfig;
import cc.dreamcode.autokey.config.PluginConfig;
import cc.dreamcode.autokey.utils.TimeUtil;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AutokeyScheduler extends BukkitRunnable {

    private final Main main;
    private final PluginConfig pluginConfig;
    private final BossBarService bossBarService;

    private final Map<AutokeyTimeConfig, BukkitRunnable> activeTimers = new ConcurrentHashMap<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Map<String, Boolean> alreadyExecutedToday = new ConcurrentHashMap<>();

    @Override
    public void run() {
        LocalTime now = LocalTime.now();

        long secondsBefore = pluginConfig.getBossBarCountdownHours() * 3600L;
        if (secondsBefore <= 0) {
            secondsBefore = 300;
        }

        List<AutokeyTimeConfig> autokeyTimes = pluginConfig.getAutokeyTimes();
        for (AutokeyTimeConfig autokeyTime : autokeyTimes) {
            try {
                LocalTime autokeyTimeLocal = LocalTime.parse(autokeyTime.getTime(), timeFormatter);
                long secondsUntil = now.until(autokeyTimeLocal, ChronoUnit.SECONDS);

                if (secondsUntil <= 0 && secondsUntil > -2) {
                    if (alreadyExecutedToday.getOrDefault(autokeyTime.getTime(), false)) {
                        continue;
                    }
                    executeCommand(autokeyTime.getCommand());
                    Bukkit.getOnlinePlayers().forEach(bossBarService::removeBossBar);
                    alreadyExecutedToday.put(autokeyTime.getTime(), true);

                    BukkitRunnable timer = activeTimers.remove(autokeyTime);
                    if (timer != null) {
                        timer.cancel();
                    }
                } else if (secondsUntil > secondsBefore && alreadyExecutedToday.getOrDefault(autokeyTime.getTime(), false)) {
                    alreadyExecutedToday.put(autokeyTime.getTime(), false);
                } else if (secondsUntil <= secondsBefore && secondsUntil > 0 && !activeTimers.containsKey(autokeyTime)) {
                    BukkitRunnable timer = createCountdownTimer(autokeyTime, secondsUntil);
                    timer.runTaskTimer(main, 0L, 20L);
                    activeTimers.put(autokeyTime, timer);
                } else if (secondsUntil > secondsBefore && activeTimers.containsKey(autokeyTime)) {
                    BukkitRunnable timer = activeTimers.remove(autokeyTime);
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            } catch (Exception e) {
                main.getDreamLogger().warning("Błąd podczas parsowania godziny: " + autokeyTime.getTime() + ". Sprawdź format HH:mm. Błąd: " + e.getMessage());
            }
        }
    }

    private BukkitRunnable createCountdownTimer(AutokeyTimeConfig config, long initialDuration) {
        final AtomicInteger remainingTime = new AtomicInteger((int) initialDuration);

        return new BukkitRunnable() {
            @Override
            public void run() {
                int seconds = remainingTime.getAndDecrement();

                if (seconds <= 0) {
                    this.cancel();
                    return;
                }

                String formattedTime = TimeUtil.convertSecondsToDisplay(seconds);
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("{remaining_time}", formattedTime);
                placeholders.put("{time}", config.getTime());

                String bossBarText = config.getBossBarMessage();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    bossBarService.sendBossBarWithPlaceholders(
                            player,
                            bossBarText,
                            placeholders,
                            seconds,
                            (int) initialDuration
                    );
                }
            }
        };
    }

    private void executeCommand(String command) {
        Bukkit.getScheduler().runTask(main, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                String parsedCommand = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            });
            main.getDreamLogger().info("Wykonano komendę automatycznego rozdania: " + command);
        });
    }
}