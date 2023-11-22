package net.certiv.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any field marked with this annotation is excluded from the serialized JSON output.
 * <p>
 * Requires use of the {@code AnnotationExclusionStrategy} in the {@code Gson} builder.
 * <p>
 * Alternative to using {@code transient}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exclude {}
