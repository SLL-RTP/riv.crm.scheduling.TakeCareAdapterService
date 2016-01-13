package se.skl.skltpservices.takecare;

import static org.junit.Assert.assertEquals;
import static se.skl.skltpservices.takecare.AbstractTakeCareRequestTransformer.buildReason;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;

public class TakeCareResponseTransformerTest {
    
    private static final Logger log = LoggerFactory.getLogger(TakeCareResponseTransformerTest.class);
    
    @Test
    public void testBuildReasonFromReasonAndPhone() throws Exception {
        TimeslotType timeslot = new TimeslotType();
        timeslot.setReason("Ont i ryggen");
        String reason = buildReason(timeslot, "0123456789", log);
        assertEquals("Ont i ryggen", reason);
    }

    @Test
    public void testBuildReasonWithoutPhone() throws Exception {
        TimeslotType timeslot = new TimeslotType();
        timeslot.setReason("Ont i ryggen");

        String reason = buildReason(timeslot, "", log);
        assertEquals("Ont i ryggen", reason);
    }

    @Test
    public void testBuildReasonWithOnlyPhone() throws Exception {
        SubjectOfCareType subjectOfCare = new SubjectOfCareType();
        subjectOfCare.setPhone("1234567890");
        TimeslotType timeslot = new TimeslotType();

        String reason = buildReason(timeslot, "0123456789", log);
        assertEquals("", reason);
    }
    
    @Test
    public void testReason300Characters() throws Exception {
        TimeslotType timeslot = new TimeslotType();
        timeslot.setReason(                       "0123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789");
        timeslot.setReason(timeslot.getReason() + "0123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789");
        timeslot.setReason(timeslot.getReason() + "0123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789");

        String reason = buildReason(timeslot, "556644221100", log);
        assertEquals(300, reason.length());
    }
    
    @Test
    public void testReason301Characters() throws Exception {
        TimeslotType timeslot = new TimeslotType();
        timeslot.setReason(                       "0123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789");
        timeslot.setReason(timeslot.getReason() + "0123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789");
        timeslot.setReason(timeslot.getReason() + "0123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789");
        timeslot.setReason(timeslot.getReason() + ".");
        
        String reason = buildReason(timeslot, "556644221100", log);
        assertEquals(300, reason.length());
    }
}
