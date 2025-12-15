package dk.easv.mytunes.BLL;

import dk.easv.mytunes.Be.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import java.net.MalformedURLException;

public class MusicManager {

    // Instance variables
    private MediaPlayer mediaPlayer;
    private double volume;
    private boolean isMuted;

    public MusicManager() {
        this.volume = 0.33;
        this.isMuted = false;
    }

    public void playMedia(Song song, Runnable onMediaEnd) throws MalformedURLException {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        Media media = new Media(song.getFile().toURL().toString());
        mediaPlayer = new MediaPlayer(media);
        setVolume(volume);
        mediaPlayer.setMute(isMuted());
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(onMediaEnd);
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

    @Override
    public String toString() {
        return "SoundManager{" +
                "volume=" + volume +
                ", isMuted=" + isMuted +
                '}';
    }

    public boolean isPlaying(){
        return mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING;
    }

    public boolean isPaused(){
        return mediaPlayer != null && mediaPlayer.getStatus() == Status.PAUSED;
    }

    public void pause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
    }

    public void resume(){
        if(mediaPlayer != null){
            mediaPlayer.play();
        }
    }
}
