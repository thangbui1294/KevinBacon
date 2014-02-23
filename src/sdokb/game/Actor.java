package sdokb.game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Actor stores the data associated with an individual actor, which might be a
 * male or female.
 * 
 * @author Richard McKenna & ______________
 */
public class Actor extends IMDBObject
{
    // ACTOR DATA, NOTE WE'RE USING imdb.com IDs, AND THAT THAT ID
    // IS INHERITED FROM THE PARENT CLASS
    private String firstName;
    private String lastName;

    // FILM IDs OF FILMS THIS ACTOR HAS BEEN IN
    private ArrayList<String> filmIDs;

    /**
     * Constructor initializes the id and actor name
     * variables, but does not setup film list. That
     * must be loaded separately via addFilm mutator method.
     * 
     * @param initId
     * @param initFirstName
     * @param initLastName 
     */
    public Actor(String initId,
            String initFirstName,
            String initLastName)
    {
        // PASS THE ID TO THE IMDBObject CONSTRUCTOR
        super(initId);

        // INIT THE FIRST AND LAST NAMES
        firstName = initFirstName;
        lastName = initLastName;
        
        // THESE FILMS MUST BE ADDED
        filmIDs = new ArrayList();
    }

    // ACCESSOR METHODS
        // -getFirstName
        // -getLastName
        // -getFilmIDs

    public String getFirstName()            {   return firstName;       }
    public String getLastName()             {   return lastName;        }
    public ArrayList<String> getFilmIDs()   {   return filmIDs;         }
 
    /**
     * This method adds the idToAdd argument to the list of films this
     * actor has been in.
     * 
     * @param idToAdd An IMDB id for a film the actor has been in, it
     * is added to the list of films for this actor.
     */    
    public void addFilmID(String idToAdd)
    {
        filmIDs.add(idToAdd);
    }

    /**
     * This method sorts the film IDs to enable fast searching using
     * binary search.
     */
    public void sortFilmIDs()
    {
        Collections.sort(filmIDs);
    }

    /**
     * Method for testing to see if a particular actor was in a film. Note
     * that it searches the list of films using binary search, so we must
     * keep that list sorted by id.
     * 
     * @param testFilmId The film we're looking for.
     * 
     * @return true if the actor was in the film argument, false otherwise.
     */
    public boolean wasActorInFilm(String testFilmId)
    {
        return (Collections.binarySearch(filmIDs, testFilmId) >= 0);
    }
    
    /**
     * Creates and returns a textual representation of this actor, which is
     * represented by last name, first name.
     * 
     * @return The full name of this actor.
     */
    @Override
    public String toString()
    {
        return firstName + " " + lastName;
    }
}