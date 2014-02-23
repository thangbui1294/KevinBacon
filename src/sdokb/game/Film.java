package sdokb.game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Film stores the data associated with an IMDB Film.
 * 
 * @author Richard McKenna & ____________________
 */
public class Film extends IMDBObject
{   
    // ALL FILMS HAVE A TITLE AND YEAR AND AN INHERITED ID
    private String title;
    private int year;

    // ACTOR IDs OF ACTORS WHO APPEARED IN THIS FILM
    private ArrayList<String> actorIDs;

    /**
     * This constructor sets up this Film for use. Note that all the
     * actors must be added later.
     * 
     * @param initId This film's IMDB id.
     * @param initTitle The film title
     * @param initYear The year the film was released.
     */
    public Film(String initId,
            String initTitle,
            int initYear)
    {
        // PASS THE ID TO THE IMDBObject CONSTRUCTOR
        super(initId);
        
        // INIT THE TITLE AND YEAR
        title = initTitle;
        year = initYear;

        // AND GET THIS DATA STRUCTURE READY TO START ADDING ACTOR IDs
        actorIDs = new ArrayList();
    }
    
    // ACCESSOR METHODS
    public String getTitle()
    {
        return title;
    }

    public int getYear()
    {
        return year;
    }

    public ArrayList<String> getActorIDs()
    {
        return actorIDs;
    }

    /**
     * Adds an actor to this film.
     * 
     * @param actorIDToAdd IMDB id of an actor who was in this film.
     */
    public void addActorID(String actorIDToAdd)
    {
        actorIDs.add(actorIDToAdd);
    }

    /**
     * Sorts the IMDB actor ids by their id so that we can use
     * binary search to look for them later.
     */
    public void sortActorIds()
    {
        Collections.sort(actorIDs);
    }
    
    /**
     * Tests to see if an actor was in this film. Note that it uses
     * a binary search algorithm, so we have to keep the ids in sorted order.
     * 
     * @param testActorId The actor we're testing for.
     * 
     * @return true if the actor was in this film, false otherwise.
     */
    public boolean wasActorInFilm(String testActorId)
    {
        return (Collections.binarySearch(actorIDs, testActorId) >= 0);
    }

    /**
     * Used for providing Film-to-Film comparisons while sorting films, which 
     * is done by film title, and then year.
     * 
     * @param otherObject Another Film to compare this one to.
     * @return Refer to the Comparable interface and String class for
     * how this will return textual comparisons.
     */
    @Override
    public int compareTo(Object otherObject)
    {
        return toString().compareTo(otherObject.toString());
    }

    /**
     * Returns a textual representation of this Film.
     * 
     * @return The title and year of this film.
     */
    @Override
    public String toString()
    {
        return title + " (" + year + ")";
    }
}