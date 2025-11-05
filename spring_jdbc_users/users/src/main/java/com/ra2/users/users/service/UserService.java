package com.ra2.users.users.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
