package dk.easv.mytunes.BLL;

import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;
import dk.easv.mytunes.DAL.PlaylistDAO;
import dk.easv.mytunes.DAL.PlaylistsSongDAO;
import dk.easv.mytunes.DAL.SongDAO;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Logic {

    private final SongDAO songData = new SongDAO();
    private final PlaylistDAO playlistData = new PlaylistDAO();
    private final PlaylistsSongDAO playlistsSongData = new PlaylistsSongDAO();
    private final MusicManager musicManager = new MusicManager();

    public Logic() throws MusicException {
    }

    public List<Song> getSongs() throws MusicException {
        return songData.getAllSongs();
    }

    public List<Playlist> getPlaylists() throws MusicException {
        return playlistData.getPlaylists();
    }

    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistData.deletePlayList(playlist);
    }

    public Song updateSong(Song song) throws MusicException {
        return songData.updateSong(song);
    }

    public void deleteSong(Song song) throws MusicException {
        songData.deleteSong(song);
    }

    public Playlist updatePlaylist(Playlist playlist) throws MusicException {
        return playlistData.updatePlaylist(playlist);
    }

    public ArrayList<IndexSong> getPlaylistsSong(Playlist playlist) throws Exception {
        return playlistsSongData.getPlaylistsSong(playlist);
    }

    public void removeSongFromPlaylist(Playlist playlist, IndexSong indexSong) throws Exception {
        playlistsSongData.removeSongFromPlaylist(playlist, indexSong.getIndex());
    }

    public void switchPlaylistSongs(Playlist playlist, int songPlacementId, int newPlacementId) throws MusicException {
        playlistsSongData.switchPlaylistSongs(playlist, songPlacementId, newPlacementId);
    }

        public Song createSong(Song song) throws MusicException {
            return songData.createSong(song);
    }

    public IndexSong addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        return playlistsSongData.addSongToPlaylist(playlist, song);
    }

    public void loadSongFile(Song song) throws Exception {
        songData.loadSongFile(song);
    }

    public void playMedia(Song song, Runnable onMediaEnd) throws MalformedURLException {
        musicManager.playMedia(song, onMediaEnd);
    }

    public boolean isMuted() {
        return musicManager.isMuted();
    }

    public void toogleMute() {
        musicManager.toogleMute();
    }

    public void setVolume(Number newValue) {
        if(musicManager.getMediaPlayer() != null) {
            musicManager.setVolume((Double) newValue);
        }
    }

}
