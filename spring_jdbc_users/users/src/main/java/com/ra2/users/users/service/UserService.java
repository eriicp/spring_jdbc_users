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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.users.users.model.Data;
import com.ra2.users.users.model.UserBatchUpload;
import com.ra2.users.users.model.Users;
import com.ra2.users.users.repository.UsersRepository;

@Service
public class UserService {

    @Autowired
    UsersRepository usersRepository;

    public List<Users> findAllUsers(){
        return usersRepository.findAllUsers();
    }

    public int createUser(Users user){
        return usersRepository.createUser(user);
    }

    public Users findUserById(Long user_id){
        return usersRepository.findUserById(user_id);
    }

    public int updateUser(Long user_id,Users user){
        return usersRepository.updateUser(user_id, user);
    }

    public int updateUserName(Long user_id,String nom){
        return updateUserName(user_id, nom);
    }

    public int deleteUser(Long user_id){
        return deleteUser(user_id);
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
                return imagePath;
            }catch(IOException e){
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
            
            return recompte;
        
        } catch (IOException e) {
            System.out.println("Error procesando CSV: " + e.getMessage());
        }
        return -1;
    }

    public int uploadJSON(MultipartFile jsonFile) throws StreamReadException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper(); 
       
        UserBatchUpload batchUpload = mapper.readValue(jsonFile.getInputStream(), UserBatchUpload.class);
        Data data = batchUpload.getData();

        // Comprobar "OK"
        if (!"OK".equals(data.getControl())) {
            throw new IllegalArgumentException("El control no és 'OK'");
        }

        // Comprobar el count
        if (data.getCount() != data.getUsers().size()) {
            throw new IllegalArgumentException("El count no coincideix amb el nombre d'usuaris");
        }

        // Guardar cada usuari
        int recompte = 0;
        for (Users user : data.getUsers()) {
            usersRepository.createUser(user);
            recompte++;
        }

        // Guardar el arxiu
        Path folderPath = Paths.get("src/main/resources/public/json_processed");
        Path filePath = folderPath.resolve(jsonFile.getOriginalFilename());
        Files.createDirectories(folderPath);
        Files.copy(jsonFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return recompte;
    }
}
