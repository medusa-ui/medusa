package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.tags.MedusaDialect;
import io.getmedusa.medusa.core.tags.action.MedusaOnChange;
import io.getmedusa.medusa.core.tags.action.MedusaOnClick;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public abstract class MedusaTagTest {

    protected static final Logger logger = LoggerFactory.getLogger(MedusaTagTest.class);
    protected Renderer renderer;
    protected final Session session = buildSession();

    private Session buildSession() {
        final Session s = new Session();
        s.setLastUsedHash("");
        return s;
    }

    @BeforeEach
    public void init() {
        this.renderer = new Renderer(Set.of(new MedusaDialect(Set.of(new MedusaOnClick(), new MedusaOnChange()))), null);
    }

}
