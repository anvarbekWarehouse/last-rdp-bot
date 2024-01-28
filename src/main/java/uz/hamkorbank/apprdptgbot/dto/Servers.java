package uz.hamkorbank.apprdptgbot.dto;

import lombok.Data;

import java.util.Date;
@Data
public class Servers {
    private Integer id;

    private String name;

    private String chatId;

    private boolean status;

    private int tryCount;

    private String username;

    private Date last_date;
}
