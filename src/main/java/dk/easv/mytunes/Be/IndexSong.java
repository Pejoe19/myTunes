package dk.easv.mytunes.Be;

public class IndexSong {
    private Song song;
    private int index;

    public IndexSong(Song song, int index) {
        this.index = index;
        this.song = song;
    }
    public Song getSong() {
        return song;
    }
    public void setSong(Song song) {
        this.song = song;
    }
    public int getIndex() {
        return index;
    }
    public String getTitle() { return song != null ? song.getTitle() : "";}
    public String getArtist() { return song != null ? song.getArtist() : "";}
    public String getCategory() { return song != null ? song.getCategory() : "";}
    public String getFormattedTime() {return song != null ? song.getFormattedTime() : "";}
    public int getId() {
        return song.getId();
    }
}
