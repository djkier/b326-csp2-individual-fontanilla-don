package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.model.Role;
import com.joysistvi.recording.model.User;

import java.util.Scanner;

public class UserMenuView {
    private final SongView songView;
    private final PlaylistView playlistView;
    private final Scanner scanner;

    public UserMenuView(SongView songView, PlaylistView playlistView, Scanner scanner) {
        this.songView = songView;
        this.playlistView = playlistView;
        this.scanner = scanner;
    }

    public void run(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() != Role.USER) {
            System.out.println("Access denied. A USER account is required.");
            return;
        }

        int userId = authenticatedUser.getId();
        int choice;

        do {
            printMenu(authenticatedUser.getUsername());
            choice = promptChoice();

            switch (choice) {
                case 1 -> songView.viewAllSongs();
                case 2 -> songView.searchSongs();
                case 3 -> playlistView.viewPlaylists(userId);
                case 4 -> playlistView.createPlaylist(userId);
                case 5 -> playlistView.managePlaylist(userId);
                case 6 -> playlistView.deletePlaylist(userId);
                case 7 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice. Try again.");
            }

            if (choice != 7) {
                pause();
            }
        } while (choice != 7);
    }

    private void printMenu(String username) {
        System.out.println("\n----- USER Menu -----");
        System.out.println("Logged in as: " + username);
        System.out.println("1. View All Active Songs");
        System.out.println("2. Search Songs");
        System.out.println("3. View My Playlists");
        System.out.println("4. Create Playlist");
        System.out.println("5. Manage a Playlist");
        System.out.println("6. Delete Playlist");
        System.out.println("7. Logout");
    }

    private int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
