package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.Utility.CliViewUtility;
import com.joysistvi.recording.controller.PlaylistController;
import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;
import com.joysistvi.recording.service.ValidationException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class PlaylistView {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final PlaylistController playlistController;
    private final Scanner scanner;

    public PlaylistView(PlaylistController playlistController, Scanner scanner) {
        this.playlistController = playlistController;
        this.scanner = scanner;
    }

    public void run(int userId) {
        while (true) {
            printMenu();
            Integer choice = promptChoice();
            if (choice == null) {
                displayError("Please enter a valid menu number.");
                pause();
                continue;
            }

            switch (choice) {
                case 1 -> viewPlaylists(userId);
                case 2 -> createPlaylist(userId);
                case 3 -> managePlaylist(userId);
                case 4 -> deletePlaylist(userId);
                case 0 -> {
                    System.out.println("Returning to user menu...");
                    return;
                }
                default -> displayError("Invalid menu selection.");
            }

            pause();
        }
    }

    private void printMenu() {
        CliViewUtility.showHeader("My Playlists");
        System.out.println("1. View My Playlists");
        System.out.println("2. Create Playlist");
        System.out.println("3. Manage a Playlist");
        System.out.println("4. Delete Playlist");
        System.out.println("0. Back");
    }

    private Integer promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readIntOrNull(scanner);
    }

    public void viewPlaylists(int userId) {
        CliViewUtility.showHeader("View My Playlists");
        try {
            printPlaylists(playlistController.handleViewPlaylists(userId));
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    public void createPlaylist(int userId) {
        CliViewUtility.showHeader("Create Playlist");
        System.out.print("Playlist name: ");
        String name = scanner.nextLine();

        try {
            boolean isSuccess = playlistController.handleCreatePlaylist(name, userId);
            if (!isSuccess) {
                displayError("Failed to create playlist.");
                return;
            }

            System.out.println("Playlist created successfully.");
            viewPlaylists(userId);
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    public void managePlaylist(int userId) {
        CliViewUtility.showHeader("Manage Playlist");
        try {
            List<Playlist> playlists = playlistController.handleViewPlaylists(userId);
            if (playlists.isEmpty()) {
                System.out.println("No playlists found.");
                return;
            }
            printPlaylists(playlists);

            System.out.print("Playlist ID to manage: ");
            Integer playlistId = InputUtility.readIntOrNull(scanner);
            if (playlistId == null) {
                displayError("Please enter a valid Playlist ID.");
                return;
            }

            Playlist playlist = playlistController.handleGetPlaylist(playlistId, userId);
            runPlaylistMenu(playlist, userId);
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    private void runPlaylistMenu(Playlist playlist, int userId) {
        while (true) {
            CliViewUtility.showHeader("Manage Playlist");
            System.out.println("1. View Songs");
            System.out.println("2. Add Song");
            System.out.println("3. Remove Song");
            System.out.println("0. Back");
            Integer choice = promptChoice();
            if (choice == null) {
                displayError("Please enter a valid menu number.");
                pause();
                continue;
            }

            switch (choice) {
                case 1 -> viewPlaylistSongs(playlist, userId);
                case 2 -> addSong(playlist, userId);
                case 3 -> removeSong(playlist, userId);
                case 0 -> {
                    System.out.println("Returning to playlists...");
                    return;
                }
                default -> displayError("Invalid menu selection.");
            }

            pause();
        }
    }

    private void viewPlaylistSongs(Playlist playlist, int userId) {
        CliViewUtility.showHeader("Playlist Songs");
        try {
            printSongs(playlistController.handleViewPlaylistSongs(playlist.getId(), userId));
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    private void addSong(Playlist playlist, int userId) {
        CliViewUtility.showHeader("Add Song");
        List<Song> songs = playlistController.handleViewAvailableSongs();
        if (songs.isEmpty()) {
            System.out.println("No active songs are available.");
            return;
        }
        printSongs(songs);

        System.out.print("Song ID to add: ");
        Integer songId = InputUtility.readIntOrNull(scanner);
        if (songId == null) {
            displayError("Please enter a valid Song ID.");
            return;
        }

        try {
            boolean isSuccess = playlistController.handleAddSong(playlist.getId(), songId, userId);
            if (isSuccess) {
                System.out.println("Song added to playlist.");
            } else {
                displayError("Failed to add song to playlist.");
            }
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    private void removeSong(Playlist playlist, int userId) {
        CliViewUtility.showHeader("Remove Song");
        try {
            List<Song> songs = playlistController.handleViewPlaylistSongs(playlist.getId(), userId);
            if (songs.isEmpty()) {
                System.out.println("No songs found in this playlist.");
                return;
            }
            printSongs(songs);

            System.out.print("Song ID to remove: ");
            Integer songId = InputUtility.readIntOrNull(scanner);
            if (songId == null) {
                displayError("Please enter a valid Song ID.");
                return;
            }

            boolean isSuccess = playlistController.handleRemoveSong(playlist.getId(), songId, userId);
            if (isSuccess) {
                System.out.println("Song removed from playlist.");
            } else {
                displayError("Failed to remove song from playlist.");
            }
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    public void deletePlaylist(int userId) {
        CliViewUtility.showHeader("Delete Playlist");
        try {
            List<Playlist> playlists = playlistController.handleViewPlaylists(userId);
            if (playlists.isEmpty()) {
                System.out.println("No playlists found.");
                return;
            }
            printPlaylists(playlists);

            System.out.print("Playlist ID to delete: ");
            Integer playlistId = InputUtility.readIntOrNull(scanner);
            if (playlistId == null) {
                displayError("Please enter a valid Playlist ID.");
                return;
            }

            boolean isSuccess = playlistController.handleDeletePlaylist(playlistId, userId);
            if (isSuccess) {
                System.out.println("Playlist deleted successfully.");
            } else {
                displayError("Failed to delete playlist.");
            }
        } catch (ValidationException e) {
            displayError(e.getMessage());
        }
    }

    public void printPlaylists(List<Playlist> playlists) {
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" + "-".repeat(20) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25.25s | %-18s |%n", "ID", "Name", "Created");
        System.out.println(border);

        for (Playlist playlist : playlists) {
            System.out.printf("| %-4s | %-25.25s | %-18s |%n",
                    playlist.getId(), playlist.getName(), playlist.getDateCreated().format(DATE_FORMAT));
        }

        System.out.println(border);
    }

    public void printSongs(List<Song> songs) {
        if (songs.isEmpty()) {
            System.out.println("No songs found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" +
                "-".repeat(20) + "+" + "-".repeat(27) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25.25s | %-18.18s | %-25.25s |%n",
                "ID", "Title", "Genre", "Album");
        System.out.println(border);

        for (Song song : songs) {
            System.out.printf("| %-4s | %-25.25s | %-18.18s | %-25.25s |%n",
                    song.getId(), song.getTitle(), song.getGenre(), displayValue(song.getAlbumName()));
        }

        System.out.println(border);
    }

    private String displayValue(String value) {
        return value == null || value.trim().isEmpty() ? "Unknown" : value;
    }

    private void displayError(String message) {
        InputUtility.displayError(message);
    }

    private void pause() {
        InputUtility.pressEnterToContinue(scanner);
    }
}
