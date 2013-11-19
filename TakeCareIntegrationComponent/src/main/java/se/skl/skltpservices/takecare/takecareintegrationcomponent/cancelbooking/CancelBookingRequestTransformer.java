package se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.yyyyMMddHHmmss;

import java.util.Date;
import org.mule.api.MuleMessage;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingType;
import se.skl.skltpservices.takecare.TakeCareRequestTransformer;
import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.booking.CancelBooking;
import se.skl.skltpservices.takecare.booking.cancelbookingrequest.ProfdocHISMessage;

import static se.skl.skltpservices.takecare.TakeCareUtil.*;

public class CancelBookingRequestTransformer extends TakeCareRequestTransformer {

    private static final Logger log = LoggerFactory.getLogger(CancelBookingRequestTransformer.class);
    private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(CancelBookingType.class);
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(CancelBooking.class);

    protected Object pojoTransform(MuleMessage muleMessage, Object src, String encoding) throws TransformerException {

        //if (logger.isDebugEnabled()) {
        log.info("Transforming request payload: {}", src);
        //}

        try {

            CancelBookingType incomingRequest = (CancelBookingType) jaxbUtil_incoming.unmarshal(src);
            String incomingBookingId = incomingRequest.getBookingId();
            String incomingMessage = incomingRequest.getMessage();
            String incomingHealthcarefacility = incomingRequest.getHealthcareFacility();

            ProfdocHISMessage message = new ProfdocHISMessage();
            message.setMsgType(REQUEST);
            message.setBookingId(incomingBookingId);
            message.setInvokingSystem(TakeCareUtil.INVOKING_SYSTEM);
            message.setPatientReason(incomingMessage);
            message.setTime(yyyyMMddHHmmss(new Date()));

            CancelBooking outgoingRequest = new CancelBooking();
            outgoingRequest.setCareunitid(incomingHealthcarefacility);
            outgoingRequest.setCareunitidtype("hsaid");
            outgoingRequest.setExternaluser(TakeCareUtil.EXTERNAL_USER);
            outgoingRequest.setTcpassword("");
            outgoingRequest.setTcusername("");
            //TakeCare eXchange can not handle xml declarations in CDATA so do not generate that.
            jaxbUtil_message.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
            //TakeCare eXchange can not handle namespaces in CDATA
            outgoingRequest.setXml(jaxbUtil_message.marshal(message, "", "ProfdocHISMessage"));
            //outgoingRequest.setXml(jaxbUtil_message.marshal(message));

            Object outgoingPayload = jaxbUtil_outgoing.marshal(outgoingRequest);

            //if (logger.isDebugEnabled()) {
            logger.info("transformed payload to: " + outgoingPayload);
            //}

            return outgoingPayload;

        } catch (Exception e) {
            throw new TransformerException(this, e);
        }

    }
}