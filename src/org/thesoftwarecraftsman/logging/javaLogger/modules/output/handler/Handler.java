/**
 * 
 */
package org.thesoftwarecraftsman.logging.javaLogger.modules.output.handler;

import org.thesoftwarecraftsman.logging.javaLogger.core.Level;
import org.thesoftwarecraftsman.logging.javaLogger.modules.output.formater.Formater;

/**
 * Choose of information stored
 * @author kadary
 * @verion 1.0
 */
public interface Handler {
	
	void log(Level level, String message, String fqcn, String handler, Formater formater);
	void log(Level level, String message, String fqcn, String handler, Formater formater, Level levelFixed);
	void log(Level level, String message, String fqcn, String name, Formater formater, boolean forceLogging);
}
