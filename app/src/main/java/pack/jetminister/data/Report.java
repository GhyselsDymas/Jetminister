package pack.jetminister.data;

public class Report {
    public static final String KEY_REPORT = "report";
    public static final String KEY_REASON = "reason";
    public static final String KEY_REPORT_BODY = "reportBody";

    private String loggerID;
    private String subjectID;
    private String reason;
    private String reportBody;

    public Report() { }

    public Report(String loggerID, String subjectID, String reason, String reportBody) {
        this.loggerID = loggerID;
        this.subjectID = subjectID;
        this.reason = reason;
        this.reportBody = reportBody;
    }

    public String getLoggerID() {
        return loggerID;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public String getReason() {return reason;}

    public void setReason(String reason) {
        this.reason = reason;
    }


    public void setLoggerID(String loggerID) {
        this.loggerID = loggerID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getReportBody() {
        return reportBody;
    }

    public void setReportBody(String reportBody) {
        this.reportBody = reportBody;
    }
}
