package se.skl.skltpservices.takecare.takecaretestproducer;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.skl.skltpservices.takecare.JaxbHelper;
import se.skl.skltpservices.takecare.TakeCareTestProducer;
import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.booking.BookingSoap;
import se.skl.skltpservices.takecare.booking.makebookingresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.makebookingresponse.ProfdocHISMessage.BookingConfirmation;
import se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking.MakeBookingRequestTransformer;

@WebService(targetNamespace = "http://tempuri.org/", name = "BookingSoap", portName = "BookingSoap")
public class MakeBookingTestProducer extends TakeCareTestProducer implements BookingSoap {

    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(
            se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(ProfdocHISMessage.class);
    public static final String TEST_CAREUNIT_OK = "HSA-VKK123";
    public static final String TEST_CAREUNIT_INVALID_ID = "0";
    public static final String TEST_ID_FAULT_TIMEOUT = "-1";
    private static final Logger log = LoggerFactory.getLogger(MakeBookingTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("TakeCareIntegrationComponent-config");
    private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));

    public String makeBooking(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {

        se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage incomingMessage = new 
                se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage();
        incomingMessage = (se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage)
                JaxbHelper.transform(incomingMessage, "urn:ProfdocHISMessage:MakeBooking:Request", xml);
        String incomingCareUnitId = incomingMessage.getCareUnitId();
        String patientReason = incomingMessage.getPatientReason();
        
        if (StringUtils.isNotBlank(patientReason)) {
            // this should never happen - should be trimmed by the adapter
            if (patientReason.length() > MakeBookingRequestTransformer.MAX_REASON_LENGTH) {
                se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage error 
                = createErrorResponseMessage(incomingCareUnitId, externaluser);
                error.getError().setCode(3002);
                error.getError().setMsg("patient reason is longer than " + MakeBookingRequestTransformer.MAX_REASON_LENGTH + " characters");;
                error.getError().setType("System");
                log.error(error.getError().getMsg());
                return this.createErrorResponseXml(error);
            }
        }

        if (TEST_CAREUNIT_INVALID_ID.equals(incomingCareUnitId)) {
            return createErrorResponse(externaluser, incomingCareUnitId);
        } else if (TEST_ID_FAULT_TIMEOUT.equals(incomingCareUnitId)) {
            try {
                Thread.sleep(SERVICE_TIMOUT_MS + 1000);
            } catch (InterruptedException e) {
            }
        }

        return createOkResponse(externaluser, careunitid, incomingMessage);

    }

    private String createOkResponse(String externaluser, String careunitid,
            se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage incomingBookingRequest) {

        ProfdocHISMessage outgoing_response = new ProfdocHISMessage();
        outgoing_response.setCareUnit(careunitid);
        outgoing_response.setCareUnitType(TakeCareUtil.HSAID);
        outgoing_response.setMethod("Booking.MakeBooking");
        outgoing_response.setMsgType(TakeCareUtil.RESPONSE);
        outgoing_response.setSystem("ProfdocHIS");
        outgoing_response.setSystemInstance(0);
        outgoing_response.setTime(yyyyMMddHHmmss(new Date()));
        outgoing_response.setUser(externaluser);

        outgoing_response.setBookingConfirmation(createBookingConfirmation(incomingBookingRequest));

        jaxbUtil_outgoing.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
        return jaxbUtil_outgoing.marshal(outgoing_response, "", "ProfdocHISMessage");
    }

    private BookingConfirmation createBookingConfirmation(
            se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage incomingBookingRequest) {

        BookingConfirmation bookingConfirmation = new BookingConfirmation();
        bookingConfirmation.setBookingId(UUID.randomUUID().toString());
        bookingConfirmation.setCareUnitId(incomingBookingRequest.getCareUnitId());
        bookingConfirmation.setCareUnitIdType(TakeCareUtil.HSAID);
        bookingConfirmation.setCareUnitName("CareUnit name");

        bookingConfirmation.setPatientId(incomingBookingRequest.getPatientId());
        bookingConfirmation.setPatientReason(incomingBookingRequest.getPatientReason());

        // ResourceType är ett heltal som anger vilken typ av resurs som
        // bokades. ResourceType har något av följande värden: 1 (namngiven
        // resurs), 2 (befattning), 3 (sängplats), eller 4 (lokal/rum).
        bookingConfirmation.setResourceId(new BigInteger("1"));
        bookingConfirmation.setResourceName("Namngiven resurs");
        bookingConfirmation.setResourceType(Short.valueOf("1"));

        bookingConfirmation.setStartTime(incomingBookingRequest.getStartTime());
        bookingConfirmation.setTimeTypeId(1);
        bookingConfirmation.setTimeTypeName("TimeType1");
        bookingConfirmation.setEndTime(incomingBookingRequest.getEndTime());

        return bookingConfirmation;
    }
}
