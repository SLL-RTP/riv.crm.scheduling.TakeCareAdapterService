package se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails.GetBookingTestProducer.TEST_BOOKINGID_INVALID_ID;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails.GetBookingTestProducer.TEST_BOOKINGID_OK;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails.GetBookingTestProducer.TEST_ID_FAULT_TIMEOUT;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.getbookingdetails.v1.GetBookingDetailsResponseType;

public class GetBookingDetailsIntegrationTest extends AbstractTestCase {

	private static final Logger log = LoggerFactory.getLogger(GetBookingDetailsIntegrationTest.class);

	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("GETBOOKINGDETAILS_INBOUND_URL");

	public GetBookingDetailsIntegrationTest() {

		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," +

		"TakeCareIntegrationComponent-common.xml," + "TakeCareIntegrationComponent-integrationtests-common.xml," +
		// FIXME. MULE STUDIO.
		// "services/GetBookingDetails-service.xml," +
				"GetBookingDetails-service.xml," + "teststub-services/GetBookingDetails-teststub-service.xml";
	}

	@Override
	protected void doSetUp() throws Exception {
		super.doSetUp();
	}

	@Test
	public void test_ok() throws Fault {
		String bookingId = TEST_BOOKINGID_OK;
		String healtcareFacility = "HSA-VKK123";

		GetBookingDetailsTestConsumer consumer = new GetBookingDetailsTestConsumer(DEFAULT_SERVICE_ADDRESS);
		GetBookingDetailsResponseType response = consumer.callService(healtcareFacility, bookingId);

		assertNotNull(response.getTimeslotDetail());
		assertEquals(bookingId, response.getTimeslotDetail().getBookingId());
		assertEquals(healtcareFacility, response.getTimeslotDetail().getHealthcareFacility());
	}

	@Test
	public void test_fault_invalidInput() throws Exception {
		try {
			String bookingId = TEST_BOOKINGID_INVALID_ID;
			String healtcareFacility = "HSA-VKK123";

			GetBookingDetailsTestConsumer consumer = new GetBookingDetailsTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(healtcareFacility, bookingId);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {

			assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());

		}
	}

	@Test
	public void test_fault_timeout() throws Fault {
		try {
			String bookingId = TEST_ID_FAULT_TIMEOUT;
			String healtcareFacility = "HSA-VKK123";
			GetBookingDetailsTestConsumer consumer = new GetBookingDetailsTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(healtcareFacility, bookingId);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {
			assertTrue("Unexpected error message: " + e.getMessage(),
					e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
		}
	}

}
