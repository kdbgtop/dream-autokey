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
public class CaseConfig extends OkaeriConfig {

    public enum TextType {
        TITLE, SUBTITLE, CHAT, ACTIONBAR
    }

    @CustomKey("count")
    private int count;

    @CustomKey("command")
    private String command;

    @CustomKey("text")
    private String text;

    @CustomKey("text-executed")
    private String textExecuted;

    @CustomKey("text-type")
    private TextType textType;
}