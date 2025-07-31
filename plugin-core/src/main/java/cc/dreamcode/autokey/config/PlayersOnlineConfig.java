package cc.dreamcode.autokey.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayersOnlineConfig extends OkaeriConfig {

    @CustomKey("min")
    private int minPlayers;

    @CustomKey("max")
    private int maxPlayers;

    @CustomKey("text")
    private String textMissingPlayers;

    @CustomKey("text-po-rozdaniu")
    private String textAfterDistribution;
}