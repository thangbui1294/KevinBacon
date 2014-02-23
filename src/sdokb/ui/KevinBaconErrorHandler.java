package sdokb.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sdokb.SixDegreesOfKevinBacon.KevinBaconPropertyType;
import properties_manager.PropertiesManager;

/**
 * This class handles any expected problems that could happen
 * during the execution of the game. For example when
 * improperly XML files are loaded or the user provides improper
 * input.
 * 
 * @author Richard McKenna & _______________________
 */
public class KevinBaconErrorHandler
{
    // WE'LL CENTER DIALOG BOXES OVER THE WINDOW, SO WE NEED THIS
    private JFrame window;

    /**
     * This simple little class just needs the window.
     * 
     * @param initWindow 
     */
    public KevinBaconErrorHandler(JFrame initWindow)
    {
        // KEEP THE WINDOW FOR LATER
        window = initWindow;
    }

    /**
     * This method provides all error feedback. It gets the feedback text,
     * which changes depending on the type of error, and presents it to
     * the user in a dialog box.
     * 
     * @param errorType Identifies the type of error that happened, which
     * allows us to get and display different text for different errors.
     */
    public void processError(KevinBaconPropertyType errorType)
    {
        // GET THE FEEDBACK TEXT
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String errorFeedbackText = props.getProperty(errorType);
        
        // NOTE THAT WE'LL USE THE SAME DIALOG TITLE FOR ALL ERROR TYPES
        String errorTitle = props.getProperty(KevinBaconPropertyType.ERROR_DIALOG_TITLE_TEXT);
        
        // POP OPEN A DIALOG TO DISPLAY TO THE USER
        JOptionPane.showMessageDialog(window, errorFeedbackText, errorTitle, JOptionPane.ERROR_MESSAGE);
    }
}