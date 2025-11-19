package com.ra2.users.users.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public int uploadCSV(MultipartFile csvFile){
        File fitxerCSV = new File(csvFile.getOriginalFilename());
        try(BufferedReader br = new BufferedReader(new FileReader(fitxerCSV))) {
            String linia;
            int numeroLinia = 0;
            int recompte = 0;

            while ((linia = br.readLine()) != null){
                numeroLinia++;

                if (numeroLinia == 1){
                    continue;
                }

                String[] dades =  linia.split(",");
                Users user = new Users();
                user.setName(dades[1]);
                user.setDescription(dades[2]);
                user.setEmail(dades[3]);
                user.setPassword(dades[4]);
                createUser(user);
                recompte++;
            }

            Path FolderPath = Paths.get("src/main/resources/public/csv_processed");
            Path filePath = FolderPath.resolve(csvFile.getOriginalFilename());
            Files.createDirectories(FolderPath);
            Files.copy(csvFile.getInputStream(), filePath);

            return recompte;
        } catch(IOException e){
            System.out.println("Error d'acces: " + e.getMessage());
        }
        return -1;
    }
}
