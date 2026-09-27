package com.joysistvi.recording.cliview;

import java.util.List;
import java.util.Scanner;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.Utility.CliViewUtility;
import com.joysistvi.recording.controller.AlbumController;
import com.joysistvi.recording.model.Album;
import com.joysistvi.recording.model.Artist;

public class AlbumView {

    private final AlbumController albumController;
    private final Scanner scanner;

    public AlbumView(AlbumController albumController, Scanner scanner) {
        this.albumController = albumController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();

            choice = promptChoice();

            switch (choice) {
                case 1 -> viewAllAlbums();
                case 2 -> searchAlbum();
                case 3 -> addAlbums();
                case 4 -> updateAlbum();
                case 5 -> archiveAlbum();
                case 6 -> restoreAlbum();
                case 7 -> deleteAlbum();
                case 8 -> viewAllArchivedAlbums();

                case 0 -> System.out.println("Returning to main menu...");
                default -> InputUtility.displayError("Invalid menu selection.");
            }

            if (choice != 0) {
                InputUtility.pressEnterToContinue(scanner);
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Album Management");
        System.out.println("1. View All Albums");
        System.out.println("2. Search Album");
        System.out.println("3. Add Album");
        System.out.println("4. Update Album");
        System.out.println("5. Archive Album");
        System.out.println("6. Restore Album");
        System.out.println("7. Delete Album");
        System.out.println("8. View All Archived Albums");
        System.out.println("0. Back");
    }

    public int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    private void viewAllAlbums() {
        CliViewUtility.showHeader("View All Albums");
        List<Album> albums = albumController.handleViewAllAlbums();
        printAlbums(albums);
    }

    private void searchAlbum() {
        CliViewUtility.showHeader("Search Albums");
        System.out.print("Enter name: ");
        String keyword = scanner.nextLine();
        List<Album> albums = albumController.searchAlbum(keyword);
        printAlbums(albums);
    }

    private void addAlbums() {
        CliViewUtility.showHeader("Add Album");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Year: ");
        int year = InputUtility.readInt(scanner);

        if (!viewAvailableArtists()) {
            System.out.println("Add an artist before creating an album.");
            return;
        }
        System.out.print("Select Artist ID: ");
        int artistId = InputUtility.readInt(scanner);

        Album album = new Album(name, year, artistId);

        boolean isSuccess = albumController.handleCreateAlbum(album);
        System.out.println(isSuccess ? "Album added successfully." : "Failed to add album.");

        if (isSuccess) {
            System.out.println();
            viewAllAlbums();
        }
    }


    //UPDATE AGAIN
    public void updateAlbum() {
        CliViewUtility.showHeader("Update Album");

        printAlbums(albumController.handleViewAllAlbums());

        System.out.print("Album ID to update: ");
        int id = InputUtility.readInt(scanner);

        Album current = albumController.handleGetAlbumById(id);

        if (current == null) {
            System.out.println("No album found with ID " + id + ". Please check the ID and try again.");
            return;
        }

        System.out.println("New Name [ " + current.getName() + " ] (press Enter to keep the current): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = current.getName();
        }

        System.out.println("New Year [ " + current.getYear() + " ] (press Enter to keep the current): ");
        int year = InputUtility.readOptionalInt(scanner, current.getYear());

        viewAvailableArtists();
        System.out.println("New Artist [ " + getArtistDisplayName(current) + " ] (press Enter to keep the current): ");
        int artistId = InputUtility.readOptionalInt(scanner, current.getArtistId());

        Album album = new Album(id, name, year, artistId);

        boolean isSuccess = albumController.handleUpdateAlbum(album);
        System.out.println(isSuccess ? "Album updated successfully." : "Failed to update album.");

        if (isSuccess) {
            System.out.println();
            viewAllAlbums();
        }
    }





    //archive may not be use on the finish implementation
    private void archiveAlbum() {
        CliViewUtility.showHeader("Archive Album");

        printAlbums(albumController.handleViewAllAlbums());

        System.out.print("Album ID to archive: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = albumController.handleArchiveAlbum(id);
        System.out.println(isSuccess ? "Album archived successfully." : "Failed to archive album.");

        if (isSuccess) {
            System.out.println();
            viewAllAlbums();
        }
    }

    private void restoreAlbum() {
        CliViewUtility.showHeader("Restore Album");

        printAlbums(albumController.handleViewArchivedAlbums());

        System.out.print("Album ID to restore: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = albumController.handleRestoreAlbum(id);
        System.out.println(isSuccess ? "Album restored successfully." : "Failed to restore album.");

        if (isSuccess) {
            System.out.println();
            viewAllArchivedAlbums();
        }
    }


    //must be use only by the admin
    private void deleteAlbum() {
        CliViewUtility.showHeader("Delete Album");

        printAlbums(albumController.handleViewArchivedAlbums());

        System.out.print("Album ID to delete: ");
        int id = InputUtility.readInt(scanner);

        boolean isSuccess = albumController.handleDeleteAlbum(id);
        System.out.println(isSuccess ? "Album deleted successfully." : "Failed to delete album.");

        if (isSuccess) {
            System.out.println();
            viewAllArchivedAlbums();
        }
    }

    private void viewAllArchivedAlbums() {
        CliViewUtility.showHeader("Archived Albums");
        List<Album> albums = albumController.handleViewArchivedAlbums();
        printAlbums(albums);
    }

    private boolean viewAvailableArtists() {
        System.out.println("\n----- Available Artists -----");
        List<Artist> artists = albumController.handleViewAvailableArtists();

        if (artists.isEmpty()) {
            System.out.println("No artists found.");
            return false;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s |%n", "ID", "Name");
        System.out.println(border);

        for (Artist artist : artists) {
            System.out.printf("| %-4s | %-25s |%n", artist.getId(), artist.getName());
        }

        System.out.println(border);
        return true;
    }

    private String getArtistDisplayName(Album album) {
        if (album.getArtistName() == null || album.getArtistName().trim().isEmpty()) {
            return "Unknown Artist (ID: " + album.getArtistId() + ")";
        }

        return album.getArtistName() + " (ID: " + album.getArtistId() + ")";
    }

    public void printAlbums(List<Album> albums) {
        if (albums.isEmpty()) {
            System.out.println("No albums found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+"
                + "-".repeat(8) + "+" + "-".repeat(27) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-6s | %-25s |%n", "ID", "Name", "Year", "Artist");
        System.out.println(border);

        for (Album album : albums) {
            System.out.printf("| %-4s | %-25s | %-6s | %-25s |%n",
                    album.getId(), album.getName(), album.getYear(), getArtistDisplayName(album));
        }

        System.out.println(border);
    }
}
