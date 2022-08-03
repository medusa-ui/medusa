package io.getmedusa.medusa.core.memory;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class SessionMemoryRepositoryTest {

    private SessionMemoryRepository repository;
    private String sessionId;
    private final Route route = new Route("/test", "", new EmbeddedSampleController());

    @BeforeEach
    public void init() {
        repository = new SessionMemoryRepository();
        sessionId = RandomUtils.generateId();
    }

    @Test
    void testStoreAndRetrieve() {
        Session session = new Session();
        session.setLastUsedHash(route.generateHash());
        final Session storedSession = repository.store(session);
        final Session retrievedSession = repository.retrieve(storedSession.getId(), route);
        Assertions.assertEquals(retrievedSession.getId(), storedSession.getId());
        Assertions.assertEquals(session.getId(), storedSession.getId());
    }

    @Test
    void testRetrieveWithoutStoring_ShouldFail() {
        Assertions.assertThrows(SecurityException.class, () -> {
            repository.retrieve(sessionId, route);
            Assertions.fail("Should not get to this point");
        });
    }

    @Test
    void testMultipleRetrievals_ShouldNotBeAllowed() {
        Session session = new Session();
        session.setLastUsedHash(route.generateHash());
        final Session storedSession = repository.store(session);
        final Session retrievedSessionFirstTime = repository.retrieve(storedSession.getId(), route);
        Assertions.assertEquals(retrievedSessionFirstTime.getId(), storedSession.getId());

        Assertions.assertThrows(SecurityException.class, () -> {
            repository.retrieve(storedSession.getId(), route);
            Assertions.fail("Should not get to this point");
        });
    }

    @UIEventPage(path = "/", file = "/pages/hello-world")
    public class EmbeddedSampleController {

        public List<Attribute> setupAttributes() {
            return List.of(new Attribute("123", 456));
        }

    }

}
