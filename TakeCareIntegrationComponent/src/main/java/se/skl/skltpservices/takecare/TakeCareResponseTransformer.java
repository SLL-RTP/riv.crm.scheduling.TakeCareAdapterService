package se.skl.skltpservices.takecare;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage;
import se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareValidationEventHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

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

    /**
     * @TODO clean up the stacktraces
     * @TODO remove the validation handler
     * @param message
     * @param namespace
     * @return
     */
    public Object transformResponse(Object message, String namespace, String incoming_string) {
        JAXBContext jc = null;

        try {
            jc = JAXBContext.newInstance(message.getClass());
            XMLFilter filter = new TakeCareNamespacePrefixMapper(namespace);

            // Set the parent XMLReader on the XMLFilter
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            filter.setParent(xr);

            // Set UnmarshallerHandler as ContentHandler on XMLFilter
            Unmarshaller unmarshaller = null;
            unmarshaller = jc.createUnmarshaller();
            unmarshaller.setEventHandler(new TakeCareValidationEventHandler());
            UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
            filter.setContentHandler(unmarshallerHandler);

            filter.parse(new InputSource(new StringReader(incoming_string)));
            message = unmarshallerHandler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return message;
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
            ProfdocHISMessage message = new ProfdocHISMessage();
            message = (ProfdocHISMessage) this.transformResponse(message, "urn:ProfdocHISMessage:Error", incoming_string);
			throw new RuntimeException("resultCode: " + message.getError().getCode() + " resultText: "
					+ message.getError().getMsg());
		}
	}

	private boolean containsError(String incoming_string) {
		return StringUtils.contains(incoming_string, "Error");
	}
}