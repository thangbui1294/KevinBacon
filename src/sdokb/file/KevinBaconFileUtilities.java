package sdokb.file;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import sdokb.SixDegreesOfKevinBacon.KevinBaconPropertyType;
import properties_manager.PropertiesManager;
import sdokb.game.Actor;
import sdokb.game.Film;
import sdokb.game.KevinBaconGameGraphManager;

/**
 * The SixDegreesOfKevinBaconFileUtilities class provides utilities for loading
 * different types of files. Static methods for loading images, text files, html
 * files, and data files for the actors and films are provided. Note that these
 * are just one and done functions, this class has no instance or static
 * variables.
 *
 * @author Richard McKenna & ____________________
 */
public class KevinBaconFileUtilities
{
    public static String SPACE = " ";
    public static String NEW_LINE = "\n";
    public static String DELIMITER = "|";
    public static String LOADING_DATA_PREFIX = "Loading Data for ";

    /**
     * This method generically loads a file into a byte array. This is useful
     * for loading large files where the all the data can be loaded all at once
     * into RAM, and then parsed.
     *
     * @param fileToLoad File to load with the full path.
     * @return A ByteArrayInputStream that can read all the bytes in memory,
     * which have already been loaded from the file.
     * @throws IOException Occurs when there is a problem reading the file.
     */
    public static ByteArrayInputStream loadFile(String fileToLoad)
            throws IOException
    {
        // OPEN THE FILE TO READ
        File fileToOpen = new File(fileToLoad);

        // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
        // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
        // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
        byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        FileInputStream fis = new FileInputStream(fileToOpen);
        BufferedInputStream bis = new BufferedInputStream(fis);

        // HERE IT IS, THE ONLY READ REQUEST WE NEED
        bis.read(bytes);
        bis.close();

        // RETURN THE STREAM TO READ THE DATA IN RAM
        return bais;
    }

    /**
     * Loads and returns the fully loaded imageFile image
     *
     * @param window The frame that will be ultimately holding the image. We
     * need this for image loading because a JFrame can serve as an
     * image-loading monitor.
     *
     * @param imageFile Image file name, without the full path, of the image
     * resource to be loaded.
     *
     * @return A constructed, fully loaded image.
     *
     * @throws IOException is thrown when the image cannot be loaded, which is
     * likely due to an improper file name or path.
     */
    public static Image loadImage(String imageFile,
            JFrame window) throws IOException
    {
        // FIRST BUILD THE PATH TO THE IMAGE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        imageFile = props.getProperty(KevinBaconPropertyType.IMG_PATH) + imageFile;

        // START THE IMAGE LOADING - NOTE THAT THIS PROCESS HAPPENS
        // ASYNCHRONOUSLY, MEANING THE IMAGE LOADS AS A BACKGROUND
        // PROCESS IN PARALLEL, SO IF WE'RE NOT CAREFUL, THE IMAGE
        // WILL NOT BE LOADED YET WHEN WE NEED IT.
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage(imageFile);

        // WE'LL USE A MEDIA TRACKER TO MAKE SURE THE IMAGE IS
        // FULLY LOADED BEFORE WE LET OUR APP MOVE ON
        MediaTracker tracker = new MediaTracker(window);
        try
        {
            // THESE ARE IMPORTANT, WE ARE TELLING SWING'S MediaTracker
            // OBJECT THAT THIS METHOD SHOULD WAIT FOR ALL THE IMAGE
            // DATA TO BE LOADED FROM THE FILE BEFORE THIS METHOD CONTINUES
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (InterruptedException ie)
        {
            // LET'S REFLECT THIS EXCEPTION BACK AS AN IOException
            throw new IOException(ie.getMessage());
        }

        // IF THE IMAGE NEVER LOADED, WE'LL THROW AN EXCEPTION
        if ((img == null) || (img.getWidth(null) <= 0))
        {
            String errorMessage = props.getProperty(KevinBaconPropertyType.IMAGE_LOADING_ERROR_TEXT);
            throw new IOException(errorMessage);
        }

        // IF NO ERROR HAPPENED, RETURN THE FULLY LOADED IMAGE, WHICH CAN NOW BE USED
        return img;
    }

    /**
     * This method loads the complete contents of the textFile argument into a
     * String and returns it.
     *
     * @param textFile The name of the text file to load. Note that the path
     * will be added by this method.
     *
     * @return All the contents of the text file in a single String.
     *
     * @throws IOException This exception is thrown when textFile is an invalid
     * file or there is some problem in accessing the file.
     */
    public static String loadTextFile(String textFile) throws IOException
    {
        // ADD THE PATH TO THE FILE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        textFile = props.getProperty(KevinBaconPropertyType.DATA_PATH) + textFile;

        // WE'LL ADD ALL THE CONTENTS OF THE TEXT FILE TO THIS STRING
        String textToReturn = "";

        // OPEN A STREAM TO READ THE TEXT FILE
        FileReader fr = new FileReader(textFile);
        BufferedReader reader = new BufferedReader(fr);

        // READ THE FILE, ONE LINE OF TEXT AT A TIME
        String inputLine = reader.readLine();
        while (inputLine != null)
        {
            // APPEND EACH LINE TO THE STRING
            textToReturn += inputLine + NEW_LINE;

            // READ THE NEXT LINE
            inputLine = reader.readLine();
        }

        // RETURN THE TEXT
        return textToReturn;
    }

    /**
     * Loads the HTML contents of the htmlFile argument into doc, which manages
     * an HTML in a tree data structure (i.e. a DOM).
     *
     * @param htmlFile The HTML file to be loaded.
     *
     * @param doc The Document Object Model this function will be loading the
     * html into.
     *
     * @throws IOException is thrown when there is an error loading the html
     * document.
     */
    public static void loadHTMLDocument(String htmlFile,
            HTMLDocument doc) throws IOException
    {
        // OPEN THE STREAM
        FileReader fr = new FileReader(htmlFile);
        BufferedReader br = new BufferedReader(fr);

        // WE'LL USE A SWING PARSER FOR PARSING THE
        // HTML DOCUMENT SUCH THAT IT PROPERLY LOADS
        // THE DOCUMENT
        HTMLEditorKit.Parser parser = new ParserDelegator();

        // THE CALLBACK KEEPS TRACK OF WHEN IT COMPLETES LOADING
        HTMLEditorKit.ParserCallback callback = doc.getReader(0);

        // LOAD AND PARSE THE WEB PAGE
        parser.parse(br, callback, true);
    }

    /**
     * This method loads all actors from the actorsFileName into the actors
     * instance variable.
     *
     * @param graph This method loads the actors found in the file into this
     * graph, which stores all the actor-film connections.
     * @param actorsFilePath The file to load that stores the actor data.
     * @throws IOException Thrown if there is a problem reading the file.
     */
    private static void loadActors(KevinBaconGameGraphManager graph,
            String actorsFilePath) throws IOException
    {
        // LOAD THE ENTIRE THING INTO RAM IN ONE GIANT byte READ
        ByteArrayInputStream bais = loadFile(actorsFilePath);

        // AND NOW READ IT AS TEXT
        InputStreamReader isr = new InputStreamReader(bais);
        BufferedReader reader = new BufferedReader(isr);

        // ONE LINE AT A TIME
        String inputLine = reader.readLine();
        StringTokenizer st;
        while (inputLine != null)
        {
            // PARSE EACH LINE. NOTE THAT EACH LINE
            // STORES DATA FOR A SINGLE ACTOR
            st = new StringTokenizer(inputLine, DELIMITER);
            String actorID = st.nextToken();
            String lastName = st.nextToken();
            String firstName = st.nextToken();

            // THIS HELPS KEEP US VERIFY FILE LOADING IS GOING SMOOTHLY
            System.out.println(LOADING_DATA_PREFIX + firstName + SPACE + lastName);

            // USE THE DATA TO MAKE AN ACTOR
            Actor actorToAdd = new Actor(
                    actorID,
                    firstName,
                    lastName);

            // NOW GET ALL THE FILMS THIS ACTOR HAS BEEN IN
            while (st.hasMoreTokens())
            {
                String filmToAdd = st.nextToken();
                actorToAdd.addFilmID(filmToAdd);
            }

            // SORT THE FILMS
            actorToAdd.sortFilmIDs();

            // ADD EACH ONE TO THE GAME GRAPH DATA STRUCTURE
            graph.addActor(actorToAdd);

            // AND ON TO THE NEXT LINE
            inputLine = reader.readLine();
        }

        // ALL THE ACTORS HAVE BEEN LOADED SO WE SHOULD BE
        // ABLE TO FIND AND SETUP KEVIN BACON AS A SPECIAL NODE
        graph.initKevinBacon();
    }

    /**
     * This method should work much like the loadActors method except instead of
     * loading the film data it loads all actor data. Note that our graph must
     * maintain actor-film and film-actor edge information.
     *
     * @param graph This method loads the films found in the file into this
     * graph, which stores all the film-actor connections.
     * @param filmsFilePath The file to load that stores the actor data.
     * @throws IOException Thrown if there is a problem reading the file.
     */
    private static void loadFilms(KevinBaconGameGraphManager graph,
            String filmsFilePath) throws IOException
    {
        // LOAD THE ENTIRE THING INTO RAM IN ONE GIANT byte READ        
        ByteArrayInputStream bais = loadFile(filmsFilePath);

        // AND NOW READ IT AS TEXT        
        InputStreamReader isr = new InputStreamReader(bais);
        BufferedReader reader = new BufferedReader(isr);

        // ONE LINE AT A TIME
        String inputLine = reader.readLine();
        StringTokenizer st;
        while (inputLine != null)
        {
            // PARSE EACH LINE. NOTE THAT EACH LINE
            // STORES DATA FOR A SINGLE FILM
            st = new StringTokenizer(inputLine, DELIMITER);
            String filmID = st.nextToken();
            String title = st.nextToken();
            String yearText = st.nextToken();

            // THIS HELPS KEEP US VERIFY FILE LOADING IS GOING SMOOTHLY
            System.out.println(LOADING_DATA_PREFIX + title);

            // USE THE DATA TO MAKE AN ACTOR
            int year = Integer.parseInt(yearText);
            Film filmToAdd = new Film(
                    filmID,
                    title,
                    year);

            // NOW GET ALL THE ACTORS THAT HAVE BEEN IN THIS FILM
            while (st.hasMoreTokens())
            {
                String actorToAdd = st.nextToken();
                filmToAdd.addActorID(actorToAdd);
            }

            // SORT THE ACTORS
            filmToAdd.sortActorIds();

            // ADD THIS FILM TO THE GRAPH
            graph.addFilm(filmToAdd);

            // AND ONTO THE NEXT LINE
            inputLine = reader.readLine();
        }
    }

    /**
     * Loads all the film and actor data from the data files into the 
     * game graph data structure.
     */
    public static void loadActorsAndFilms(KevinBaconGameGraphManager graph) throws IOException
    {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String dataPath = props.getProperty(KevinBaconPropertyType.DATA_PATH);
        String actorsFileName = props.getProperty(KevinBaconPropertyType.ACTORS_FILE_NAME);
        loadActors(graph, dataPath + actorsFileName);
        String filmsFileName = props.getProperty(KevinBaconPropertyType.FILMS_FILE_NAME);
        loadFilms(graph, dataPath + filmsFileName);
    }
}
