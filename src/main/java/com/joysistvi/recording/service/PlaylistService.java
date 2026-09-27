package com.joysistvi.recording.service;

import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getPlaylistsByUserId(int userId);
    Playlist getPlaylistByIdAndUserId(int playlistId, int userId);
    boolean createPlaylist(String name, int userId);
    List<Song> getSongsByPlaylistIdAndUserId(int playlistId, int userId);
    List<Song> getAvailableSongs();
    boolean addSongToPlaylist(int playlistId, int songId, int userId);
    boolean removeSongFromPlaylist(int playlistId, int songId, int userId);
    boolean deletePlaylist(int playlistId, int userId);
}
