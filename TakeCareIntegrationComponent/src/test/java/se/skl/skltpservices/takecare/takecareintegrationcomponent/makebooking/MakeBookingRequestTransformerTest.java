package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.Test;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.MiscUtil;

import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.JaxbHelper;
import se.skl.skltpservices.takecare.booking.MakeBooking;
import se.skl.skltpservices.takecare.booking.makebookingrequest.ProfdocHISMessage;

public class MakeBookingRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(MakeBooking.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformerWithMandatoryRivTaFields() throws Exception {

		// Specify input and expected result
		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/MakeBooking/request-input.xml");

		MakeBookingRequestTransformer transformer = new MakeBookingRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* Bookings */
		MakeBooking bookings = (MakeBooking) jaxbUtil_outgoing.unmarshal(result);
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
        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:MakeBooking:Request", xml);
		String msgCareunitId = message.getCareUnitId();
		String msgCareunitIdType = message.getCareUnitIdType();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgMessageType = message.getMsgType();
		BigInteger msgTime = message.getTime();
		String patientReason = message.getPatientReason();
		BigInteger endTime = message.getEndTime();
		BigInteger patientId = message.getPatientId();
		BigInteger resourceId = message.getResourceId();
		BigInteger startTime = message.getStartTime();
		int timeTypeId = message.getTimeTypeId();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("191414141414", patientId.toString());
		assertEquals("Only phone, no reason specified", "0566789009", patientReason);
		assertNotNull(endTime);
		assertNotNull(startTime);
		assertNull(resourceId);
		assertEquals(0, timeTypeId);
		assertNotNull(msgTime);
	}

	@Test
	public void testTransformerWithAllTakeCareFields() throws Exception {

		// Specify input and expected result
		String input = MiscUtil
				.readFileAsString("src/test/resources/testfiles/MakeBooking/request-input-all-takecare-fields.xml");

		MakeBookingRequestTransformer transformer = new MakeBookingRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* Bookings */
		MakeBooking bookings = (MakeBooking) jaxbUtil_outgoing.unmarshal(result);
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
        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:MakeBooking:Request", xml);
		String msgCareunitId = message.getCareUnitId();
		String msgCareunitIdType = message.getCareUnitIdType();
		String msgInvokingSystem = message.getInvokingSystem();
		String msgMessageType = message.getMsgType();
		BigInteger msgTime = message.getTime();
		String patientReason = message.getPatientReason();
		BigInteger endTime = message.getEndTime();
		BigInteger patientId = message.getPatientId();
		BigInteger resourceId = message.getResourceId();
		BigInteger startTime = message.getStartTime();
		int timeTypeId = message.getTimeTypeId();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("191414141414", patientId.toString());
		assertEquals("Reason 0566789009", patientReason);
		assertNotNull(endTime);
		assertNotNull(startTime);
		assertEquals(new BigInteger("2"), resourceId);
		assertEquals(1, timeTypeId);
		assertNotNull(msgTime);
	}

}