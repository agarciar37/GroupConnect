package err;

public class UsernameExistsException extends Exception{
    public UsernameExistsException(String message){
        super(message);
    }
}
