package se.skl.skltpservices.takecare.takecareintegrationcomponent.getalltimetypes;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesResponseType;
import se.riv.crm.scheduling.getalltimetypes.v1.ObjectFactory;
import se.riv.crm.scheduling.v1.TimeTypeType;
import se.skl.skltpservices.takecare.TakeCareResponseTransformer;
import se.skl.skltpservices.takecare.booking.GetTimeTypesResponse;
import se.skl.skltpservices.takecare.booking.gettimetypesresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.gettimetypesresponse.ProfdocHISMessage.TimeTypes;
import se.skl.skltpservices.takecare.booking.gettimetypesresponse.ProfdocHISMessage.TimeTypes.TimeType;
import se.skl.skltpservices.takecare.TakeCareNamespacePrefixMapper;
import se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareValidationEventHandler;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

public class GetAllTimeTypesResponseTransformer extends TakeCareResponseTransformer {

    private static final Logger log = LoggerFactory.getLogger(GetAllTimeTypesResponseTransformer.class);

    private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetTimeTypesResponse.class);
    private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
    private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAllTimeTypesResponseType.class);



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
        GetTimeTypesResponse incoming_res = (GetTimeTypesResponse) jaxbUtil_incoming.unmarshal(src);
        String incoming_string = incoming_res.getGetTimeTypesResult();
        return incoming_string;
    }

    private Object transformResponse(String incoming_string) {
        //Do unmarshalling
        ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) super.transformResponse(message, "urn:ProfdocHISMessage:GetTimeTypes:Response", incoming_string);
        TimeTypes incoming_timeTypes = message.getTimeTypes();

        JAXBElement<GetAllTimeTypesResponseType> outgoing_res = new ObjectFactory()
                .createGetAllTimeTypesResponse(new GetAllTimeTypesResponseType());

        for (TimeType incoming_timeType : incoming_timeTypes.getTimeType()) {
            TimeTypeType outgoing_timeType = new TimeTypeType();
            outgoing_timeType.setTimeTypeId(String.valueOf(incoming_timeType.getTimeTypeId()));
            outgoing_timeType.setTimeTypeName(incoming_timeType.getTimeTypeName());
            outgoing_res.getValue().getListOfTimeTypes().add(outgoing_timeType);
        }
        return jaxbUtil_outgoing.marshal(outgoing_res);
    }
}