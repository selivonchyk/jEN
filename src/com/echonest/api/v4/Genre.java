package com.echonest.api.v4;

import java.util.Map;

/**
 * Represents and Echo Nest Genre
 * 
 * @author selivonchyks
 * 
 */
public class Genre extends ENItem {
    private final static String PATH = "genre";
    private final static String TYPE = "genre";
    
    public final static String GENRE_NAME = "name";
    public final static String GENRE_SIMILARITY = "similarity";

    Genre(EchoNestAPI en, @SuppressWarnings("rawtypes") Map data) throws EchoNestException {
        super(en, TYPE, PATH, data);
    }

    /**
     * Gets the ID for the genre
     */
    @Override
    public String getID() {
        if (data.containsKey("id")) {
            return (String) data.get("id");
        } else {
            return (String) data.get(GENRE_NAME);
        }
    }

    Genre(EchoNestAPI en, String id) throws EchoNestException {
        this(en, id, false);
    }

    Genre(EchoNestAPI en, String idOrName, boolean byName)
            throws EchoNestException {
        super(en, TYPE, PATH, idOrName, byName);
    }

    /**
     * Gets the name of the genre
     * 
     * @return the name of the genre
     * @throws EchoNestException
     */
    public String getName() throws EchoNestException {
        return getString(GENRE_NAME);
    }
    
    /**
     * Gets similarity
     * 
     * @return the similarity of the genre
     * @throws EchoNestException
     */
    public Double getSimilarity() throws EchoNestException {
    	return getDouble(GENRE_SIMILARITY);
    }
    
    @Override
    protected String findID() throws EchoNestException {
        if (data.get("id") != null) {
            return (String) data.get("id");
        } else if (data.get(GENRE_NAME) != null) {
            return (String) data.get(GENRE_NAME);
        } else {
            throw new EchoNestException(
                    EchoNestException.ERR_MISSING_PARAMETER, "Missing ID");
        }
    }
}
