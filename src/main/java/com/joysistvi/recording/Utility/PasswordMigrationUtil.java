package com.joysistvi.recording.Utility;

import com.joysistvi.recording.model.User;
import com.joysistvi.recording.repository.UserRepo;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public final class PasswordMigrationUtil {
    private final UserRepo userRepo;

    public PasswordMigrationUtil(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void migratePlaintextPasswordsToBCrypt() {
        System.out.println("Starting one-time plaintext-to-BCrypt password migration.");
        System.out.println("Do not run this migration more than once.");

        List<User> users = userRepo.getAllUsersForPasswordMigration();
        int migratedCount = 0;
        int failedCount = 0;

        for (User user : users) {
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                System.err.println("Skipped user ID " + user.getId() + ": password is empty.");
                failedCount++;
                continue;
            }

            try {
                String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                if (userRepo.updatePassword(user.getId(), passwordHash)) {
                    migratedCount++;
                    continue;
                }

                System.err.println("Failed to migrate password for user ID " + user.getId() + ".");
            } catch (RuntimeException e) {
                System.err.println("Failed to hash password for user ID " + user.getId() + ".");
            }
            failedCount++;
        }

        System.out.println("Password migration finished.");
        System.out.println("Migrated users: " + migratedCount);
        System.out.println("Failed or skipped users: " + failedCount);
    }
}
