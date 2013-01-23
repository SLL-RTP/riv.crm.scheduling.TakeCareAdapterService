package se.skl.skltpservices.takecare.takecareintegrationcomponent;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Take Care throws their own errors in ProfdocHISMessage that contains error
 * description and codes, read their specification for more info about this.
 * However in the cases when we do not get a known exception from Take Care we
 * need to handle it.
 * 
 */
public class ExceptionTransformer extends AbstractMessageTransformer {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object transformMessage(MuleMessage msg, String outputEncoding) throws TransformerException {
		logger.debug("Exception transformer executing...");

		if (msg.getExceptionPayload() != null) {
			logger.debug("Exception payload detected!");
			logger.debug("Exception: {}", msg.getExceptionPayload().getMessage());
			logger.debug("Root exception: {}", msg.getExceptionPayload().getRootException().getMessage());

			// How to handle any exceptions when communicating with Take Care?
		}

		// No error, return incoming payload!
		return msg.getPayload();
	}

}
