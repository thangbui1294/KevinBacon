package sdokb;

import java.io.IOException;
import sdokb.ui.KevinBaconUI;
import sdokb.ui.KevinBaconErrorHandler;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;
import sdokb.file.KevinBaconFileUtilities;

/**
 * Six Degrees of Kevin Bacon is a game based on the premise that
 * one can reach any Hollywood actor via an Actor-to-Film-to-Actor
 * graph data structure using six or less edges. This version of 
 * the game will be played using only Academy Award-winning actors,
 * meaning those who have won best actor/actress and/or best supporting
 * actor/actress. To start, the player will be given the name of an actor, 
 * and the player will have to walk the graph by selecting films 
 * and actors and wins by reaching Kevin Bacon. The player loses when
 * ending up in a dead end film that doesn't connect to any actors
 * who have not already been used.
 * 
 * @author Richard McKenna & Thang Bui
 */
public class SixDegreesOfKevinBacon
{
    // THIS HAS THE FULL USER INTERFACE AND ONCE IN EVENT
    // HANDLING MODE, BASICALLY IT BECOMES THE FOCAL
    // POINT, RUNNING THE UI AND EVERYTHING ELSE
    static KevinBaconUI ui = new KevinBaconUI();
    
    // WE'LL LOAD ALL THE UI AND LANGUAGE PROPERTIES FROM FILES,
    // BUT WE'LL NEED THESE VALUES TO START THE PROCESS
    static String PROPERTY_TYPES_LIST = "property_types.txt";
    static String UI_PROPERTIES_FILE_NAME = "properties.xml";
    static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";    
    static String DATA_PATH = "./data/";

    /**
     * This is where the game application starts execution. We'll
     * load the application properties and then use them to build our
     * user interface and start the window in event handling mode. Once
     * in that mode, all code execution will happen in response to a 
     * user request.
     * 
     * @param args This application does not use any command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(KevinBaconPropertyType.UI_PROPERTIES_FILE_NAME, UI_PROPERTIES_FILE_NAME);
            props.addProperty(KevinBaconPropertyType.PROPERTIES_SCHEMA_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            props.addProperty(KevinBaconPropertyType.DATA_PATH.toString(), DATA_PATH);
            props.loadProperties(UI_PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            KevinBaconFileUtilities.loadActorsAndFilms(ui.getGSM().getGameGraphManager());
                               
            // NOW START THE UI IN EVENT HANDLING MODE
            ui.startUI();
        }
        // THERE WAS A PROBLEM LOADING THE PROPERTIES FILE
        catch(InvalidXMLFileFormatException ixmlffe)
        {
            // LET THE ERROR HANDLER PROVIDE THE RESPONSE
            KevinBaconErrorHandler errorHandler = ui.getErrorHandler();
            errorHandler.processError(KevinBaconPropertyType.INVALID_XML_FILE_ERROR_TEXT);
        }
        catch(IOException ioe)
        {
            // LET THE ERROR HANDLER PROVIDE THE RESPONSE
            KevinBaconErrorHandler errorHandler = ui.getErrorHandler();
            errorHandler.processError(KevinBaconPropertyType.DATA_FILE_LOADING_ERROR_TEXT);
            System.exit(0);
        }
    }
    
    /**
     * SixDegreesOfKevinBaconPropertyType represents the types of data 
     * that will need to be extracted from XML files. Using XML properties 
     * files makes it easy to switch between languages, which is important
     * if one wants to maximize the number of users for an application.
     */
    public enum KevinBaconPropertyType
    {
        /* SETUP FILE NAMES */
        UI_PROPERTIES_FILE_NAME,
        PROPERTIES_SCHEMA_FILE_NAME,

        /* DIRECTORIES FOR FILE LOADING */
        DATA_PATH,
        IMG_PATH,
        
        /* WINDOW DIMENSIONS */
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        
        /* LANGUAGE OPTIONS PROPERTIES */
        LANGUAGE_OPTIONS,
        LANGUAGE_DATA_FILE_NAMES,
        LANGUAGE_IMAGE_NAMES,
        
        /* GAME TEXT */
        SPLASH_SCREEN_TITLE_TEXT,
        GAME_TITLE_TEXT,
        GAME_SUBHEADER_TEXT,
        WIN_DISPLAY_TEXT,
        LOSS_DISPLAY_TEXT,
        GAME_RESULTS_TEXT,
        ACTOR_LABEL,
        FILM_LABEL,
        SELECT_LABEL,
        EXIT_REQUEST_TEXT,
        YES_TEXT,
        NO_TEXT,
        DEFAULT_YES_TEXT,
        DEFAULT_NO_TEXT,
        DEFAULT_EXIT_TEXT,

        /* IMAGE FILE NAMES */
        WINDOW_ICON,
        SPLASH_SCREEN_IMAGE_NAME,
        GAME_IMG_NAME,
        STATS_IMG_NAME,
        HELP_IMG_NAME,
        EXIT_IMG_NAME,
        NEW_GAME_IMG_NAME,
        HOME_IMG_NAME,
        
        /* DATA FILE STUFF */
        GAME_FILE_NAME,
        STATS_FILE_NAME,
        HELP_FILE_NAME,
        ACTORS_FILE_NAME,
        FILMS_FILE_NAME,
        
        /* TOOLTIPS */
        GAME_TOOLTIP,
        STATS_TOOLTIP,
        HELP_TOOLTIP,
        EXIT_TOOLTIP,
        NEW_GAME_TOOLTIP,
        HOME_TOOLTIP,
        
        /* FONT DATA */
        GUESSES_FONT_FAMILY,
        GUESSES_FONT_SIZE,        
        
        /* THESE ARE FOR LANGUAGE-DEPENDENT ERROR HANDLING,
           LIKE FOR TEXT PUT INTO DIALOG BOXES TO NOTIFY
           THE USER WHEN AN ERROR HAS OCCURED */
        ERROR_DIALOG_TITLE_TEXT,
        DEAD_END_GUESS_ERROR_TEXT,
        IMAGE_LOADING_ERROR_TEXT,
        INVALID_URL_ERROR_TEXT,
        INVALID_DOC_ERROR_TEXT,
        INVALID_XML_FILE_ERROR_TEXT,
        DATA_FILE_LOADING_ERROR_TEXT
    }
}