package uz.hamkorbank.apprdptgbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hamkorbank.apprdptgbot.dto.ResultTelegramm;
import uz.hamkorbank.apprdptgbot.dto.Servers;
import uz.hamkorbank.apprdptgbot.inter.RestConstants;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TgService {
    private final TgImplService tgImplService;
    private final GsonService gsonService;
    private final RestTemplate restTemplate;

    private static final String SERVERS_FILE = "servers.json";

    public void updateTelegram(Update update) throws ClassNotFoundException {
        if (update.hasCallbackQuery()){

            if(update.getCallbackQuery().getFrom().getId().toString().equals("5565614634")
                    && update.getCallbackQuery().getData().length()>6){
                tgImplService.done(update);
            }else {
                tgImplService.whenTake(update);
            }
        }
        if (update.hasMessage()){
            String text = update.getMessage().getText();
            System.out.println(text);
            switch (text){
                case "/start":
                    tgImplService.whenStart(update);
                    break;
                case "/status":
                    tgImplService.whenStatus(update);
                    break;
                case "RESTART🔄":
                    tgImplService.whenStatus(update);
                    break;
                case "BOOKING🎯":
                    tgImplService.whenStatus(update);
                    break;
                case "FINISHED🏳":
                    tgImplService.whenFinished(update);
                    break;
                case "REGISTER📝":
                    tgImplService.whenRegister(update);
                    break;
                case "OK":
                    tgImplService.whenRegister(update);
                    break;
                default:
                    tgImplService.whenDefaultMessage(update);
            }
        }
    }

    public void scheduledServer() {
        List<Servers> serversList = null;
        serversList = gsonService.fileReaderServer(SERVERS_FILE);
        for (Servers servers : serversList) {
            if (!servers.isStatus()){
                if(servers.getTryCount() == 6){



                    SendMessage sendMessage = new SendMessage(servers.getChatId(),
                            "Sizga tegishli " + servers.getName()
                                    + " yakunlandi Agar udalyonka ishltayotgan bo'lsanggiz qayta o'zlashtirib oling ");
                    ResultTelegramm resultTelegramm = restTemplate.postForObject(
                            RestConstants.TELEGRAM_BASE_URL + RestConstants.BOT_TOKEN + "/sendMessage",
                            sendMessage, ResultTelegramm.class);

                    servers.setChatId(null);
                    servers.setStatus(true);
                    servers.setTryCount(0);
                    servers.setUsername(null);
                    servers.setLast_date(null);
                }
                if(servers.getTryCount() < 6){
                    servers.setTryCount(servers.getTryCount() + 1);
                }

            }
        }
        gsonService.fileWriterServer(SERVERS_FILE, serversList);
    }
}
