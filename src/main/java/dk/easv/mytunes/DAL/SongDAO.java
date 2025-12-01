package dk.easv.mytunes.DAL;

import dk.easv.mytunes.Be.Song;
import dk.easv.mytunes.BLL.MusicException;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    private final DBConnector dbConnector = new DBConnector();

    public SongDAO() throws MusicException {
    }

    public List<Song> getAllSongs() throws MusicException {
        List<Song> songs = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM Songs";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String category = rs.getString("Category");
                LocalTime localTime = rs.getTime("Time").toLocalTime();
                int time = localTime.toSecondOfDay();
                String filePath = rs.getString("File");

                songs.add(new Song(id, title, artist, category, time, filePath));
            }

        } catch (Exception e) {
            throw new MusicException("Could not load songs from DB", e);
        }
        return songs;
    }



    public Song updateSong(Song song) throws MusicException {
        try (Connection conn = dbConnector.getConnection())
        {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dbo.Songs " +
                            "SET Title = ?, Artist = ?, Category = ?, Time = ? " +
                            "WHERE Id = ?"
            );

            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getCategory());
            //ps.setBytes(4, null);

            int timeInSeconds = song.getTime();
            int hours = timeInSeconds / 3600;
            int minutes = (timeInSeconds % 3600) / 60;
            int seconds = timeInSeconds % 60;
            String sqlString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            ps.setString(4, sqlString);
            ps.setInt(5, song.getId());
            ps.executeUpdate();

            return song;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new MusicException("Could not update the song in the database");
        }
    }

    public void deleteSong(Song song) throws MusicException {
        String SQLDeleteInSongs = "delete from dbo.Songs where id=?";
        try (Connection conn = DBConnector.getStaticConnection()){
            PreparedStatement stmt = conn.prepareStatement(SQLDeleteInSongs, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,song.getId());
            stmt.executeQuery();
        }
        catch (Exception e){
            throw new MusicException("Could not delete song in the database songs",e);
        }

        String SQLDeleteInRelation = "delete from dbo.SongPlaylistRelation WHERE SongId = ?";
        try (Connection conn = DBConnector.getStaticConnection()){
            PreparedStatement stmt = conn.prepareStatement(SQLDeleteInRelation, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,song.getId());
            stmt.executeQuery();
        }
        catch (Exception e){
            throw new MusicException("Could not delete song in the database relations",e);
        }
    }
    public Song createSong(Song song) throws MusicException {
        String sql = "INSERT INTO dbo.Songs (Title, Artist, Category, Time) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Convert seconds → HH:MM:SS
            int timeInSeconds = song.getTime();
            int hours = timeInSeconds / 3600;
            int minutes = (timeInSeconds % 3600) / 60;
            int seconds = timeInSeconds % 60;
            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getCategory());
            ps.setTime(4, Time.valueOf(formattedTime));
            //ps.setString(5, null);

            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);
                return new Song(newId, song.getTitle(), song.getArtist(), song.getCategory(), song.getTime(), song.getFilePath());
            } else {
                throw new MusicException("Creating song failed: No ID returned.");
            }

        } catch (SQLException e) {
            throw new MusicException("Could not create song in the database", e);
        }
    }
}