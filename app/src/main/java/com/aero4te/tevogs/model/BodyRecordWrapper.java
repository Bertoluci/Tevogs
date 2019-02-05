package com.aero4te.tevogs.model;

import java.io.Serializable;

public class BodyRecordWrapper extends RecordWrapper implements Serializable {

    //<editor-fold desc="Properties">
    private String serverIPv4;

    private DistributionType distributionType;

    private String distributionPort;

    private String multicastGroup;

    private String clientId;

    private String componentID;

    private int authkeyLength;

    private String authkey;

    private int wifiSSIDLength;

    private String wifiSSID;

    private int wifiPasswordLength;

    private String wifiPassword;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public BodyRecordWrapper(String serverIPv4, DistributionType distributionType, String distributionPort, String multicastGroup, String clientId, String componentID, String authkey, String wifiSSID, String wifiPassword) {
        this.serverIPv4 = serverIPv4;
        this.distributionType = distributionType;
        this.distributionPort = distributionPort;
        this.multicastGroup = multicastGroup;
        this.clientId = clientId;
        this.componentID = componentID;
        this.authkey = authkey;
        this.wifiSSID = wifiSSID;
        this.wifiPassword = wifiPassword;
    }

    public BodyRecordWrapper (String json) {
        this();
        BodyRecordWrapper brw = (BodyRecordWrapper) fromJson(json);
        this.serverIPv4 = brw.getServerIPv4();
        this.distributionType = brw.getDistributionType();
        this.distributionPort = brw.getDistributionPort();
        this.multicastGroup = brw.getMulticastGroup();
        this.clientId = brw.getClientId();
        this.componentID = brw.getComponentID();
        this.authkey = brw.getAuthkey();
        this.wifiSSID = brw.getWifiSSID();
        this.wifiPassword = brw.getWifiPassword();
    }

    private BodyRecordWrapper() {}
    //</editor-fold>

    //<editor-fold desc="Getters">
    public String getServerIPv4() {
        return serverIPv4;
    }

    public DistributionType getDistributionType() {
        return distributionType;
    }

    public String getDistributionPort() {
        return distributionPort;
    }

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public String getClientId() {
        return clientId;
    }

    public String getComponentID() {
        return componentID;
    }

    public int getAuthkeyLength() {
        return authkeyLength;
    }

    public String getAuthkey() {
        return authkey;
    }

    public int getWifiSSIDLength() {
        return wifiSSIDLength;
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public int getWifiPasswordLength() {
        return wifiPasswordLength;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setServerIPv4(String serverIPv4) {
        this.serverIPv4 = serverIPv4;
    }

    public void setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
    }

    public void setDistributionPort(String distributionPort) {
        this.distributionPort = distributionPort;
    }

    public void setMulticastGroup(String multicastGroup) {
        this.multicastGroup = multicastGroup;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
        authkeyLength = this.authkey.getBytes().length;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
        wifiSSIDLength = this.wifiSSID.getBytes().length;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
        wifiPasswordLength = this.wifiPassword.getBytes().length;
    }
    //</editor-fold>
}
