package dk.easv.mytunes.BLL;

import dk.easv.mytunes.Be.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.MalformedURLException;

public class MusicManager {
    private MediaPlayer mediaPlayer;
    private double volume;
    private boolean isMuted;

    public MusicManager() {
        this.volume = 0.33;
        this.isMuted = false;
    }

    public void playMedia(Song song) throws MalformedURLException {
        Media media = new Media(song.getFile().toURL().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setVolume(double volume) {
        this.volume = volume;
        mediaPlayer.setVolume(volume);
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void toogleMute() {
        if(mediaPlayer != null){
            isMuted = !isMuted;
            mediaPlayer.setMute(isMuted);
        }
    }

    public void stopMedia() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    @Override
    public String toString() {
        return "SoundManager{" +
                "volume=" + volume +
                ", isMuted=" + isMuted +
                '}';
    }
}
