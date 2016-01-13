package se.skl.skltpservices.takecare.takecaretestproducer;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report validation error and warnings when marshalling or unmarshalling with jaxb.
 * 
 * User: khaled.daham@callistaenterprise.se
 */
public class TakeCareValidationEventHandler implements ValidationEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TakeCareValidationEventHandler.class);
    public boolean handleEvent(ValidationEvent event) {
        log.debug("Event");
        log.debug(" severity         : {}", event.getSeverity());
        log.debug(" message          : {}", event.getMessage());
        log.debug(" linked exception : {}", event.getLinkedException());
        log.debug(" locator:::");
        log.debug("   line number    : {}", event.getLocator().getLineNumber());
        log.debug("   column number  : {}", event.getLocator().getColumnNumber());
        log.debug("   offset         : {}", event.getLocator().getOffset());
        log.debug("   object         : {}", event.getLocator().getObject());
        log.debug("   node           : {}", event.getLocator().getNode());
        log.debug("   URL            : {}", event.getLocator().getURL());
        return true;
    }
}
