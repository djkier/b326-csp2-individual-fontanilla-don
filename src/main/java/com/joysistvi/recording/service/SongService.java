package com.joysistvi.recording.service;

import com.joysistvi.recording.model.Song;

import java.util.List;

public interface SongService {
    List<Song> getAllSongs();
    Song getSongById(int id);
    Song findSongById(int id);
    List<Song> searchSongs(String keyword);
    boolean createSong(Song song);
    boolean updateSong(Song song);
    boolean archiveSong(int id);
    boolean restoreSong(int id);
    boolean deleteSong(int id);
    List<Song> getAllArchivedSongs();
}
