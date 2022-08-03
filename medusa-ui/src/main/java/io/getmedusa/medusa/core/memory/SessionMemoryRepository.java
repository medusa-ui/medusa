package io.getmedusa.medusa.core.memory;

import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.session.StandardSessionTagValues;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//decides on redis or in-memory storage based on properties

@Repository
public class SessionMemoryRepository {

    //TODO temp only in memory, eventually needs to make a call to hit redis or in-memory
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
        if(session.isMatched()) {
            throw new SecurityException("Trying to match an already-matched session. Potential for session hijacking.");
        } else {
            session.setMatched();
            return store(session);
        }
    }

    public List<Session> findSessionsByIds(Collection<String> sessionIds) {
        return sessionIds.stream().map(sessions::get).toList();
    }

    public List<Session> findSessionsByTag(String sessionTagKey, String sessionTagValue) {
        if(sessionTagValue.equals(StandardSessionTagValues.ALL)) {
            return sessions.values().stream().toList();
        } else {
            return sessions.values().stream().filter(session -> sessionTagValue.equals(session.getTags().getOrDefault(sessionTagKey, null))).toList();
        }
    }
}
