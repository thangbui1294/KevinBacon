package sdokb.game;

import java.util.*;
/**
 * This class keeps track of the edges in a path through the actor-film
 * graph. This is useful for finding shortest paths through the graph.
 * 
 * @author Richard McKenna & ________________
 */
public class Connection implements Comparable, Comparator
{
    // A CONNECTION HAS TWO ACTOR NODES AND A FILM NODE
    private String actor1Id;
    private String filmId;
    private String actor2Id;
    
    /**
     * During gameplay we'll know the first actor and film before
     * we'll know the second actor, so we'll only initialize the
     * first two with this constructor.
     * 
     * @param initActor1Id The id of the first actor in the connection.
     * @param initFilmId The film used to connect the two actors.
     */
    public Connection(String initActor1Id, String initFilmId)
    {
        // KEEP THESE FOR LATER
        actor1Id = initActor1Id;
        filmId = initFilmId;
        
        // BE CAFEUL WITH THIS
        actor2Id = null;
    }

    /**
     * Used for initializing a connection when all three nodes are
     * known values.
     * 
     * @param initActor1Id The first actor in the connection.
     * @param initFilmId The film connecting the two actors.
     * @param initActor2Id The second actor in the connection.
     */
    public Connection(  String initActor1Id,
                        String initFilmId,
                        String initActor2Id)
    {
        // USE THE OTHER CONSTRUCTOR TO INIT THE FIRST TWO THINGS
        this(initActor1Id, initFilmId);
        
        // AND INIT THE SECOND ACTOR
        actor2Id = initActor2Id;
    }

    // ACCESSOR METHODS

    public String getFilmId()   {   return filmId;      }
    public String getActor1Id() {   return actor1Id;    }
    public String getActor2Id() {   return actor2Id;    }
    
    // MUTATOR METHODS
    
    public void setFilmId(String initFilmId)
    {
        filmId = initFilmId;
    }
    
    public void setActor2Id(String initActor2Id)
    {
        actor2Id = initActor2Id;
    }

    // METHODS FOR TESTING CERTAIN CONDITIONS
    
    /**
     * Tests to see if this connection knows the second actor or not.
     * 
     * @return true if both actors are known, false otherwise.
     */
    public boolean hasTwoActors()
    {
        return (actor2Id != null);
    }
    
    /**
     * Tests to see if the testActorId argument is one of the actors
     * part of this connection.
     * 
     * @param testActorId The actor to test.
     * 
     * @return true if that actor is one of the two in this connection,
     * false otherwise.
     */
    public boolean hasActor(String testActorId)
    {
        if (actor1Id.equals(testActorId))       {   return true;    } 
        else if (actor2Id == null)              {   return false;   }
        else if (actor2Id.equals(testActorId))  {   return true;    } 
        else                                    {   return false;   }
    }
    
    /**
     * Tests to see if the testFilmId argument is part of this connection.
     * 
     * @param testFilmId The film to test.
     * 
     * @return true if the film is part of the connection, false otherwise.
     */
    public boolean hasFilm(String testFilmId)
    {
        return testFilmId.equals(filmId);
    }

    /**
     * Used for comparing Connection objects for the purpose
     * of sorting.
     * 
     * @param obj Another Connection to compare to.
     * 
     * @return 0 if obj is equivalent to this one, meaning it has
     * the same actors (in same order) and film. Return 1 if
     * this connection is before obj, otherwise -1.
     */
    @Override
    public int compareTo(Object obj)
    {
        if (equals(obj))
        {
            return 0;
        } else
        {
            Connection otherConnection = (Connection) obj;
            return (filmId + actor1Id + actor2Id)
                    .compareTo(otherConnection.filmId
                            + otherConnection.actor1Id
                            + otherConnection.actor2Id);
        }
    }

    /**
     * Tests to see if this Connection is equivalent to obj.
     * @param obj Another Connection to use in this comparison.
     * @return true if they have the same actors and film, false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        Connection otherConnection = (Connection) obj;
        boolean sameActors;
        sameActors = ((actor1Id.equals(otherConnection.actor1Id)
                && actor2Id.equals(otherConnection.actor2Id))
                || (actor1Id.equals(otherConnection.actor2Id)
                && actor2Id.equals(otherConnection.actor1Id)));
        return sameActors && (filmId.equals(otherConnection.filmId));
    }

    /**
     * Used for comparator comparisions for the purpose of searching
     * or sorting.
     * 
     * @param arg0 The first Connection in the comparison.
     * @param arg1 The second Connection in the comparison.
     * @return Works the same as compareTo, making arg0 this and
     * arg1 the obj argument.
     */
    @Override
    public int compare(Object arg0, Object arg1)
    {
        Connection conn0 = (Connection) arg0;
        Connection conn1 = (Connection) arg1;
        return conn0.compareTo(conn1);
    }
    
    /**
     * Creates and returns a textual representation of this connection, which is
     * represented by the three ids.
     * 
     * @return the three IMDB ids strung together.
     */
    @Override
    public String toString()
    {
        return actor1Id + filmId + actor2Id;
    }
}