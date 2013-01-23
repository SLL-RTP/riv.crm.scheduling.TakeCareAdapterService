package se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingResponderInterface;
import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingResponderService;
import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingResponseType;
import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingType;

public class CancelBookingTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(CancelBookingTestConsumer.class);

	private CancelBookingResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("CANCELBOOKING_INBOUND_URL");
		String bookingId = "1234567890";

		CancelBookingTestConsumer consumer = new CancelBookingTestConsumer(serviceAddress);
		CancelBookingResponseType response = consumer.callService(bookingId);
		log.info("Returned resulttext = " + response.getResultText());
		log.info("Returned resultcode = " + response.getResultCode());
	}

	public CancelBookingTestConsumer(String serviceAddress) {
		try {
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new CancelBookingResponderService(url).getCancelBookingResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public CancelBookingResponseType callService(String bookingId) throws Fault {
		log.debug("Calling CancelBooking-service with bookingId {}", bookingId);
		CancelBookingType request = new CancelBookingType();
		request.setBookingId(bookingId);
		request.setHealthcareFacility("HSA-VKK123");
		request.setMessage(null);
		return _service.cancelBooking(new AttributedURIType(), request);
	}
}