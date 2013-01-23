package se.skl.skltpservices.takecare.takecareintegrationcomponent.updatebooking;

import javax.xml.bind.JAXBElement;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.updatebooking.v1.ObjectFactory;
import se.riv.crm.scheduling.updatebooking.v1.UpdateBookingResponseType;
import se.riv.crm.scheduling.v1.ResultCodeEnum;
import se.skl.skltpservices.takecare.TakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.RescheduleBookingResponse;

public class UpdateBookingResponseTransformer extends TakeCareResponseTransformer {

	private static final Logger log = LoggerFactory.getLogger(UpdateBookingResponseTransformer.class);

	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(RescheduleBookingResponse.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(UpdateBookingResponseType.class);

	/**
	 * Simple pojo transformer that transforms Take Care format to
	 * crm:scheduling 1.0.
	 * 
	 * @param src
	 * @param outputEncoding
	 */
	public Object pojoTransform(Object src, String outputEncoding) throws TransformerException {
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
		RescheduleBookingResponse incoming_res = (RescheduleBookingResponse) jaxbUtil_incoming.unmarshal(src);
		String incoming_string = incoming_res.getRescheduleBookingResult();
		return incoming_string;
	}

	private Object transformResponse(String incoming_string) {
		JAXBElement<UpdateBookingResponseType> outgoing_res = new ObjectFactory()
				.createUpdateBookingResponse(new UpdateBookingResponseType());
		outgoing_res.getValue().setResultCode(ResultCodeEnum.OK);
		outgoing_res.getValue().setResultText("");
		return jaxbUtil_outgoing.marshal(outgoing_res);
	}

}