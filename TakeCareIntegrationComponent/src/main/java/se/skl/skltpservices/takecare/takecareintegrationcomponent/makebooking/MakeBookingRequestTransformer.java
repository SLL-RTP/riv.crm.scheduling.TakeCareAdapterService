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

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.makebooking.v1.MakeBookingType;
import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;
import se.skl.skltpservices.takecare.AbstractTakeCareRequestTransformer;
import se.skl.skltpservices.takecare.booking.MakeBooking;
import se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage;

public class MakeBookingRequestTransformer extends AbstractTakeCareRequestTransformer {

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
            MakeBookingType incomingRequest   = (MakeBookingType) jaxbUtil_incoming.unmarshal(src);
            TimeslotType incomingTimeslot     = incomingRequest.getRequestedTimeslot();
            if (incomingTimeslot == null) {
                throw new RuntimeException("missing requestedTimeslot in MakeBooking");
            }

            String incomingHealthcarefacility = incomingTimeslot.getHealthcareFacility();
            String incomingStartTime          = incomingTimeslot.getStartTimeInclusive();
            String incomingEndTime            = incomingTimeslot.getEndTimeExclusive();
            String incomingTimeTypeId         = incomingTimeslot.getTimeTypeID();
            String incomingResourceId         = incomingTimeslot.getResourceID();
            String incomingSubjectOfCare      = incomingTimeslot.getSubjectOfCare();
            
            String phone = "";
            SubjectOfCareType subjectOfCare = incomingRequest.getSubjectOfCareInfo();
            if (subjectOfCare != null && StringUtils.isNotEmpty(subjectOfCare.getPhone())) {
                phone = subjectOfCare.getPhone();
                muleMessage.setProperty("telephone", new Boolean(true), PropertyScope.SESSION);
            } else {
                muleMessage.setProperty("telephone", new Boolean(false), PropertyScope.SESSION);
            }
            String incomingReason = buildReason(incomingTimeslot, phone, log);

            ProfdocHISMessage message = new ProfdocHISMessage();
            message.setCareUnitId    (incomingHealthcarefacility);
            message.setCareUnitIdType(HSAID);
            message.setInvokingSystem(INVOKING_SYSTEM);
            message.setMsgType       (REQUEST);
            message.setEndTime       (numericToBigInteger(toTakeCareLongTime(incomingEndTime)));
            message.setPatientId     (numericToBigInteger(incomingSubjectOfCare));
            message.setPatientReason (incomingReason);
            message.setResourceId    (numericToBigInteger(incomingResourceId));
            message.setStartTime     (numericToBigInteger(toTakeCareLongTime(incomingStartTime)));
            message.setTime          (yyyyMMddHHmmss(new Date()));
            message.setTimeTypeId    (numericToInt(incomingTimeTypeId));

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

            String outgoingPayload = jaxbUtil_outgoing.marshal(outgoingRequest);

            if (logger.isDebugEnabled()) {
                logger.debug("transformed payload to: " + outgoingPayload);
            }
            return outgoingPayload;
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
    }
}