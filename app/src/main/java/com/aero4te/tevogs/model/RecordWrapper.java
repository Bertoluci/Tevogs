package com.aero4te.tevogs.model;

import com.google.gson.Gson;

public abstract class RecordWrapper<T> implements IRecordWrapper {

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public T fromJson(String json) {
        Gson gson = new Gson();
        T recordWrapper = (T) gson.fromJson(json, this.getClass());
        return recordWrapper;
    }
}
