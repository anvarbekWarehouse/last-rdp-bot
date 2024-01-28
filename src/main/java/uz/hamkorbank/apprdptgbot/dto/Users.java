package uz.hamkorbank.apprdptgbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    private int id;

    private String chatId;

    private String username;

    private boolean status;

    private String fullName;
}
