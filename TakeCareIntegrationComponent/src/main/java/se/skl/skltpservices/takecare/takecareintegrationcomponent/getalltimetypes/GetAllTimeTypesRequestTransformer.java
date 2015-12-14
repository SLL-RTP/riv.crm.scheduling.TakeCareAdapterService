package se.skl.skltpservices.takecare.takecareintegrationcomponent.getalltimetypes;

import org.mule.api.transformer.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import se.riv.crm.scheduling.getalltimetypes.v1.GetAllTimeTypesType;
import se.skl.skltpservices.takecare.AbstractTakeCareRequestTransformer;
import se.skl.skltpservices.takecare.booking.GetTimeTypes;
import se.skl.skltpservices.takecare.booking.gettimetypesrequest.ProfdocHISMessage;

import java.util.Date;
import org.mule.api.MuleMessage;

import static se.skl.skltpservices.takecare.TakeCareDateHelper.yyyyMMddHHmmss;
import static se.skl.skltpservices.takecare.TakeCareUtil.*;

public class GetAllTimeTypesRequestTransformer extends AbstractTakeCareRequestTransformer {

	private static final Logger log = LoggerFactory.getLogger(GetAllTimeTypesRequestTransformer.class);

	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);
	private static final JaxbUtil jaxbUtil_incoming = new JaxbUtil(GetAllTimeTypesType.class);
	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetTimeTypes.class);

	protected Object pojoTransform(MuleMessage muleMessage, Object src, String encoding) throws TransformerException {

		if (logger.isDebugEnabled()) {
			log.debug("Transforming request payload: {}", src);
		}

		try {
			GetAllTimeTypesType incomingRequest = (GetAllTimeTypesType) jaxbUtil_incoming.unmarshal(src);
			String incomingHealthcarefacility = incomingRequest.getHealthcareFacility();

			ProfdocHISMessage message = new ProfdocHISMessage();
			message.setCareUnitId(incomingHealthcarefacility);
			message.setCareUnitIdType(HSAID);
			message.setInvokingSystem(INVOKING_SYSTEM);
			message.setMsgType(REQUEST);
			message.setTime(yyyyMMddHHmmss(new Date()));
			message.setTimeTypeRequest(WEB);

			GetTimeTypes outgoingRequest = new GetTimeTypes();
			outgoingRequest.setCareunitid(incomingHealthcarefacility);
			outgoingRequest.setCareunitidtype("hsaid");
			outgoingRequest.setExternaluser(EXTERNAL_USER);
			outgoingRequest.setTcpassword("");
			outgoingRequest.setTcusername("");
            //TakeCare eXchange can not handle xml declarations in CDATA so do not generate that.
            jaxbUtil_message.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
            //TakeCare eXchange can not handle namespaces in CDATA
			outgoingRequest.setXml(jaxbUtil_message.marshal(message, "", "ProfdocHISMessage"));
			Object outgoingPayload = jaxbUtil_outgoing.marshal(outgoingRequest);

			if (logger.isDebugEnabled()) {
				logger.debug("transformed payload to: " + outgoingPayload);
			}
			return outgoingPayload;
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}
}