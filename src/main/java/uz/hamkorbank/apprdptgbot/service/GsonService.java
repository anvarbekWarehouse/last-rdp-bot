package uz.hamkorbank.apprdptgbot.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.catalina.Server;
import org.springframework.stereotype.Service;
import uz.hamkorbank.apprdptgbot.dto.Servers;
import uz.hamkorbank.apprdptgbot.dto.Users;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GsonService {
    String systemPath = System.getProperty("user.dir");
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    public List<Users> fileReaderUser(String fileName){
        String path = systemPath + "/"+fileName;
        File file = new File(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Users[] data = gson.fromJson(reader, Users[].class);
            List<Users> usersList = new ArrayList<>(Arrays.asList(data));
            return usersList;
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Servers> fileReaderServer(String fileName){
        String path = systemPath + "/"+fileName;
        File file = new File(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Servers[] data = gson.fromJson(reader, Servers[].class);
            List<Servers> serversList = new ArrayList<>(Arrays.asList(data));
            return serversList;
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fileWriterUser(String fileName, List<Users> usersList){
        String path = systemPath + "/"+fileName;
        File file = new File(path);
       try ( FileWriter fileWriter = new FileWriter(file)){
           gson.toJson(usersList, fileWriter);
           fileWriter.close();
       } catch (IOException e) {
           e.printStackTrace();
       }

    }
    public void fileWriterServer(String fileName, List<Servers> serversList){
       String path = systemPath + "/"+fileName;
        File file = new File(path);
        try ( FileWriter fileWriter = new FileWriter(file)){
            gson.toJson(serversList, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
