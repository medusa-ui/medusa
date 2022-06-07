package io.getmedusa.medusa.core.memory;

import io.getmedusa.medusa.core.session.Session;
import org.springframework.stereotype.Repository;

//decides on redis or in-memory storage based on properties

@Repository
public class SessionMemoryRepository {

    public Session store(Session session) {
        return session;
    }

}
