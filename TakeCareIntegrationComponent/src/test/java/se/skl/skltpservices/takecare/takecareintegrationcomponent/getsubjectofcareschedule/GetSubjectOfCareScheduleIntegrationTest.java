package se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule;

import static org.junit.Assert.*;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule.GetBookingsTestProducer.TEST_ID_FAULT_TIMEOUT;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule.GetBookingsTestProducer.TEST_SUBJECTOFCARE_INVALID_ID;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule.GetBookingsTestProducer.TEST_SUBJECTOFCARE_OK;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleResponseType;

public class GetSubjectOfCareScheduleIntegrationTest extends AbstractTestCase {

	private static final Logger log = LoggerFactory.getLogger(GetSubjectOfCareScheduleIntegrationTest.class);

	private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";

	private static final String DEFAULT_SERVICE_ADDRESS = getAddress("GETSUBJECTOFCARESCHEDULE_INBOUND_URL");

	public GetSubjectOfCareScheduleIntegrationTest() {

		// Only start up Mule once to make the tests run faster...
		// Set to false if tests interfere with each other when Mule is started
		// only once.
		setDisposeContextPerClass(true);
	}

	protected String getConfigResources() {
		return "soitoolkit-mule-jms-connector-activemq-embedded.xml," +

		"TakeCareIntegrationComponent-common.xml,"
				+ "TakeCareIntegrationComponent-integrationtests-common.xml,"
				+
				// FIXME. MULE STUDIO.
				// "services/GetSubjectOfCareSchedule-service.xml," +
				"GetSubjectOfCareSchedule-service.xml,"
				+ "teststub-services/GetSubjectOfCareSchedule-teststub-service.xml";
	}

	@Override
	protected void doSetUp() throws Exception {
		super.doSetUp();
	}

	@Test
	public void test_ok() throws Fault {
		String subjectOfCare = TEST_SUBJECTOFCARE_OK;
		String healtcareFacility = "HSA-VKK123";
		GetSubjectOfCareScheduleTestConsumer consumer = new GetSubjectOfCareScheduleTestConsumer(
				DEFAULT_SERVICE_ADDRESS);

		GetSubjectOfCareScheduleResponseType response = consumer.callService(healtcareFacility, subjectOfCare);
		assertNotNull(response.getTimeslotDetail());
		assertEquals(subjectOfCare, response.getTimeslotDetail().get(0).getSubjectOfCare());
		assertEquals(subjectOfCare, response.getTimeslotDetail().get(1).getSubjectOfCare());
	}

	@Test
	public void test_fault_invalidInput() throws Exception {
		try {
			String subjectOfCare = TEST_SUBJECTOFCARE_INVALID_ID;
			String healtcareFacility = "HSA-VKK123";
			GetSubjectOfCareScheduleTestConsumer consumer = new GetSubjectOfCareScheduleTestConsumer(
					DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(healtcareFacility, subjectOfCare);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {

			assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());

		}
	}

	@Test
	public void test_fault_timeout() throws Fault {
		try {
			String subjectOfCare = TEST_ID_FAULT_TIMEOUT;
			String healtcareFacility = "HSA-VKK123";
			GetSubjectOfCareScheduleTestConsumer consumer = new GetSubjectOfCareScheduleTestConsumer(
					DEFAULT_SERVICE_ADDRESS);
			Object response = consumer.callService(healtcareFacility, subjectOfCare);
			fail("expected fault, but got a response of type: "
					+ ((response == null) ? "NULL" : response.getClass().getName()));
		} catch (SOAPFaultException e) {
			assertTrue("Unexpected error message: " + e.getMessage(),
					e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
		}
	}

}
