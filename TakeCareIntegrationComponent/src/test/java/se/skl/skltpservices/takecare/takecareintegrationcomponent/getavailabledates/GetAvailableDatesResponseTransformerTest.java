package se.skl.skltpservices.takecare.takecareintegrationcomponent.getavailabledates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mule.api.transformer.TransformerException;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class GetAvailableDatesResponseTransformerTest {

    @Test
    public void testTransformer_ok() throws Exception {

        // Specify input and expected result

        String input = MiscUtil.readFileAsString("src/test/resources/testfiles/GetAvailableDates/response-input.xml");

        String expectedResult = MiscUtil
                .readFileAsString("src/test/resources/testfiles/GetAvailableDates/response-output.xml");

        // Create the transformer under test and let it perform the
        // transformation

        GetAvailableDatesResponseTransformer transformer = new GetAvailableDatesResponseTransformer();
        String result = (String) transformer.pojoTransform(input, "UTF-8", "");

        // Compare the result to the expected value
        assertEquals(expectedResult, result);
    }

    @Test(expected = TransformerException.class)
    public void testTransformer_error() throws Exception {

        String input = MiscUtil
                .readFileAsString("src/test/resources/testfiles/GetAvailableDates/response-error-input.xml");
        GetAvailableDatesResponseTransformer transformer = new GetAvailableDatesResponseTransformer();
        transformer.pojoTransform(input, "UTF-8", "");

        fail("Expected RuntimeException!");
    }

    @Test
    public void testTransformer_error_correctMessage() throws Exception {

        String input = MiscUtil
                .readFileAsString("src/test/resources/testfiles/GetAvailableDates/response-error-input.xml");
        GetAvailableDatesResponseTransformer transformer = new GetAvailableDatesResponseTransformer();

        try {
            transformer.pojoTransform(input, "UTF-8", "");
        } catch (Exception e) {
            assertEquals("resultCode: 3001 resultText: Illegal argument! (java.lang.RuntimeException)", e.getMessage());
            return;
        }

        fail("Expected RuntimeException!");
    }
}