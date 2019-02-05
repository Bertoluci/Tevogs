package com.aero4te.tevogs.model;

import java.io.Serializable;

public class HeaderRecordWrapper extends RecordWrapper implements Serializable {

    //<editor-fold desc="Constants - default values">
    public static String DEFAULT_ID = "TEVOGS";// + (Integer.toHexString(0x00));

    public static int DEFAULT_FORMAT_VERSION = 1;
    //</editor-fold>

    //<editor-fold desc="Properties">
    private String tagId;

    private int formatVersion;

    private byte[] iv;

    private int totalLength;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public HeaderRecordWrapper(String tagId, int formatVersion, byte[] iv) {
        this.tagId = tagId;
        this.formatVersion = formatVersion;
        this.iv = iv;
    }

    public HeaderRecordWrapper(int formatVersion, byte[] iv) {
        this(DEFAULT_ID, formatVersion, iv);
    }

    public HeaderRecordWrapper (String json) {
        this();
        HeaderRecordWrapper hrw = (HeaderRecordWrapper) fromJson(json);
        this.tagId = hrw.getTagId();
        this.formatVersion = hrw.getFormatVersion();
        this.iv = hrw.getIv();
    }

    private HeaderRecordWrapper() {}
    //</editor-fold>

    //<editor-fold desc="Getters">
    public String getTagId() {
        return tagId;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public byte[] getIv() {
        return iv;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public void setFormatVersion(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }
    //</editor-fold>
}
