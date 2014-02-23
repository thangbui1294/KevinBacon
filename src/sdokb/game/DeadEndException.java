package sdokb.game;

import properties_manager.PropertiesManager;
import sdokb.SixDegreesOfKevinBacon.KevinBaconPropertyType;

/**
 * This exception represents the situation where the user has selected
 * a node that does not lead to any nodes that have not yet been visited. The
 * game cannot continue in such a situtation.
 * 
 * @author Richard McKenna & ___________________
 */
public class DeadEndException extends Exception
{
    // THIS WILL STORE INFORMATION ABOUT THE ILLEGAL
    // WORD THAT CAUSED THIS EXCEPTION
    private String deadEndGuess;

    /**
     * This constructor will keep the node guess information that led us
     * to this problem so that whoever catches this exception may use it in
     * providing informative feedback.
     * 
     * @param initDeadEndGuess The dead end actor or film.
     */
    public DeadEndException(String initDeadEndGuess)
    {
        // STORE THE DEAD END GUESS SO THAT WE MAY
        // PROVIDE FEEDBACK IF WE WISH
        deadEndGuess = initDeadEndGuess;
    }
    
    /**
     * This method returns a textual summary of this exception.
     * 
     * @return A textual summary of this exception, which can
     * be used to provide feedback.
     */
    @Override
    public String toString()
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String illegalGuessFeedback = props.getProperty(KevinBaconPropertyType.DEAD_END_GUESS_ERROR_TEXT);
        return deadEndGuess + illegalGuessFeedback;
    }
}