package se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking;

import javax.xml.bind.JAXBElement;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.cancelbooking.v1.CancelBookingResponseType;
import se.riv.crm.scheduling.cancelbooking.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.ResultCodeEnum;
import se.skl.skltpservices.takecare.TakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.CancelBookingResponse;
import se.skl.skltpservices.takecare.booking.cancelbookingresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.cancelbookingresponse.ProfdocHISMessage.BookingStatus;

public class CancelBookingResponseTransformer extends TakeCareResponseTransformer {

	private static final Logger log = LoggerFactory.getLogger(CancelBookingResponseTransformer.class);

	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(CancelBookingResponse.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(CancelBookingResponseType.class);

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
		CancelBookingResponse incoming_res = (CancelBookingResponse) jaxbUtil_incoming.unmarshal(src);
		String incoming_string = incoming_res.getCancelBookingResult();
		return incoming_string;
	}

	private Object transformResponse(String incoming_string) {
        ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) super.transformResponse(message, "urn:ProfdocHISMessage:CancelBooking:Response", incoming_string);
		BookingStatus incoming_timeTypes = message.getBookingStatus();

		JAXBElement<CancelBookingResponseType> outgoing_res = new ObjectFactory()
				.createCancelBookingResponse(new CancelBookingResponseType());

		outgoing_res.getValue().setResultCode(ResultCodeEnum.OK);
		outgoing_res.getValue().setResultText(incoming_timeTypes.getStatus());

		return jaxbUtil_outgoing.marshal(outgoing_res);
	}

}