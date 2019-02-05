package com.aero4te.tevogs.model;

import java.io.Serializable;

public class MessageWrapper extends RecordWrapper implements Serializable {

    //<editor-fold desc="Properties">
    private HeaderRecordWrapper headerRecordWrapper;

    private BodyRecordWrapper bodyRecordWrapper;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public MessageWrapper(HeaderRecordWrapper headerRecordWrapper, BodyRecordWrapper bodyRecordWrapper) {
        headerRecordWrapper = headerRecordWrapper;
        bodyRecordWrapper = bodyRecordWrapper;
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
    //</editor-fold>
}
