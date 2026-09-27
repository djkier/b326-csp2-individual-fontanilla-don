package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.model.Role;
import com.joysistvi.recording.model.User;

import java.util.Scanner;

public class AdminMenuView {
    private final ArtistView artistView;
    private final AlbumView albumView;
    private final SongView songView;
    private final UserView userView;
    private final Scanner scanner;

    public AdminMenuView(
            ArtistView artistView,
            AlbumView albumView,
            SongView songView,
            UserView userView,
            Scanner scanner
    ) {
        this.artistView = artistView;
        this.albumView = albumView;
        this.songView = songView;
        this.userView = userView;
        this.scanner = scanner;
    }

    public void run(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() != Role.ADMIN) {
            InputUtility.displayError("Access denied. An ADMIN account is required.");
            pause();
            return;
        }

        int choice;

        do {
            printMenu(authenticatedUser.getUsername());
            choice = promptChoice();

            switch (choice) {
                case 1 -> artistView.run();
                case 2 -> albumView.run();
                case 3 -> songView.run();
                case 4 -> userView.run();
                case 5 -> runCatalogMenu();
                case 6 -> System.out.println("Logging out...");
                default -> {
                    InputUtility.displayError("Invalid menu selection.");
                    pause();
                }
            }
        } while (choice != 6);
    }

    private void printMenu(String username) {
        System.out.println("\n----- ADMIN Menu -----");
        System.out.println("Logged in as: " + username);
        System.out.println("1. Artist Management");
        System.out.println("2. Album Management");
        System.out.println("3. Song Management");
        System.out.println("4. User Management");
        System.out.println("5. Browse/Search Song Catalog");
        System.out.println("6. Logout");
    }

    private void runCatalogMenu() {
        int choice;

        do {
            System.out.println("\n----- Song Catalog -----");
            System.out.println("1. View All Active Songs");
            System.out.println("2. Search Songs");
            System.out.println("0. Back");
            choice = promptChoice();

            switch (choice) {
                case 1 -> songView.viewAllSongs();
                case 2 -> songView.searchSongs();
                case 0 -> System.out.println("Returning to admin menu...");
                default -> InputUtility.displayError("Invalid menu selection.");
            }

            if (choice != 0) {
                pause();
            }
        } while (choice != 0);
    }

    private int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    private void pause() {
        InputUtility.pressEnterToContinue(scanner);
    }
}
