import java.io.IOException;

public class CamUnavailableException extends Exception{
    CamUnavailableException() {
        super("Camera is not available Right now", new IOException("Unable to fetch Picture data"));
    }
}
