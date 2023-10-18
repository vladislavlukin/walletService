package walletService.exceptions;

public class DatabaseConnectionException extends Exception{
    public DatabaseConnectionException(String message) {
        super(message);
    }
}
