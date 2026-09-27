package com.joysistvi.recording;

import com.joysistvi.recording.cliview.AdminMenuView;
import com.joysistvi.recording.cliview.AlbumView;
import com.joysistvi.recording.cliview.ArtistView;
import com.joysistvi.recording.cliview.LoginView;
import com.joysistvi.recording.cliview.PlaylistView;
import com.joysistvi.recording.cliview.SongView;
import com.joysistvi.recording.cliview.UserMenuView;
import com.joysistvi.recording.cliview.UserView;
import com.joysistvi.recording.config.DBConnection;
import com.joysistvi.recording.controller.AlbumController;
import com.joysistvi.recording.controller.ArtistController;
import com.joysistvi.recording.controller.PlaylistController;
import com.joysistvi.recording.controller.SongController;
import com.joysistvi.recording.controller.UserController;
import com.joysistvi.recording.repository.AlbumRepo;
import com.joysistvi.recording.repository.AlbumRepoImpl;
import com.joysistvi.recording.repository.ArtistRepo;
import com.joysistvi.recording.repository.ArtistRepoImpl;
import com.joysistvi.recording.repository.PlaylistRepo;
import com.joysistvi.recording.repository.PlaylistRepoImpl;
import com.joysistvi.recording.repository.SongRepo;
import com.joysistvi.recording.repository.SongRepoImpl;
import com.joysistvi.recording.repository.UserRepo;
import com.joysistvi.recording.repository.UserRepoImpl;
import com.joysistvi.recording.service.AlbumService;
import com.joysistvi.recording.service.AlbumServiceImpl;
import com.joysistvi.recording.service.ArtistService;
import com.joysistvi.recording.service.ArtistServiceImpl;
import com.joysistvi.recording.service.PlaylistService;
import com.joysistvi.recording.service.PlaylistServiceImpl;
import com.joysistvi.recording.service.SongService;
import com.joysistvi.recording.service.SongServiceImpl;
import com.joysistvi.recording.service.UserService;
import com.joysistvi.recording.service.UserServiceImpl;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();

        ArtistRepo artistRepo = new ArtistRepoImpl(dbConnection);
        AlbumRepo albumRepo = new AlbumRepoImpl(dbConnection);
        SongRepo songRepo = new SongRepoImpl(dbConnection);
        UserRepo userRepo = new UserRepoImpl(dbConnection);
        PlaylistRepo playlistRepo = new PlaylistRepoImpl(dbConnection);

        ArtistService artistService = new ArtistServiceImpl(artistRepo);
        AlbumService albumService = new AlbumServiceImpl(albumRepo);
        SongService songService = new SongServiceImpl(songRepo, albumService);
        UserService userService = new UserServiceImpl(userRepo);
        PlaylistService playlistService = new PlaylistServiceImpl(playlistRepo, songService);

        ArtistController artistController = new ArtistController(artistService);
        AlbumController albumController = new AlbumController(albumService, artistService);
        SongController songController = new SongController(songService, albumService);
        UserController userController = new UserController(userService);
        PlaylistController playlistController = new PlaylistController(playlistService);

        try (Scanner scanner = new Scanner(System.in)) {
            ArtistView artistView = new ArtistView(artistController, scanner);
            AlbumView albumView = new AlbumView(albumController, scanner);
            SongView songView = new SongView(songController, scanner);
            UserView userView = new UserView(userController, scanner);
            PlaylistView playlistView = new PlaylistView(playlistController, scanner);

            AdminMenuView adminMenuView = new AdminMenuView(
                    artistView,
                    albumView,
                    songView,
                    userView,
                    scanner
            );
            UserMenuView userMenuView = new UserMenuView(songView, playlistView, scanner);
            LoginView loginView = new LoginView(userController, adminMenuView, userMenuView, scanner);

            loginView.run();
        }
    }
}
