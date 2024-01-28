package uz.hamkorbank.apprdptgbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageOwn {
    @JsonProperty(value = "chat_id")
    private String chatId;

    private String text;

    @JsonProperty(value = "reply_markup")
    private ReplyKeyboardMarkup replyKeyboardMarkup;

}
