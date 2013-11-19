package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import javax.xml.bind.*;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.makebooking.v1.MakeBookingResponseType;
import se.riv.crm.scheduling.makebooking.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.ResultCodeEnum;
import se.skl.skltpservices.takecare.TakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.MakeBookingResponse;
import se.skl.skltpservices.takecare.booking.makebookingresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.makebookingresponse.ProfdocHISMessage.BookingConfirmation;

public class MakeBookingResponseTransformer extends TakeCareResponseTransformer {

    private static final Logger log = LoggerFactory.getLogger(MakeBookingResponseTransformer.class);
    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(MakeBookingResponse.class);
    private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(MakeBookingResponseType.class);

    /**
     * Simple pojo transformer that transforms Take Care format to
     * crm:scheduling 1.0.
     *
     * @param src
     * @param outputEncoding
     */
    public Object pojoTransform(Object src, String outputEncoding, Object property) throws TransformerException {
        if (logger.isDebugEnabled()) {
            log.debug("Transforming response payload: {}", src);
        }

        try {
            String incoming_string = extractResponse(src);
            handleTakeCareErrorMessages(incoming_string);
            Object payloadOut = transformResponse(incoming_string, property);

            if (logger.isDebugEnabled()) {
                logger.debug("transformed payload to: " + payloadOut);
            }
            return payloadOut;
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
    }

    private String extractResponse(Object src) {
        MakeBookingResponse incoming_res = (MakeBookingResponse) jaxbUtil_incoming.unmarshal(src);
        String incoming_string = incoming_res.getMakeBookingResult();
        return incoming_string;
    }

    private Object transformResponse(String incoming_string, Object property) {
        ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) super.transformResponse(message, "urn:ProfdocHISMessage:MakeBooking:Response", incoming_string);

        JAXBElement<MakeBookingResponseType> outgoing_res = new ObjectFactory()
                .createMakeBookingResponse(new MakeBookingResponseType());
        BookingConfirmation incoming_bookingconfirm = message.getBookingConfirmation();
        outgoing_res.getValue().setBookingId(incoming_bookingconfirm.getBookingId());
        if ((Boolean) property) {
            outgoing_res.getValue().setResultCode(ResultCodeEnum.INFO);
            outgoing_res.getValue().setResultText("Tyvärr kunde inte telefonnumret sparas med bokningen");
        } else {
            outgoing_res.getValue().setResultCode(ResultCodeEnum.OK);
            outgoing_res.getValue().setResultText("");
        }
        return jaxbUtil_outgoing.marshal(outgoing_res);
    }
}