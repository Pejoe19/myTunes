package dk.easv.mytunes.Be;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String category;
    private int time; // seconds
    private String filePath;

    public Song(int id, String title, String artist, String category, int time, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.filePath = filePath;
    }

    public Song(String title, String artist, String category, int time, String filePath) {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
