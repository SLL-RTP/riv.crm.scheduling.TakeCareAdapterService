package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking.MakeBookingTestProducer.TEST_CAREUNIT_INVALID_ID;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking.MakeBookingTestProducer.TEST_CAREUNIT_OK;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking.MakeBookingTestProducer.TEST_ID_FAULT_TIMEOUT;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.makebooking.v1.MakeBookingResponseType;

public class MakeBookingIntegrationTest extends AbstractTestCase {

    private static final Logger log = LoggerFactory.getLogger(MakeBookingIntegrationTest.class);
    private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
    private static final String DEFAULT_SERVICE_ADDRESS = getAddress("MAKEBOOKING_INBOUND_URL_1");
    private static final String SECOND_SERVICE_ADDRESS = getAddress("MAKEBOOKING_INBOUND_URL_2");

    public MakeBookingIntegrationTest() {

        // Only start up Mule once to make the tests run faster...
        // Set to false if tests interfere with each other when Mule is started
        // only once.
        setDisposeContextPerClass(true);
    }

    protected String getConfigResources() {
        return "soitoolkit-mule-jms-connector-activemq-embedded.xml,"      + 
               "TakeCareIntegrationComponent-common.xml,"                  + 
               "TakeCareIntegrationComponent-integrationtests-common.xml," + 
               "MakeBooking-1-service.xml,"                            + 
               "MakeBooking-2-service.xml,"                            + 
               "teststub-services/MakeBooking-1-service.xml," +
               "teststub-services/MakeBooking-2-service.xml";
    }

    @Override
    protected void doSetUp() throws Exception {
        super.doSetUp();
    }

    @Test
    public void test_ok() throws Fault {
        String healthcareFacility = TEST_CAREUNIT_OK;
        String subjectOfCare = "191414141414";
        MakeBookingTestConsumer consumer = new MakeBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
        MakeBookingResponseType response = consumer.callService(healthcareFacility, subjectOfCare, false);
        assertNotNull(response.getBookingId());
        assertEquals("OK", response.getResultCode().toString());
        assertEquals("", response.getResultText());
        
        consumer = new MakeBookingTestConsumer(SECOND_SERVICE_ADDRESS);
        response = consumer.callService(healthcareFacility, subjectOfCare, false);
        assertNotNull(response.getBookingId());
        assertEquals("OK", response.getResultCode().toString());
        assertEquals("", response.getResultText());
    }

    @Test
    public void test_fault_invalidInput() throws Exception {
        try {
            String healthcareFacility = TEST_CAREUNIT_INVALID_ID;
            String subjectOfCare = "191414141414";

            MakeBookingTestConsumer consumer = new MakeBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
            Object response = consumer.callService(healthcareFacility, subjectOfCare, true);
            fail("expected fault, but got a response of type: "
                    + ((response == null) ? "NULL" : response.getClass().getName()));
        } catch (SOAPFaultException e) {
            assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());
        }
    }

    @Test
    public void test_fault_timeout() throws Fault {
        try {
            String healthcareFacility = TEST_ID_FAULT_TIMEOUT;
            String subjectOfCare = "191414141414";

            MakeBookingTestConsumer consumer = new MakeBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
            Object response = consumer.callService(healthcareFacility, subjectOfCare, true);
            fail("expected fault, but got a response of type: "
                    + ((response == null) ? "NULL" : response.getClass().getName()));
        } catch (SOAPFaultException e) {
            assertTrue("Unexpected error message: " + e.getMessage(),
                    e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
        }
    }

    @Test
    public void test_with_telephonenumber() throws Fault {
        String healthcareFacility = TEST_CAREUNIT_OK;
        String subjectOfCare = "191414141414";
        MakeBookingTestConsumer consumer = new MakeBookingTestConsumer(DEFAULT_SERVICE_ADDRESS);
        MakeBookingResponseType response = consumer.callService(healthcareFacility, subjectOfCare, true);

        assertNotNull(response.getBookingId());
        assertEquals("INFO", response.getResultCode().toString());
        assertEquals("Tyvärr kunde inte telefonnumret sparas med bokningen", response.getResultText());
    }
}
