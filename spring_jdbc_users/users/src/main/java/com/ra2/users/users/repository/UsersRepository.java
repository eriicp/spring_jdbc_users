package com.ra2.users.users.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ra2.users.users.model.Users;

@Repository
public class UsersRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Crear un nuevo usuario
    public int createUser(Users user) {
        String sql = "INSERT INTO users (name, description, email, password, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
            user.getName(), 
            user.getDescription(), 
            user.getEmail(), 
            user.getPassword(),
            LocalDateTime.now(),
            LocalDateTime.now());
    }

    // Obtener todos los usuarios
    public List<Users> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // Obtener usuario por ID
    public Users findUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<Users> users = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return users.isEmpty() ? null : users.get(0);
    }

    // Actualizar usuario completo
    public int updateUser(Long id, Users user) {
        String sql = "UPDATE users SET name = ?, description = ?, email = ?, password = ?, dataUpdated = ? WHERE id = ?";
        return jdbcTemplate.update(sql, 
            user.getName(), 
            user.getDescription(), 
            user.getEmail(), 
            user.getPassword(),
            LocalDateTime.now(),
            id);
    }

    // Actualizar solo el nombre
    public int updateUserName(Long id, String name) {
        String sql = "UPDATE users SET name = ?, dataUpdated = ? WHERE id = ?";
        return jdbcTemplate.update(sql, name, LocalDateTime.now(), id);
    }

    // Eliminar usuario
    public int deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // Mapper para convertir ResultSet a objeto Users
    public class UserRowMapper implements RowMapper<Users> {

        @Override
        public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
            Users user = new Users();
            
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setDescription(rs.getString("description"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            
            // Mapear campos de timestamp con manejo de nulls
            Timestamp ultimAccesTimestamp = rs.getTimestamp("ultimAcces");
            if (ultimAccesTimestamp != null) {
                user.setUltimAcces(ultimAccesTimestamp.toLocalDateTime());
            }
            
            Timestamp dataCreatedTimestamp = rs.getTimestamp("dataCreated");
            if (dataCreatedTimestamp != null) {
                user.setDataCreated(dataCreatedTimestamp.toLocalDateTime());
            }
            
            Timestamp dataUpdatedTimestamp = rs.getTimestamp("dataUpdated");
            if (dataUpdatedTimestamp != null) {
                user.setDataUpdated(dataUpdatedTimestamp.toLocalDateTime());
            }
            
            return user;
        }
    }
}