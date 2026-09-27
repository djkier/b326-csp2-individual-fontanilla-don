package com.joysistvi.recording.cliview;

import com.joysistvi.recording.Utility.InputUtility;
import com.joysistvi.recording.Utility.CliViewUtility;
import com.joysistvi.recording.controller.UserController;
import com.joysistvi.recording.model.Role;
import com.joysistvi.recording.model.User;

import java.util.Scanner;

public class LoginView {
    private final UserController userController;
    private final AdminMenuView adminMenuView;
    private final UserMenuView userMenuView;
    private final Scanner scanner;

    public LoginView(
            UserController userController,
            AdminMenuView adminMenuView,
            UserMenuView userMenuView,
            Scanner scanner
    ) {
        this.userController = userController;
        this.adminMenuView = adminMenuView;
        this.userMenuView = userMenuView;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptChoice();

            switch (choice) {
                case 1 -> login();
                case 0 -> System.out.println("Exiting application...");
                default -> {
                    InputUtility.displayError("Invalid menu selection.");
                    pause();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Recording Studio App");
        System.out.println("1. Login");
        System.out.println("0. Exit");
    }

    private int promptChoice() {
        System.out.print("Choice: ");
        return InputUtility.readInt(scanner);
    }

    private void login() {
        CliViewUtility.showHeader("Login");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User authenticatedUser = userController.handleAuthenticate(username, password);
        if (authenticatedUser == null) {
            InputUtility.displayError("Invalid username or password.");
            pause();
            return;
        }

        if (authenticatedUser.getRole() == Role.ADMIN) {
            adminMenuView.run(authenticatedUser);
        } else if (authenticatedUser.getRole() == Role.USER) {
            userMenuView.run(authenticatedUser);
        } else {
            InputUtility.displayError("This account has an unsupported role.");
            pause();
        }
    }

    private void pause() {
        InputUtility.pressEnterToContinue(scanner);
    }
}
