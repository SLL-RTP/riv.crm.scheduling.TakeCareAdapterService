package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabletimeslots;

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
import se.skl.skltpservices.takecare.booking.GetAvailableTimeslots;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsrequest.ProfdocHISMessage;

public class GetAvailableTimeslotsRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetAvailableTimeslots.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result
		String input = MiscUtil
				.readFileAsString("src/test/resources/testfiles/GetAvailableTimeslots/request-input.xml");

		// Create the transformer under test and let it perform the
		// transformation

		GetAvailableTimeslotsRequestTransformer transformer = new GetAvailableTimeslotsRequestTransformer();
		String result = (String) transformer.pojoTransform(null, input, "UTF-8");

		/* GetAvailableTimeslots */
		GetAvailableTimeslots timeTypes = new GetAvailableTimeslots();
        timeTypes = (GetAvailableTimeslots) jaxbUtil_outgoing.unmarshal(result);
        //timeTypes = (GetAvailableTimeslots) JaxbHelper.transform(timeTypes, "", result);
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

        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:GetAvailableTimeslots:Request", xml);
		String msgCareunitId = message.getCareUnitId();
		String msgCareunitIdType = message.getCareUnitIdType();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgMessageType = message.getMsgType();
		BigInteger msgTime = message.getTime();
		String msgBookingId = message.getBookingId();
		long msgEndDate = message.getEndDate();
		BigInteger msgResourceId = message.getResourceId();
		long msgSTartDate = message.getStartDate();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("1234567890", msgBookingId);
		assertEquals(12121212121200l, msgSTartDate);
		assertEquals(12121213121200L, msgEndDate);
		assertNull(msgResourceId);
		assertNotNull(msgTime);
	}

	//@Test(expected = TransformerException.class)
	public void testBadInputGivesTransformerException() throws Exception {
		String input = MiscUtil
				.readFileAsString("src/test/resources/testfiles/GetAvailableTimeslots/request-bad-input.xml");
		GetAvailableTimeslotsRequestTransformer transformer = new GetAvailableTimeslotsRequestTransformer();
		transformer.pojoTransform(null, input, "UTF-8");
		fail("Expected TransformException when bad input");
	}
}