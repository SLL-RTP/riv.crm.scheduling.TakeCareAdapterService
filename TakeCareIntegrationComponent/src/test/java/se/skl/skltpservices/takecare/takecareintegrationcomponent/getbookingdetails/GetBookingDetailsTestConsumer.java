package se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.getbookingdetails.v1.GetBookingDetailsResponderInterface;
import se.riv.crm.scheduling.getbookingdetails.v1.GetBookingDetailsResponderService;
import se.riv.crm.scheduling.getbookingdetails.v1.GetBookingDetailsResponseType;
import se.riv.crm.scheduling.getbookingdetails.v1.GetBookingDetailsType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class GetBookingDetailsTestConsumer extends TakeCareTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(GetBookingDetailsTestConsumer.class);

	private GetBookingDetailsResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("GETBOOKINGDETAILS_INBOUND_URL");
		String bookingId = "1234567890";
		String healthcareFacility = "HSA-VKK123";

		GetBookingDetailsTestConsumer consumer = new GetBookingDetailsTestConsumer(serviceAddress);
		GetBookingDetailsResponseType response = consumer.callService(healthcareFacility, bookingId);
		log.info("Returned value = " + response.getTimeslotDetail());
	}

	public GetBookingDetailsTestConsumer(String serviceAddress) {
		try {
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new GetBookingDetailsResponderService(url).getGetBookingDetailsResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public GetBookingDetailsResponseType callService(String healthcareFacility, String bookingId) throws Fault {
		log.debug("Calling GetBookingDetails-service with healthcareFacility {}, bookingId {}", healthcareFacility,
				bookingId);

		GetBookingDetailsType request = new GetBookingDetailsType();
		request.setHealthcareFacility(healthcareFacility);
		request.setBookingId(bookingId);

		return _service.getBookingDetails(new AttributedURIType(), request);
	}
}