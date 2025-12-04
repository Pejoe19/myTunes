package dk.easv.mytunes.Be;

import java.io.File;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String category;
    private int time; // seconds
    private File file;

    public Song(int id, String title, String artist, String category, int time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
    }

    public Song(String title, String artist, String category, int time) {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
    }

    public String getFormattedTime() {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%d.%02d", minutes, seconds);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public File getFile() { return file; }

    public void setFile(File file) { this.file = file; }


}
