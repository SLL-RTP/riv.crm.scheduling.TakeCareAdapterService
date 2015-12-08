package se.skl.skltpservices.takecare.takecareintegrationcomponent.transform;

import org.mule.api.MuleMessage;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lookup the producer's url using the hsaId
 *
 * @author Martin Flower
 */
public class TakeCareUrlRetrieverTransformer extends AbstractMessageTransformer {
    
    private static final Logger log = LoggerFactory.getLogger(TakeCareUrlRetrieverTransformer.class);

    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) {
        String hsaId = message.getOutboundProperty("hsaId");
        log.debug("hsaId:" + hsaId);
        String takecareUrl = lookup(hsaId);
        message.setOutboundProperty("takecareUrl", takecareUrl);
        log.debug("retrieved takecareUrl:" + takecareUrl);
        return message;
    }

    private String lookup(String hsaId) {
        // TODO - implementation of lookup
        log.debug("lookup(" + hsaId + ")");
        return "localhost:8082/TakeCare/services/getAllTimeTypes-soap-teststub/v1";
    }
}
