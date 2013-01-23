package se.skl.skltpservices.takecare;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;

public abstract class TakeCareRequestTransformer extends AbstractMessageTransformer {

	/**
	 * Simple pojo transformer that transforms crm:scheduling 1.0 to Take Care
	 * format.
	 * 
	 * @param message
	 * @param outputEncoding
	 */
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		Object src = ((Object[]) message.getPayload())[1];
		message.setPayload(pojoTransform(src, outputEncoding));
		return message;
	}

	protected abstract Object pojoTransform(Object src, String encoding) throws TransformerException;

	protected static final String buildReason(SubjectOfCareType subjectOfCare, TimeslotType incomingTimeslot) {
		String reason = "";
		if (incomingTimeslot != null && StringUtils.isNotEmpty(incomingTimeslot.getReason())) {
			reason = incomingTimeslot.getReason() + " ";
		}
		if (subjectOfCare != null && StringUtils.isNotEmpty(subjectOfCare.getPhone())) {
			reason += subjectOfCare.getPhone();
		}
		return reason;
	}

}
