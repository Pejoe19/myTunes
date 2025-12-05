package dk.easv.mytunes.DAL;

import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {

    private final DBConnector dbConnector;

    {
        try {
            dbConnector = new DBConnector();
        } catch (MusicException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<Playlist> getPlaylists() throws MusicException {
        ArrayList<Playlist> playlists = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement())
        {
            String sql = "SELECT p.Id, p.Name,\n" +
                    "COUNT(relation.songId) AS NumberOfSongs,\n" +
                    "ISNULL(SUM(DATEDIFF(SECOND, 0, s.Time)), 0) AS TotalSeconds\n" +
                    "FROM Playlists p\n" +
                    "LEFT JOIN SongPlaylistRelation relation ON p.Id = relation.playlistId\n" +
                    "LEFT JOIN Songs s ON relation.songId = s.Id\n" +
                    "GROUP BY p.Id, p.Name\n" +
                    "ORDER BY p.Id;";
            ResultSet rs = stmt.executeQuery(sql);

            // Loop through rows from the database result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int numberOfSongs = rs.getInt("NumberOfSongs");
                int totalSeconds = rs.getInt("TotalSeconds");

                Playlist playlist = new Playlist(id, name, numberOfSongs, totalSeconds);
                playlists.add(playlist);
            }

            return playlists;
        }
        catch (SQLException ex)
        {
            throw new MusicException("Could not get songs from database", ex);
        }
    }

    public void deletePlayList(Playlist playlist) throws Exception {
        String SQL = "delete from dbo.Playlists where id=?";
        try (Connection conn = DBConnector.getStaticConnection()){
            PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,playlist.getId());
            stmt.executeQuery();
        }
        catch (Exception e){
            throw new Exception("something went wrong",e);
        }
    }

    public Playlist updatePlaylist(Playlist playlist) throws MusicException {
        String sql = "UPDATE dbo.Playlists SET Name = ? WHERE Id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new MusicException("Failed to update playlist, no rows affected");
            }
            return playlist;
        } catch (SQLException e) {
            throw new MusicException("Could not update playlist in database", e);
        }
    }

    public Playlist createPlaylist(Playlist playlist) throws MusicException {
        String sql;
            sql = "INSERT INTO dbo.Playlists (Name) VALUES (?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, playlist.getName());
            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);
                return new Playlist(newId, playlist.getName());
            } else {
                throw new MusicException("Creating playlist failed: No ID returned.");
            }
        } catch (SQLException e) {
            throw new MusicException("Could not create playlist in the database", e);
        }
    }
}
