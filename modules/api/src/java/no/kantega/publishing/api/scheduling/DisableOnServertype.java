package no.kantega.publishing.api.scheduling;

import no.kantega.publishing.api.runtime.ServerType;

import java.lang.annotation.*;

/**
 * Used on @org.springframework.scheduling.annotation.Scheduled methods.
 * When a method is annotated with <code>@DisableOnServertype(type=)</code>
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableOnServertype {
    ServerType value();
}
