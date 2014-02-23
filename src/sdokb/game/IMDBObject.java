package sdokb.game;

/**
 * Actors and films will be used interchangeably in methods at times via
 * their ids, so this parent class allows that to happen.
 * 
 * @author Richard McKenna & __________________
 */
public class IMDBObject implements Comparable
{
    // IMDB id NUMBER FOR THIS FILM/ACTOR
    private String id;

    /**
     * Initializes the id only.
     * 
     * @param initId The IMDB id for this Film/Actor
     */
    public IMDBObject(String initId)
    {
        id = initId;
    }

    // ACCESSOR METHOD
    public String getId()
    {
        return id;
    }   

    /**
     * Used for sorting these objects by id.
     */
    @Override
    public int compareTo(Object otherObject)
    {
        return toString().compareTo(otherObject.toString());        
    }

    /**
     * Used for testing the equivalence of these object by id.
     */
    @Override
    public boolean equals(Object obj)
    {
        IMDBObject otherObj = (IMDBObject) obj;
        return otherObj.getId().equals(id);
    }

    /**
     * Returns a textual representation of this object, which is its id.
     */
    @Override
    public String toString()
    {
        return id;
    }
}