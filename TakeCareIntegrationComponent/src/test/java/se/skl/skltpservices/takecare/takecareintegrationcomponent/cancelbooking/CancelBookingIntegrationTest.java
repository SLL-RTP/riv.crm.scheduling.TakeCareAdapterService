package se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking.CancelBookingTestProducer.TEST_BOOKINGID_OK;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking.CancelBookingTestProducer.TEST_BOOKING_INVALID_ID;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking.CancelBookingTestProducer.TEST_ID_FAULT_TIMEOUT;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingResponseType;

public class CancelBookingIntegrationTest extends AbstractTestCase {

	private static final Logger log = LoggerFactory.getLogger(CancelBookingIntegrationTest.class);

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("CANCELBOOKING_INBOUND_URL_1");
    private static final String SECOND_SERVICE_ADDRESS = getAddress("CANCELBOOKING_INBOUND_URL_2");

	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";

	public CancelBookingIntegrationTest() {

		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	protected String getConfigResources() {
        return "soitoolkit-mule-jms-connector-activemq-embedded.xml,"      + 
                "TakeCareIntegrationComponent-common.xml,"                  + 
                "TakeCareIntegrationComponent-integrationtests-common.xml," + 
                "CancelBooking-1-service.xml,"                            + 
                "CancelBooking-2-service.xml,"                            + 
                "teststub-services/CancelBooking-teststub-1-service.xml," +
                "teststub-services/CancelBooking-teststub-2-service.xml";
	}

	@Override
	protected void doSetUp() throws Exception {
		super.doSetUp();
	}

	//@Test
	public void test_ok() throws Fault {
		String bookingId = TEST_BOOKINGID_OK;
		CancelBookingTestConsumer consumer = new CancelBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
		CancelBookingResponseType response = consumer.callService(bookingId);
		assertEquals("OK", response.getResultCode().toString());
		assertEquals("CANCELLED", response.getResultText());
		
        consumer = new CancelBookingTestConsumer(SECOND_SERVICE_ADDRESS);
        response = consumer.callService(bookingId);
        assertEquals("OK", response.getResultCode().toString());
        assertEquals("CANCELLED", response.getResultText());
	}

	@Test
	public void test_fault_invalidInput() throws Exception {
		try {
			String bookingId = TEST_BOOKING_INVALID_ID;
			CancelBookingTestConsumer consumer = new CancelBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(bookingId);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {
			assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());
		}
	}

	//@Test
	public void test_fault_timeout() throws Fault {
		try {
			String bookingId = TEST_ID_FAULT_TIMEOUT;
			CancelBookingTestConsumer consumer = new CancelBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(bookingId);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {
			assertTrue("Unexpected error message: " + e.getMessage(),
					e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
		}

	}

}
