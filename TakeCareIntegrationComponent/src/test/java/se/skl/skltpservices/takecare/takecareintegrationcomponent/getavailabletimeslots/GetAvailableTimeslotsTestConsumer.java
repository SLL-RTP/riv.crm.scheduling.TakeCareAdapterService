package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabletimeslots;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsResponderInterface;
import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsResponderService;
import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsResponseType;
import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class GetAvailableTimeslotsTestConsumer extends TakeCareTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableTimeslotsTestConsumer.class);

	private GetAvailableTimeslotsResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("GETAVAILABLETIMESLOTS_INBOUND_URL");
		String healthcareFacility = "HSA-VKK123";
		String careTypeId = "121212121212";

		GetAvailableTimeslotsTestConsumer consumer = new GetAvailableTimeslotsTestConsumer(serviceAddress);
		GetAvailableTimeslotsResponseType response = consumer.callService(healthcareFacility, careTypeId);
		log.info("Returned value = " + response.getTimeslotDetail());
	}

	public GetAvailableTimeslotsTestConsumer(String serviceAddress) {
		try {
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new GetAvailableTimeslotsResponderService(url).getGetAvailableTimeslotsResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public GetAvailableTimeslotsResponseType callService(String healthcareFacility, String careTypeId) throws Fault {
		log.debug("Calling GetAvailableTimeslots-service with healthcareFacility = {}, careTypeId = {}",
				healthcareFacility, careTypeId);
		GetAvailableTimeslotsType request = new GetAvailableTimeslotsType();
		request.setBookingId(UUID.randomUUID().toString());
		request.setCareTypeID(careTypeId);
		request.setCareTypeName("Tolvan Tolvansson");
		request.setEndDateInclusive(yyyyMMddHHmmss(new Date()));
		request.setHealthcareFacility(healthcareFacility);
		request.setStartDateInclusive(yyyyMMddHHmmss(new Date()));
		request.setTimeTypeID("4");
		request.setTimeTypeName("careTypeId");
		return _service.getAvailableTimeslots(new AttributedURIType(), request);
	}
}