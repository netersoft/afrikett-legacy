package com.neteru.afrikett.core.models.RemoteDB;

import com.neteru.afrikett.core.utilities.Timing;

@SuppressWarnings("unused")
public class Report {

    private String reportId;
    private String reportDate;
    private String subjectId;
    private String subjectName;
    private String subjectImg;
    private String subjectInfo;
    private Integer subjectType;
    private String reporterId;
    private String reporterComment;

    public Report(){}

    public Report(String reportId, String subjectId, String subjectName, String subjectImg, String subjectInfo, Integer subjectType, String reporterId, String reporterComment){

        this.reportId = reportId;
        this.reportDate = Timing.getCurrentDate();
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectImg = subjectImg;
        this.subjectInfo = subjectInfo;
        this.subjectType = subjectType;
        this.reporterId = reporterId;
        this.reporterComment = reporterComment;

    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectImg() {
        return subjectImg;
    }

    public void setSubjectImg(String subjectImg) {
        this.subjectImg = subjectImg;
    }

    public String getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(String subjectInfo) {
        this.subjectInfo = subjectInfo;
    }

    public Integer getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Integer subjectType) {
        this.subjectType = subjectType;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterComment() {
        return reporterComment;
    }

    public void setReporterComment(String reporterComment) {
        this.reporterComment = reporterComment;
    }
}
