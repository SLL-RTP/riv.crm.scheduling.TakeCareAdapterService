package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabletimeslots;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.yyyyMMddHHmmss;
import static se.skl.skltpservices.takecare.TakeCareUtil.EXTERNAL_USER;
import static se.skl.skltpservices.takecare.TakeCareUtil.HSAID;
import static se.skl.skltpservices.takecare.TakeCareUtil.INVOKING_SYSTEM;
import static se.skl.skltpservices.takecare.TakeCareUtil.REQUEST;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToInteger;

import java.util.Date;
import org.mule.api.MuleMessage;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsType;
import se.skl.skltpservices.takecare.AbstractTakeCareRequestTransformer;
import se.skl.skltpservices.takecare.booking.GetAvailableTimeslots;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsrequest.ProfdocHISMessage;

public class GetAvailableTimeslotsRequestTransformer extends AbstractTakeCareRequestTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableTimeslotsRequestTransformer.class);

	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetAvailableTimeslotsType.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAvailableTimeslots.class);

	protected Object pojoTransform(MuleMessage muleMessage, Object src, String encoding) throws TransformerException {

		if (logger.isDebugEnabled()) {
			log.debug("Transforming request payload: {}", src);
		}

		try {
			GetAvailableTimeslotsType incomingRequest = (GetAvailableTimeslotsType) jaxbUtil_incoming.unmarshal(src);
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
            if (incomingBookingId != null) {
                if (incomingBookingId.isEmpty()) {
                    message.setTimeTypeId(numericToInteger(incomingTymeTypeId));
                }
            } else {
                message.setTimeTypeId(numericToInteger(incomingTymeTypeId));
            }

			GetAvailableTimeslots outgoingRequest = new GetAvailableTimeslots();
			outgoingRequest.setCareunitid(incomingHealthcarefacility);
			outgoingRequest.setCareunitidtype("hsaid");
			outgoingRequest.setExternaluser(EXTERNAL_USER);
			outgoingRequest.setTcpassword("");
			outgoingRequest.setTcusername("");
            //TakeCare eXchange can not handle xml declarations in CDATA so do not generate that.
            jaxbUtil_message.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
            //TakeCare eXchange can not handle namespaces in CDATA
            outgoingRequest.setXml(jaxbUtil_message.marshal(message, "", "ProfdocHISMessage"));
			//outgoingRequest.setXml(jaxbUtil_message.marshal(message));

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