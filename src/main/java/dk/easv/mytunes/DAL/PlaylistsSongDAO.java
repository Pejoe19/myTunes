package dk.easv.mytunes.DAL;

import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistsSongDAO {

    private final DBConnector dbConnector = new DBConnector();

    public PlaylistsSongDAO() throws MusicException {
    }

    public ArrayList<IndexSong> getPlaylistsSong(Playlist playlist) throws Exception {
        String sql = "select * from dbo.SongPlaylistRelation where playlistId = ?";
        ArrayList<IndexSong> indexSongArrayList = new ArrayList<>();
        try(Connection conn = DBConnector.getStaticConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,playlist.getId());
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                int index = rs.getInt("index");
                int songId = rs.getInt("SongId");
                Song placeholdersong = new Song(songId, "", "", "", 0, "");
                IndexSong indexSong = new IndexSong(placeholdersong,index);
                indexSongArrayList.add(indexSong);
            }
            return indexSongArrayList;
        }
        catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        String sql = "DELETE FROM dbo.SongPlaylistRelation WHERE PlaylistId = ? AND SongId = ?";

        try (Connection conn = DBConnector.getStaticConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, song.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Could not remove song from playlist", e);
        }
    }

    public void switchPlaylistSongs(Playlist playlist, int songPlacementId, int newPlacementId) throws MusicException {
        String sql =
                "UPDATE dbo.SongPlaylistRelation " +
                        "SET [Index] = CASE " +
                        "   WHEN [Index] = ? THEN ? " +
                        "   WHEN [Index] = ? THEN ? " +
                        "END " +
                        "WHERE PlaylistID = ? AND [Index] IN (?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, songPlacementId);
            ps.setInt(2, newPlacementId);
            ps.setInt(3, newPlacementId);
            ps.setInt(4, songPlacementId);
            ps.setInt(5, playlist.getId());
            ps.setInt(6, songPlacementId);
            ps.setInt(7, newPlacementId);

            ps.executeUpdate();
        }
        catch (Exception ex) {
            throw new MusicException("Could not move the song", ex);
        }
    }
}
