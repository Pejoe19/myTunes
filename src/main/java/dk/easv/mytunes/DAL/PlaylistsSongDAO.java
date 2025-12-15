package dk.easv.mytunes.DAL;

import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistsSongDAO {

    // Instance variables
    private final DBConnector dbConnector = new DBConnector();

    public PlaylistsSongDAO() throws MusicException {
    }

    public ArrayList<IndexSong> getPlaylistsSong(Playlist playlist) throws MusicException {
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
                Song placeholdersong = new Song(songId, "", "", "", 0);
                IndexSong indexSong = new IndexSong(placeholdersong,index);
                indexSongArrayList.add(indexSong);
            }
            return indexSongArrayList;
        }
        catch (Exception e) {
            throw new MusicException(e);
        }
    }

    public void removeSongFromPlaylist(Playlist playlist, int songIndex) throws MusicException {
        String sql = "DELETE FROM dbo.SongPlaylistRelation WHERE PlaylistId = ? AND [Index] = ?";

        try (Connection conn = DBConnector.getStaticConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, songIndex);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new MusicException("Could not remove song from playlist", e);
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

    public IndexSong addSongToPlaylist(Playlist playlist, Song song) throws MusicException {
        String sqlGetIndex = "SELECT ISNULL(MAX([Index]), 0) + 1 AS NextIndex FROM dbo.SongPlaylistRelation WHERE PlaylistId = ?";
        String sqlInsert = "INSERT INTO dbo.SongPlaylistRelation (PlaylistId, SongId, [Index]) VALUES (?, ?, ?)";
        try (Connection conn = DBConnector.getStaticConnection()) {
            //Find next available index for this playlist
            int nextIndex = 1;
            try (PreparedStatement psIndex = conn.prepareStatement(sqlGetIndex)) {
                psIndex.setInt(1, playlist.getId());
                ResultSet rs = psIndex.executeQuery();
                if (rs.next()) {
                    nextIndex = rs.getInt("NextIndex");
                }
            }
            // Insert the new song relation
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, playlist.getId());
                psInsert.setInt(2, song.getId());
                psInsert.setInt(3, nextIndex);
                psInsert.executeUpdate();
            }
            // Return the IndexSong object (so GUI can update instantly)
            return new IndexSong(song, nextIndex);
        } catch (Exception e) {
            throw new MusicException("Could not add song to playlist", e);
        }
    }
}
