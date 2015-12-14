package se.skl.skltpservices.takecare;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;

import se.riv.crm.scheduling.v1.TimeslotType;

public abstract class AbstractTakeCareRequestTransformer extends AbstractMessageTransformer {

    public final static int MAX_REASON_LENGTH = 300;

    /**
     * Pojo transformer that transforms crm:scheduling 1.0 to Take Care format.
     *
     * @param message
     * @param outputEncoding
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        Object src = ((Object[]) message.getPayload())[1];
        message.setPayload(pojoTransform(message, src, outputEncoding));
        return message;
    }

    protected abstract Object pojoTransform(MuleMessage message, Object src, String encoding) throws TransformerException;

    // 
    
    private final static boolean MAP_PHONE_NUMBER = false;

    protected static final String buildReason(TimeslotType incomingTimeslot, String phone, Logger log) {
        String reason = "";
        if (incomingTimeslot != null && StringUtils.isNotEmpty(incomingTimeslot.getReason())) {
            reason = incomingTimeslot.getReason();
        }
        if (reason.length() > MAX_REASON_LENGTH) {
            log.warn("truncated reason from length {} to {}", reason.length(), MAX_REASON_LENGTH);
            reason = reason.substring(0, MAX_REASON_LENGTH);
        }
        // TODO - MAP_PHONE_NUMBER = false - can this code be deleted?
        if (MAP_PHONE_NUMBER) {
            if (reason.length() > (MAX_REASON_LENGTH - (phone.trim().length() + 1))) {
                reason = reason.substring(0, MAX_REASON_LENGTH - (phone.trim().length() + 1)) + phone.trim().length();
            } else {
                reason += phone.trim();
            }
        }
        return reason;
    }
}
