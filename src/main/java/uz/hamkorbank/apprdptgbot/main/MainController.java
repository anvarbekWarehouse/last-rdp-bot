package uz.hamkorbank.apprdptgbot.main;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.hamkorbank.apprdptgbot.dto.Users;
import uz.hamkorbank.apprdptgbot.service.GsonService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final GsonService gsonService;

    @GetMapping("/")
    public ResponseEntity<?> main(){

        return ResponseEntity.ok("Welcome my site");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(gsonService.fileReaderUser("users.json"));
    }
    @GetMapping("/write")
    public ResponseEntity<?> write(){
        List<Users> usersList = gsonService.fileReaderUser("users.json");
        Users user = new Users(usersList.size()+1, "5451511","ahbhsdb", true, "Anvarbek");
        usersList.add(user);
        System.out.println(usersList);

        gsonService.fileWriterUser("users.json", usersList);
        return ResponseEntity.ok(usersList);
    }


}
