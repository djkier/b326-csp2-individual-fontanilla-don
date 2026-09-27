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
        validateId(userId, "user");
        return playlistRepo.getPlaylistsByUserId(userId);
    }

    @Override
    public Playlist getPlaylistByIdAndUserId(int playlistId, int userId) {
        validateOwnershipIds(playlistId, userId);

        Playlist playlist = playlistRepo.getPlaylistByIdAndUserId(playlistId, userId);
        if (playlist == null) {
            throw new ValidationException("Playlist not found or you do not have access to it.");
        }
        return playlist;
    }

    @Override
    public boolean createPlaylist(String name, int userId) {
        validateId(userId, "user");
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Playlist name is required.");
        }

        return playlistRepo.createPlaylist(new Playlist(name.trim(), userId));
    }

    @Override
    public List<Song> getSongsByPlaylistIdAndUserId(int playlistId, int userId) {
        getPlaylistByIdAndUserId(playlistId, userId);
        return playlistRepo.getSongsByPlaylistIdAndUserId(playlistId, userId);
    }

    @Override
    public List<Song> getAvailableSongs() {
        return songService.getAllSongs();
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId, int userId) {
        validatePlaylistSongIds(playlistId, songId, userId);
        if (playlistRepo.getPlaylistByIdAndUserId(playlistId, userId) == null) {
            throw new ValidationException("Playlist not found or you do not have access to it.");
        }

        Song song = songService.findSongById(songId);
        if (song == null) {
            throw new ValidationException("Song not found.");
        }
        if (song.isArchived()) {
            throw new ValidationException("Archived songs cannot be added to a playlist.");
        }
        if (playlistRepo.isSongInPlaylist(playlistId, songId, userId)) {
            throw new ValidationException("This song is already in the playlist.");
        }

        return playlistRepo.addSongToPlaylist(playlistId, songId, userId);
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId, int userId) {
        validatePlaylistSongIds(playlistId, songId, userId);
        if (playlistRepo.getPlaylistByIdAndUserId(playlistId, userId) == null) {
            throw new ValidationException("Playlist not found or you do not have access to it.");
        }
        if (!playlistRepo.isSongInPlaylist(playlistId, songId, userId)) {
            throw new ValidationException("The selected song is not in this playlist.");
        }

        return playlistRepo.removeSongFromPlaylist(playlistId, songId, userId);
    }

    @Override
    public boolean deletePlaylist(int playlistId, int userId) {
        validateOwnershipIds(playlistId, userId);
        if (playlistRepo.getPlaylistByIdAndUserId(playlistId, userId) == null) {
            throw new ValidationException("Playlist not found or you do not have access to it.");
        }

        return playlistRepo.deletePlaylist(playlistId, userId);
    }

    private void validateOwnershipIds(int playlistId, int userId) {
        validateId(playlistId, "playlist");
        validateId(userId, "user");
    }

    private void validatePlaylistSongIds(int playlistId, int songId, int userId) {
        validateOwnershipIds(playlistId, userId);
        validateId(songId, "song");
    }

    private void validateId(int id, String type) {
        if (id <= 0) {
            throw new ValidationException("Invalid " + type + " ID.");
        }
    }
}
