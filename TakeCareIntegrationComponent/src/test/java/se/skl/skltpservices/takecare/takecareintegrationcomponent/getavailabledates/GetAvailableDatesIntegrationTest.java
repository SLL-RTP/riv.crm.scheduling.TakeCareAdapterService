package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates.GetAvailableDatesTestProducer.TEST_HEALTHCAREFACILITY_INVALID_ID;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates.GetAvailableDatesTestProducer.TEST_HEALTHCAREFACILITY_OK;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates.GetAvailableDatesTestProducer.TEST_ID_FAULT_TIMEOUT;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesResponseType;

public class GetAvailableDatesIntegrationTest extends AbstractTestCase {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableDatesIntegrationTest.class);

	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("GETAVAILABLEDATES_INBOUND_URL");

	public GetAvailableDatesIntegrationTest() {

		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," +

		"TakeCareIntegrationComponent-common.xml," + "TakeCareIntegrationComponent-integrationtests-common.xml," +
		// FIXME. MULE STUDIO.
		// "services/GetAvailableDates-service.xml," +
				"GetAvailableDates-service.xml," + "teststub-services/GetAvailableDates-teststub-service.xml";
	}

	@Override
	protected void doSetUp() throws Exception {
		super.doSetUp();
	}

	@Test
	public void test_ok() throws Fault {

		String careTypeId = "191414141414";
		String healthcareFacility = TEST_HEALTHCAREFACILITY_OK;

		GetAvailableDatesTestConsumer consumer = new GetAvailableDatesTestConsumer(DEFAULT_SERVICE_ADDRESS);
		GetAvailableDatesResponseType response = consumer.callService(careTypeId, healthcareFacility);

		// These are set by Take Care
		assertNotNull(response.getPerformerAvailabilityByDate().get(0).getDate());
		assertEquals("HSA-VKK123", response.getPerformerAvailabilityByDate().get(0).getHealthcareFacility());
		assertEquals("HSA-VKK123", response.getPerformerAvailabilityByDate().get(0).getResourceName());
		assertEquals("0", response.getPerformerAvailabilityByDate().get(0).getTimeTypeID());

		// Theses are not set by Take Care
		assertNull(response.getPerformerAvailabilityByDate().get(0).getPerformer());
		assertNull(response.getPerformerAvailabilityByDate().get(0).getResourceID());
		assertNull(response.getPerformerAvailabilityByDate().get(0).getTimeTypeName());
		assertNull(response.getPerformerAvailabilityByDate().get(0).getCareTypeID());
		assertNull(response.getPerformerAvailabilityByDate().get(0).getCareTypeName());
	}

	@Test
	public void test_fault_invalidInput() throws Exception {
		try {
			String careTypeId = "191414141414";
			String healthcareFacility = TEST_HEALTHCAREFACILITY_INVALID_ID;

			GetAvailableDatesTestConsumer consumer = new GetAvailableDatesTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(careTypeId, healthcareFacility);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {

			assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());

		}
	}

	@Test
	public void test_fault_timeout() throws Fault {
		try {
			String careTypeId = "191414141414";
			String healthcareFacility = TEST_ID_FAULT_TIMEOUT;

			GetAvailableDatesTestConsumer consumer = new GetAvailableDatesTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(careTypeId, healthcareFacility);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {
			assertTrue("Unexpected error message: " + e.getMessage(),
					e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
		}

		// Sleep for a short time period to allow the JMS response message to be
		// delivered, otherwise ActiveMQ data store seems to be corrupt
		// afterwards...
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
	}

}
