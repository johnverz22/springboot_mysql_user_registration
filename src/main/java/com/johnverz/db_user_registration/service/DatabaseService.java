package com.johnverz.db_user_registration.service;

import com.johnverz.db_user_registration.entity.Database;
import com.johnverz.db_user_registration.entity.User;
import com.johnverz.db_user_registration.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DatabaseService {

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private MySqlUserService mySqlUserService;

    public List<Database> findByUser(User user) {
        return databaseRepository.findByUser(user);
    }

    public boolean existsByNameAndUser(String name, User user) {
        return databaseRepository.existsByNameAndUser(name, user);
    }

    public Database createDatabase(String name, String description, User user) throws Exception {
        if (existsByNameAndUser(name, user)) {
            throw new Exception("Database already exists");
        }

        // Create database entry
        Database database = new Database(name, description, user);

        try {
            // Create actual MySQL database and grant permissions
            String fullDbName = user.getUsername() + "_" + name;
            mySqlUserService.createDatabase(fullDbName, user.getUsername());

            // Save to our tracking database
            return databaseRepository.save(database);
        } catch (Exception e) {
            throw new Exception("Failed to create database: " + e.getMessage());
        }
    }

    public void deleteDatabase(Long id, User user) throws Exception {
        Database database = databaseRepository.findById(id)
                .orElseThrow(() -> new Exception("Database not found"));

        if (!database.getUser().getId().equals(user.getId())) {
            throw new Exception("Unauthorized access");
        }

        try {
            // Drop actual MySQL database
            String fullDbName = user.getUsername() + "_" + database.getName();
            mySqlUserService.dropDatabase(fullDbName);

            // Remove from our tracking database
            databaseRepository.delete(database);
        } catch (Exception e) {
            throw new Exception("Failed to delete database: " + e.getMessage());
        }
    }
}
