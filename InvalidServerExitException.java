
package chatroom.supplements;

/* This exception happens when someone tries to close the server when
   there are clients still connected. Clients need connection!!!! */
public class InvalidServerExitException extends Exception {
    public InvalidServerExitException(String message) {
        super(message);
    }
}

