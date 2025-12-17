package dk.easv.mytunes.BLL;

public class MusicException extends Exception {
    // Different constructors for the MusicException
    public MusicException(String message) { super(message); }

    public MusicException(String message, Throwable cause) { super(message, cause); }

    public MusicException(Throwable cause) { super(cause); }
}