package com.ra2.users.users.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class UserLogging {


    public void info(String clase, String method, String descripcio){
        escriureLog("info", clase, method, descripcio);
    }

    public void error(String clase, String method, String descripcio){
        escriureLog("error", clase, method, descripcio);
    }
    
    public String obtenirFitxerAvui(){
        String fitxerAvui = String.format("logs/aplicacio-%s",LocalDate.now());
        return fitxerAvui;
    }

    private void escriureLog(String nivell , String clase, String method, String descripcio){
        String fitxerAvui = obtenirFitxerAvui();
        Path CurrentFile = Paths.get(fitxerAvui);

        try (BufferedWriter writer = Files.newBufferedWriter(
            CurrentFile, 
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND)){
            
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");            
            String timestamp = LocalDateTime.now().format(dateFormat);
            String entradaLog = String.format("[%s] %s - %s - %s - %s",timestamp,nivell,clase,method,descripcio);
            writer.write(entradaLog);
            writer.newLine(); 
        }catch (IOException e) {
            System.out.println("Error al escirure el log" + e.getMessage());
        }
    
    }
}