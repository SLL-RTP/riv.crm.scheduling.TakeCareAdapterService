package se.skl.skltpservices.takecare.takecareintegrationcomponent.updatebooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.Test;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.MiscUtil;

import se.skl.skltpservices.takecare.TakeCareUtil;
import se.skl.skltpservices.takecare.JaxbHelper;
import se.skl.skltpservices.takecare.booking.RescheduleBooking;
import se.skl.skltpservices.takecare.booking.reschedulebookingrequest.ProfdocHISMessage;

public class UpdateBookingRequestTransformerTest {

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(RescheduleBooking.class);
	private static final JaxbUtil jaxbUtil_message = new JaxbUtil(ProfdocHISMessage.class);

	@Test
	public void testTransformerWithMandatoryRivTaFields() throws Exception {

		// Specify input and expected result
		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/UpdateBooking/request-input.xml");

		UpdateBookingRequestTransformer transformer = new UpdateBookingRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* RescheduleBookings */
		RescheduleBooking rebBookings = (RescheduleBooking) jaxbUtil_outgoing.unmarshal(result);
		String careunitId = rebBookings.getCareunitid();
		String careunitType = rebBookings.getCareunitidtype();
		String externalUser = rebBookings.getExternaluser();
		String tcPassword = rebBookings.getTcpassword();
		String tcUsername = rebBookings.getTcusername();
		String xml = rebBookings.getXml();

		assertEquals(TakeCareUtil.HSAID, careunitType);
		assertEquals("HSA-VKK123", careunitId);
		assertEquals(TakeCareUtil.EXTERNAL_USER, externalUser);
		assertEquals("", tcPassword);
		assertEquals("", tcUsername);

		/* ProfdocHISMessage */
		ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:RescheduleBooking:Request", xml);
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
		String bookingId = message.getBookingId();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("191414141414", patientId.toString());
		assertEquals("Only phone, no reason specified", "0566789009", patientReason);
		assertEquals("111222333", bookingId);
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
				.readFileAsString("src/test/resources/testfiles/UpdateBooking/request-input-all-takecare-fields.xml");

		UpdateBookingRequestTransformer transformer = new UpdateBookingRequestTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		/* RescheduleBookings */
		RescheduleBooking reBookings = (RescheduleBooking) jaxbUtil_outgoing.unmarshal(result);
		String careunitId = reBookings.getCareunitid();
		String careunitType = reBookings.getCareunitidtype();
		String externalUser = reBookings.getExternaluser();
		String tcPassword = reBookings.getTcpassword();
		String tcUsername = reBookings.getTcusername();
		String xml = reBookings.getXml();

		assertEquals(TakeCareUtil.HSAID, careunitType);
		assertEquals("HSA-VKK123", careunitId);
		assertEquals(TakeCareUtil.EXTERNAL_USER, externalUser);
		assertEquals("", tcPassword);
		assertEquals("", tcUsername);

		/* ProfdocHISMessage */
		ProfdocHISMessage message = new ProfdocHISMessage();
        message = (ProfdocHISMessage) JaxbHelper.transform(message, "urn:ProfdocHISMessage:RescheduleBooking:Request", xml);
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
		String bookingId = message.getBookingId();

		assertEquals("HSA-VKK123", msgCareunitId);
		assertEquals(TakeCareUtil.HSAID, msgCareunitIdType);
		assertEquals(TakeCareUtil.INVOKING_SYSTEM, msgInvokingSystem);
		assertEquals(TakeCareUtil.REQUEST, msgMessageType);
		assertEquals("191414141414", patientId.toString());
		assertEquals("1234567890", bookingId);
		assertEquals("Reason 1234567890", patientReason);
		assertNotNull(endTime);
		assertNotNull(startTime);
		assertEquals(new BigInteger("2"), resourceId);
		assertEquals(1, timeTypeId);
		assertNotNull(msgTime);
	}
}