package se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcareschedule.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.TakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.GetBookingsResponse;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage.Bookings;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage.Bookings.Booking;
import static se.skl.skltpservices.takecare.TakeCareUtil.shortToBoolean;

public class GetSubjectOfCareScheduleResponseTransformer extends TakeCareResponseTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetSubjectOfCareScheduleResponseTransformer.class);

	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetBookingsResponse.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetSubjectOfCareScheduleResponseType.class);

	/**
	 * Simple pojo transformer that transforms Take Care format to
	 * crm:scheduling 1.0.
	 * 
	 * @param src
	 * @param outputEncoding
	 */
	public Object pojoTransform(Object src, String outputEncoding) throws TransformerException {

		if (logger.isDebugEnabled()) {
			log.debug("Transforming response payload: {}", src);
		}

		try {
			String incoming_string = extractResponse(src);
			handleTakeCareErrorMessages(incoming_string);
			Object payloadOut = transformResponse(incoming_string);

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + payloadOut);
			}

			return payloadOut;

		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}

	private String extractResponse(Object src) {
		GetBookingsResponse incoming_res = (GetBookingsResponse) jaxbUtil_incoming.unmarshal(src);
		String incoming_string = incoming_res.getGetBookingsResult();
		return incoming_string;
	}

	private Object transformResponse(String incoming_string) {
		ProfdocHISMessage message = (ProfdocHISMessage) jaxbUtil_message.unmarshal(incoming_string);

		JAXBElement<GetSubjectOfCareScheduleResponseType> outgoing_res = new ObjectFactory()
				.createGetSubjectOfCareScheduleResponse(new GetSubjectOfCareScheduleResponseType());

		List<Bookings> incoming_timeTypes = message.getBookings();
		if (!incoming_timeTypes.isEmpty()) {
			Bookings bookings = incoming_timeTypes.get(0);

			for (Booking booking : bookings.getBooking()) {

				TimeslotType timeslot = new TimeslotType();
				timeslot.setBookingId(booking.getBookingId());
				timeslot.setCancelBookingAllowed(shortToBoolean(booking.getCancelAllowed()));
				// timeslot.setCareTypeID(value);
				// timeslot.setCareTypeName(value);
				timeslot.setEndTimeExclusive(String.valueOf(booking.getEndTime()));
				timeslot.setHealthcareFacility(bookings.getCareUnitId());
				timeslot.setHealthcareFacilityName(bookings.getCareUnitName());
				// timeslot.setMessageAllowed(value);
				timeslot.setPerformer(String.valueOf(booking.getResources().getResource().get(0).getResourceType()));
				timeslot.setPerformerName(String.valueOf(booking.getResources().getResource().get(0).getResourceName()));
				// timeslot.setPurpose(value);
				timeslot.setReason(booking.getPatientReason());
				timeslot.setRebookingAllowed(shortToBoolean(booking.getRescheduleAllowed()));
				timeslot.setResourceID("");
				timeslot.setResourceName("");
				timeslot.setStartTimeInclusive(String.valueOf(booking.getStartTime()));
				timeslot.setSubjectOfCare(String.valueOf(booking.getPatientId()));
				timeslot.setTimeTypeID(String.valueOf(booking.getTimeTypeId()));
				timeslot.setTimeTypeName(booking.getTimeTypeName());

				outgoing_res.getValue().getTimeslotDetail().add(timeslot);
			}

		}

		return jaxbUtil_outgoing.marshal(outgoing_res);
	}
}