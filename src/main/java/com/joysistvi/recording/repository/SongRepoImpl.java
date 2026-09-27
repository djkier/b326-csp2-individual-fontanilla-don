package com.joysistvi.recording.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.joysistvi.recording.config.DBConnection;
import com.joysistvi.recording.model.Song;

public class SongRepoImpl implements SongRepo {
    private static final String SONG_WITH_RELATIONSHIPS =
            "SELECT songs.*, albums.name AS album_name, artists.name AS artist_name " +
            "FROM songs " +
            "LEFT JOIN albums ON songs.album_id = albums.id " +
            "LEFT JOIN artists ON albums.artist_id = artists.id ";

    private final DBConnection dbConnection;

    public SongRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Song> getAllSongs() {
        return getSongsByArchiveStatus(false);
    }

    @Override
    public Song getSongById(int id) {
        String query = SONG_WITH_RELATIONSHIPS + "WHERE songs.id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapSong(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Song By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        List<Song> songs = new ArrayList<>();
        String query = SONG_WITH_RELATIONSHIPS +
                "WHERE songs.is_archived = 0 AND " +
                "(songs.title LIKE ? OR songs.genre LIKE ? OR albums.name LIKE ? OR artists.name LIKE ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            String searchValue = "%" + keyword + "%";
            for (int index = 1; index <= 4; index++) {
                prep.setString(index, searchValue);
            }

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    songs.add(mapSong(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Songs Error: " + e.getMessage());
        }

        return songs;
    }

    @Override
    public boolean createSong(Song song) {
        String query = "INSERT INTO songs (title, length, genre, album_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            setSongFields(prep, song);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create Song Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateSong(Song song) {
        String query = "UPDATE songs SET title = ?, length = ?, genre = ?, album_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            setSongFields(prep, song);
            prep.setInt(5, song.getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Song Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean archiveSong(int id) {
        return updateArchiveStatus(id, true, "Archive Song");
    }

    @Override
    public boolean restoreSong(int id) {
        return updateArchiveStatus(id, false, "Restore Song");
    }

    @Override
    public boolean deleteSong(int id) {
        String query = "DELETE FROM songs WHERE id = ? AND is_archived = 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Song Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Song> getAllArchivedSongs() {
        return getSongsByArchiveStatus(true);
    }

    private List<Song> getSongsByArchiveStatus(boolean archived) {
        List<Song> songs = new ArrayList<>();
        String query = SONG_WITH_RELATIONSHIPS + "WHERE songs.is_archived = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setBoolean(1, archived);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    songs.add(mapSong(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Songs Error: " + e.getMessage());
        }

        return songs;
    }

    private boolean updateArchiveStatus(int id, boolean archived, String action) {
        String query = "UPDATE songs SET is_archived = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setBoolean(1, archived);
            prep.setInt(2, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(action + " Error: " + e.getMessage());
        }

        return false;
    }

    private void setSongFields(PreparedStatement prep, Song song) throws SQLException {
        prep.setString(1, song.getTitle());
        prep.setInt(2, song.getLength());
        prep.setString(3, song.getGenre());
        prep.setInt(4, song.getAlbumId());
    }

    private Song mapSong(ResultSet result) throws SQLException {
        Song song = new Song(
                result.getInt("id"),
                result.getString("title"),
                result.getInt("length"),
                result.getString("genre"),
                result.getInt("album_id")
        );
        song.setAlbumName(result.getString("album_name"));
        song.setArtistName(result.getString("artist_name"));
        song.setArchived(result.getBoolean("is_archived"));
        return song;
    }
}
