package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.Utility.CliViewUtility;
import com.joysistvi.recording.model.Role;
import com.joysistvi.recording.model.User;

import java.util.Scanner;

public class UserMenuView {
    private final SongView songView;
    private final PlaylistView playlistView;
    private final Scanner scanner;

    public UserMenuView(
            SongView songView,
            PlaylistView playlistView,
            Scanner scanner
    ) {
        this.songView = songView;
        this.playlistView = playlistView;
        this.scanner = scanner;
    }

    public void run(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() != Role.USER) {
            displayError("Access denied. A USER account is required.");
            pause();
            return;
        }

        while (true) {
            printMenu();
            Integer choice = promptChoice();
            if (choice == null) {
                displayError("Please enter a valid menu number.");
                pause();
                continue;
            }

            switch (choice) {
                case 1 -> songView.viewAllSongs();
                case 2 -> songView.searchSongs();
                case 3 -> playlistView.run(authenticatedUser.getId());
                case 4 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> displayError("Invalid menu selection.");
            }

            pause();
        }
    }

    private void printMenu() {
        CliViewUtility.showHeader("User Menu");
        System.out.println("1. View All Active Songs");
        System.out.println("2. Search Songs");
        System.out.println("3. My Playlists");
        System.out.println("4. Logout");
    }

    private Integer promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readIntOrNull(scanner);
    }

    private void displayError(String message) {
        InputUtility.displayError(message);
    }

    private void pause() {
        InputUtility.pressEnterToContinue(scanner);
    }
}
