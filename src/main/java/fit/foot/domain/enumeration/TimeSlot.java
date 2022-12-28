package fit.foot.domain.enumeration;

import java.time.LocalTime;

public enum TimeSlot {
    SLOT_8_10("08:00-10:00", LocalTime.of(7, 0), LocalTime.of(9, 0)),
    SLOT_10_12("10:00-12:00", LocalTime.of(9, 0), LocalTime.of(11, 0)),
    SLOT_12_14("12:00-14:00", LocalTime.of(11, 0), LocalTime.of(13, 0)),
    SLOT_14_16("14:00-16:00", LocalTime.of(13, 0), LocalTime.of(15, 0)),
    SLOT_16_18("16:00-18:00", LocalTime.of(15, 0), LocalTime.of(17, 0)),
    SLOT_18_20("18:00-20:00", LocalTime.of(17, 0), LocalTime.of(19, 0)),
    SLOT_20_22("20:00-22:00", LocalTime.of(19, 0), LocalTime.of(21, 0));

    private final String label;
    private final LocalTime startTime;
    private final LocalTime endTime;

    TimeSlot(String label, LocalTime startTime, LocalTime endTime) {
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getLabel() {
        return label;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
