package se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule;

import java.util.Date;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleType;
import se.skl.skltpservices.takecare.TakeCareDateHelper;
import se.skl.skltpservices.takecare.TakeCareRequestTransformer;
import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.booking.GetBookings;
import se.skl.skltpservices.takecare.booking.getbookingsrequest.ProfdocHISMessage;

public class GetSubjectOfCareScheduleRequestTransformer extends TakeCareRequestTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetSubjectOfCareScheduleRequestTransformer.class);

	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetSubjectOfCareScheduleType.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetBookings.class);

	/**
	 * Simple pojo transformer method that can be tested with plain unit
	 * testing...
	 */
	protected Object pojoTransform(Object src, String encoding) throws TransformerException {

		if (logger.isDebugEnabled()) {
			log.debug("Transforming request payload: {}", src);
		}

		try {

			GetSubjectOfCareScheduleType incomingRequest = (GetSubjectOfCareScheduleType) jaxbUtil_incoming
					.unmarshal(src);
			String incomingHealthcarefacility = incomingRequest.getHealthcareFacility();
			String incomingSubjectOfCare = incomingRequest.getSubjectOfCare();

			ProfdocHISMessage message = new ProfdocHISMessage();
			message.setCareUnitId(incomingHealthcarefacility);
			message.setCareUnitIdType(TakeCareUtil.HSAID);
			message.setInvokingSystem(TakeCareUtil.INVOKING_SYSTEM);
			message.setMsgType(TakeCareUtil.REQUEST);
			message.setBookingId(null);
			message.setPatientId(incomingSubjectOfCare);
			message.setTime(TakeCareDateHelper.yyyyMMddHHmmss(new Date()));

			GetBookings outgoingRequest = new GetBookings();
			outgoingRequest.setCareunitid(incomingHealthcarefacility);
			outgoingRequest.setCareunitidtype(TakeCareUtil.HSAID);
			outgoingRequest.setExternaluser(TakeCareUtil.EXTERNAL_USER);
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