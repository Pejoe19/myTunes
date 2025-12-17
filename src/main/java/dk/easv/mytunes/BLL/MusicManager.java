package dk.easv.mytunes.BLL;

// program imports
import dk.easv.mytunes.Be.Song;

// java imports
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import java.net.MalformedURLException;

public class MusicManager {

    // Instance variables
    private MediaPlayer mediaPlayer;
    private double volume;
    private boolean isMuted;

    // Constructor
    public MusicManager() {
        this.volume = 0.33;
        this.isMuted = false;
    }

    /**
     * Plays the param song and set the song to be played next
     * @param song to be played now
     * @param onMediaEnd To be played afterward
     * @throws MusicException if something goes wrong
     */
    public void playMedia(Song song, Runnable onMediaEnd) throws MusicException {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        Media media = null;
        try {
            media = new Media(song.getFile().toURL().toString());
        } catch (MalformedURLException e) {
            throw new MusicException("URL Malformatted");
        }
        mediaPlayer = new MediaPlayer(media);
        setVolume(volume);
        mediaPlayer.setMute(isMuted());
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(onMediaEnd);
    }

    /**
     * Returns the mediaPlayer
     * @return mediaPlayer
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Changes the volume to the param volume value
     * @param volume value to set the volume to
     */
    public void setVolume(double volume) {
        this.volume = volume;
        mediaPlayer.setVolume(volume);
    }

    /**
     * Returns if the music is muted
     * @return boolean
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Toggle music mute
     */
    public void toggleMute() {
        if(mediaPlayer != null){
            isMuted = !isMuted;
            mediaPlayer.setMute(isMuted);
        }
    }

    /**
     * Returns a string version of the instance variables for the musicManager
     * @return String
     */
    @Override
    public String toString() {
        return "SoundManager{" +
                "volume=" + volume +
                ", isMuted=" + isMuted +
                '}';
    }

    /**
     * Tells if the music is playing
     * @return boolean
     */
    public boolean isPlaying(){
        return mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING;
    }

    /**
     * Tells if the music is paused
     * @return boolean
     */
    public boolean isPaused(){
        return mediaPlayer != null && mediaPlayer.getStatus() == Status.PAUSED;
    }

    /**
     * Pauses the music
     */
    public void pause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
    }

    /**
     * Resumes the music
     */
    public void resume(){
        if(mediaPlayer != null){
            mediaPlayer.play();
        }
    }
}
