package se.skl.skltpservices.takecare.takecareintegrationcomponent.updatebooking;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.updatebooking.v1.UpdateBookingResponderInterface;
import se.riv.crm.scheduling.updatebooking.v1.UpdateBookingResponderService;
import se.riv.crm.scheduling.updatebooking.v1.UpdateBookingResponseType;
import se.riv.crm.scheduling.updatebooking.v1.UpdateBookingType;
import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class UpdateBookingTestConsumer extends TakeCareTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(UpdateBookingTestConsumer.class);

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private UpdateBookingResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("UPDATEBOOKING_INBOUND_URL");
		String subjectOfCare = "191414141414";
		String healthcareFacility = "HSA-VKK123";

		UpdateBookingTestConsumer consumer = new UpdateBookingTestConsumer(serviceAddress);
		UpdateBookingResponseType response = consumer.callService(healthcareFacility, subjectOfCare);
		log.info("Returned resulttext = " + response.getResultText());
		log.info("Returned resultcode = " + response.getResultCode());
	}

	public UpdateBookingTestConsumer(String serviceAddress) {
		try {
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new UpdateBookingResponderService(url).getUpdateBookingResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public UpdateBookingResponseType callService(String healthcareFacility, String subjectOfCare) throws Fault {
		log.debug("Calling UpdateBooking-service with healthcareFacility {}, subjectOfCare {}", healthcareFacility,
				subjectOfCare);
		UpdateBookingType request = new UpdateBookingType();
		request.setNotification("Notification value");
		request.setRequestedTimeslot(createTimeslot(healthcareFacility, subjectOfCare));
		request.setSubjectOfCareInfo(createSubjectOfCare());

		return _service.updateBooking(new AttributedURIType(), request);
	}

	private SubjectOfCareType createSubjectOfCare() {
		SubjectOfCareType subjectOfCare = new SubjectOfCareType();
		subjectOfCare.setAddress("En adress");
		subjectOfCare.setCoaddress("En CO adress");
		subjectOfCare.setEmail("email@email.dummy");
		subjectOfCare.setPhone("0001112223333");
		return subjectOfCare;
	}

	private TimeslotType createTimeslot(String healthcareFacility, String subjectOfCare) {
		TimeslotType timeslot = new TimeslotType();
		timeslot.setCareTypeID("3");
		timeslot.setCareTypeName("Caretype name");
		timeslot.setHealthcareFacility(healthcareFacility);
		timeslot.setHealthcareFacilityName("Healtcare facility name");
		timeslot.setPerformer("Performer HSAID");
		timeslot.setPerformerName("Performer name");
		timeslot.setPurpose("Purpose for patient to book");
		timeslot.setReason("Reason for patient to book");
		timeslot.setResourceID("1");
		timeslot.setResourceName("Resource name");
		timeslot.setSubjectOfCare(subjectOfCare);
		timeslot.setTimeTypeID("0");
		timeslot.setTimeTypeName("Timetype name");
		timeslot.setBookingId("191414141414-394-191212121212-20101101-800-1-93-30-4");

		timeslot.setStartTimeInclusive(dateFormat.format(new Date()));
		timeslot.setEndTimeExclusive(dateFormat.format(new Date()));
		return timeslot;
	}
}