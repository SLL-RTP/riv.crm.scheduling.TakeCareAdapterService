package se.skl.skltpservices.takecare;

import static org.junit.Assert.assertEquals;
import static se.skl.skltpservices.takecare.TakeCareRequestTransformer.buildReason;

import org.junit.Test;

import se.riv.crm.scheduling.v1.SubjectOfCareType;
import se.riv.crm.scheduling.v1.TimeslotType;

public class TakeCareResponseTransformerTest {

    @Test
    public void testBuildReasonFromReasonAndPhone() throws Exception {
        SubjectOfCareType subjectOfCare = new SubjectOfCareType();
        subjectOfCare.setPhone("1234567890");
        TimeslotType timeslot = new TimeslotType();
        timeslot.setReason("Ont i ryggen");

        String reason = buildReason(subjectOfCare, timeslot);
        assertEquals("Ont i ryggen", reason);
    }

    @Test
    public void testBuildReasonWithoutPhone() throws Exception {
        SubjectOfCareType subjectOfCare = new SubjectOfCareType();
        TimeslotType timeslot = new TimeslotType();
        timeslot.setReason("Ont i ryggen");

        String reason = buildReason(subjectOfCare, timeslot);
        assertEquals("Ont i ryggen", reason);
    }

    @Test
    public void testBuildReasonWithOnlyPhone() throws Exception {
        SubjectOfCareType subjectOfCare = new SubjectOfCareType();
        subjectOfCare.setPhone("1234567890");
        TimeslotType timeslot = new TimeslotType();

        String reason = buildReason(subjectOfCare, timeslot);
        assertEquals("", reason);
    }
}
