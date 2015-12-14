package se.skl.skltpservices.takecare.takecaretestproducer;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage;

public class TakeCareTestProducer {
    
    private static final Logger log = LoggerFactory.getLogger(TakeCareTestProducer.class);

	private static final JaxbUtil jaxbUtil_error = new JaxbUtil(ProfdocHISMessage.class);

	protected BigInteger yyyyMMddHHmmss(Date date) {
		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMddHHmmss");
		return new BigInteger(dateFormat.format(date));
	}

	protected BigInteger yyyyMMddHHmm(Date date) {
		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMddHHmm");
		return new BigInteger(dateFormat.format(date));
	}

    protected String createErrorResponseXml(se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage error_response) {
        jaxbUtil_error.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
        return jaxbUtil_error.marshal(error_response, "", "ProfdocHISMessage");
    }
	
    protected se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage 
        createErrorResponseMessage(String careunitId, String externalUser) {

        se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage error_response = new se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage();
        error_response.setCareUnitType("hsaid");
        error_response.setCareUnit(careunitId);
        error_response.setError(new se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage.Error());
        error_response.getError().setCode(3001);
        error_response.getError().setMsg("Illegal argument!");
        error_response.getError().setType("System");
        error_response.setMethod("Booking.GetTimeTypes");
        error_response.setMsgType("Error");
        error_response.setSystem("ProfdocHIS");
        error_response.setSystemInstance(0);
        error_response.setTime(yyyyMMddHHmmss(new Date()));
        error_response.setUser(externalUser);
        return error_response;
    }
	
	protected String createErrorResponse(String careunitId, String externalUser) {
	    se.skl.skltpservices.takecare.booking.error.ProfdocHISMessage error_response
	       = createErrorResponseMessage(careunitId, externalUser);
	    return this.createErrorResponseXml(error_response);
	}

	/**
	 * @return
	 */
    public Object unmarshalXmlToProfdocHISMessage(Object profdocHISMessage, String namespace, String xml) {
        
        log.debug("unmarshallXml:" + xml);
        
        JAXBContext jc = null;

        try {
            jc = JAXBContext.newInstance(profdocHISMessage.getClass());
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

            filter.parse(new InputSource(new StringReader(xml)));
            profdocHISMessage = unmarshallerHandler.getResult();
            log.debug("unmarshalled to message of type " + profdocHISMessage.getClass().getName());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return profdocHISMessage;
    }
	
    public String cancelBooking(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }
    
    public String getAvailableDates(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }

    public String getAvailableTimeslots(String tcusername, String tcpassword, String externaluser,
            String careunitidtype, String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }

    public String getBookings(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }

    public String getTimeTypes(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }

    public String makeBooking(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }
    
    public String rescheduleBooking(String tcusername, String tcpassword, String externaluser, String careunitidtype,
            String careunitid, String xml) {
        throw new UnsupportedOperationException();
    }
}
