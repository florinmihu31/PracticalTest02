package ro.pub.cs.systems.eim.practicaltest02.model;

public class Information {
    private String value;
    private Long unixTime;

    public Information(String value, Long unixTime) {
        this.value = value;
        this.unixTime = unixTime;
    }

    public String getValue() {
        return value;
    }

    public Long getUnixTime() {
        return unixTime;
    }

    @Override
    public String toString() {
        return "Information{" +
                "value='" + value + '\'' +
                ", unixTime=" + unixTime +
                '}';
    }
}
