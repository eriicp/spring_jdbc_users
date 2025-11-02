package com.ra2.users.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ra2.users.users.model.Users;
import com.ra2.users.users.repository.UsersRepository;

@RestController
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;

    @PostMapping("/api/users")
    public ResponseEntity<String> create(@RequestBody Users user) {        
        int result = usersRepository.createUser(user);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el usuario");
        }
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<Users>> read() {
        List<Users> users = usersRepository.findAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/api/users/{user_id}")
    public ResponseEntity<Users> readById(@PathVariable Long user_id) {
        Users user = usersRepository.findUserById(user_id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    
    @PutMapping("/api/users/{user_id}")
    public ResponseEntity<String> update(@PathVariable Long user_id, @RequestBody Users user) {    
        int result = usersRepository.updateUser(user_id, user);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Usuario actualizado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PatchMapping("/api/users/{user_id}/name")
    public ResponseEntity<String> updateName(@PathVariable Long user_id, @RequestParam String nom) {
        int result = usersRepository.updateUserName(user_id, nom);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Nombre actualizado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @DeleteMapping("/api/users/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id) {
        int result = usersRepository.deleteUser(user_id);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
}