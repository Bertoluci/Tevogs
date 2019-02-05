package com.aero4te.tevogs.model;

import java.io.Serializable;

public class MesaageWrwpper extends RecordWrapper implements Serializable {

    //<editor-fold desc="Properties">
    private HeaderRecordWrapper headerRecordWrapper;

    private BodyRecordWrapper bodyRecordWrapper;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public MesaageWrwpper(HeaderRecordWrapper headerRecordWrapper, BodyRecordWrapper bodyRecordWrapper) {
        headerRecordWrapper = headerRecordWrapper;
        bodyRecordWrapper = bodyRecordWrapper;
    }

    public MesaageWrwpper (String json) {
        this();
        MesaageWrwpper mw = (MesaageWrwpper) fromJson(json);
        headerRecordWrapper = mw.getHeaderRecordWrapper();
        bodyRecordWrapper = mw.getBodyRecordWrapper();
    }

    private MesaageWrwpper() {}
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
