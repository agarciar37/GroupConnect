package err;

public class EmailExistsException extends Exception{
    public EmailExistsException(String message) {
        super(message);
    }
}
