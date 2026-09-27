package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.controller.UserController;
import com.joysistvi.recording.model.User;

import java.util.List;
import java.util.Scanner;

public class UserView {
    private final UserController userController;
    private final Scanner scanner;

    public UserView(UserController userController, Scanner scanner) {
        this.userController = userController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptChoice();

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> searchUsers();
                case 0 -> System.out.println("Returning to main menu...");
                default -> InputUtility.displayError("Invalid menu selection.");
            }

            if (choice != 0) {
                InputUtility.pressEnterToContinue(scanner);
            }
        } while (choice != 0);
    }

    private void printMenu() {
        System.out.println("\n----- User Management -----");
        System.out.println("1. View All Users");
        System.out.println("2. Search Users");
        System.out.println("0. Back");
    }

    private int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    private void viewAllUsers() {
        System.out.println("\n----- View All Users -----");
        printUsers(userController.handleViewAllUsers());
    }

    private void searchUsers() {
        System.out.println("\n----- Search Users -----");
        System.out.print("Enter username: ");
        printUsers(userController.handleSearchUsers(scanner.nextLine()));
    }

    public void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25.25s | %-10s |%n", "ID", "Username", "Role");
        System.out.println(border);

        for (User user : users) {
            System.out.printf("| %-4s | %-25.25s | %-10s |%n",
                    user.getId(), user.getUsername(), user.getRole());
        }

        System.out.println(border);
    }
}
