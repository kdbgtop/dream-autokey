package cc.dreamcode.autokey.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayersOnlineConfig extends OkaeriConfig {

    private final int minPlayers;
    private final int maxPlayers;
    private final String bossBarMessage;
    private final String titleExecuted;
    private final String command;

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getBossBarMessage() {
        return bossBarMessage;
    }

    public String getTitleExecuted() {
        return titleExecuted;
    }

    public String getCommand() {
        return command;
    }
}