package pack.jetminister.data;

import org.threeten.bp.LocalDateTime;

public class Report {
    public static final String KEY_REPORT = "report";
    public static final String KEY_REASON = "reason";
    public static final String KEY_REPORT_BODY = "reportBody";

    private String loggerID;
    private String aboutID;
    private String reason;
    private String reportBody;
    private String reportTimeStamp;

    public Report() { }

    public Report(String loggerID, String aboutID, String reason, String reportBody, String reportTimeStamp) {
        this.loggerID = loggerID;
        this.aboutID = aboutID;
        this.reason = reason;
        this.reportBody = reportBody;
        this.reportTimeStamp = reportTimeStamp;
    }

    public String getLoggerID() {
        return loggerID;
    }

    public String getAboutID() {
        return aboutID;
    }

    public String getReason() {return reason;}

    public String getReportBody() {
        return reportBody;
    }

    public String getReportTimeStamp() {
        return reportTimeStamp;
    }


    public void setLoggerID(String loggerID) {
        this.loggerID = loggerID;
    }

    public void setAboutID(String aboutID) {
        this.aboutID = aboutID;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setReportBody(String reportBody) {
        this.reportBody = reportBody;
    }

    public void setReportTimeStamp(String reportTimeStamp) {
        this.reportTimeStamp = reportTimeStamp;
    }
}

