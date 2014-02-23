package sdokb.game;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * KevinBaconGameData stores the data necessary for a single
 * game, including the path the user has followed thus far. It is
 * important that this class works in concert with the
 * KevinBaconGameStateManager.
 * 
 * @author Richard McKenna & ________________
 */
public class KevinBaconGameData
{
    // THE STARTING ACTOR FOR THIS GAME, THE PLAYER MUST MAKE IT
    // FROM THIS ACTOR TO KEVIN BACON
    private Actor startingActor;

    // THIS STORES THE PATH THE PLAYER FOLLOWED
    private ArrayList<Connection> gamePath;
    
    // THE LAST NODE IN THE PATH, IT'S A SPECIAL NODE BECAUSE IT
    // MIGHT BE MISSING THE SECOND ACTOR
    private Connection lastNode;
    
    // THE OPTIMAL PATH AS DETERMINED VIA A BREATH-FIRST SEARCH
    private ArrayList<Connection> shortestPath;
    
    // A SORTED MAP OF ALL THE ID GUESSES SO FAR. IT PROVIDES
    // FAST, EASY ACCESS TO USE TO CHECK FOR DUPLICATE GUESSES
    private Map<String,IMDBObject> guessMap;

    // AT ANY POINT THE GAME IS EITHER EXPECTING A FILM OR ACTOR
    // FROM THE PLAYER, NEVER BOTH. THIS KEEPS TRACK OF WHICH
    private boolean waitingForFilm;
    
    // WHEN A MOVIE WITH KEVIN BACON IS FOUND, THE GAME 
    // IS OVER AND THE PLAYER WINS, BUT IF
    // THE PLAYER GIVES UP OR REACHES A DEAD END
    // THIS WELL REMAIN FALSE
    private boolean kevinBaconFound;
    
    // START AND END TIME WILL BE USED TO CALCULATE THE 
    // TIME IT TAKES TO PLAY THIS GAME
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    
    // THESE ARE USED FOR FORMATTING THE TIME OF GAME
    private final long MILLIS_IN_A_SECOND = 1000;
    private final long MILLIS_IN_A_MINUTE = 1000 * 60;
    private final long MILLIS_IN_AN_HOUR  = 1000 * 60 * 60;
    
    /*
     * Construct this object when a game begins.
     */
    public KevinBaconGameData(  Actor initStartingActor,
                                ArrayList<Connection> initShortestPath)
    {
        // INITIALIZE EVERYTHING TO A BRAND NEW GAME, NOTE THAT 
        // THE PATH STARTS OUT AS EMPTY AND A null LAST NODE
        gamePath = new ArrayList();
        lastNode = null;
        
        // KEEP THE STARTING ACTOR AND SHORTEST PATH
        startingActor = initStartingActor;
        shortestPath = initShortestPath;
        
        // FIRST GUESS HAS TO BE A FILM
        waitingForFilm = true;
        kevinBaconFound = false;
        
        // START THE CLOCK
        startTime = new GregorianCalendar();
        endTime = null;
        
        // SETUP THE LIST FOR ALL GAME IDS USED IN THE PATH
        guessMap = new HashMap();
        guessMap.put(startingActor.getId(), startingActor);
    }
    
    // ACCESSOR METHODS
    public Actor getStartingActor()                 {   return startingActor;       }
    public int getDegrees()                         {   return gamePath.size();     }
    public Iterator<Connection> gamePathIterator()  {   return gamePath.iterator(); }
    public Connection getLastConnection()           {   return lastNode;            }

    /**
     * Accessor method for testing to see if Kevin Bacon
     * has been found this game or not.
     * 
     * @return true if the secret word has been found this
     * game, false otherwise.
     */
    public boolean isKevinBaconFound() 
    { 
        return kevinBaconFound; 
    }
    
    /**
     * Tests to see if the game was a perfect win, meaning it was
     * done via the fewest connections possible.
     */
    public boolean isPerfectWin()
    {
        if (kevinBaconFound && (gamePath.size() == shortestPath.size()))
            return true;
        else
            return false;
    }
    
    /**
     * Accessor method for testing to see if the game is waiting
     * for the user to enter a film or not.
     * 
     * @return true if the game is waiting for the user to pick
     * a film, false otherwise (an actor).
     */
    public boolean isWaitingForFilm()
    {
        return waitingForFilm;
    }
    
    /**
     * Gets the total time (in milliseconds) that this game took.
     * 
     * @return The time of the game in milliseconds.
     */
    public long getTimeOfGame()
    {
        // IF THE GAME ISN'T OVER YET, THERE IS NO POINT IN CONTINUING
        if (endTime == null)
        {
            return -1;
        }
        
        // THE TIME OF THE GAME IS END-START
        long startTimeInMillis = startTime.getTimeInMillis();
        long endTimeInMillis = endTime.getTimeInMillis();
        
        // CALC THE DIFF AND RETURN IT
        long diff = endTimeInMillis - startTimeInMillis;
        return diff;
    }

    /**
     * Tests to see if the guessId argument has been made. If it has, true
     * is returned, otherwise false.
     */
    public boolean hasGuessBeenMade(String guessId)
    {
        return guessMap.containsKey(guessId);
    }

    /**
     * This method builds and returns a list of all the ids referenced by
     * the guessId that have not already been used in this game.
     */
    public ArrayList<String> getNonRepeatingIds(String guessId, KevinBaconGameGraphManager graph)
    {
        // THE NON-DUPLICATES TO RETURN
        ArrayList<String> nonDuplicates = new ArrayList();

        // IF IT'S A FILM, THE QUESTION IS ARE THERE ANY ACTORS IN
        // IT THAT WE HAVEN'T GUESSED ALREADY
        ArrayList<String> guessIds;
        if (this.isWaitingForFilm())
        {
            Film film = graph.getFilm(guessId);
            guessIds = film.getActorIDs();
        }
        else
        {
            Actor actor = graph.getActor(guessId);
            guessIds = actor.getFilmIDs();
        }

        // ONLY ADD THE NON-DUPLICATES
        for (String id : guessIds)
        {
            if (!guessMap.containsKey(id))
            {
                nonDuplicates.add(id);
            }
        }        
        return nonDuplicates;
    }

    /**
     * Ends the game as a win.
     */
    public void endGameAsWin(Actor kevinBacon)
    {
        kevinBaconFound = true;
        endTime = new GregorianCalendar();
        lastNode.setActor2Id(kevinBacon.getId());
    }

    /**
     * Ends the game as a loss.
     */
    public void endGameAsLoss()
    {
        endTime = new GregorianCalendar();
    }

    /**
     * Tests to see if the current game path contains the testActorId. Returns
     * true if it does, false otherwise.
     */
    public boolean gamePathContainsActor(String testActorId)
    {
        Iterator<Connection> it = gamePath.iterator();
        while(it.hasNext())
        {
            Connection connection = it.next();
            if (connection.getActor1Id().equals(testActorId)
                    ||
               (connection.getActor2Id() != null) && connection.getActor2Id().equals(testActorId))
                return true;
        }
        return false;
    }
    
    /**
     * Tests to see if the game path contains the testFilmeId. It returns true
     * if it does, false otherwise.
     */
    public boolean gamePathContainsFilm(String testFilmId)
    {
        Iterator<Connection> it = gamePath.iterator();
        while(it.hasNext())
        {
            Connection connection = it.next();
            if (connection.getFilmId() != null)
            {
                if (connection.getFilmId().equals(testFilmId))
                    return true;
            }
        }
        return false;        
    }
    
    /**
     * This method constructs and returns a textual description of the time
     * it took to complete the game.
     */
    public String getGameTimeDescription()
    {
        // CALCULATE GAME TIME USING HOURS : MINUTES : SECONDS
        long timeInMillis = this.getTimeOfGame();
        long hours = timeInMillis/MILLIS_IN_AN_HOUR;
        timeInMillis -= hours * MILLIS_IN_AN_HOUR;        
        long minutes = timeInMillis/MILLIS_IN_A_MINUTE;
        timeInMillis -= minutes * MILLIS_IN_A_MINUTE;
        long seconds = timeInMillis/MILLIS_IN_A_SECOND;
        
        // AND NOW BUILD THE STRING SUMMARY. START WITH THE ACTOR
        String text = "";
        
        // THEN ADD THE TIME OF GAME SUMMARIZED IN PARENTHESES
        String minutesText = "" + minutes;
        if (minutes < 10)   minutesText = "0" + minutesText;
        String secondsText = "" + seconds;
        if (seconds < 10)   secondsText = "0" + secondsText;
        text += " (" + hours + ":" + minutesText + ":" + secondsText + ")";
        
        return text;
    }
    
    /**
     * A textual representation of this object.
     */
    @Override
    public String toString()
    {
        return startingActor.toString();
    }
}