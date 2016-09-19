package com.teehalf.pillreminder.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by ajay on 02-May-16.
 */
public class RxBus {

    private static final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());

    public static void send(Object o) {
        _bus.onNext(o);
    }

    public static Observable<Object> toObserverable() {
        return _bus;
    }

}
