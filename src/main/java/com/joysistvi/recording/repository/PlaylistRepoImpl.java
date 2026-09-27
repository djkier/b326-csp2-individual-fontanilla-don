package com.joysistvi.recording.repository;

import com.joysistvi.recording.config.DBConnection;
import com.joysistvi.recording.model.Playlist;
import com.joysistvi.recording.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRepoImpl implements PlaylistRepo {
    private static final String PLAYLIST_SONGS_WITH_RELATIONSHIPS =
            "SELECT songs.*, albums.name AS album_name, artists.name AS artist_name " +
            "FROM playlist_songs " +
            "INNER JOIN playlists ON playlist_songs.playlist_id = playlists.id " +
            "INNER JOIN songs ON playlist_songs.song_id = songs.id " +
            "LEFT JOIN albums ON songs.album_id = albums.id " +
            "LEFT JOIN artists ON albums.artist_id = artists.id ";

    private final DBConnection dbConnection;

    public PlaylistRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(int userId) {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT id, name, date_created, user_id FROM playlists WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, userId);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    playlists.add(mapPlaylist(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get User Playlists Error: " + e.getMessage());
        }

        return playlists;
    }

    @Override
    public Playlist getPlaylistByIdAndUserId(int playlistId, int userId) {
        String query = "SELECT id, name, date_created, user_id FROM playlists WHERE id = ? AND user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, playlistId);
            prep.setInt(2, userId);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapPlaylist(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Owned Playlist Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean createPlaylist(Playlist playlist) {
        String query = "INSERT INTO playlists (name, date_created, user_id) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, playlist.getName());
            prep.setTimestamp(2, Timestamp.valueOf(playlist.getDateCreated()));
            prep.setInt(3, playlist.getUserId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create Playlist Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Song> getSongsByPlaylistIdAndUserId(int playlistId, int userId) {
        List<Song> songs = new ArrayList<>();
        String query = PLAYLIST_SONGS_WITH_RELATIONSHIPS +
                "WHERE playlists.id = ? AND playlists.user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, playlistId);
            prep.setInt(2, userId);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    songs.add(mapSong(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Playlist Songs Error: " + e.getMessage());
        }

        return songs;
    }

    @Override
    public boolean isSongInPlaylist(int playlistId, int songId, int userId) {
        String query = "SELECT 1 FROM playlist_songs " +
                "INNER JOIN playlists ON playlist_songs.playlist_id = playlists.id " +
                "WHERE playlists.id = ? AND playlist_songs.song_id = ? AND playlists.user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, playlistId);
            prep.setInt(2, songId);
            prep.setInt(3, userId);

            try (ResultSet result = prep.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.err.println("Check Playlist Song Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId, int userId) {
        String query = "INSERT INTO playlist_songs (playlist_id, song_id) " +
                "SELECT id, ? FROM playlists WHERE id = ? AND user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, songId);
            prep.setInt(2, playlistId);
            prep.setInt(3, userId);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add Playlist Song Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId, int userId) {
        String query = "DELETE playlist_songs FROM playlist_songs " +
                "INNER JOIN playlists ON playlist_songs.playlist_id = playlists.id " +
                "WHERE playlists.id = ? AND playlist_songs.song_id = ? AND playlists.user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, playlistId);
            prep.setInt(2, songId);
            prep.setInt(3, userId);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Remove Playlist Song Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deletePlaylist(int playlistId, int userId) {
        String deleteSongs = "DELETE playlist_songs FROM playlist_songs " +
                "INNER JOIN playlists ON playlist_songs.playlist_id = playlists.id " +
                "WHERE playlists.id = ? AND playlists.user_id = ?";
        String deletePlaylist = "DELETE FROM playlists WHERE id = ? AND user_id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement songPrep = conn.prepareStatement(deleteSongs);
                 PreparedStatement playlistPrep = conn.prepareStatement(deletePlaylist)) {
                songPrep.setInt(1, playlistId);
                songPrep.setInt(2, userId);
                songPrep.executeUpdate();

                playlistPrep.setInt(1, playlistId);
                playlistPrep.setInt(2, userId);
                boolean deleted = playlistPrep.executeUpdate() > 0;

                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Delete Playlist Error: " + e.getMessage());
        }

        return false;
    }

    private Playlist mapPlaylist(ResultSet result) throws SQLException {
        return new Playlist(
                result.getInt("id"),
                result.getString("name"),
                result.getTimestamp("date_created").toLocalDateTime(),
                result.getInt("user_id")
        );
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
