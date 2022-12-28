package fit.foot.domain.enumeration;

import java.time.LocalTime;

public enum TimeSlot {
    SLOT_8_10("8:00-10:00", LocalTime.of(8, 0), LocalTime.of(10, 0)),
    SLOT_10_12("10:00-12:00", LocalTime.of(10, 0), LocalTime.of(12, 0)),
    SLOT_12_14("12:00-14:00", LocalTime.of(12, 0), LocalTime.of(14, 0)),
    SLOT_14_16("14:00-16:00", LocalTime.of(14, 0), LocalTime.of(16, 0)),
    SLOT_16_18("16:00-18:00", LocalTime.of(16, 0), LocalTime.of(18, 0)),
    SLOT_18_20("18:00-20:00", LocalTime.of(18, 0), LocalTime.of(20, 0)),
    SLOT_20_22("20:00-22:00", LocalTime.of(20, 0), LocalTime.of(22, 0));

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
