package sdokb.ui;

import java.util.ArrayList;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sdokb.SixDegreesOfKevinBacon.KevinBaconPropertyType;
import sdokb.game.KevinBaconGameStateManager;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;
import sdokb.game.DeadEndException;
import sdokb.game.IMDBObject;

/**
 * This class handles responses to user interactions of all sorts. Note that 
 * we are registering anonymous event handlers that forward handing to methods
 * inside this class.
 * 
 * @author Richard McKenna & ___________________
 */
public class KevinBaconEventHandler
{
    // THIS PROVIDES ACCESS TO ALL APPLICATION DATA AND UI
    // COMPONENTS, SO IT LET'S THE HANDLERS RESPOND APPROPRIATELY
    private KevinBaconUI ui;

    /**
     * Constructor that simply saves the ui for later.
     * 
     * @param initUI 
     */
    public KevinBaconEventHandler(KevinBaconUI initUI)
    {
        ui = initUI;
    }

    /**
     * This method responds to when the user wishes to switch between 
     * the Game, Stats, and Help screens.
     * 
     * @param uiState The ui state, or screen, that the user wishes
     * to switch to.
     */
    public void respondToSwitchScreenRequest(KevinBaconUI.UIState uiState)
    {
        // RELAY THE CHANGE TO THE UI
        ui.changeWorkspace(uiState);
    }
    
    /**
     * This method responds to when the user requests to go the help
     * screen's home page. It responds by loading that page.
     */
    public void respondToGoHomeRequest()
    {
        JEditorPane helpPage = ui.getHelpPane();
        ui.loadPage(helpPage, KevinBaconPropertyType.HELP_FILE_NAME);
    }
    
    /**
     * This method handles when the user selects a language for the application.
     * At that time we'll need to load language-specific controls.
     * 
     * @param language The language selected by the user.
     */
    public void respondToSelectLanguageRequest(String language)
    {
        // WE'LL NEED THESE TO INIT THE UI SCREENS
        KevinBaconGameStateManager gsm = ui.getGSM();
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // GET THE SELECTED LANGUAGE & IT'S XML FILE
        ArrayList<String> languages = props.getPropertyOptionsList(KevinBaconPropertyType.LANGUAGE_OPTIONS);
        ArrayList<String> languageData = props.getPropertyOptionsList(KevinBaconPropertyType.LANGUAGE_DATA_FILE_NAMES);
        int langIndex = languages.indexOf(language);
        String langDataFile = languageData.get(langIndex);
        String langSchema = props.getProperty(KevinBaconPropertyType.PROPERTIES_SCHEMA_FILE_NAME);
        try
        {
            // LOAD THE LANGUAGE SPECIFIC PROPERTIES
            props.loadProperties(langDataFile, langSchema);
                       
            // INITIALIZE THE USER INTERFACE WITH THE SELECTED LANGUAGE
            ui.initUI();
            
            // WE'LL START THE GAME TOO
            gsm.startNewGame();
        }
        catch(InvalidXMLFileFormatException ixmlffe)
        {
            ui.getErrorHandler().processError(KevinBaconPropertyType.INVALID_XML_FILE_ERROR_TEXT);
            System.exit(0);
        }        
    }

    /**
     * This method responds to when the user presses the
     * new game method. 
     */
    public void respondToNewGameRequest()
    {
        KevinBaconGameStateManager gsm = ui.getGSM();
        gsm.startNewGame();
    }

    /**
     * This method responds to when the user presses enter in
     * the guess text field.
     */
    public void respondToGuessRequest(IMDBObject guess)
    {
        KevinBaconGameStateManager gsm = ui.getGSM();

        // THEN PROCESS THE GUESS
        try
        {
            gsm.processGuess(guess);
        }
        catch(DeadEndException dee)
        {
            ui.getErrorHandler().processError(KevinBaconPropertyType.DEAD_END_GUESS_ERROR_TEXT);
        }
    }        

    /**
     * This method responds to when the user requests to exit the application.
     * 
     * @param window The window that the user has requested to close.
     */
    public void respondToExitRequest(JFrame window)
    {
        // ENGLIS IS THE DEFAULT
        String options[] = new String[]{"Yes", "No"};
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        options[0] = props.getProperty(KevinBaconPropertyType.DEFAULT_YES_TEXT);
        options[1] = props.getProperty(KevinBaconPropertyType.DEFAULT_NO_TEXT);
        String verifyExit = props.getProperty(KevinBaconPropertyType.DEFAULT_EXIT_TEXT);
        
        // NOW WE'LL CHECK TO SEE IF LANGUAGE SPECIFIC VALUES HAVE BEEN SET
        if (props.getProperty(KevinBaconPropertyType.YES_TEXT) != null)
        {
            options[0] = props.getProperty(KevinBaconPropertyType.YES_TEXT);
            options[1] = props.getProperty(KevinBaconPropertyType.NO_TEXT);
            verifyExit = props.getProperty(KevinBaconPropertyType.EXIT_REQUEST_TEXT);
        }
        
        // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
        int selection = JOptionPane.showOptionDialog(   window, 
                                                        verifyExit, 
                                                        verifyExit, 
                                                        JOptionPane.YES_NO_OPTION, 
                                                        JOptionPane.ERROR_MESSAGE,
                                                        null,
                                                        options,
                                                        null);
        // WHAT'S THE USER'S DECISION?
        if (selection == JOptionPane.YES_NO_OPTION)
        {
            // YES, LET'S EXIT
            System.exit(0);
        }
    }    
}