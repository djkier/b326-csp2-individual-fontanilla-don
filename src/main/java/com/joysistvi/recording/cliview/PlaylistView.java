package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.controller.PlaylistController;
import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;

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
        int choice;

        do {
            printMenu();
            choice = promptChoice();

            switch (choice) {
                case 1 -> viewPlaylists(userId);
                case 2 -> createPlaylist(userId);
                case 3 -> managePlaylist(userId);
                case 4 -> deletePlaylist(userId);
                case 0 -> System.out.println("Returning to user menu...");
                default -> System.out.println("Invalid choice. Try again.");
            }

            if (choice != 0) {
                pause();
            }
        } while (choice != 0);
    }

    private void printMenu() {
        System.out.println("\n----- My Playlists -----");
        System.out.println("1. View My Playlists");
        System.out.println("2. Create Playlist");
        System.out.println("3. Manage a Playlist");
        System.out.println("4. Delete Playlist");
        System.out.println("0. Back");
    }

    private int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    public void viewPlaylists(int userId) {
        System.out.println("\n----- My Playlists -----");
        printPlaylists(playlistController.handleViewPlaylists(userId));
    }

    public void createPlaylist(int userId) {
        System.out.println("\n----- Create Playlist -----");
        System.out.print("Playlist name: ");
        String name = scanner.nextLine();

        boolean isSuccess = playlistController.handleCreatePlaylist(name, userId);
        System.out.println(isSuccess ? "Playlist created successfully." : "Failed to create playlist.");

        if (isSuccess) {
            viewPlaylists(userId);
        }
    }

    public void managePlaylist(int userId) {
        System.out.println("\n----- Manage a Playlist -----");
        List<Playlist> playlists = playlistController.handleViewPlaylists(userId);
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        printPlaylists(playlists);

        System.out.print("Playlist ID to manage: ");
        int playlistId = InputUtility.readInt(scanner);
        Playlist playlist = playlistController.handleGetPlaylist(playlistId, userId);
        if (playlist == null) {
            return;
        }

        runPlaylistMenu(playlist, userId);
    }

    private void runPlaylistMenu(Playlist playlist, int userId) {
        int choice;

        do {
            System.out.println("\n----- " + playlist.getName() + " -----");
            System.out.println("1. View Songs");
            System.out.println("2. Add Song");
            System.out.println("3. Remove Song");
            System.out.println("0. Back");
            choice = promptChoice();

            switch (choice) {
                case 1 -> viewPlaylistSongs(playlist.getId(), userId);
                case 2 -> addSong(playlist.getId(), userId);
                case 3 -> removeSong(playlist.getId(), userId);
                case 0 -> System.out.println("Returning to playlists...");
                default -> System.out.println("Invalid choice. Try again.");
            }

            if (choice != 0) {
                pause();
            }
        } while (choice != 0);
    }

    private void viewPlaylistSongs(int playlistId, int userId) {
        System.out.println("\n----- Playlist Songs -----");
        printSongs(playlistController.handleViewPlaylistSongs(playlistId, userId));
    }

    private void addSong(int playlistId, int userId) {
        System.out.println("\n----- Add Song -----");
        List<Song> songs = playlistController.handleViewAvailableSongs();
        if (songs.isEmpty()) {
            System.out.println("No active songs are available.");
            return;
        }
        printSongs(songs);

        System.out.print("Song ID to add: ");
        int songId = InputUtility.readInt(scanner);
        boolean isSuccess = playlistController.handleAddSong(playlistId, songId, userId);
        System.out.println(isSuccess ? "Song added to playlist." : "Failed to add song to playlist.");
    }

    private void removeSong(int playlistId, int userId) {
        System.out.println("\n----- Remove Song -----");
        List<Song> songs = playlistController.handleViewPlaylistSongs(playlistId, userId);
        if (songs.isEmpty()) {
            System.out.println("No songs found in this playlist.");
            return;
        }
        printSongs(songs);

        System.out.print("Song ID to remove: ");
        int songId = InputUtility.readInt(scanner);
        boolean isSuccess = playlistController.handleRemoveSong(playlistId, songId, userId);
        System.out.println(isSuccess ? "Song removed from playlist." : "Failed to remove song from playlist.");
    }

    public void deletePlaylist(int userId) {
        System.out.println("\n----- Delete Playlist -----");
        List<Playlist> playlists = playlistController.handleViewPlaylists(userId);
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        printPlaylists(playlists);

        System.out.print("Playlist ID to delete: ");
        int playlistId = InputUtility.readInt(scanner);
        boolean isSuccess = playlistController.handleDeletePlaylist(playlistId, userId);
        System.out.println(isSuccess ? "Playlist deleted successfully." : "Failed to delete playlist.");
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

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
