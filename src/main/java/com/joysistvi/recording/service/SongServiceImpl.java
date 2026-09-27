package com.joysistvi.recording.service;

import com.joysistvi.recording.model.Album;
import com.joysistvi.recording.model.Song;
import com.joysistvi.recording.repository.SongRepo;

import java.util.List;

public class SongServiceImpl implements SongService {
    private final SongRepo songRepo;
    private final AlbumService albumService;

    public SongServiceImpl(SongRepo songRepo, AlbumService albumService) {
        this.songRepo = songRepo;
        this.albumService = albumService;
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepo.getAllSongs();
    }

    @Override
    public Song getSongById(int id) {
        if (id <= 0) {
            System.out.println("Invalid song ID.");
            return null;
        }

        Song song = songRepo.getSongById(id);
        if (song == null) {
            System.out.println("Song not found.");
        }
        return song;
    }

    @Override
    public Song findSongById(int id) {
        if (id <= 0) {
            return null;
        }
        return songRepo.getSongById(id);
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return songRepo.searchSongs(keyword.trim());
    }

    @Override
    public boolean createSong(Song song) {
        if (!hasValidSongFields(song, false)) {
            return false;
        }

        normalizeText(song);
        return songRepo.createSong(song);
    }

    @Override
    public boolean updateSong(Song song) {
        if (!hasValidSongFields(song, true)) {
            return false;
        }

        normalizeText(song);
        return songRepo.updateSong(song);
    }

    @Override
    public boolean archiveSong(int id) {
        if (id <= 0) {
            System.out.println("Invalid song ID for archive.");
            return false;
        }
        return songRepo.archiveSong(id);
    }

    @Override
    public boolean restoreSong(int id) {
        if (id <= 0) {
            System.out.println("Invalid song ID for restore.");
            return false;
        }
        return songRepo.restoreSong(id);
    }

    @Override
    public boolean deleteSong(int id) {
        if (id <= 0) {
            System.out.println("Invalid song ID for deletion.");
            return false;
        }
        return songRepo.deleteSong(id);
    }

    @Override
    public List<Song> getAllArchivedSongs() {
        return songRepo.getAllArchivedSongs();
    }

    private boolean hasValidSongFields(Song song, boolean isUpdate) {
        if (song == null || (isUpdate && song.getId() <= 0)) {
            System.out.println("Invalid song data.");
            return false;
        }
        if (song.getTitle() == null || song.getTitle().trim().isEmpty()) {
            System.out.println("Song title is required.");
            return false;
        }
        if (song.getLength() <= 0) {
            System.out.println("Song length must be greater than zero seconds.");
            return false;
        }
        if (song.getGenre() == null || song.getGenre().trim().isEmpty()) {
            System.out.println("Song genre is required.");
            return false;
        }
        if (song.getAlbumId() <= 0) {
            System.out.println("Invalid album ID.");
            return false;
        }

        Album album = albumService.getAlbumById(song.getAlbumId());
        if (album == null) {
            System.out.println("The selected album does not exist.");
            return false;
        }
        if (album.isArchived()) {
            System.out.println("The selected album is archived and cannot receive songs.");
            return false;
        }

        return true;
    }

    private void normalizeText(Song song) {
        song.setTitle(song.getTitle().trim());
        song.setGenre(song.getGenre().trim());
    }
}
