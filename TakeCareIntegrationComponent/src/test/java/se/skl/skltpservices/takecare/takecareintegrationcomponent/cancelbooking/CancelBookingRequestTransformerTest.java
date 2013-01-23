package se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;

import org.junit.Test;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.MiscUtil;

import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.booking.CancelBooking;
import se.skl.skltpservices.takecare.booking.cancelbookingrequest.ProfdocHISMessage;

public class CancelBookingRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(CancelBooking.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result
		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/CancelBooking/request-input.xml");

		// Create the transformer under test and let it perform the
		// transformation

		CancelBookingRequestTransformer transformer = new CancelBookingRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* CancelBooking */
		CancelBooking cancelBooking = (CancelBooking) jaxbUtil_outgoing.unmarshal(result);
		String careunitId = cancelBooking.getCareunitid();
		String careunitType = cancelBooking.getCareunitidtype();
		String externalUser = cancelBooking.getExternaluser();
		String tcPassword = cancelBooking.getTcpassword();
		String tcUsername = cancelBooking.getTcusername();
		String xml = cancelBooking.getXml();

		assertEquals(TakeCareUtil.HSAID, careunitType);
		assertEquals("HSA-VKK123", careunitId);
		assertEquals(TakeCareUtil.EXTERNAL_USER, externalUser);
		assertEquals("", tcPassword);
		assertEquals("", tcUsername);

		/* ProfdocHISMessage */
		ProfdocHISMessage message = (ProfdocHISMessage) jaxbUtil_message.unmarshal(xml);
		String msgBookingId = message.getBookingId();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgPatientReason = message.getPatientReason();
		BigInteger msgTime = message.getTime();

		assertEquals("1234567890", msgBookingId);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals("I want to cancel this", msgPatientReason);
		assertNotNull(msgTime);

	}
}