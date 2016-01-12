package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.makebooking.v1.MakeBookingResponderInterface;
import se.riv.crm.scheduling.makebooking.v1.MakeBookingResponderService;
import se.riv.crm.scheduling.makebooking.v1.MakeBookingResponseType;
import se.riv.crm.scheduling.makebooking.v1.MakeBookingType;
import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class MakeBookingTestConsumer extends TakeCareTestConsumer {

    private static final Logger log = LoggerFactory.getLogger(MakeBookingTestConsumer.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private MakeBookingResponderInterface _service = null;

    public static void main(String[] args) throws Fault {
        String serviceAddress = getAddress("MAKEBOOKING_INBOUND_URL");
        String subjectOfCare = "191414141414";
        String healthcareFacility = "HSA-VKK123";

        MakeBookingTestConsumer consumer = new MakeBookingTestConsumer(serviceAddress);
        MakeBookingResponseType response = consumer.callService(healthcareFacility, subjectOfCare, true);
        log.info("Returned bookingId = " + response.getBookingId());
        log.info("Returned resulttext = " + response.getResultText());
        log.info("Returned resultcode = " + response.getResultCode());
    }

    public MakeBookingTestConsumer(String serviceAddress) {
        try {
            URL url = new URL(serviceAddress + "?wsdl");
            _service = new MakeBookingResponderService(url).getMakeBookingResponderPort();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
        }
    }
    
    public MakeBookingResponseType callService(String healthcareFacility, String subjectOfCare, String requestedTimeslotReason) throws Fault {
        log.debug("Calling MakeBooking-service with healthcareFacility {}, subjectOfCare {}", healthcareFacility, subjectOfCare);
        MakeBookingType request = new MakeBookingType();
        request.setRequestedTimeslot(createTimeslot(healthcareFacility, subjectOfCare));
        request.getRequestedTimeslot().setReason(requestedTimeslotReason);
        return callService(request, healthcareFacility);
    }

    /**
     * tfn is an ugly hack
     *
     * @param healthcareFacility
     * @param subjectOfCare
     * @param tfn
     * @return
     * @throws Fault
     */
    public MakeBookingResponseType callService(String healthcareFacility, String subjectOfCare, boolean tfn) throws Fault {
        log.debug("Calling MakeBooking-service with healthcareFacility {}, subjectOfCare {}", healthcareFacility, subjectOfCare);
        MakeBookingType request = new MakeBookingType();
        request.setRequestedTimeslot(createTimeslot(healthcareFacility, subjectOfCare));
        request.setSubjectOfCareInfo(createSubjectOfCare(tfn));
        return callService(request, healthcareFacility);
    }
    
    private MakeBookingResponseType callService(MakeBookingType request, String healthcareFacility) {
        request.setHealthcareFacilityMed(healthcareFacility);
        request.setNotification("Notification value");
        return _service.makeBooking(new AttributedURIType(), request);
    }

    private SubjectOfCareType createSubjectOfCare(boolean tfn) {
        SubjectOfCareType subjectOfCare = new SubjectOfCareType();
        subjectOfCare.setAddress("En adress");
        subjectOfCare.setCoaddress("En CO adress");
        subjectOfCare.setEmail("email@email.dummy");
        if (tfn) {
            subjectOfCare.setPhone("0001112223333");
        } else {
            subjectOfCare.setPhone("");
        }
        return subjectOfCare;
    }

    private TimeslotType createTimeslot(String healthcareFacility, String subjectOfCare) {
        TimeslotType timeslot = new TimeslotType();
        timeslot.setCareTypeID("3");
        timeslot.setCareTypeName("Caretype name");
        timeslot.setHealthcareFacility(healthcareFacility);
        timeslot.setHealthcareFacilityName("Healtcare facility name");
        timeslot.setPerformer("Performer HSAID");
        timeslot.setPerformerName("Performer name");
        timeslot.setPurpose("Purpose for patient to book");
        timeslot.setReason("Reason for patient to book");
        timeslot.setResourceID("1");
        timeslot.setResourceName("Resource name");
        timeslot.setSubjectOfCare(subjectOfCare);
        timeslot.setTimeTypeID("0");
        timeslot.setTimeTypeName("Timetype name");

        timeslot.setStartTimeInclusive(dateFormat.format(new Date()));
        timeslot.setEndTimeExclusive(dateFormat.format(new Date()));
        return timeslot;
    }
}