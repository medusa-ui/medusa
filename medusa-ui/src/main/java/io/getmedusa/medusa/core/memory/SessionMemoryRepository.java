package io.getmedusa.medusa.core.memory;

import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

//decides on redis or in-memory storage based on properties

@Repository
public class SessionMemoryRepository {

    //TODO temp in memory
    private Map<String, Session> sessions = new HashMap<>();

    public Session store(Session session) {
        sessions.put(session.getId(), session);
        return session;
    }

    public Session retrieve(String sessionId, Route routeForValidation) {
        final Session session = sessions.get(sessionId);
        if(session == null || !routeForValidation.generateHash().equals(session.getLastUsedHash())) {
            throw new SecurityException("Illegal request made: A session was attempted to be retrieved with a mismatching route");
        }
        return session;
    }

}
