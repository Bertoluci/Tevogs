package com.aero4te.tevogs.model;

public interface IRecordWrapper<T> {

    public String toJson();

    public T fromJson(String json);
}
