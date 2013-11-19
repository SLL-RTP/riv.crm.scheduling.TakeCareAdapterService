package se.skl.skltpservices.takecare.takecareintegrationcomponent.cancelbooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mule.api.transformer.TransformerException;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class CancelBookingResponseTransformerTest {

    @Test
    public void testTransformer_ok() throws Exception {

        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/CancelBooking/response-input.xml");
        String expectedResult = MiscUtil
                .readFileAsString("src/test/resources/testfiles/CancelBooking/response-output.xml");
        CancelBookingResponseTransformer transformer = new CancelBookingResponseTransformer();
        String result = (String) transformer.pojoTransform(input, "UTF-8", "2");

        assertEquals(expectedResult, result);
    }

    @Test(expected = TransformerException.class)
    public void testTransformer_error() throws Exception {
        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/CancelBooking/response-error-input.xml");
        CancelBookingResponseTransformer transformer = new CancelBookingResponseTransformer();
        transformer.pojoTransform(input, "UTF-8", "");
        fail("Expected RuntimeException!");
    }

    @Test
    public void testTransformer_error_correctMessage() throws Exception {

        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/CancelBooking/response-error-input.xml");
        CancelBookingResponseTransformer transformer = new CancelBookingResponseTransformer();

        try {
            transformer.pojoTransform(input, "UTF-8", "");
        } catch (Exception e) {
            assertEquals("resultCode: 3001 resultText: Illegal argument! (java.lang.RuntimeException)", e.getMessage());
            return;
        }

        fail("Expected RuntimeException!");
    }

    @Test(expected = TransformerException.class)
    public void testTransformer_bad_input() throws Exception {
        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/CancelBooking/response-bad-input.xml");
        CancelBookingResponseTransformer transformer = new CancelBookingResponseTransformer();
        transformer.pojoTransform(input, "UTF-8", "");
        fail("Expected TransformerException!");
    }

    @Test(expected = TransformerException.class)
    public void testTransformer_bad_input_correctMessage() throws Exception {
        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/CancelBooking/response-bad-input.xml");
        CancelBookingResponseTransformer transformer = new CancelBookingResponseTransformer();
        transformer.pojoTransform(input, "UTF-8", "");
        fail("Expected TransformerException!");
    }
}