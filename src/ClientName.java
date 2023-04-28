import java.io.Serializable;

/**
 * 
 * This class represents a client name object and implements Serializable
 * interface for object serialization.
 * 
 * @author Janko Leskovac and Kristijan Nincevic
 * @version 1.0
 * @since 2023-04-20
 */

public class ClientName implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;

    /**
     * Constructs a client name object with a given name.
     * 
     * @param name the name of the client
     */
    public ClientName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the client.
     * 
     * @return the name of the client
     */
    public String getName() {
        return name;
    }
}
