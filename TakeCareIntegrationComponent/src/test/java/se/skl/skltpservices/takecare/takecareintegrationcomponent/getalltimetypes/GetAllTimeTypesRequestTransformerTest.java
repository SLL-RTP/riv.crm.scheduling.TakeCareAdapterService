package se.skl.skltpservices.takecare.takecareintegrationcomponent.getalltimetypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;
import org.mule.api.transformer.TransformerException;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.MiscUtil;

import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.booking.GetTimeTypes;
import se.skl.skltpservices.takecare.booking.gettimetypesrequest.ProfdocHISMessage;

public class GetAllTimeTypesRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetTimeTypes.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformer_ok() throws Exception {
		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/GetAllTimeTypes/request-input.xml");

		GetAllTimeTypesRequestTransformer transformer = new GetAllTimeTypesRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* GetTimeTypes */
		GetTimeTypes timeTypes = (GetTimeTypes) jaxbUtil_outgoing.unmarshal(result);
		String careunitId = timeTypes.getCareunitid();
		String careunitType = timeTypes.getCareunitidtype();
		String externalUser = timeTypes.getExternaluser();
		String tcPassword = timeTypes.getTcpassword();
		String tcUsername = timeTypes.getTcusername();
		String xml = timeTypes.getXml();

		assertEquals(TakeCareUtil.HSAID, careunitType);
		assertEquals("HSAVKK123", careunitId);
		assertEquals(TakeCareUtil.EXTERNAL_USER, externalUser);
		assertEquals("", tcPassword);
		assertEquals("", tcUsername);

		/* ProfdocHISMessage */
		ProfdocHISMessage message = (ProfdocHISMessage) jaxbUtil_message.unmarshal(xml);
		String msgCareunitId = message.getCareUnitId();
		String msgCareunitIdType = message.getCareUnitIdType();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgMessageType = message.getMsgType();
		BigInteger msgTime = message.getTime();
		String msgTimeTypeRequest = message.getTimeTypeRequest();

		assertEquals("HSAVKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals(TakeCareUtil.WEB, msgTimeTypeRequest);
		assertNotNull(msgTime);
	}

	@Test(expected = TransformerException.class)
	public void testBadInputGivesTransformerException() throws Exception {
		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/GetAllTimeTypes/request-bad-input.xml");
		GetAllTimeTypesRequestTransformer transformer = new GetAllTimeTypesRequestTransformer();
		transformer.pojoTransform(input, "UTF-8");
		fail("Expected TransformException when bad input");
	}
}