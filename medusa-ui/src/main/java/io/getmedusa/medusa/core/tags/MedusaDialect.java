package io.getmedusa.medusa.core.tags;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Medusa Custom Thymeleaf-dialect: scan the ApplicationContext for Beans annotated with {@link MedusaTag}
 */

public class MedusaDialect extends AbstractProcessorDialect {

    private static final Logger logger = LoggerFactory.getLogger(MedusaDialect.class);

    private final Set<IProcessor> medusaTags = new HashSet<>();

    public MedusaDialect(Collection<IProcessor> iProcessors ) {
        super("Medusa", MedusaTag.prefix, MedusaTag.precedence);
        this.medusaTags.addAll(iProcessors);
        logger.debug("MedusaDialect with tags: {}", medusaTags);
    }

    @Override
    public Set<IProcessor> getProcessors(String s) {
        return medusaTags;
    }

}