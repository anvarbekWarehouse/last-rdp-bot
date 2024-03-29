package uz.hamkorbank.apprdptgbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultTelegramm {
    private boolean ok;

    private Message result;
}
