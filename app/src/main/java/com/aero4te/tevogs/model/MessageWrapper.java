package com.aero4te.tevogs.model;

import java.io.Serializable;

public class MessageWrapper extends RecordWrapper implements Serializable {

    //<editor-fold desc="Properties">
    private HeaderRecordWrapper headerRecordWrapper;

    private BodyRecordWrapper bodyRecordWrapper;

    private String aarPackageName;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public MessageWrapper(HeaderRecordWrapper headerRecordWrapper, BodyRecordWrapper bodyRecordWrapper) {
        this.headerRecordWrapper = headerRecordWrapper;
        this.bodyRecordWrapper = bodyRecordWrapper;
    }

    public MessageWrapper(HeaderRecordWrapper headerRecordWrapper, BodyRecordWrapper bodyRecordWrapper, String aarPackageName) {
        this(headerRecordWrapper, bodyRecordWrapper);
        this.aarPackageName = aarPackageName;
    }

    public MessageWrapper(String json) {
        this();
        MessageWrapper mw = (MessageWrapper) fromJson(json);
        headerRecordWrapper = mw.getHeaderRecordWrapper();
        bodyRecordWrapper = mw.getBodyRecordWrapper();
    }

    private MessageWrapper() {}
    //</editor-fold>

    //<editor-fold desc="Getters">
    public HeaderRecordWrapper getHeaderRecordWrapper() {
        return headerRecordWrapper;
    }

    public BodyRecordWrapper getBodyRecordWrapper() {
        return bodyRecordWrapper;
    }

    public String getAarPackageName() {
        return aarPackageName;
    }
    //</editor-fold>
}
