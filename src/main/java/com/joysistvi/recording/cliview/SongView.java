package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.controller.SongController;
import com.joysistvi.recording.model.Album;
import com.joysistvi.recording.model.Song;

import java.util.List;
import java.util.Scanner;

public class SongView {
    private final SongController songController;
    private final Scanner scanner;

    public SongView(SongController songController, Scanner scanner) {
        this.songController = songController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptChoice();

            switch (choice) {
                case 1 -> viewAllSongs();
                case 2 -> searchSongs();
                case 3 -> addSong();
                case 4 -> updateSong();
                case 5 -> archiveSong();
                case 6 -> restoreSong();
                case 7 -> deleteSong();
                case 8 -> viewAllArchivedSongs();
                case 0 -> System.out.println("Returning to main menu...");
                default -> InputUtility.displayError("Invalid menu selection.");
            }

            if (choice != 0) {
                InputUtility.pressEnterToContinue(scanner);
            }
        } while (choice != 0);
    }

    private void printMenu() {
        System.out.println("\n----- Song Management -----");
        System.out.println("1. View All Songs");
        System.out.println("2. Search Songs");
        System.out.println("3. Add Song");
        System.out.println("4. Update Song");
        System.out.println("5. Archive Song");
        System.out.println("6. Restore Song");
        System.out.println("7. Delete Song");
        System.out.println("8. View All Archived Songs");
        System.out.println("0. Back");
    }

    private int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    public void viewAllSongs() {
        System.out.println("\n----- View All Songs -----");
        printSongs(songController.handleViewAllSongs());
    }

    public void searchSongs() {
        System.out.println("\n----- Search Songs -----");
        System.out.print("Enter title, genre, album, or artist: ");
        String keyword = scanner.nextLine();
        if (keyword.trim().isEmpty()) {
            InputUtility.displayError("Search keyword cannot be empty.");
            return;
        }
        printSongs(songController.handleSearchSongs(keyword));
    }

    private void addSong() {
        System.out.println("\n----- Add Song -----");

        if (!viewAvailableAlbums()) {
            System.out.println("Add an album before creating a song.");
            return;
        }

        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Length in seconds: ");
        int length = InputUtility.readInt(scanner);
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Select Album ID: ");
        int albumId = InputUtility.readInt(scanner);

        boolean isSuccess = songController.handleCreateSong(new Song(title, length, genre, albumId));
        System.out.println(isSuccess ? "Song added successfully." : "Failed to add song.");

        if (isSuccess) {
            System.out.println();
            viewAllSongs();
        }
    }

    private void updateSong() {
        System.out.println("\n----- Update Song -----");
        viewAllSongs();

        System.out.print("Song ID to update: ");
        int id = InputUtility.readInt(scanner);
        Song current = songController.handleGetSongById(id);

        if (current == null) {
            System.out.println("No song found with ID " + id + ". Please check the ID and try again.");
            return;
        }

        System.out.print("New Title [" + current.getTitle() + "] (press Enter to keep the current): ");
        String title = scanner.nextLine();
        if (title.trim().isEmpty()) {
            title = current.getTitle();
        }

        System.out.print("New Length in seconds [" + current.getLength() + "] (press Enter to keep the current): ");
        int length = InputUtility.readOptionalInt(scanner, current.getLength());

        System.out.print("New Genre [" + current.getGenre() + "] (press Enter to keep the current): ");
        String genre = scanner.nextLine();
        if (genre.trim().isEmpty()) {
            genre = current.getGenre();
        }

        if (!viewAvailableAlbums()) {
            System.out.println("No active albums are available for this song.");
            return;
        }
        System.out.print("New Album [" + getAlbumDisplayName(current) + "] " +
                "(press Enter to keep the current): ");
        int albumId = InputUtility.readOptionalInt(scanner, current.getAlbumId());

        Song song = new Song(id, title, length, genre, albumId);
        boolean isSuccess = songController.handleUpdateSong(song);
        System.out.println(isSuccess ? "Song updated successfully." : "Failed to update song.");

        if (isSuccess) {
            System.out.println();
            viewAllSongs();
        }
    }

    private void archiveSong() {
        System.out.println("\n----- Archive Song -----");
        viewAllSongs();
        System.out.print("Song ID to archive: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = songController.handleArchiveSong(id);
        System.out.println(isSuccess ? "Song archived successfully." : "Failed to archive song.");
    }

    private void restoreSong() {
        System.out.println("\n----- Restore Song -----");
        viewAllArchivedSongs();
        System.out.print("Song ID to restore: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = songController.handleRestoreSong(id);
        System.out.println(isSuccess ? "Song restored successfully." : "Failed to restore song.");
    }

    private void deleteSong() {
        System.out.println("\n----- Delete Archived Song -----");
        viewAllArchivedSongs();
        System.out.print("Archived Song ID to delete permanently: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = songController.handleDeleteSong(id);
        System.out.println(isSuccess ? "Song deleted successfully." :
                "Failed to delete song. Only archived songs can be deleted.");
    }

    private void viewAllArchivedSongs() {
        System.out.println("\n----- View All Archived Songs -----");
        printSongs(songController.handleViewArchivedSongs());
    }

    private boolean viewAvailableAlbums() {
        System.out.println("\n----- Available Albums -----");
        List<Album> albums = songController.handleViewAvailableAlbums();

        if (albums.isEmpty()) {
            System.out.println("No active albums found.");
            return false;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" +
                "-".repeat(8) + "+" + "-".repeat(27) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25.25s | %-6s | %-25.25s |%n", "ID", "Album", "Year", "Artist");
        System.out.println(border);

        for (Album album : albums) {
            System.out.printf("| %-4s | %-25.25s | %-6s | %-25.25s |%n",
                    album.getId(), album.getName(), album.getYear(), displayValue(album.getArtistName()));
        }

        System.out.println(border);
        return true;
    }

    public void printSongs(List<Song> songs) {
        if (songs.isEmpty()) {
            System.out.println("No songs found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" +
                "-".repeat(10) + "+" + "-".repeat(20) + "+" + "-".repeat(27) + "+" +
                "-".repeat(27) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25.25s | %-8s | %-18.18s | %-25.25s | %-25.25s |%n",
                "ID", "Title", "Length", "Genre", "Album", "Artist");
        System.out.println(border);

        for (Song song : songs) {
            System.out.printf("| %-4s | %-25.25s | %-8s | %-18.18s | %-25.25s | %-25.25s |%n",
                    song.getId(), song.getTitle(), formatLength(song.getLength()), song.getGenre(),
                    displayValue(song.getAlbumName()), displayValue(song.getArtistName()));
        }

        System.out.println(border);
    }

    private String getAlbumDisplayName(Song song) {
        return displayValue(song.getAlbumName()) + " (ID: " + song.getAlbumId() + ")";
    }

    private String displayValue(String value) {
        return value == null || value.trim().isEmpty() ? "Unknown" : value;
    }

    private String formatLength(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
