package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabletimeslots;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.toRivTaLongTime;
import static se.skl.skltpservices.takecare.TakeCareUtil.bigIntegerToString;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getavailabletimeslots.v1.GetAvailableTimeslotsResponseType;
import se.riv.crm.scheduling.getavailabletimeslots.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.TakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.GetAvailableTimeslotsResponse;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsresponse.ProfdocHISMessage.AvailableTimeslots;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsresponse.ProfdocHISMessage.AvailableTimeslots.Timeslot;

public class GetAvailableTimeslotsResponseTransformer extends TakeCareResponseTransformer {

    private static final Logger log = LoggerFactory.getLogger(GetAvailableTimeslotsResponseTransformer.class);

    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetAvailableTimeslotsResponse.class);
    private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAvailableTimeslotsResponseType.class);

    /**
     * Simple pojo transformer that transforms Take Care format to
     * crm:scheduling 1.0.
     *
     * @param src
     * @param outputEncoding
     */
    protected Object pojoTransform(Object src, String outputEncoding) throws TransformerException {

        if (logger.isDebugEnabled()) {
            log.debug("Transforming response payload: {}", src);
        }

        try {
            String incoming_string = extractResponse(src);
            handleTakeCareErrorMessages(incoming_string);
            Object payloadOut = transformResponse(incoming_string);

            if (logger.isDebugEnabled()) {
                logger.debug("transformed payload to: " + payloadOut);
            }

            return payloadOut;

        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
    }

    private String extractResponse(Object src) {
        GetAvailableTimeslotsResponse incoming_res = (GetAvailableTimeslotsResponse) jaxbUtil_incoming.unmarshal(src);
        String incoming_string = incoming_res.getGetAvailableTimeslotsResult();
        return incoming_string;
    }

    private Object transformResponse(String incoming_string) {
        ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) super.transformResponse(message, "urn:ProfdocHISMessage:GetAvailableTimeslots:Response", incoming_string);
        List<AvailableTimeslots> incoming_availableTimeslots = message.getAvailableTimeslots();

        AvailableTimeslots availableDates = incoming_availableTimeslots.get(0);
        String careUnitId = availableDates.getCareUnitId();
        String careunitName = availableDates.getCareUnitName();
        // String timeTypeId = availableDates.getCareUnitIdType();

        JAXBElement<GetAvailableTimeslotsResponseType> outgoing_res = new ObjectFactory()
                .createGetAvailableTimeslotsResponse(new GetAvailableTimeslotsResponseType());

        for (Timeslot timeSlot : availableDates.getTimeslot()) {
            TimeslotType timeslotType = new TimeslotType();
            timeslotType.setEndTimeExclusive(toRivTaLongTime(timeSlot.getEndTime()));
            timeslotType.setStartTimeInclusive(toRivTaLongTime(timeSlot.getStartTime()));
            timeslotType.setHealthcareFacility(careUnitId);
            timeslotType.setPerformer("");
            timeslotType.setBookingId("");
            timeslotType.setPurpose("");
            timeslotType.setReason("");
            timeslotType.setResourceName(timeSlot.getResourceName());
            timeslotType.setHealthcareFacilityName(careunitName);
            timeslotType.setPerformerName("");
            timeslotType.setResourceID(bigIntegerToString(timeSlot.getResourceId()));
            timeslotType.setTimeTypeName(timeSlot.getTimeTypeName());
            timeslotType.setTimeTypeID(String.valueOf(timeSlot.getTimeTypeId()));
            timeslotType.setCareTypeName("");
            timeslotType.setCareTypeID("");
            timeslotType.setCancelBookingAllowed(false);
            timeslotType.setRebookingAllowed(false);
            timeslotType.setMessageAllowed(true);
            timeslotType.setSubjectOfCare("");
            outgoing_res.getValue().getTimeslotDetail().add(timeslotType);
        }
        return jaxbUtil_outgoing.marshal(outgoing_res);
    }
}