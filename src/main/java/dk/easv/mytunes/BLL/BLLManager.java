package dk.easv.mytunes.BLL;

// program imports
import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;
import dk.easv.mytunes.DAL.PlaylistDAO;
import dk.easv.mytunes.DAL.PlaylistsSongDAO;
import dk.easv.mytunes.DAL.SongDAO;

// java imports
import java.util.ArrayList;
import java.util.List;

public class BLLManager {

    // Instance variables
    private final SongDAO songData = new SongDAO();
    private final PlaylistDAO playlistData = new PlaylistDAO();
    private final PlaylistsSongDAO playlistsSongData = new PlaylistsSongDAO();
    private final MusicManager musicManager = new MusicManager();

    // Constructor
    public BLLManager() throws MusicException {
    }

    /**
     * Gets the songs from the data layer
     * @return a list of songs
     * @throws MusicException if something goes wrong
     */
    public List<Song> getSongs() throws MusicException {
        return songData.getAllSongs();
    }

    /**
     * Gets the playlists from the data layer
     * @return a list of songs
     * @throws MusicException if something goes wrong
     */
    public List<Playlist> getPlaylists() throws MusicException {
        return playlistData.getPlaylists();
    }

    /**
     * Tell the data layer to delete the param playlist
     * @param playlist to be deleted
     * @throws MusicException if something goes wrong
     */
    public void deletePlaylist(Playlist playlist) throws MusicException {
        playlistData.deletePlayList(playlist);
    }

    /**
     * Tells the data layer to update the param song
     * @param song the updated song
     * @return the updated song
     * @throws MusicException if something goes wrong
     */
    public Song updateSong(Song song) throws MusicException {
        return songData.updateSong(song);
    }

    /**
     * Tells the data layer to delete the param song from the application
     * @param song to be deleted
     * @throws MusicException if something goes wrong
     */
    public void deleteSong(Song song) throws MusicException {
        songData.deleteSong(song);
    }

    /**
     * Tells the data layer to update the param playlist
     * @param playlist to be updated to
     * @return updated playlist
     * @throws MusicException if something goes wrong
     */
    public Playlist updatePlaylist(Playlist playlist) throws MusicException {
        return playlistData.updatePlaylist(playlist);
    }

    /**
     * Get the songs on the param playlist from the data layer
     * @param playlist the playlist to get songs from
     * @return an arraylist of indexSongs
     * @throws MusicException if something goes wrong
     */
    public ArrayList<IndexSong> getPlaylistsSong(Playlist playlist) throws MusicException {
        return playlistsSongData.getPlaylistsSong(playlist);
    }

    /**
     * Tells the data layer to remove the param song from the param playlist
     * @param playlist to remove from
     * @param indexSong to be removed
     * @throws MusicException if something goes wrong
     */
    public void removeSongFromPlaylist(Playlist playlist, IndexSong indexSong) throws MusicException {
        playlistsSongData.removeSongFromPlaylist(playlist, indexSong.getIndex());
    }

    /**
     * Tell the data layer to switch the placement of the two param songs on the param playlist
     * @param playlist where the switch have to occur
     * @param songPlacementId the placementId of the first song
     * @param newPlacementId the placementId of the second song
     * @throws MusicException if something goes wrong
     */
    public void switchPlaylistSongs(Playlist playlist, int songPlacementId, int newPlacementId) throws MusicException {
        playlistsSongData.switchPlaylistSongs(playlist, songPlacementId, newPlacementId);
    }

    /**
     * Tells the data layer to create the param song
     * @param song to be created
     * @return the song that has been created
     * @throws MusicException if something goes wrong
     */
    public Song createSong(Song song) throws MusicException {
        return songData.createSong(song);
    }

    /**
     * Tells the data layer to add param song to param playlist
     * @param playlist the playlist to add the song to
     * @param song the song to be added
     * @return the indexSong that has been created
     * @throws MusicException if something goes wrong
     */
    public IndexSong addSongToPlaylist(Playlist playlist, Song song) throws MusicException {
        return playlistsSongData.addSongToPlaylist(playlist, song);
    }

    /**
     * Tell the data layer to load the param song
     * @param song to be loaded
     * @throws MusicException if something goes wrong
     */
    public void loadSongFile(Song song) throws MusicException {
        songData.loadSongFile(song);
    }

    /**
     * Tell the musicManager to play the param song and the param song to pla next
     * @param song to play now
     * @param onMediaEnd song to play afterward
     * @throws MusicException if something goes wrong
     */
    public void playMedia(Song song, Runnable onMediaEnd) throws MusicException {
        musicManager.playMedia(song, onMediaEnd);
    }

    /**
     * Checks the musicManger for if the music is muted
     * @return boolean for whether the music is muted or not
     */
    public boolean isMuted() {
        return musicManager.isMuted();
    }

    /**
     *Tells the musicManager to toggle mute
     */
    public void toogleMute() {
        musicManager.toggleMute();
    }

    /**
     * Tells the music manager to change the volume to the param value
     * @param newValue the new value to set the volume
     */
    public void setVolume(Number newValue) {
        if(musicManager.getMediaPlayer() != null) {
            musicManager.setVolume((Double) newValue);
        }
    }

    /**
     * Tells the data layer to create the param playlist
     * @param playlist to be created
     * @return the created playlist
     * @throws MusicException if something goes wrong
     */
    public Playlist createPlaylist(Playlist playlist) throws MusicException {
        return playlistData.createPlaylist(playlist);
    }

    /**
     * Checks the musicManager whether the music is playing
     * @return a boolean for if the music is playing
     * @throws MusicException if something goes wrong
     */
    public boolean isPlaying() throws MusicException {
        return musicManager.isPlaying();
    }

    /**
     * Checks the musicManager whether the music is paused
     * @return a boolean for if the music is paused
     * @throws MusicException if something goes wrong
     */
    public boolean isPaused() throws MusicException {
        return musicManager.isPaused();
    }

    /**
     * Pauses the music in the musicManager
     * @throws MusicException if something goes wrong
     */
    public void pause() throws MusicException {
        musicManager.pause();
    }

    /**
     * Resumes the music in the musicManager
     * @throws MusicException if something goes wrong
     */
    public void resume() throws MusicException {
        musicManager.resume();
    }
}
