package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.toTakeCareLongTime;
import static se.skl.skltpservices.takecare.TakeCareDateHelper.yyyyMMddHHmmss;
import static se.skl.skltpservices.takecare.TakeCareUtil.EXTERNAL_USER;
import static se.skl.skltpservices.takecare.TakeCareUtil.HSAID;
import static se.skl.skltpservices.takecare.TakeCareUtil.INVOKING_SYSTEM;
import static se.skl.skltpservices.takecare.TakeCareUtil.REQUEST;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToBigInteger;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToInt;

import java.util.Date;
import org.mule.api.MuleMessage;

import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.makebooking.v1.MakeBookingType;
import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.TakeCareRequestTransformer;
import se.skl.skltpservices.takecare.booking.MakeBooking;
import se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage;

public class MakeBookingRequestTransformer extends TakeCareRequestTransformer {

    private static final Logger log = LoggerFactory.getLogger(MakeBookingRequestTransformer.class);
    private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(MakeBookingType.class);
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(MakeBooking.class);

    /**
     * Simple pojo transformer method that can be tested with plain unit
     * testing...
     */
    protected Object pojoTransform(MuleMessage muleMessage, Object src, String encoding) throws TransformerException {

        if (logger.isDebugEnabled()) {
            log.debug("Transforming request payload: {}", src);
        }

        try {
            MakeBookingType incomingRequest = (MakeBookingType) jaxbUtil_incoming.unmarshal(src);
            TimeslotType incomingTimeslot = incomingRequest.getRequestedTimeslot();
            SubjectOfCareType subjectOfCare = incomingRequest.getSubjectOfCareInfo();

            String incomingHealthcarefacility = incomingTimeslot.getHealthcareFacility();
            String incominStartTime = incomingTimeslot.getStartTimeInclusive();
            String incominEndTime = incomingTimeslot.getEndTimeExclusive();
            String incomingTimeTypeId = incomingTimeslot.getTimeTypeID();
            String incomingResourceId = incomingTimeslot.getResourceID();
            String incomingSubjectOfCare = incomingTimeslot.getSubjectOfCare();
            String incomingReason = buildReason(subjectOfCare, incomingTimeslot);
            if (muleMessage != null) {
                if (incomingRequest.getSubjectOfCareInfo().getPhone().length() > 0) {
                    muleMessage.setProperty("telephone", new Boolean(true), PropertyScope.SESSION);
                } else {
                    muleMessage.setProperty("telephone", new Boolean(false), PropertyScope.SESSION);
                }
            }

            ProfdocHISMessage message = new ProfdocHISMessage();
            message.setCareUnitId(incomingHealthcarefacility);
            message.setCareUnitIdType(HSAID);
            message.setInvokingSystem(INVOKING_SYSTEM);
            message.setMsgType(REQUEST);
            message.setEndTime(numericToBigInteger(toTakeCareLongTime(incominEndTime)));
            message.setPatientId(numericToBigInteger(incomingSubjectOfCare));
            message.setPatientReason(incomingReason);
            message.setResourceId(numericToBigInteger(incomingResourceId));
            message.setStartTime(numericToBigInteger(toTakeCareLongTime(incominStartTime)));
            message.setTime(yyyyMMddHHmmss(new Date()));
            message.setTimeTypeId(numericToInt(incomingTimeTypeId));

            MakeBooking outgoingRequest = new MakeBooking();
            outgoingRequest.setCareunitid(incomingHealthcarefacility);
            outgoingRequest.setCareunitidtype(HSAID);
            outgoingRequest.setExternaluser(EXTERNAL_USER);
            outgoingRequest.setTcpassword("");
            outgoingRequest.setTcusername("");
            //TakeCare eXchange can not handle xml declarations in CDATA so do not generate that.
            jaxbUtil_message.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
            //TakeCare eXchange can not handle namespaces in CDATA
            outgoingRequest.setXml(jaxbUtil_message.marshal(message, "", "ProfdocHISMessage"));
            //outgoingRequest.setXml(jaxbUtil_message.marshal(message));

            Object outgoingPayload = jaxbUtil_outgoing.marshal(outgoingRequest);

            if (logger.isDebugEnabled()) {
                logger.debug("transformed payload to: " + outgoingPayload);
            }
            return outgoingPayload;
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
    }
}