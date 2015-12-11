package se.skl.skltpservices.takecare.takecareintegrationcomponent.getalltimetypes;

import static se.skl.skltpservices.takecare.TakeCareUtil.HSAID;
import static se.skl.skltpservices.takecare.TakeCareUtil.RESPONSE;

import java.util.Date;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.skl.skltpservices.takecare.TakeCareTestProducer;
import se.skl.skltpservices.takecare.booking.BookingSoap;
import se.skl.skltpservices.takecare.booking.gettimetypesresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.gettimetypesresponse.ProfdocHISMessage.TimeTypes;
import se.skl.skltpservices.takecare.booking.gettimetypesresponse.ProfdocHISMessage.TimeTypes.TimeType;

@WebService(targetNamespace = "http://tempuri.org/", name = "BookingSoap", portName = "BookingSoap")
public class GetTimeTypesTestProducer extends TakeCareTestProducer implements BookingSoap {

    public static final String TEST_HEALTHCAREFACILITY_OK = "HSA-VKK123";
    public static final String TEST_HEALTHCAREFACILITY_INVALID_ID = "-1";
    public static final String TEST_ID_FAULT_TIMEOUT = "0";
    private static final Logger log = LoggerFactory.getLogger(GetTimeTypesTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("TakeCareIntegrationComponent-config");
    private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(ProfdocHISMessage.class);

    public String getTimeTypes(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {

        if (TEST_HEALTHCAREFACILITY_INVALID_ID.equals(careunitid)) {
            return createErrorResponse(externaluser, careunitid);
        } else if (TEST_ID_FAULT_TIMEOUT.equals(careunitid)) {
            try {
                Thread.sleep(SERVICE_TIMOUT_MS + 1000);
            } catch (InterruptedException e) {
            }
        }
        String result = createOkResponse(externaluser, careunitid);
        
        log.debug("result of getTimeTypes():" + result);
        
        return result;
    }

    private String createOkResponse(String externaluser, String careunitid) {
        ProfdocHISMessage outgoing_response = new ProfdocHISMessage();
        outgoing_response.setCareUnit(careunitid);
        outgoing_response.setCareUnitType(HSAID);
        outgoing_response.setMethod("Booking.GetTimeTypes");
        outgoing_response.setMsgType(RESPONSE);
        outgoing_response.setSystem("ProfdocHIS");
        outgoing_response.setSystemInstance(0);
        outgoing_response.setTime(yyyyMMddHHmmss(new Date()));
        outgoing_response.setUser(externaluser);
        outgoing_response.setTimeTypes(buildTimeTypes(careunitid));
        jaxbUtil_outgoing.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
        return jaxbUtil_outgoing.marshal(outgoing_response, "", "ProfdocHISMessage");
    }

    private TimeTypes buildTimeTypes(String careunitId) {
        TimeTypes timeTypes = new TimeTypes();
        timeTypes.setCareUnitId(careunitId);
        timeTypes.setCareUnitIdType(HSAID);
        timeTypes.setCareUnitName("Careunit name");

        timeTypes.getTimeType().add(createTimeType(0, "Tidstyp0"));
        timeTypes.getTimeType().add(createTimeType(1, "Tidstyp1"));
        return timeTypes;
    }

    private TimeType createTimeType(int timeTypeId, String timeTypeName) {
        TimeType timeType = new TimeType();
        timeType.setTimeTypeId(timeTypeId);
        timeType.setTimeTypeName(timeTypeName);
        return timeType;
    }

}
