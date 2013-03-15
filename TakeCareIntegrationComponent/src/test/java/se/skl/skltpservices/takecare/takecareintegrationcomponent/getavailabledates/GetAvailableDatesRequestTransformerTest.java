package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;
import org.mule.api.transformer.TransformerException;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.MiscUtil;

import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.JaxbHelper;
import se.skl.skltpservices.takecare.booking.GetAvailableDates;
import se.skl.skltpservices.takecare.booking.getavailabledatesrequest.ProfdocHISMessage;

public class GetAvailableDatesRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAvailableDates.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformer_ok() throws Exception {
		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/GetAvailableDates/request-input.xml");

		GetAvailableDatesRequestTransformer transformer = new GetAvailableDatesRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* GetTimeTypes */
		GetAvailableDates timeTypes = (GetAvailableDates) jaxbUtil_outgoing.unmarshal(result);
		String careunitId = timeTypes.getCareunitid();
		String careunitType = timeTypes.getCareunitidtype();
		String externalUser = timeTypes.getExternaluser();
		String tcPassword = timeTypes.getTcpassword();
		String tcUsername = timeTypes.getTcusername();
		String xml = timeTypes.getXml();

		assertEquals(TakeCareUtil.HSAID, careunitType);
		assertEquals("HSA-VKK123", careunitId);
		assertEquals(TakeCareUtil.EXTERNAL_USER, externalUser);
		assertEquals("", tcPassword);
		assertEquals("", tcUsername);

		/* ProfdocHISMessage */
		ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:GetAvailableDates:Request", xml);
		String msgCareunitId = message.getCareUnitId();
		String msgCareunitIdType = message.getCareUnitIdType();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgMessageType = message.getMsgType();
		BigInteger msgTime = message.getTime();
		String msgBookingId = message.getBookingId();
		long msgEndDate = message.getEndDate();
		BigInteger msgResourceId = message.getResourceId();
		long msgSTartDate = message.getStartDate();
		Integer msgTimeTypeId = message.getTimeTypeId();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("", msgBookingId);
		assertEquals(Integer.valueOf(1), msgTimeTypeId);
		assertNull(msgResourceId);
		assertNotNull(msgTime);
		assertNotNull(msgEndDate);
		assertNotNull(msgSTartDate);

	}

	@Test(expected = TransformerException.class)
	public void testBadInputGivesTransformerException() throws Exception {
		String input = MiscUtil
				.readFileAsString("src/test/resources/testfiles/GetAvailableDates/request-bad-input.xml");
		GetAvailableDatesRequestTransformer transformer = new GetAvailableDatesRequestTransformer();
		transformer.pojoTransform(input, "UTF-8");
		fail("Expected TransformException when bad input");
	}
}