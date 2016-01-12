package se.skl.skltpservices.takecare.takecareintegrationcomponent.getalltimetypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;
import static se.skl.skltpservices.takecare.takecaretestproducer.GetTimeTypesTestProducer.TEST_HEALTHCAREFACILITY_INVALID_ID;
import static se.skl.skltpservices.takecare.takecaretestproducer.GetTimeTypesTestProducer.TEST_HEALTHCAREFACILITY_OK;
import static se.skl.skltpservices.takecare.takecaretestproducer.GetTimeTypesTestProducer.TEST_ID_FAULT_TIMEOUT;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.soitoolkit.commons.mule.test.junit4.AbstractTestCase;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;

import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesResponseType;

public class GetAllTimeTypesIntegrationTest extends AbstractTestCase {

    private static final String EXPECTED_ERR_TIMEOUT_MSG = "Read timed out";
    private static final String DEFAULT_SERVICE_ADDRESS = getAddress("GETALLTIMETYPES_INBOUND_URL_1");
    private static final String SECOND_SERVICE_ADDRESS  = getAddress("GETALLTIMETYPES_INBOUND_URL_2");

    public GetAllTimeTypesIntegrationTest() {

        // Only start up Mule once to make the tests run faster...
        // Set to false if tests interfere with each other when Mule is started
        // only once.
        setDisposeContextPerClass(true);
    }

    protected String getConfigResources() {
        return "soitoolkit-mule-jms-connector-activemq-embedded.xml,"      + 
               "TakeCareIntegrationComponent-common.xml,"                  + 
               "TakeCareIntegrationComponent-integrationtests-common.xml," + 
               "GetAllTimeTypes-1-service.xml,"                            + 
               "GetAllTimeTypes-2-service.xml,"                            + 
               "teststub-services/GetTimeTypes-1-service.xml,"    +
               "teststub-services/GetTimeTypes-2-service.xml";
    }

    @Override
    protected void doSetUp() throws Exception {
        super.doSetUp();
    }

    @Test
    public void test_ok() throws Fault {
        String healthcareFacility = TEST_HEALTHCAREFACILITY_OK;

        GetAllTimeTypesTestConsumer consumer = new GetAllTimeTypesTestConsumer(DEFAULT_SERVICE_ADDRESS);
        GetAllTimeTypesResponseType response = consumer.callService(healthcareFacility);
        assertEquals("0", response.getListOfTimeTypes().get(0).getTimeTypeId());
        assertEquals("Tidstyp0", response.getListOfTimeTypes().get(0).getTimeTypeName());
        assertEquals("1", response.getListOfTimeTypes().get(1).getTimeTypeId());
        assertEquals("Tidstyp1", response.getListOfTimeTypes().get(1).getTimeTypeName());
        
        consumer = new GetAllTimeTypesTestConsumer(SECOND_SERVICE_ADDRESS);
        response = consumer.callService(healthcareFacility);
        assertEquals("0", response.getListOfTimeTypes().get(0).getTimeTypeId());
        assertEquals("Tidstyp0", response.getListOfTimeTypes().get(0).getTimeTypeName());
        assertEquals("1", response.getListOfTimeTypes().get(1).getTimeTypeId());
        assertEquals("Tidstyp1", response.getListOfTimeTypes().get(1).getTimeTypeName());
    }

    @Test
    public void test_fault_invalidInput() throws Exception {
        try {
            String healthcareFacility = TEST_HEALTHCAREFACILITY_INVALID_ID;

            GetAllTimeTypesTestConsumer consumer = new GetAllTimeTypesTestConsumer(DEFAULT_SERVICE_ADDRESS);
            GetAllTimeTypesResponseType response = consumer.callService(healthcareFacility);

            fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));

        } catch (SOAPFaultException e) {
            assertEquals("resultCode: 3001 resultText: Illegal argument!", e.getMessage());
        }
    }

    @Test
    public void test_fault_timeout() throws Fault {
        try {
            String healthcareFacility = TEST_ID_FAULT_TIMEOUT;

            GetAllTimeTypesTestConsumer consumer = new GetAllTimeTypesTestConsumer(DEFAULT_SERVICE_ADDRESS);
            GetAllTimeTypesResponseType response = consumer.callService(healthcareFacility);

            fail("expected fault, but got a response of type: " + ((response == null) ? "NULL" : response.getClass().getName()));
        } catch (SOAPFaultException e) {
            assertTrue("Unexpected error message: " + e.getMessage(), e.getMessage().startsWith(EXPECTED_ERR_TIMEOUT_MSG));
        }
    }
}
