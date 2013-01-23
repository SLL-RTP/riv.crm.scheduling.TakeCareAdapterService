package se.skl.skltpservices.takecare;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage;

/**
 * Base class for Take Care response transformers. Place common features to be
 * used by all transformers here.
 */
public abstract class TakeCareResponseTransformer extends AbstractMessageTransformer {

	private static final JaxbUtil jaxbUtil_error = new JaxbUtil(ProfdocHISMessage.class);

	/**
	 * Message aware transformer.
	 */
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		if (message.getExceptionPayload() != null) {
			return message;
		}
		return pojoTransform(message.getPayload(), outputEncoding);
	}

	protected abstract Object pojoTransform(Object src, String outputEncoding) throws TransformerException;

	/**
	 * Take Care error messages are in the ProfDocHISMessage in a element called
	 * Error. Check if it exist and in that case make sure the errortext and
	 * code is propagated to the user/system calling the service.
	 * 
	 * @param incoming_string
	 *            The message containing the response from Take Care
	 */
	protected void handleTakeCareErrorMessages(String incoming_string) {
		if (containsError(incoming_string)) {
			ProfdocHISMessage message = (ProfdocHISMessage) jaxbUtil_error.unmarshal(incoming_string);
			throw new RuntimeException("resultCode: " + message.getError().getCode() + " resultText: "
					+ message.getError().getMsg());
		}
	}

	private boolean containsError(String incoming_string) {
		return StringUtils.contains(incoming_string, "Error");
	}

}
