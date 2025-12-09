package com.ra2.users.users.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.users.users.logging.UserLogging;
import com.ra2.users.users.model.Data;
import com.ra2.users.users.model.UserBatchUpload;
import com.ra2.users.users.model.Users;
import com.ra2.users.users.repository.UsersRepository;

@Service
public class UserService {

    @Autowired
    UsersRepository usersRepository;
    UserLogging UserLogging;

    public List<Users> findAllUsers(){
        try {
            List<Users> users = usersRepository.findAllUsers();
            UserLogging.info("UserService", "findAllUsers", "Recuperats " + users.size() + " usuaris.");
            return users;
        } catch (DataAccessException e) {
            UserLogging.error("UserService", "findAllUsers", "Error al recuperar usuaris: " + e.getMessage());
            throw e;
        }
    }

    public int createUser(Users user){
        try {
            int result = usersRepository.createUser(user);
            UserLogging.info("UserService", "createUser", "Usuario " + user.getName() + " creat.");
            return result;
        } catch (DataAccessException e) {
            UserLogging.error("UserService", "createUser", "Error al crear usuari " +": " + e.getMessage());
            throw e; 
        }
    }

    public Users findUserById(Long user_id){
        try {
            Users user = usersRepository.findUserById(user_id);
            String logDesc = (user != null) 
                ? "Usuari ID " + user_id + " trobat." 
                : "Usuari ID " + user_id + " no trobat (null).";
            UserLogging.info("UserService", "findUserById", logDesc);
            return user;
        } catch (DataAccessException e) {
            UserLogging.error("UserService", "findUserById", "Error al buscar usuari ID " + user_id + ": " + e.getMessage());
            throw e; 
        }
    }

    public int updateUser(Long user_id,Users user){
        try {
            int result = usersRepository.updateUser(user_id, user);
            UserLogging.info("UserService", "updateUser", "Usuari ID " + user_id + " actualizat.");
            return result;
        } catch (DataAccessException e) {
            UserLogging.error("UserService", "updateUser", "Error al actualizar usuari ID " + user_id + ": " + e.getMessage());
            throw e;
        }
    }

    public int updateUserName(Long user_id,String nom){
        try {
            int result = usersRepository.updateUserName(user_id, nom);
            UserLogging.info("UserService", "updateUserName", "Nom d' usuari ID " + user_id);
            return result;
        } catch (DataAccessException e) {
            UserLogging.error("UserService", "updateUserName", "Error al actualizar nom d' usuari ID " + user_id + ": " + e.getMessage());
            throw e;
        }
    }

    public int deleteUser(Long user_id){
        try {
            int result = usersRepository.deleteUser(user_id);
            UserLogging.info("UserService", "deleteUser", "Usuari ID " + user_id + " eliminat.");
            return result;
        } catch (DataAccessException e) {
            UserLogging.error("UserService", "deleteUser", "Error al eliminar usuari ID " + user_id + ": " + e.getMessage());
            throw e;
        }
    }

    public String uploadImage(Long User_id, MultipartFile imagFile){
        Users user = usersRepository.findUserById(User_id);
        if (user != null) {
            try{
                Path FolderPath = Paths.get("src/main/resources/public/images");
                Path filePath = FolderPath.resolve(imagFile.getOriginalFilename());
                Files.createDirectories(FolderPath);
                Files.copy(imagFile.getInputStream(), filePath);
                String imagePath = "/images/" + imagFile.getOriginalFilename();
                usersRepository.addImagePath(imagePath,User_id);
                UserLogging.info("UserService","uploadImage","Imatge pujada");
                return imagePath;
            }catch(IOException e){
                UserLogging.error("UserService","uploadImage",e.getMessage());
                return "ERROR";
            }
        }
        else{
            return "No existeix l'usuari";
        }
    }
    
    public int uploadCSV(MultipartFile csvFile) {
        int recompte = 0;
        
        try (InputStream inputStream = csvFile.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            
            String linia;
            int numeroLinia = 0;

            while ((linia = br.readLine()) != null) {
                numeroLinia++;

                if (numeroLinia == 1) {
                continue;
                }

                String[] dades = linia.split(",");
            
                //Agafa tots els camps separats per comas
                if (dades.length >= 5) {
                    Users user = new Users();
                    user.setName(dades[1]);
                    user.setDescription(dades[2]);
                    user.setEmail(dades[3]);
                    user.setPassword(dades[4]);
                    createUser(user);
                    recompte++;
                }
            }

            // Guardar el arxiu procesat 
            Path folderPath = Paths.get("src/main/resources/public/csv_processed");
            Path filePath = folderPath.resolve(csvFile.getOriginalFilename());
            
            Files.createDirectories(folderPath);
            Files.copy(csvFile.getInputStream(), filePath);
            
            UserLogging.info("UserService","uploadCSV","CSV pujat");
            return recompte;
        
        } catch (IOException e) {
            UserLogging.error("UserService","uploadCSV",e.getMessage());
            System.out.println("Error procesando CSV: " + e.getMessage());
        }
        return -1;
    }

public int uploadJSON(MultipartFile jsonFile) {
        int recompte = 0;
        try {
            ObjectMapper mapper = new ObjectMapper(); 
           
            UserBatchUpload batchUpload = mapper.readValue(jsonFile.getInputStream(), UserBatchUpload.class);
            Data data = batchUpload.getData();

            if (!"OK".equals(data.getControl())) {
                UserLogging.error("UserService", "uploadJSON", "El control no és 'OK' al fitxer JSON.");
                throw new IllegalArgumentException("El control no és 'OK'");
            }

            if (data.getCount() != data.getUsers().size()) {
                UserLogging.error("UserService", "uploadJSON", "El count (" + data.getCount() + ") no coincideix amb el nombre d'usuaris (" + data.getUsers().size() + ").");
                throw new IllegalArgumentException("El count no coincideix amb el nombre d'usuaris");
            }

            for (Users user : data.getUsers()) {
                usersRepository.createUser(user);
                recompte++;
            }

            Path folderPath = Paths.get("src/main/resources/public/json_processed");
            Path filePath = folderPath.resolve(jsonFile.getOriginalFilename());
            Files.createDirectories(folderPath);
            Files.copy(jsonFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            UserLogging.info("UserService", "uploadJSON", "JSON '" + jsonFile.getOriginalFilename() + "' processat correctament. Usuaris creats: " + recompte);
            return recompte;
            
        } catch (Exception e) {
            UserLogging.error("UserService", "uploadJSON", "Error processant el fitxer JSON '" + jsonFile.getOriginalFilename() + "'. Causa: " + e.getMessage());
            throw new RuntimeException("Error processant el fitxer JSON.", e);
        }
    }
}
