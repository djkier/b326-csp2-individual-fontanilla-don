package com.joysistvi.recording.controller;

import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;
import com.joysistvi.recording.service.PlaylistService;

import java.util.List;

public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public List<Playlist> handleViewPlaylists(int userId) {
        return playlistService.getPlaylistsByUserId(userId);
    }

    public Playlist handleGetPlaylist(int playlistId, int userId) {
        return playlistService.getPlaylistByIdAndUserId(playlistId, userId);
    }

    public boolean handleCreatePlaylist(String name, int userId) {
        return playlistService.createPlaylist(name, userId);
    }

    public List<Song> handleViewPlaylistSongs(int playlistId, int userId) {
        return playlistService.getSongsByPlaylistIdAndUserId(playlistId, userId);
    }

    public List<Song> handleViewAvailableSongs() {
        return playlistService.getAvailableSongs();
    }

    public boolean handleAddSong(int playlistId, int songId, int userId) {
        return playlistService.addSongToPlaylist(playlistId, songId, userId);
    }

    public boolean handleRemoveSong(int playlistId, int songId, int userId) {
        return playlistService.removeSongFromPlaylist(playlistId, songId, userId);
    }

    public boolean handleDeletePlaylist(int playlistId, int userId) {
        return playlistService.deletePlaylist(playlistId, userId);
    }
}
