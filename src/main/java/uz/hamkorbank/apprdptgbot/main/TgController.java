package uz.hamkorbank.apprdptgbot.main;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hamkorbank.apprdptgbot.service.TgService;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TgController {
    private final TgService tgService;
    @PostMapping
    public void getUpdate(@RequestBody Update update) throws ClassNotFoundException {
        tgService.updateTelegram(update);
    }

    @Scheduled(fixedRate = 1800000)
    public void scheduledServer(){
        tgService.scheduledServer();
    }
}
