package se.skl.skltpservices.takecare.takecareintegrationcomponent.updatebooking;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.toTakeCareLongTime;
import static se.skl.skltpservices.takecare.TakeCareDateHelper.yyyyMMddHHmmss;
import static se.skl.skltpservices.takecare.TakeCareUtil.EXTERNAL_USER;
import static se.skl.skltpservices.takecare.TakeCareUtil.HSAID;
import static se.skl.skltpservices.takecare.TakeCareUtil.INVOKING_SYSTEM;
import static se.skl.skltpservices.takecare.TakeCareUtil.REQUEST;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToBigInteger;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToInt;

import java.util.Date;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.updatebooking.v1.UpdateBookingType;
import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.TakeCareRequestTransformer;
import se.skl.skltpservices.takecare.booking.RescheduleBooking;
import se.skl.skltpservices.takecare.booking.reschedulebookingrequest.ProfdocHISMessage;

public class UpdateBookingRequestTransformer extends TakeCareRequestTransformer {

	private static final Logger log = LoggerFactory.getLogger(UpdateBookingRequestTransformer.class);

	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(UpdateBookingType.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(RescheduleBooking.class);

	/**
	 * Simple pojo transformer method that can be tested with plain unit
	 * testing...
	 */
	protected Object pojoTransform(Object src, String encoding) throws TransformerException {

		if (logger.isDebugEnabled()) {
			log.debug("Transforming request payload: {}", src);
		}

		try {
			UpdateBookingType incomingRequest = (UpdateBookingType) jaxbUtil_incoming.unmarshal(src);
			TimeslotType incomingTimeslot = incomingRequest.getRequestedTimeslot();
			SubjectOfCareType subjectOfCare = incomingRequest.getSubjectOfCareInfo();

			String incomingHealthcarefacility = incomingTimeslot.getHealthcareFacility();
			String incominStartTime = incomingTimeslot.getStartTimeInclusive();
			String incominEndTime = incomingTimeslot.getEndTimeExclusive();
			String incomingTimeTypeId = incomingTimeslot.getTimeTypeID();
			String incomingResourceId = incomingTimeslot.getResourceID();
			String incomingSubjectOfCare = incomingTimeslot.getSubjectOfCare();
			String incomingReason = buildReason(subjectOfCare, incomingTimeslot);
			String bookingId = incomingTimeslot.getBookingId();

			ProfdocHISMessage message = new ProfdocHISMessage();
			message.setCareUnitId(incomingHealthcarefacility);
			message.setCareUnitIdType(HSAID);
			message.setInvokingSystem(INVOKING_SYSTEM);
			message.setMsgType(REQUEST);
			message.setEndTime(numericToBigInteger(toTakeCareLongTime(incominEndTime)));
			message.setPatientId(numericToBigInteger(incomingSubjectOfCare));
			message.setPatientReason(incomingReason);
			message.setResourceId(numericToBigInteger(incomingResourceId));
			message.setStartTime(numericToBigInteger(toTakeCareLongTime(incominStartTime)));
			message.setTime(yyyyMMddHHmmss(new Date()));
			message.setBookingId(bookingId);
			message.setTimeTypeId(numericToInt(incomingTimeTypeId));

			RescheduleBooking outgoingRequest = new RescheduleBooking();
			outgoingRequest.setCareunitid(incomingHealthcarefacility);
			outgoingRequest.setCareunitidtype(HSAID);
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