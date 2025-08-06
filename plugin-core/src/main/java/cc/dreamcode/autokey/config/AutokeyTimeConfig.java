package cc.dreamcode.autokey.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AutokeyTimeConfig extends OkaeriConfig {

    @Comment
    @Comment("Godzina, o której ma nastąpić rozdanie kluczy (format HH:mm).")
    private String time;

    @Comment
    @Comment("Komenda do wykonania przez konsole po rozdaniu.")
    private String command;

    @Comment("Wiadomość na bossbarze, wyświetlana przed rozdaniem. Użyj placeholderów: {remaining_time} i {time}.")
    private String bossBarMessage;

    public AutokeyTimeConfig(String time, String command, String bossBarMessage) {
        this.time = time;
        this.command = command;
        this.bossBarMessage = bossBarMessage;
    }
}