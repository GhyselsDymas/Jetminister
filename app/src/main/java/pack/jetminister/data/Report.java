package pack.jetminister.data;

public class Report {
    public static final String KEY_REPORT = "report";
    public static final String KEY_REPORTS = "reports";
    public static final String KEY_REASON = "reason";
    public static final String KEY_REPORT_BODY = "reportBody";
    private String reason;
    private String reportBody;

    public Report() { }

    public Report(String reason, String reportBody) {
        this.reason = reason;
        this.reportBody = reportBody;
    }

    public String getReason() {return reason;}

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getReportBody() {
        return reportBody;
    }

    public void setReportBody(String reportBody) {
        this.reportBody = reportBody;
    }
}
