package dk.easv.mytunes.Be;


import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private int id;
    private String name;
    private int numberOfSongs = 0;
    private List<IndexSong> songList;
    private int playTime = 0; //seconds
    private String formattedTime;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        songList = new ArrayList<>();
    }

    public Playlist(int id, String name, int numberOfSongs, int playTime) {
        this.id = id;
        this.name = name;
        this.numberOfSongs = numberOfSongs;
        this.playTime = playTime;
        this.formattedTime = getFormattedTime();
        songList = new ArrayList<>();
    }

    public String getFormattedTime() {
        int minutes = playTime / 60;
        int seconds = playTime % 60;
        return String.format("%d.%02d", minutes, seconds);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public List<IndexSong> getSongList() {
        return songList;
    }

    public void addSongToSongList(IndexSong song) {
        songList.add(song);
    }

    public void setSongList(ArrayList<IndexSong> songList) {
        this.songList.clear();
        this.songList.addAll(songList);
    }

    public void sortSongList() {
        songList.sort((a,b) -> {return a.getIndex()-b.getIndex();});
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
