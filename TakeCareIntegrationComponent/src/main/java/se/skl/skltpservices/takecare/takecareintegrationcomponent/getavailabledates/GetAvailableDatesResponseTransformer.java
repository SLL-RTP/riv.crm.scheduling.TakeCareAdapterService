package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.riv.crm.scheduling.getavailabledates.v1.GetAvailableDatesResponseType;
import se.riv.crm.scheduling.getavailabledates.v1.ObjectFactory;
import se.riv.crm.scheduling.getavailabledates.v1.PerformerAvailabilityByDateType;
import se.skl.skltpservices.takecare.AbstractTakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.GetAvailableDatesResponse;
import se.skl.skltpservices.takecare.booking.getavailabledatesresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.getavailabledatesresponse.ProfdocHISMessage.AvailableDates;

public class GetAvailableDatesResponseTransformer extends AbstractTakeCareResponseTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetAvailableDatesResponseTransformer.class);

	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetAvailableDatesResponse.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAvailableDatesResponseType.class);

	/**
	 * Simple pojo transformer that transforms Take Care format to
	 * crm:scheduling 1.0.
	 * 
	 * @param src
	 * @param outputEncoding
	 */
	protected Object pojoTransform(Object src, String outputEncoding, Object property) throws TransformerException {

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
		GetAvailableDatesResponse incoming_res = (GetAvailableDatesResponse) jaxbUtil_incoming.unmarshal(src);
		String incoming_string = incoming_res.getGetAvailableDatesResult();
		return incoming_string;
	}

	private Object transformResponse(String incoming_string) {
        ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) super.transformResponse(message, "urn:ProfdocHISMessage:GetAvailableDates:Response", incoming_string);
		List<AvailableDates> incoming_availableDates = message.getAvailableDates();

		AvailableDates availableDates = incoming_availableDates.get(0);
		String careUnitId = availableDates.getCareUnitId();
		String careunitName = availableDates.getCareUnitName();
		Integer timeTypeId = availableDates.getTimeTypeId();

		JAXBElement<GetAvailableDatesResponseType> outgoing_res = new ObjectFactory()
				.createGetAvailableDatesResponse(new GetAvailableDatesResponseType());

		for (Long date : availableDates.getDate()) {
			PerformerAvailabilityByDateType performerAvailabilityByDate = new PerformerAvailabilityByDateType();
			// performerAvailabilityByDate.setCareTypeID(value);
			// performerAvailabilityByDate.setCareTypeName(value);
			performerAvailabilityByDate.setDate(date.toString());
			performerAvailabilityByDate.setHealthcareFacility(careUnitId);
			// performerAvailabilityByDate.setPerformer(value);
			// performerAvailabilityByDate.setResourceID(value);
			performerAvailabilityByDate.setResourceName(careunitName);
			performerAvailabilityByDate.setTimeTypeID(String.valueOf(timeTypeId));
			// performerAvailabilityByDate.setTimeTypeName(value);
			outgoing_res.getValue().getPerformerAvailabilityByDate().add(performerAvailabilityByDate);
		}

		return jaxbUtil_outgoing.marshal(outgoing_res);
	}

}