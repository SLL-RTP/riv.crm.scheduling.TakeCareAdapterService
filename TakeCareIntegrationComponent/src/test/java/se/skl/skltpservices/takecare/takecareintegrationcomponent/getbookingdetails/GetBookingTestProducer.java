package se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails;

import java.math.BigInteger;
import java.util.Date;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;
import se.skl.skltpservices.takecare.JaxbHelper;

import se.skl.skltpservices.takecare.TakeCareTestProducer;
import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.booking.BookingSoap;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage.Bookings;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage.Bookings.Booking;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage.Bookings.Booking.Resources;
import se.skl.skltpservices.takecare.booking.getbookingsresponse.ProfdocHISMessage.Bookings.Booking.Resources.Resource;

@WebService(targetNamespace = "http://tempuri.org/", name = "BookingSoap", portName = "BookingSoap")
public class GetBookingTestProducer extends TakeCareTestProducer implements BookingSoap {

    public static final String TEST_BOOKINGID_OK = "194001079120";
    public static final String TEST_BOOKINGID_INVALID_ID = "194001079121";
    public static final String TEST_ID_FAULT_TIMEOUT = "194001079122";
    private static final Logger log = LoggerFactory.getLogger(GetBookingTestProducer.class);
    private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("TakeCareIntegrationComponent-config");
    private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));
    private final short NORMAL_BOOKING = 0;
    private final short GROUP_BOOKING = 1;
    private final short TEAM_BOOKING = 2;
    private final short GROUP_TEAM_BOOKING = 2;
    private final int TIMETYPE_NORMAL = 0;
    private final short RESOURCE_INAKTIV_RESURS = 0;
    private final short RESOURCE_NAMNGIVEN = 1;
    private final short RESOURCE_BEFATTNING = 2;
    private final short RESOURCE_SANGPLATS = 3;
    private final short RESOURCE_LOKAL_RUM = 4;
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(
            se.skl.skltpservices.takecare.booking.getbookingsrequest.ProfdocHISMessage.class);

    public String getBookings(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {

        se.skl.skltpservices.takecare.booking.getbookingsrequest.ProfdocHISMessage incomingMessage =
                new se.skl.skltpservices.takecare.booking.getbookingsrequest.ProfdocHISMessage();
        //jaxbUtil_incoming.unmarshal(xml);
        incomingMessage = (se.skl.skltpservices.takecare.booking.getbookingsrequest.ProfdocHISMessage)
                JaxbHelper.transform(incomingMessage, "urn:ProfdocHISMessage:GetBookings:Request", xml);

        String bookingId = incomingMessage.getBookingId();

        if (TEST_BOOKINGID_INVALID_ID.equals(bookingId)) {
            return createErrorResponse(externaluser, careunitid);
        } else if (TEST_ID_FAULT_TIMEOUT.equals(bookingId)) {
            try {
                Thread.sleep(SERVICE_TIMOUT_MS + 1000);
            } catch (InterruptedException e) {
            }
        }

        return createOkResponse(externaluser, careunitid, bookingId);
    }

    private String createOkResponse(String externaluser, String careunitid, String bookingId) {
        ProfdocHISMessage outgoing_response = new ProfdocHISMessage();
        outgoing_response.setCareUnit(careunitid);
        outgoing_response.setCareUnitType(TakeCareUtil.HSAID);
        outgoing_response.setMethod("Booking.Bookings");
        outgoing_response.setMsgType(TakeCareUtil.RESPONSE);
        outgoing_response.setSystem("ProfdocHIS");
        outgoing_response.setSystemInstance(0);
        outgoing_response.setTime(yyyyMMddHHmmss(new Date()));
        outgoing_response.setUser(externaluser);
        outgoing_response.getBookings().add(getBooking(careunitid, bookingId));
        jaxbUtil_outgoing.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
        return jaxbUtil_outgoing.marshal(outgoing_response, "", "ProfdocHISMessage");
    }

    private Bookings getBooking(String careUnitId, String bookingId) {
        Bookings bookings = new Bookings();
        bookings.setCareUnitId(careUnitId);
        bookings.setCareUnitIdType(TakeCareUtil.HSAID);
        bookings.setCareUnitName("Careunit name");
        bookings.getBooking().add(createBooking(NORMAL_BOOKING, bookingId));
        return bookings;
    }

    private Booking createBooking(short bookingType, String bookingId) {
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setBookingType(bookingType);
        booking.setCancelAllowed(Short.valueOf("0"));
        booking.setEndTime(yyyyMMddHHmm(new Date()));
        booking.setPatientId(new BigInteger("191414141414"));
        booking.setPatientReason("Patientens orsak till bokningen");
        booking.setRescheduleAllowed(Short.valueOf("1"));
        booking.setResources(createResources());
        booking.setStartTime(yyyyMMddHHmm(new Date()));
        booking.setTimeTypeId(TIMETYPE_NORMAL);
        booking.setTimeTypeName("NORMAL");
        return booking;
    }

    private Resources createResources() {
        Resources resources = new Resources();
        resources.getResource().add(createReqource("1", "Namngiven resurs 1"));
        resources.getResource().add(createReqource("2", "Namngiven resurs 2"));
        return resources;
    }

    private Resource createReqource(String id, String name) {
        Resource resource = new Resource();
        resource.setResourceId(new BigInteger(id));
        resource.setResourceName(name);
        resource.setResourceType(RESOURCE_NAMNGIVEN);
        return resource;
    }
}
