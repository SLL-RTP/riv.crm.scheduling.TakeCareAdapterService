package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.yyyyMMddHHmmss;
import static se.skl.skltpservices.takecare.TakeCareUtil.EXTERNAL_USER;
import static se.skl.skltpservices.takecare.TakeCareUtil.HSAID;
import static se.skl.skltpservices.takecare.TakeCareUtil.INVOKING_SYSTEM;
import static se.skl.skltpservices.takecare.TakeCareUtil.REQUEST;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToInteger;

import java.util.Date;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesType;
import se.skl.skltpservices.takecare.TakeCareRequestTransformer;
import se.skl.skltpservices.takecare.booking.GetAvailableDates;
import se.skl.skltpservices.takecare.booking.getavailabledatesrequest.ProfdocHISMessage;

public class GetAvailableDatesRequestTransformer extends TakeCareRequestTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableDatesRequestTransformer.class);

	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetAvailableDatesType.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAvailableDates.class);

	protected Object pojoTransform(Object src, String encoding) throws TransformerException {

		if (logger.isDebugEnabled()) {
			log.debug("Transforming request payload: {}", src);
		}

		try {
			GetAvailableDatesType incomingRequest = (GetAvailableDatesType) jaxbUtil_incoming.unmarshal(src);
			String incomingHealthcarefacility = incomingRequest.getHealthcareFacility();
			String incomingBookingId = incomingRequest.getBookingId();
			String incomingEndDate = incomingRequest.getEndDateInclusive();
			String incomingStartDate = incomingRequest.getStartDateInclusive();
			String incomingTymeTypeId = incomingRequest.getTimeTypeID();

			ProfdocHISMessage message = new ProfdocHISMessage();
			message.setCareUnitId(incomingHealthcarefacility);
			message.setCareUnitIdType(HSAID);
			message.setInvokingSystem(INVOKING_SYSTEM);
			message.setMsgType(REQUEST);
			message.setTime(yyyyMMddHHmmss(new Date()));
			message.setBookingId(incomingBookingId);
			message.setEndDate(Long.valueOf(incomingEndDate));
			message.setResourceId(null);
			message.setStartDate(Long.valueOf(incomingStartDate));
			message.setTimeTypeId(numericToInteger(incomingTymeTypeId));

			GetAvailableDates outgoingRequest = new GetAvailableDates();
			outgoingRequest.setCareunitid(incomingHealthcarefacility);
			outgoingRequest.setCareunitidtype("hsaid");
			outgoingRequest.setExternaluser(EXTERNAL_USER);
			outgoingRequest.setTcpassword("");
			outgoingRequest.setTcusername("");
			outgoingRequest.setXml(jaxbUtil_message.marshal(message));

			Object outgoingPayload = jaxbUtil_outgoing.marshal(outgoingRequest);

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + outgoingPayload);
			}

			return outgoingPayload;

		} catch (Exception e) {
			throw new TransformerException(this, e);
		}

	}
}