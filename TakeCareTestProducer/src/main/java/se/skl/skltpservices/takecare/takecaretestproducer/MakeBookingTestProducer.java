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

import se.skl.skltpservices.takecare.booking.BookingSoap;
import se.skl.skltpservices.takecare.booking.makebookingresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.makebookingresponse.ProfdocHISMessage.BookingConfirmation;

@WebService(targetNamespace = "http://tempuri.org/", 
                       name = "BookingSoap", 
                   portName = "BookingSoap")
public class MakeBookingTestProducer extends TakeCareTestProducer implements BookingSoap {

    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(ProfdocHISMessage.class);
    public static final String TEST_CAREUNIT_OK = "HSA-VKK123";
    public static final String TEST_CAREUNIT_INVALID_ID = "0";
    public static final String TEST_ID_FAULT_TIMEOUT = "-1";
    private static final Logger log = LoggerFactory.getLogger(MakeBookingTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("TakeCareTestProducer-config");
    private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));

    public String makeBooking(String tcusername, String tcpassword, String externaluser, String careunitidtype, String careunitid, String xml) {

        log.debug("Incoming username to TakeCare {}"      , tcusername);
        log.debug("Incoming password to TakeCare {}"      , tcpassword);
        log.debug("Incoming externaluser to TakeCare {}"  , externaluser);
        log.debug("Incoming careunitidtype to TakeCare {}", careunitidtype);
        log.debug("Incoming careunitid to TakeCare {}"    , careunitid);
        log.debug("Incoming xml to TakeCare {}"           , xml);

        // ProfdocHISMessage - request
        se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage incomingMessage
                = (se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage) 
                   super.unmarshalXmlToProfdocHISMessage(new se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage(), "urn:ProfdocHISMessage:MakeBooking:Request", xml);

        String incomingCareUnitId = incomingMessage.getCareUnitId();
        
        String patientReason = incomingMessage.getPatientReason();
        if (StringUtils.isNotBlank(patientReason)) {
            if (patientReason.length() > 300) {
                // this should never happen - should be trimmed by the adapter
                se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage error 
                = createErrorResponseMessage(incomingCareUnitId, externaluser);
                error.getError().setCode(3002);
                error.getError().setMsg("patient reason is longer than 300 characters");;
                error.getError().setType("System");
                log.error(error.getError().getMsg());
                return this.createErrorResponseXml(error);
            }
        }

        if (TEST_CAREUNIT_INVALID_ID.equals(incomingCareUnitId)) {
            return createErrorResponse(incomingCareUnitId, externaluser);
        } else if (TEST_ID_FAULT_TIMEOUT.equals(incomingCareUnitId)) {
            try {
                Thread.sleep(SERVICE_TIMOUT_MS + 1000);
            } catch (InterruptedException e) {
            }
        }

        // ProfdocHISMessage - response
        return createOkResponse(externaluser, careunitid, incomingMessage);
    }

    private String createOkResponse(String externaluser, String careunitid,
            se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage incomingMessage) {

        ProfdocHISMessage outgoingMessage = new ProfdocHISMessage();
        outgoingMessage.setCareUnit(careunitid);
        outgoingMessage.setCareUnitType(TakeCareUtil.HSAID);
        outgoingMessage.setMethod("Booking.MakeBooking");
        outgoingMessage.setMsgType(TakeCareUtil.RESPONSE);
        outgoingMessage.setSystem("ProfdocHIS");
        outgoingMessage.setSystemInstance(0);
        outgoingMessage.setTime(yyyyMMddHHmmss(new Date()));
        outgoingMessage.setUser(externaluser);

        outgoingMessage.setBookingConfirmation(createBookingConfirmation(incomingMessage));

        jaxbUtil_outgoing.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
        return jaxbUtil_outgoing.marshal(outgoingMessage, "", "ProfdocHISMessage");
    }

    private BookingConfirmation createBookingConfirmation(
            se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage incomingMessage) {

        BookingConfirmation bookingConfirmation = new BookingConfirmation();
        bookingConfirmation.setBookingId(UUID.randomUUID().toString());
        bookingConfirmation.setCareUnitId(incomingMessage.getCareUnitId());
        bookingConfirmation.setCareUnitIdType(TakeCareUtil.HSAID);
        bookingConfirmation.setCareUnitName("CareUnit name");

        bookingConfirmation.setPatientId(incomingMessage.getPatientId());
        bookingConfirmation.setPatientReason(incomingMessage.getPatientReason());

        // 1 - namngiven resurs
        // 2 - befattning
        // 3 - sängplats       
        // 4 - lokal/rum
        bookingConfirmation.setResourceId(new BigInteger("1"));
        bookingConfirmation.setResourceName("Namngiven resurs");
        bookingConfirmation.setResourceType(Short.valueOf("1"));

        bookingConfirmation.setStartTime(incomingMessage.getStartTime());
        bookingConfirmation.setTimeTypeId(1);
        bookingConfirmation.setTimeTypeName("TimeType1");
        bookingConfirmation.setEndTime(incomingMessage.getEndTime());

        return bookingConfirmation;
    }
}
