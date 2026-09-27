package com.joysistvi.recording;

import com.joysistvi.recording.Utility.PasswordMigrationUtil;
import com.joysistvi.recording.config.DBConnection;
import com.joysistvi.recording.repository.UserRepo;
import com.joysistvi.recording.repository.UserRepoImpl;

public class App {

    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        UserRepo userRepo = new UserRepoImpl(dbConnection);
        PasswordMigrationUtil passwordMigrationUtil = new PasswordMigrationUtil(userRepo);
        
    }
}
