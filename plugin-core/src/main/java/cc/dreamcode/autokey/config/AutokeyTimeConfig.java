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
public class AutokeyTimeConfig extends OkaeriConfig {

    @CustomKey("time")
    private String time;

    @CustomKey("command")
    private String command;

    @CustomKey("bossbar")
    private String bossbar;
}