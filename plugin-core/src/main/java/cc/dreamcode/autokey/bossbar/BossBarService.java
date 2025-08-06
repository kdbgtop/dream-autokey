package cc.dreamcode.autokey.bossbar;

import cc.dreamcode.autokey.config.PluginConfig;
import cc.dreamcode.utilities.bukkit.StringColorUtil;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BossBarService {

    private final PluginConfig pluginConfig;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public void sendBossBarWithPlaceholders(Player player, String text, Map<String, String> placeholders, int remainingSeconds, int totalDuration) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());

        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(
                    StringColorUtil.fixColor(text),
                    pluginConfig.getBossBarColor(),
                    pluginConfig.getBossBarStyle()
            );
            bossBar.addPlayer(player);
            playerBossBars.put(player.getUniqueId(), bossBar);
        }

        String processedText = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            processedText = processedText.replace(entry.getKey(), entry.getValue());
        }

        bossBar.setTitle(StringColorUtil.fixColor(processedText));

        double progress = (double) remainingSeconds / totalDuration;
        if (progress < 0.0) progress = 0.0;
        if (progress > 1.0) progress = 1.0;
        bossBar.setProgress(progress);
    }

    public void removeBossBar(Player player) {
        BossBar bossBar = playerBossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void sendTitle(Player player, String title) {
        player.sendTitle(StringColorUtil.fixColor(title), null, 10, 70, 20);
    }
}