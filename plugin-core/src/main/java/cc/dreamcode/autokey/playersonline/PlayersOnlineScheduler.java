package cc.dreamcode.autokey.playersonline;

import cc.dreamcode.autokey.Main;
import cc.dreamcode.autokey.bossbar.BossBarService;
import cc.dreamcode.autokey.config.PlayersOnlineConfig;
import cc.dreamcode.autokey.config.PluginConfig;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayersOnlineScheduler extends BukkitRunnable {

    private final Main main;
    private final PluginConfig pluginConfig;
    private final BossBarService bossBarService;
    private boolean giveawayExecuted = false;

    @Override
    public void run() {
        PlayersOnlineConfig playersOnlineConfig = pluginConfig.getPlayersOnline();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int minPlayers = playersOnlineConfig.getMinPlayers();
        int maxPlayers = playersOnlineConfig.getMaxPlayers();

        if (onlinePlayers >= minPlayers && onlinePlayers < maxPlayers) {
            int playersNeeded = maxPlayers - onlinePlayers;
            String bossBarMessage = playersOnlineConfig.getBossBarMessage();
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{players_needed}", String.valueOf(playersNeeded));

            double progress = 1.0 - ((double) playersNeeded / (maxPlayers - minPlayers));

            for (Player player : Bukkit.getOnlinePlayers()) {
                bossBarService.sendBossBarWithPlaceholders(
                        player,
                        bossBarMessage,
                        placeholders,
                        playersNeeded,
                        maxPlayers
                );
            }
            giveawayExecuted = false;
        } else if (onlinePlayers >= maxPlayers) {
            if (!giveawayExecuted) {
                executeCommand(playersOnlineConfig.getCommand());
                Bukkit.getOnlinePlayers().forEach(player -> {
                    bossBarService.removeBossBar(player);
                    bossBarService.sendTitle(player, playersOnlineConfig.getTitleExecuted());
                });
                giveawayExecuted = true;
            }
        } else {
            Bukkit.getOnlinePlayers().forEach(bossBarService::removeBossBar);
            giveawayExecuted = false;
        }
    }

    private void executeCommand(String command) {
        Bukkit.getScheduler().runTask(main, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                String parsedCommand = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            });
            main.getDreamLogger().info("Wykonano komendę rozdania kluczy (odliczanie online): " + command);
        });
    }
}