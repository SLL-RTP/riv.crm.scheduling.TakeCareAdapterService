package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesResponderInterface;
import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesResponderService;
import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesResponseType;
import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class GetAvailableDatesTestConsumer extends TakeCareTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableDatesTestConsumer.class);

	private GetAvailableDatesResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("GETAVAILABLEDATES_INBOUND_URL");
		String careTypeId = "191414141414";
		String healthcareFacility = "HSA-VKK123";

		GetAvailableDatesTestConsumer consumer = new GetAvailableDatesTestConsumer(serviceAddress);
		GetAvailableDatesResponseType response = consumer.callService(careTypeId, healthcareFacility);
		log.info("Returned value = " + response.getPerformerAvailabilityByDate().toString());
	}

	public GetAvailableDatesTestConsumer(String serviceAddress) {
		try {
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new GetAvailableDatesResponderService(url).getGetAvailableDatesResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public GetAvailableDatesResponseType callService(String careTypeId, String healthcareFacility) throws Fault {
		log.debug("Calling GetAvailableDates-service with careTypeId = {}, healthcareFacility {}", careTypeId,
				healthcareFacility);

		GetAvailableDatesType request = new GetAvailableDatesType();
		request.setBookingId(UUID.randomUUID().toString());
		request.setCareTypeID(careTypeId);
		request.setCareTypeName("Caretype name");
		request.setEndDateInclusive(yyyyMMddHHmmss(new Date()));
		request.setHealthcareFacility(healthcareFacility);
		request.setStartDateInclusive(yyyyMMddHHmmss(new Date()));
		request.setTimeTypeID("1");
		request.setTimeTypeName("Timetype name");

		return _service.getAvailableDates(new AttributedURIType(), request);
	}
}