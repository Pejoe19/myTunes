package dk.easv.mytunes.DAL;

import dk.easv.mytunes.Be.Song;
import dk.easv.mytunes.BLL.MusicException;

import java.io.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    // Instance variables
    private final DBConnector dbConnector = new DBConnector();

    public SongDAO() throws MusicException {
    }

    /**
     * get all the song from the database without the file
     * @return list of song
     * @throws MusicException
     */
    public List<Song> getAllSongs() throws MusicException {
        List<Song> songs = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT Id, Title, Artist, Category, Time FROM Songs";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String category = rs.getString("Category");
                LocalTime localTime = rs.getTime("Time").toLocalTime();
                int time = localTime.toSecondOfDay();

                songs.add(new Song(id, title, artist, category, time));
            }

        } catch (Exception e) {
            throw new MusicException("Could not load songs from DB", e);
        }
        return songs;
    }


    /**
     * update a song on id with title, artist, category and time and optional a file
     * @param song the song with the changes
     * @return the update song
     * @throws MusicException
     */
    public Song updateSong(Song song) throws MusicException {
        try (Connection conn = dbConnector.getConnection())
        {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dbo.Songs " +
                            (song.getFile() != null ? "SET Title = ?, Artist = ?, Category = ?, Time = ?, [File] = ? ":"SET Title = ?, Artist = ?, Category = ?, Time = ? ") +
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
            if(song.getFile() != null) {
                FileInputStream fileInputStream = new FileInputStream(song.getFile());
                ps.setBinaryStream(5,fileInputStream);
                ps.setInt(6, song.getId());
            }
            else {
                ps.setInt(5, song.getId());
            }
            ps.executeUpdate();

            return song;

        } catch (SQLException | FileNotFoundException ex) {
            if(ex.getClass().getName().equals(FileNotFoundException.class.getName())) {
                throw new MusicException("something is wrong with the song file", ex);
            }
            throw new MusicException("Could not update the song in the database");
        }
    }

    /**
     * delete the song on id in the database
     * @param song to delete
     * @throws MusicException
     */
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

    /**
     * this writ a song to the database with title, artist, category and time, and optional a file
     * @param song the song to write to the database
     * @return the song with the id
     * @throws MusicException
     */
    public Song createSong(Song song) throws MusicException {
        String sql;
        if(song.getFile() != null)
            sql = "INSERT INTO dbo.Songs (Title, Artist, Category, Time, [File]) VALUES (?, ?, ?, ?, ?)";
        else
            sql = "INSERT INTO dbo.Songs (Title, Artist, Category, Time) VALUES (?, ?, ?, ?)";

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
            if(song.getFile() != null) {
                FileInputStream fileInputStream = new FileInputStream(song.getFile());
                ps.setBinaryStream(5,fileInputStream);
            }

            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);
                return new Song(newId, song.getTitle(), song.getArtist(), song.getCategory(), song.getTime());
            } else {
                throw new MusicException("Creating song failed: No ID returned.");
            }

        } catch (SQLException | FileNotFoundException e) {
            if(e.getClass().getName().equals(FileNotFoundException.class.getName())) {
                throw new MusicException("something is wrong with the song file", e);
            }
            throw new MusicException("Could not create song in the database", e);
        }
    }

    /**
     * load the file of a song from the database
     * @param song the song to load the file for
     * @throws MusicException
     */
    public void loadSongFile(Song song) throws MusicException {
        // Skip if already loaded or cached
        if (song.getFile() != null && song.getFile().exists()) {
            return;
        }
        // Define cache folder
        File tempDir = new File("src/main/resources/temp/");
        if (!tempDir.exists()) tempDir.mkdirs();
        // Use a consistent filename
        File cachedFile = new File(tempDir, song.getTitle().replaceAll(" ", "_"));
        // If cached version exists, use it directly
        if (cachedFile.exists()) {
            song.setFile(cachedFile);
            return;
        }
        // Otherwise, load it from the database once
        String SQL = "SELECT [File] FROM Songs WHERE Id = ?";
        try (Connection conn = DBConnector.getStaticConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, song.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("file");
                if (inputStream != null) {
                    try (OutputStream outputStream = new FileOutputStream(cachedFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        song.setFile(cachedFile);
                    }
                }
            }
        } catch (Exception e) {
            throw new MusicException("Failed to load song file from database", e);
        }
    }
}