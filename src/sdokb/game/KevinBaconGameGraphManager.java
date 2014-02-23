package sdokb.game;

import java.util.*;
/**
 * This class manages the data associated with the game graph, including
 * having access to all the Films and all the Actors as well as Kevin Bacon.
 * 
 * @author Richard McKenna & ____________________
 */
public class KevinBaconGameGraphManager
{
    // A SPECIAL ACTOR, STAR OF FOOTLOOSE AND THE RIVER WILD
    public Actor kevinBacon;

    // THIS WILL STORE ALL OF OUR FILM DATA
    private TreeMap<String, Film> films;

    // THIS WILL STORE ALL OF OUR ACTOR DATA
    private TreeMap<String, Actor> actors;
    private ArrayList<String> actorIDs;

    /**
     * This constructor initializes the graph's data structures so
     * that the file data can be properly loaded.
     */
    public KevinBaconGameGraphManager()
    {
        // CONSTRUCT OUR GRAPH DATA STRUCTURES
        films = new TreeMap();
        actors = new TreeMap();        
        actorIDs = new ArrayList();
    }

    // ACCESSOR METHODS
    
    public Actor getKevinBacon()            {   return kevinBacon;              }    
    public Actor getActor(String actorID)   {   return actors.get(actorID);     }
    public Film getFilm(String filmID)      {   return films.get(filmID);       }

    /*
     * This method returns an Iterator that can be used to access all
     * Films in this graph.
     */
    public Iterator<Map.Entry<String, Film>> filmsIterator()
    {
        return films.entrySet().iterator();
    }

    /*
     * This method returns an Iterator that can be used to access all
     * Actors in this graph.
     */
    public Iterator<Map.Entry<String, Actor>> actorsIterator()
    {
        return actors.entrySet().iterator();
    }
    
    /**
     * Picks a random actor from the graph, used for picking the starting actor.
     */
    public Actor pickRandomActor()
    {
        int randomIndex = (int)(actors.size() * Math.random());
        String randomId = actorIDs.get(randomIndex);
        return actors.get(randomId);
    }

    /**
     * Initialized Kevin Bacon, which can only be done after all actor and
     * film data has been loaded from the files.
     */
    public void initKevinBacon()
    {
        kevinBacon = actors.get("nm0000102");
    }
    
    /**
     * Tests to see if Kevin Bacon was in the testFilmId argument film.
     */
    public boolean wasKevinBaconInFilm(String testFilmId)
    {
        Film testFilm = films.get(testFilmId);
        return testFilm.wasActorInFilm(kevinBacon.getId());
    }

    /**
     * Adds an actor to the graph.
     */
    public void addActor(Actor actorToAdd)
    {
        actors.put(actorToAdd.getId(), actorToAdd);
        actorIDs.add(actorToAdd.getId());
    }
    
    /**
     * Adds a film to the graph.
     */
    public void addFilm(Film filmToAdd)
    {
        films.put(filmToAdd.getId(), filmToAdd);
    }
    
   /**
     * This method examines the Actor corresponding to the provided 
     * actorID and build return a ArrayList with a constructed Connection 
     * object for each actor connected by one degree to the actor referenced 
     * in the argument.
     */
    public ArrayList<Connection> getAllNeighbors(String actorID)
    {
        ArrayList<Connection> connections = new ArrayList();
        Actor actor = actors.get(actorID);
        for (int i = 0; i < actor.getFilmIDs().size(); i++)
        {
            String filmID = actor.getFilmIDs().get(i);
            Film film = films.get(filmID);
            for (int j = 0; j < film.getActorIDs().size(); j++)
            {
                String actorID2 = film.getActorIDs().get(j);
                if (!actorID2.equals(actorID))
                {
                    Connection connection;
                    connection = new Connection(filmID, actorID, actorID2);
                    connections.add(connection);
                }
            }
        }
        return connections;
    }

    /**
     * Helper method for path generation.
     */
    private ArrayList<Connection> generatePath(ArrayList<String> actorIDs,
            ArrayList<String> filmIDs)
    {
        ArrayList<Connection> path = new ArrayList();
        for (int i = 0; i < filmIDs.size(); i++)
        {
            Connection c = new Connection(filmIDs.get(i),
                    actorIDs.get(i),
                    actorIDs.get(i + 1));
            path.add(c);
        }
        return path;
    }

   /**
     * This method uses a Greedy-type algorithm to find a path from
     * the actor argument to Kevin Bacon. Note that it need not be the
     * shortest path, but it doesn't repeat any actors or films in the path.
     */
    public ArrayList<Connection> findPathToKevinBacon(Actor actor)
    {
        ArrayList<String> actorIDsInPath = new ArrayList();
        ArrayList<String> filmIDsInPath = new ArrayList();
        TreeMap<String, String> closedActorIDs;
        closedActorIDs = new TreeMap();
        TreeMap<String, String> closedFilmIDs;
        closedFilmIDs = new TreeMap();

        actorIDsInPath.add(actor.getId());
        closedActorIDs.put(actor.getId(), actor.getId());

	// WHILE THE PATH IS NOT EMPTY
        // WHICH MEANS THERE MIGHT STILL
        // BE A PATH
        while (!actorIDsInPath.isEmpty())
        {
            String lastActorID = actorIDsInPath.get(actorIDsInPath.size() - 1);
            Actor lastActor = actors.get(lastActorID);

            // GET ALL FILMS FOR lastActor
            ArrayList<Connection> neighbors = getAllNeighbors(lastActorID);

            // IF KEVIN BACON HAS BEEN IN ANY
            // OF THOSE FILMS, ADD THE FILM TO 
            // THE PATH AND WE'RE DONE
            Iterator<Connection> it = neighbors.iterator();
            while (it.hasNext())
            {
                Connection c = it.next();
                if (c.hasActor(kevinBacon.getId()))
                {
                    Film filmForPath = films.get(c.getFilmId());
                    filmIDsInPath.add(c.getFilmId());
                    actorIDsInPath.add(kevinBacon.getId());
                    ArrayList<Connection> path = generatePath(actorIDsInPath, filmIDsInPath);
                    return path;
                }
            }
            
            // REMOVE ALL CONNECTIONS FROM
            // THE CLOSED LIST OF ACTORS
            // AND THE CLOSED LIST OF FILMS
            for (int i = 0; i < neighbors.size(); i++)
            {
                Connection c = neighbors.get(i);
                String filmToTest = c.getFilmId();
                if (closedActorIDs.containsKey(c.getActor2Id()))
                {
                    neighbors.remove(i);
                    i--;
                } else if (closedFilmIDs.containsKey(filmToTest))
                {
                    neighbors.remove(i);
                    i--;
                }
            }

            // IF NO MORE NEIGHBORS, THEN WE HAVE
            // A DEAD END, WHICH MEANS WE HAVE TO GO
            // ANOTHER WAY. THAT MEANS WE HAVE TO
            // REMOVE THE LAST NODE FROM THE PATH, ALSO
            // ADD IT TO THE CLOSED LIST
            if (neighbors.isEmpty())
            {
                actorIDsInPath.remove(actorIDsInPath.size() - 1);
                filmIDsInPath.remove(filmIDsInPath.size() - 1);
            } else
            {
                Connection c = neighbors.get(0);
                actorIDsInPath.add(c.getActor2Id());
                closedActorIDs.put(c.getActor2Id(), c.getActor2Id());
                filmIDsInPath.add(c.getFilmId());
                closedFilmIDs.put(c.getFilmId(), c.getFilmId());
            }
        }
        return new ArrayList();
    }
    
   /**
     * This method does the same thing as the other path finding
     * algorithm, except this one finds the optimal path. The easiest 
     * way to do this is with a breadth first search algorithm.
     */
    public ArrayList<Connection> findShortestPathToKevinBacon(Actor actor)
    {
	// WE'LL MAINTAIN A SHORTEST PATH FROM THE
        // STARTING ACTOR TO EACH ACTOR WE ENCOUNTER
        TreeMap<String, ArrayList<Connection>> shortestPaths;
        shortestPaths = new TreeMap();

        // THIS WILL STORE THE PATH WE ARE CURRENTLY
        // BUILDING UPON
        ArrayList<Connection> currentPath;

	// WE ARE USING A BREADTH FIRST SEARCH, AND
        // WE'LL ONLY CHECK EACH Actor AND Film ONCE
        // WE ARE USING 2 DATA STRUCTURES FOR EACH
        // BECAUSE WE WILL USE ONE AS A LIST OF 
        // ITEMS TO CHECK IN ORDER, AND ANOTHER
        // FOR FAST SEARCHING
        ArrayList<Actor> actorsVisited = new ArrayList();
        TreeMap<String, Actor> actorsVisitedFast = new TreeMap();
        ArrayList<Film> filmsVisited = new ArrayList();
        TreeMap<String, Film> filmsVisitedFast = new TreeMap();

        // INDEX OF Actors AND Films TO CHECK
        int actorIndex = 0;
        int filmIndex = 0;

	// THE SHORTEST PATH FROM THE START ACTOR
        // TO THE START ACTOR IS NOTHING, SO WE'll
        // START OUT WITH AN EMPTY ArrayList
        actorsVisited.add(actor);
        actorsVisitedFast.put(actor.getId(), actor);
        shortestPaths.put(actor.getId(), new ArrayList<Connection>());

	// GO THROUGH ALL THE ACTORS WE HAVE REACHED
        // NEVER RE-VISITING AN ACTOR
        while (actorIndex < actorsVisited.size())
        {
            // FIRST GET ALL THE MOVIES FOR THE
            // ACTOR AT THE actorIndex
            Actor currentActor = actorsVisited.get(actorIndex);

            // MAKE THE SHORTEST PATH FOR THE CURRENT
            // ACTOR THE CURRENT PATH, SINCE WE WILL
            // BUILD ON IT
            currentPath = shortestPaths.get(currentActor.getId());

            Iterator<String> itFilmIDs = currentActor.getFilmIDs().iterator();
            while (itFilmIDs.hasNext())
            {
                String filmID = itFilmIDs.next();
                Film film = films.get(filmID);
                if (!filmsVisitedFast.containsKey(filmID))
                {
                    filmsVisited.add(film);
                    filmsVisitedFast.put(film.getId(), film);
                }
            }

            while (filmIndex < filmsVisited.size())
            {
		// NOW GO THROUGH THE FILMS AND GET
                // ALL THE ACTORS WHO WERE IN THOSE
                // FILMS, DO NOT GET ACTORS ALREADY
                // VISITED
                Film currentFilm = filmsVisited.get(filmIndex);
                Iterator<String> itActorIDs = currentFilm.getActorIDs().iterator();
                while (itActorIDs.hasNext())
                {
                    String actorID = itActorIDs.next();
                    Actor actorToTest = actors.get(actorID);
                    if (!actorsVisitedFast.containsKey(actorID))
                    {
                        actorsVisited.add(actorToTest);
                        actorsVisitedFast.put(actorID, actorToTest);
                        ArrayList<Connection> actorPath;
                        actorPath = (ArrayList<Connection>) currentPath.clone();
                        Connection c = new Connection(currentFilm.getId(),
                                currentActor.getId(),
                                actorToTest.getId());
                        actorPath.add(c);
                        shortestPaths.put(actorID, actorPath);

                        // IF THIS IS KEVIN BACON WE'RE DONE
                        if (actorID.equals(kevinBacon.getId()))
                        {
                            return actorPath;
                        }
                    }
                }
                filmIndex++;
            }
            actorIndex++;
        }
        return new ArrayList();
    }
}
