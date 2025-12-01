package dk.easv.mytunes.GUI;

import dk.easv.mytunes.BLL.Logic;
import dk.easv.mytunes.BLL.MusicException;
import dk.easv.mytunes.Be.IndexSong;
import dk.easv.mytunes.Be.Playlist;
import dk.easv.mytunes.Be.Song;
import dk.easv.mytunes.DAL.PlaylistDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final Logic logic = new Logic();
    private final PlaylistDAO playlistDAO = new PlaylistDAO();
    private ObservableList<Song> songs;
    private ObservableList<Playlist> playlists;
    private ObservableList<IndexSong> activePlaylist;

    public Model() throws MusicException {
    }

    public ObservableList<Song> loadSongs() throws MusicException {
        songs = FXCollections.observableArrayList(logic.getSongs());
        return songs;
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

    public void displayPlaylist(Playlist playlist) throws Exception {
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

    public void deletePlaylist(Playlist playlist) throws Exception {
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

    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        logic.removeSongFromPlaylist(playlist, song);
    }

    public void switchPlaylistOrder(Playlist playlist, int songPlacementId, int newPlacementId) throws MusicException {
        logic.switchPlaylistSongs(playlist, songPlacementId, newPlacementId);

        IndexSong song = activePlaylist.get(songPlacementId);
        activePlaylist.remove(song);
        activePlaylist.add(newPlacementId, song);
    }
}
