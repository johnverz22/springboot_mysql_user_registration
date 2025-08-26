package com.johnverz.db_user_registration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class MySqlUserService {

    @Value("${mysql.admin.host}")
    private String mysqlHost;

    @Value("${mysql.admin.port}")
    private String mysqlPort;

    @Value("${mysql.admin.username}")
    private String adminUsername;

    @Value("${mysql.admin.password}")
    private String adminPassword;

    private Connection getAdminConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%s/", mysqlHost, mysqlPort);
        return DriverManager.getConnection(url, adminUsername, adminPassword);
    }

    public void createMySqlUser(String username, String password) throws SQLException {
        try (Connection conn = getAdminConnection();
             Statement stmt = conn.createStatement()) {

            // Create MySQL user
            String createUserSql = String.format(
                    "CREATE USER '%s'@'%%' IDENTIFIED BY '%s'",
                    username, password
            );
            stmt.executeUpdate(createUserSql);

            // Grant basic privileges (will grant specific database privileges when databases are created)
            String grantSql = String.format(
                    "GRANT USAGE ON *.* TO '%s'@'%%'",
                    username
            );
            stmt.executeUpdate(grantSql);

            stmt.executeUpdate("FLUSH PRIVILEGES");
        }
    }

    public void createDatabase(String databaseName, String username) throws SQLException {
        try (Connection conn = getAdminConnection();
             Statement stmt = conn.createStatement()) {

            // Create database
            String createDbSql = String.format("CREATE DATABASE `%s`", databaseName);
            stmt.executeUpdate(createDbSql);

            // Grant full privileges to user for this database
            String grantSql = String.format(
                    "GRANT ALL PRIVILEGES ON `%s`.* TO '%s'@'%%'",
                    databaseName, username
            );
            stmt.executeUpdate(grantSql);

            stmt.executeUpdate("FLUSH PRIVILEGES");
        }
    }

    public void dropDatabase(String databaseName) throws SQLException {
        try (Connection conn = getAdminConnection();
             Statement stmt = conn.createStatement()) {

            String dropDbSql = String.format("DROP DATABASE IF EXISTS `%s`", databaseName);
            stmt.executeUpdate(dropDbSql);
        }
    }
}