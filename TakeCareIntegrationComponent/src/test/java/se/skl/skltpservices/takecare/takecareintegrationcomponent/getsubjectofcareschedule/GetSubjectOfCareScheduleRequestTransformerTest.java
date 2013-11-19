package se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule;

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
import se.skl.skltpservices.takecare.booking.GetBookings;
import se.skl.skltpservices.takecare.booking.getbookingsrequest.ProfdocHISMessage;

public class GetSubjectOfCareScheduleRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(GetBookings.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformer_ok() throws Exception {

		String input = MiscUtil
				.readFileAsString("src/test/resources/testfiles/GetSubjectOfCareSchedule/request-input.xml");

		GetSubjectOfCareScheduleRequestTransformer transformer = new GetSubjectOfCareScheduleRequestTransformer();
		String result = (String) transformer.pojoTransform(null, input, "UTF-8");

		/* Bookings */
		GetBookings bookings = (GetBookings) jaxbUtil_outgoing.unmarshal(result);
		String careunitId = bookings.getCareunitid();
		String careunitType = bookings.getCareunitidtype();
		String externalUser = bookings.getExternaluser();
		String tcPassword = bookings.getTcpassword();
		String tcUsername = bookings.getTcusername();
		String xml = bookings.getXml();

		assertEquals(TakeCareUtil.HSAID, careunitType);
		assertEquals("HSA-VKK123", careunitId);
		assertEquals(TakeCareUtil.EXTERNAL_USER, externalUser);
		assertEquals("", tcPassword);
		assertEquals("", tcUsername);

		/* ProfdocHISMessage */
		ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:GetBookings:Request", xml);
		String msgCareunitId = message.getCareUnitId();
		String msgCareunitIdType = message.getCareUnitIdType();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgMessageType = message.getMsgType();
		BigInteger msgTime = message.getTime();
		String bookingId = message.getBookingId();
		String patientId = message.getPatientId();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("191414141414", patientId);
		assertNull(bookingId);
		assertNotNull(msgTime);

	}

	@Test(expected = TransformerException.class)
	public void testBadInputGivesTransformerException() throws Exception {
		String input = MiscUtil
				.readFileAsString("src/test/resources/testfiles/GetSubjectOfCareSchedule/request-bad-input.xml");
		GetSubjectOfCareScheduleRequestTransformer transformer = new GetSubjectOfCareScheduleRequestTransformer();
		transformer.pojoTransform(null, input, "UTF-8");
		fail("Expected TransformException when bad input");
	}
}