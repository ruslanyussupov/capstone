package com.ruslaniusupov.achievity.data;

public interface StatusCallback {

    void onSuccess();

    void onFailure(Exception e);

}
