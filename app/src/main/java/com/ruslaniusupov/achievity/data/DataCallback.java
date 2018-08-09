package com.ruslaniusupov.achievity.data;

public interface DataCallback<T> {

    void onDataLoaded(T data);

    void onDataNotAvailable(Exception e);

}
