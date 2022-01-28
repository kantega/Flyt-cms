package no.kantega.publishing.spring.velocity;

/**
 *
 */
import org.apache.velocity.app.VelocityEngine;

/**
 * Interface to be implemented by objects that configure and manage a
 * VelocityEngine for automatic lookup in a web environment. Detected
 * and used by VelocityView.
 *
 * @author Rod Johnson
 * @see VelocityConfigurer
 * @see VelocityView
 */
public interface VelocityConfig {

    /**
     * Return the VelocityEngine for the current web application context.
     * May be unique to one servlet, or shared in the root context.
     * @return the VelocityEngine
     */
    VelocityEngine getVelocityEngine();

}
