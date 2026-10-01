package com.example.smartpantry.data;

/**
 * Receives the outcome of an asynchronous repository call. Both methods are
 * always invoked on the main (UI) thread
 *
 * @param <T> type of the successful result
 */
public interface ResultCallback<T> {

    void onSuccess(T result);

    /** @param message a short, user-presentable description of what went wrong */
    void onError(String message);
}