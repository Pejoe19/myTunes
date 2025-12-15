package dk.easv.mytunes.GUI;

import dk.easv.mytunes.BLL.Logic;
import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;
import dk.easv.mytunes.DAL.PlaylistDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class Model {

    // Instance variables
    private final Logic logic = new Logic();
    private ObservableList<Song> songs;
    private ObservableList<Playlist> playlists;
    private ObservableList<IndexSong> activePlaylist;
    private int currentSongIndex = -1;
    private boolean playingFromPlaylist = false;

    public Model() throws MusicException {
    }

    public ObservableList<Song> loadSongs() throws MusicException {
        songs = FXCollections.observableArrayList(logic.getSongs());
        return songs;
    }

    public void createSong(Song song) throws MusicException {
        Song created = logic.createSong(song);
        songs.add(created);
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public ObservableList<Playlist> loadPlaylists() throws MusicException {
        // Gets the data from logic
        playlists = FXCollections.observableList(logic.getPlaylists());
        return playlists;
    }

    public ObservableList<IndexSong> initializeActivePlayList() {
        activePlaylist = FXCollections.observableList(new ArrayList<>());
        return activePlaylist;
    }

    public void displayPlaylist(Playlist playlist) throws MusicException {
        activePlaylist.clear();

        // Always fetch the songs for this playlist from DB
        ArrayList<IndexSong> playlistSongs = logic.getPlaylistsSong(playlist);

        // Link each IndexSong to the correct Song from the loaded song list
        for (IndexSong indexSong : playlistSongs) {
            int songId = indexSong.getSong().getId();

            // Try to find the real song from the main library list
            Song realSong = songs.stream()
                    .filter(s -> s.getId() == songId)
                    .findFirst()
                    .orElse(null);

            // Replace placeholder with the real Song
            if (realSong != null) {
                indexSong.setSong(realSong);
            }
        }

        // Sort songs in playlist and refresh view
        playlist.setSongList(playlistSongs);
        playlist.sortSongList();
        activePlaylist.addAll(playlist.getSongList());
    }

    public void clearActivePlaylist() { activePlaylist.clear();}

    public int updateSong(Song song) throws MusicException {
        //
        Song updatedSong = logic.updateSong(song);

        //Applies the update in the observable list
        if(updatedSong != null){
            for(int i = 0; i < songs.size(); i++){
                Song currentSong = (Song) songs.get(i);
                if(currentSong.getId() == song.getId()){
                    songs.remove(i);
                    songs.add(i, updatedSong);
                    return i;
                }
            }
        }
        return 0;
    }

    public void deletePlaylist(Playlist playlist) throws MusicException {
        logic.deletePlaylist(playlist);
        playlists.remove(playlist);
    }

    public void deleteSong(Song song) throws MusicException {
        logic.deleteSong(song);
        songs.remove(song);
    }

    public void updatePlaylist(Playlist playlist) throws MusicException {
        Playlist updated = logic.updatePlaylist(playlist);
        if (updated != null) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).getId() == playlist.getId()) {
                    playlists.set(i, updated);
                    break;
                }
            }
        }
    }

    public void setCurrentlyPlayingSong(Song song, boolean fromPlaylist) {
        if (song == null) return;
        // Husk hvilken sang der er den aktuelle
        currentSong = song;
        // Husk hvor vi spiller fra (playlist eller bibliotek)
        playingFromPlaylist = fromPlaylist;
        if (fromPlaylist) {
            // Søg efter sangen i den aktive playlist
            if (activePlaylist != null && !activePlaylist.isEmpty()) {
                for (int i = 0; i < activePlaylist.size(); i++) {
                    IndexSong indexSong = activePlaylist.get(i);
                    if (indexSong.getSong() != null && indexSong.getSong().getId() == song.getId()) {
                        currentSongIndex = i;
                        return;
                    }
                }
            }
        } else {
            // Søg efter sangen i biblioteket (songs-listen)
            if (songs != null && !songs.isEmpty()) {
                for (int i = 0; i < songs.size(); i++) {
                    Song s = songs.get(i);
                    if (s != null && s.getId() == song.getId()) {
                        currentSongIndex = i;
                        return;
                    }
                }
            }
        }
    }

    private Song currentSong; // den sang der faktisk afspilles / er sat som "current"

    public Song getCurrentSong() {
        return currentSong;
    }

    public Song getNextSong() {
        if (playingFromPlaylist && activePlaylist != null && !activePlaylist.isEmpty()) {
            if (currentSongIndex < activePlaylist.size() - 1) {
                currentSongIndex++;
                currentSong = activePlaylist.get(currentSongIndex).getSong();
                return currentSong;
            }
        } else if (!playingFromPlaylist && songs != null && !songs.isEmpty()) {
            if (currentSongIndex < songs.size() - 1) {
                currentSongIndex++;
                currentSong = songs.get(currentSongIndex);
                return currentSong;
            }
        }
        return null;
    }

    public Song getPreviousSong() {
        if (playingFromPlaylist && activePlaylist != null && !activePlaylist.isEmpty()) {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                currentSong = activePlaylist.get(currentSongIndex).getSong();
                return currentSong;
            }
        } else if (!playingFromPlaylist && songs != null && !songs.isEmpty()) {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                currentSong = songs.get(currentSongIndex);
                return currentSong;
            }
        }
        return null;
    }

    public void removeSongFromPlaylist(Playlist playlist, IndexSong indexSong) throws MusicException {
        logic.removeSongFromPlaylist(playlist, indexSong);
    }

    public void switchPlaylistOrder(Playlist playlist, int songPlacementId, int newPlacementId) throws MusicException {
        logic.switchPlaylistSongs(playlist, songPlacementId, newPlacementId);

        IndexSong song = activePlaylist.get(songPlacementId);
        activePlaylist.remove(song);
        activePlaylist.add(newPlacementId, song);
    }

    public void addSongToPlaylist(Playlist playlist, Song song) throws MusicException {
        IndexSong addedSong = logic.addSongToPlaylist(playlist, song);
        activePlaylist.add(addedSong);
    }

    public void loadAndPlayMedia (Song song, Runnable onMediaEnd) throws MusicException {
        if (song.getFile() == null || !song.getFile().exists()) {
            logic.loadSongFile(song);
        }
        if(song.getFile() != null) {
            logic.playMedia(song, onMediaEnd);
        }
        else {
            onMediaEnd.run();
        }
    }

    public void loadSongFile(Song song) throws Exception {
        logic.loadSongFile(song);
    }

    public boolean isMuted() {
        return logic.isMuted();
    }

    public void toogleMute() {
        logic.toogleMute();
    }

    public void setVolume(Number newValue) {
        logic.setVolume(newValue);
    }

    public void createPlaylist(Playlist playlist) throws MusicException {
        Playlist created = logic.createPlaylist(playlist);
        playlists.add(created);
    }

    public boolean isPlaying() throws MusicException {
        return logic.isPlaying();
    }

    public boolean isPaused() throws MusicException {
        return logic.isPaused();
    }

    public void pause() throws MusicException {
        logic.pause();
    }

    public void resume() throws MusicException {
        logic.resume();
    }
}
