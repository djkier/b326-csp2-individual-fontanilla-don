package com.joysistvi.recording.repository;

import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;

import java.util.List;

public interface PlaylistRepo {
    List<Playlist> getPlaylistsByUserId(int userId);
    Playlist getPlaylistByIdAndUserId(int playlistId, int userId);
    boolean createPlaylist(Playlist playlist);
    List<Song> getSongsByPlaylistIdAndUserId(int playlistId, int userId);
    boolean isSongInPlaylist(int playlistId, int songId, int userId);
    boolean addSongToPlaylist(int playlistId, int songId, int userId);
    boolean removeSongFromPlaylist(int playlistId, int songId, int userId);
    boolean deletePlaylist(int playlistId, int userId);
}
