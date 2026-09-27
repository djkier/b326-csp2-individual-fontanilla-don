package com.joysistvi.recording.service;

import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;
import com.joysistvi.recording.repository.PlaylistRepo;

import java.util.List;

public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepo playlistRepo;
    private final SongService songService;

    public PlaylistServiceImpl(PlaylistRepo playlistRepo, SongService songService) {
        this.playlistRepo = playlistRepo;
        this.songService = songService;
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(int userId) {
        if (!isValidId(userId, "user")) {
            return List.of();
        }
        return playlistRepo.getPlaylistsByUserId(userId);
    }

    @Override
    public Playlist getPlaylistByIdAndUserId(int playlistId, int userId) {
        if (!hasValidOwnershipIds(playlistId, userId)) {
            return null;
        }

        Playlist playlist = playlistRepo.getPlaylistByIdAndUserId(playlistId, userId);
        if (playlist == null) {
            System.out.println("Playlist not found.");
        }
        return playlist;
    }

    @Override
    public boolean createPlaylist(String name, int userId) {
        if (!isValidId(userId, "user")) {
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Playlist name is required.");
            return false;
        }

        return playlistRepo.createPlaylist(new Playlist(name.trim(), userId));
    }

    @Override
    public List<Song> getSongsByPlaylistIdAndUserId(int playlistId, int userId) {
        if (getPlaylistByIdAndUserId(playlistId, userId) == null) {
            return List.of();
        }
        return playlistRepo.getSongsByPlaylistIdAndUserId(playlistId, userId);
    }

    @Override
    public List<Song> getAvailableSongs() {
        return songService.getAllSongs();
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId, int userId) {
        if (!hasValidPlaylistSongIds(playlistId, songId, userId)) {
            return false;
        }
        if (playlistRepo.getPlaylistByIdAndUserId(playlistId, userId) == null) {
            System.out.println("Playlist not found.");
            return false;
        }

        Song song = songService.getSongById(songId);
        if (song == null) {
            return false;
        }
        if (song.isArchived()) {
            System.out.println("Archived songs cannot be added to a playlist.");
            return false;
        }
        if (playlistRepo.isSongInPlaylist(playlistId, songId, userId)) {
            System.out.println("This song is already in the playlist.");
            return false;
        }

        return playlistRepo.addSongToPlaylist(playlistId, songId, userId);
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId, int userId) {
        if (!hasValidPlaylistSongIds(playlistId, songId, userId)) {
            return false;
        }
        if (playlistRepo.getPlaylistByIdAndUserId(playlistId, userId) == null) {
            System.out.println("Playlist not found.");
            return false;
        }
        if (!playlistRepo.isSongInPlaylist(playlistId, songId, userId)) {
            System.out.println("The selected song is not in this playlist.");
            return false;
        }

        return playlistRepo.removeSongFromPlaylist(playlistId, songId, userId);
    }

    @Override
    public boolean deletePlaylist(int playlistId, int userId) {
        if (!hasValidOwnershipIds(playlistId, userId)) {
            return false;
        }
        if (playlistRepo.getPlaylistByIdAndUserId(playlistId, userId) == null) {
            System.out.println("Playlist not found.");
            return false;
        }

        return playlistRepo.deletePlaylist(playlistId, userId);
    }

    private boolean hasValidOwnershipIds(int playlistId, int userId) {
        return isValidId(playlistId, "playlist") && isValidId(userId, "user");
    }

    private boolean hasValidPlaylistSongIds(int playlistId, int songId, int userId) {
        return hasValidOwnershipIds(playlistId, userId) && isValidId(songId, "song");
    }

    private boolean isValidId(int id, String type) {
        if (id <= 0) {
            System.out.println("Invalid " + type + " ID.");
            return false;
        }
        return true;
    }
}
