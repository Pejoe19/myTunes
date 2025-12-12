package dk.easv.mytunes.Be;

import java.io.File;

public class Song {
    // Instance variables
    private int id;
    private String title;
    private String artist;
    private String category;
    private int time; // seconds
    private File file;

    public Song(int id, String title, String artist, String category, File file, int time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.file = file;
    }

    public Song(int id, String title, String artist, String category, int time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
    }

    public Song(String title, String artist, String category, File file, int time) {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.file = file;
    }

    public String getFormattedTime() {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%d.%02d", minutes, seconds);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCategory() {
        return category;
    }

    public int getTime() {
        return time;
    }

    public File getFile() { return file; }

    public void setFile(File file) { this.file = file; }
}
