package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabletimeslots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecaretestproducer.GetAvailableTimeslotsTestProducer.TEST_HEALTHCAREFACILITY_INVALID_ID;
import static se.skl.skltpservices.takecare.takecaretestproducer.GetAvailableTimeslotsTestProducer.TEST_HEALTHCAREFACILITY_OK;
import static se.skl.skltpservices.takecare.takecaretestproducer.GetAvailableTimeslotsTestProducer.TEST_ID_FAULT_TIMEOUT;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsResponseType;

public class GetAvailableTimeslotsIntegrationTest extends AbstractTestCase {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableTimeslotsIntegrationTest.class);

	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("GETAVAILABLETIMESLOTS_INBOUND_URL_1");
    private static final String SECOND_SERVICE_ADDRESS = getAddress("GETAVAILABLETIMESLOTS_INBOUND_URL_2");

	public GetAvailableTimeslotsIntegrationTest() {

		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	protected String getConfigResources() {
        return "soitoolkit-mule-jms-connector-activemq-embedded.xml,"      + 
               "TakeCareIntegrationComponent-common.xml,"                  + 
               "TakeCareIntegrationComponent-integrationtests-common.xml," + 
               "GetAvailableTimeslots-1-service.xml,"                            + 
               "GetAvailableTimeslots-2-service.xml,"                            + 
               "teststub-services/GetAvailableTimeslots-1-service.xml," +
               "teststub-services/GetAvailableTimeslots-2-service.xml";
	}

	@Override
	protected void doSetUp() throws Exception {
		super.doSetUp();
	}

	@Test
	public void test_ok() throws Fault {
		String healthCareFacility = TEST_HEALTHCAREFACILITY_OK;
		String careTypeId = "121212121212";
		GetAvailableTimeslotsTestConsumer consumer = new GetAvailableTimeslotsTestConsumer(DEFAULT_SERVICE_ADDRESS);
		GetAvailableTimeslotsResponseType response = consumer.callService(healthCareFacility, careTypeId);
		checkResponse(response);
		
        consumer = new GetAvailableTimeslotsTestConsumer(SECOND_SERVICE_ADDRESS);
        response = consumer.callService(healthCareFacility, careTypeId);
        checkResponse(response);
	}

	private void checkResponse(GetAvailableTimeslotsResponseType response) {
        assertEquals("HSA-VKK123", response.getTimeslotDetail().get(0).getHealthcareFacility());
        assertEquals("careunit name", response.getTimeslotDetail().get(0).getHealthcareFacilityName());
        assertEquals("191212121212", response.getTimeslotDetail().get(0).getResourceID());
        assertEquals("Tolvansson Tolvan (läk)", response.getTimeslotDetail().get(0).getResourceName());
        assertEquals("4", response.getTimeslotDetail().get(0).getTimeTypeID());
        assertNotNull(response.getTimeslotDetail().get(0).getStartTimeInclusive());
        assertNotNull(response.getTimeslotDetail().get(0).getEndTimeExclusive());
        assertEquals("Nybesök Web", response.getTimeslotDetail().get(0).getTimeTypeName());
        assertEquals(false, response.getTimeslotDetail().get(0).isCancelBookingAllowed());
        assertEquals(true, response.getTimeslotDetail().get(0).isMessageAllowed());
        assertEquals(false, response.getTimeslotDetail().get(0).isRebookingAllowed());

        assertEquals("", response.getTimeslotDetail().get(0).getBookingId());
        assertEquals("", response.getTimeslotDetail().get(0).getCareTypeID());
        assertEquals("", response.getTimeslotDetail().get(0).getCareTypeName());
        assertEquals("", response.getTimeslotDetail().get(0).getPerformer());
        assertEquals("", response.getTimeslotDetail().get(0).getPerformerName());
        assertEquals("", response.getTimeslotDetail().get(0).getPurpose());
        assertEquals("", response.getTimeslotDetail().get(0).getReason());
        assertEquals("", response.getTimeslotDetail().get(0).getSubjectOfCare());
    }

    @Test
	public void test_fault_invalidInput() throws Exception {
		try {
			String healthCareFacility = TEST_HEALTHCAREFACILITY_INVALID_ID;
			String careTypeId = "121212121212";
			GetAvailableTimeslotsTestConsumer consumer = new GetAvailableTimeslotsTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(healthCareFacility, careTypeId);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {

			assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());

		}
	}

	@Test
	public void test_fault_timeout() throws Fault {
		try {
			String healthCareFacility = TEST_ID_FAULT_TIMEOUT;
			String careTypeId = "121212121212";
			GetAvailableTimeslotsTestConsumer consumer = new GetAvailableTimeslotsTestConsumer(DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(healthCareFacility, careTypeId);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {
			assertTrue("Unexpected error message: " + e.getMessage(),
					e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
		}
	}

}
