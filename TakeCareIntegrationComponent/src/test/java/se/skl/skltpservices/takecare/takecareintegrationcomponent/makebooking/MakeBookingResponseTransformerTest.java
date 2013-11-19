package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class MakeBookingResponseTransformerTest {

    @Test
    public void testTransformer_ok() throws Exception {
        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/MakeBooking/response-input.xml");
        String expectedResult = MiscUtil
                .readFileAsString("src/test/resources/testfiles/MakeBooking/response-output.xml");
        MakeBookingResponseTransformer transformer = new MakeBookingResponseTransformer();
        String result = (String) transformer.pojoTransform(input, "UTF-8", false);
        assertEquals(expectedResult, result);
    }

    /**
     * Make sure the proper informative return message is sent if a telephone number has been sent to TakeCare
     * @throws Exception 
     */
    @Test
    public void testTransformer_ok_withTelephone() throws Exception {
        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/MakeBooking/response-input.xml");
        String expectedResult = MiscUtil
                .readFileAsString("src/test/resources/testfiles/MakeBooking/response-output-telephone.xml");
        MakeBookingResponseTransformer transformer = new MakeBookingResponseTransformer();
        String result = (String) transformer.pojoTransform(input, "UTF-8", true);
        assertEquals(expectedResult, result);
    }
}