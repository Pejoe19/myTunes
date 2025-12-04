package dk.easv.mytunes.BLL;

public class MusicManager {

    private double volume;
    private boolean isMuted;

    public MusicManager() {
        this.volume = 0.33;
        this.isMuted = false;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public boolean toogleMute() {
        isMuted = !isMuted;
        return isMuted;
    }

    @Override
    public String toString() {
        return "SoundManager{" +
                "volume=" + volume +
                ", isMuted=" + isMuted +
                '}';
    }
}
