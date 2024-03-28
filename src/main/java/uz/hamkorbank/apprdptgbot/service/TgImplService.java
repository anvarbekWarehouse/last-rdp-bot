package uz.hamkorbank.apprdptgbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.hamkorbank.apprdptgbot.dto.*;
import uz.hamkorbank.apprdptgbot.inter.RestConstants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TgImplService {
    
    private final GsonService gsonService;
    private final RestTemplate restTemplate;

    private static final String USERS_FILE = "users.json";
    private static final String SERVERS_FILE = "servers.json";

    public void whenTake(Update update) {
        List<Servers> serversList = null;
        serversList = gsonService.fileReaderServer(SERVERS_FILE);
        String bookingServer = "";
        String message = null;
        String chatId = update.getCallbackQuery().getFrom().getId().toString();
        String username = update.getCallbackQuery().getFrom().getUserName();
        for (Servers servers : serversList) {
            if (servers.getId().toString().equals(update.getCallbackQuery().getData())) {
                if (servers.isStatus()) {
                    bookingServer = servers.getName();
                    message = "BAND QILINDI ";
                    servers.setStatus(false);
                    servers.setChatId(chatId);
                    servers.setUsername("https://t.me/" + username);
                    servers.setLast_date(Date.from(Instant.now()));
                } else {
                    if (servers.getChatId().equals(chatId)) {
                        bookingServer = servers.getName();
                        message = "YAKUNLANDI ";
                        servers.setStatus(true);
                        servers.setChatId(null);
                        servers.setTryCount(0);
                        servers.setUsername(null);
                        servers.setLast_date(null);
                    } else {
                        bookingServer = "";
                        message = "SIZ BAND QILMAGANSIZ ";
                    }
                }
            }
            gsonService.fileWriterServer(SERVERS_FILE, serversList);
        }

        serversList = gsonService.fileReaderServer(SERVERS_FILE);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        int i = 1;
        if (!serversList.isEmpty()) {
            for (Servers server : serversList) {
                var button = new InlineKeyboardButton();
                if (server.isStatus()) {
                    button.setText("✅✅✅" + server.getName());
                    button.setCallbackData(server.getId().toString());
                } else {
                    button.setText("‼‼‼⚠⚠⚠" + server.getName());
                    button.setCallbackData(server.getId().toString());
                    button.setUrl(server.getUsername());
                }

                rowInLine.add(button);
                if (i % 2 == 0) {
                    rowsInLine.add(rowInLine);
                    rowInLine = new ArrayList<>();
                }
                i++;
            }
        }
        markupInLine.setKeyboard(rowsInLine);
        SendMessageInlineKeyboard sendMessage = new SendMessageInlineKeyboard(update.getCallbackQuery().getFrom().getId().toString(),
                message + bookingServer, markupInLine);
        ResultTelegramm resultTelegramm = restTemplate.postForObject(
                RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                sendMessage, ResultTelegramm.class);
    }

    public void whenStart(Update update) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("RESTART🔄");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("BOOKING🎯");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("FINISHED🏳");
        KeyboardRow row4 = new KeyboardRow();
        row3.add("REGISTER📝");
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboardMarkup.setKeyboard(keyboard);

        SendMessageOwn sendMessage = new SendMessageOwn(update.getMessage().getChatId().toString(),
                "Xush kelibsiz "+update.getMessage().getChat().getFirstName(), keyboardMarkup);

        ResultTelegramm resultTelegramm = restTemplate.postForObject(
                RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                sendMessage, ResultTelegramm.class);
    }

    public void whenStatus(Update update) {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getChat().getFirstName();
        List<Users> usersList = gsonService.fileReaderUser(USERS_FILE);
        boolean isUser = true;
        for (Users users : usersList) {
            if (users.getChatId().equals(chatId.toString()) && !users.isStatus()){
                isUser = false;
                SendMessage sendMessage = new SendMessage(chatId.toString(), "Siz tasdiqdan o'tmagansiz!!");
                ResultTelegramm resultTelegramm = restTemplate.postForObject(
                        RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                        sendMessage, ResultTelegramm.class);
            }else if (users.getChatId().equals(chatId.toString()) && users.isStatus()){
                isUser = false;
                List<Servers> serversList = gsonService.fileReaderServer(SERVERS_FILE);
                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                int i = 1;
                if (!serversList.isEmpty()) {
                    for (Servers server : serversList) {
                        var button = new InlineKeyboardButton();
                        if (server.isStatus()) {
                            button.setText("✅✅✅" + server.getName());
                            button.setCallbackData(server.getId().toString());
                        } else {
                            button.setText("⚠⚠⚠ " + server.getName() + " \n"+ server.getUsername());
                            button.setCallbackData(server.getId().toString());
                            button.setUrl(server.getUsername());
                        }

                        rowInLine.add(button);
                        if (i % 2 == 0) {
                            rowsInLine.add(rowInLine);
                            rowInLine = new ArrayList<>();
                        }
                        i++;
                    }
                }
                markupInLine.setKeyboard(rowsInLine);
                SendMessageInlineKeyboard sendMessage = new SendMessageInlineKeyboard(update.getMessage().getChatId().toString(),
                        "BAND BO'LMAGANNI TANLANG!!!", markupInLine);

                ResultTelegramm resultTelegramm = restTemplate.postForObject(
                        RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                        sendMessage, ResultTelegramm.class);
            }
        }
        if (isUser){
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                    "REGISTER📝 DAN O'TING");

            ResultTelegramm resultTelegramm = restTemplate.postForObject(
                    RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                    sendMessage, ResultTelegramm.class);
        }

    }

    public void whenFinished(Update update) {
        Long chatIdR = update.getMessage().getChatId();
        List<Users> usersList = gsonService.fileReaderUser(USERS_FILE);
        boolean isUser = true;
        for (Users users : usersList) {
            if (users.getChatId().equals(chatIdR.toString()) && !users.isStatus()){
                isUser =false;
                SendMessage sendMessage = new SendMessage(chatIdR.toString(), "Siz tasdiqdan otmagansiz!!");
                ResultTelegramm resultTelegramm = restTemplate.postForObject(
                        RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                        sendMessage, ResultTelegramm.class);
            }else if (users.getChatId().equals(chatIdR.toString()) && users.isStatus()){
                isUser =false;
                List<Servers> serversList = gsonService.fileReaderServer(SERVERS_FILE);
                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                int i = 1;
                if (!serversList.isEmpty()) {
                    for (Servers server : serversList) {
                        var button = new InlineKeyboardButton();
                        if (server.isStatus()) {
                            button.setText("✅✅✅" + server.getName());
                            button.setCallbackData(server.getId().toString());
                        } else {
                            button.setText("⚠" + server.getName());
                            button.setCallbackData(server.getId().toString());
                        }

                        rowInLine.add(button);
                        if (i % 2 == 0) {
                            rowsInLine.add(rowInLine);
                            rowInLine = new ArrayList<>();
                        }
                        i++;
                    }
                }
                markupInLine.setKeyboard(rowsInLine);
                SendMessageInlineKeyboard sendMessage = new SendMessageInlineKeyboard(update.getMessage().getChatId().toString(),
                        "O'ZINGGIZ BAND QILGANNI TANLANG!!!", markupInLine);

                ResultTelegramm resultTelegramm = restTemplate.postForObject(
                        RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                        sendMessage, ResultTelegramm.class);
            }
        }
        if (isUser){
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                    "REGISTER📝 DAN O'TING");

            ResultTelegramm resultTelegramm = restTemplate.postForObject(
                    RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                    sendMessage, ResultTelegramm.class);
        }

    }

    public void whenRegister(Update update) {
        Long chatId = update.getMessage().getChat().getId();
        String firstName = update.getMessage().getChat().getFirstName();
        String userName = update.getMessage().getChat().getUserName();
        List<Users> usersList = gsonService.fileReaderUser(USERS_FILE);
        boolean isUser = true;
        for (Users users : usersList) {
            if (users.getChatId().equals(chatId.toString()) && !users.isStatus()){
                isUser = false;
                SendMessage sendMessage = new SendMessage(chatId.toString(),
                        "Sizda tasdiqlanmagan profil bor Anvarbek Turdaliyevga murojat qiling!!");
                ResultTelegramm resultTelegramm = restTemplate.postForObject(
                        RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                        sendMessage, ResultTelegramm.class);
            }else if (users.getChatId().equals(chatId.toString()) && users.isStatus()){
                isUser = false;
                SendMessage sendMessage = new SendMessage(chatId.toString(),
                        "Sizda account mavjud RDP band qilish uchun BOOKING buttonni bosing!!");
                ResultTelegramm resultTelegramm = restTemplate.postForObject(
                        RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                        sendMessage, ResultTelegramm.class);
            }
        }
        if(isUser){

            //
            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var button = new InlineKeyboardButton();
            button.setText("DONE✅");
            button.setCallbackData(chatId.toString());
            rowInLine.add(button);
            rowsInLine.add(rowInLine);
            markupInLine.setKeyboard(rowsInLine);
            Users user = new Users(usersList.size()+1, chatId.toString(), userName, false, firstName);
            usersList.add(user);
            gsonService.fileWriterUser(USERS_FILE, usersList);
            String id = "5565614634";

            String text = update.getMessage().getChat().getFirstName() + " yangi acoount "+
                    "\n  https://t.me/" + update.getMessage().getChat().getUserName();
            SendMessageInlineKeyboard sendMessage = new SendMessageInlineKeyboard(id,
                    text, markupInLine);

            ResultTelegramm resultTelegramm = restTemplate.postForObject(
                    RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                    sendMessage, ResultTelegramm.class);
        }


    }

    public void whenDefaultMessage(Update update) {
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                "Mavjud bo'lmagan buyruq berildi.");

        ResultTelegramm resultTelegramm = restTemplate.postForObject(
                RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                sendMessage, ResultTelegramm.class);
    }

    public void done(Update update) {
        String userChatId = update.getCallbackQuery().getData();
        List<Users> usersList = gsonService.fileReaderUser(USERS_FILE);
        List<Users> newUsers = new ArrayList<>();
        for (Users users : usersList) {
            if (userChatId.equals(users.getChatId())){
                users.setStatus(true);
                System.out.println("true");
            }
            newUsers.add(users);
        }
        gsonService.fileWriterUser(USERS_FILE, newUsers);

        //Message to User
        SendMessage sendMessage = new SendMessage(userChatId,
                "Ro'yhatdan o'tdinggiz foydalanishinggiz mumkun!");

        ResultTelegramm resultTelegramm = restTemplate.postForObject(
                RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                sendMessage, ResultTelegramm.class);

        //Message to Admin
        String id = "5565614634";
        SendMessage sendMessageAdmin = new SendMessage(id,
                "SUCCESS!");

        ResultTelegramm resultTelegrammAdmin = restTemplate.postForObject(
                RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                sendMessageAdmin, ResultTelegramm.class);
    }
}
