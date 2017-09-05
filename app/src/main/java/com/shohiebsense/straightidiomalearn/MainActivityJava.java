package com.shohiebsense.straightidiomalearn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by shohiebsense on 14/06/17.
 */

@EActivity(R.layout.activity_main)
public class MainActivityJava extends AppCompatActivity {

    Observable<String> myObservable;
    Observer<String> myObserver;


    @AfterViews
    void init(){
        setTitle("Activity 1");
        createObservableAndObserver();
        myObservable.subscribe(myObserver);
        Observable.fromArray(Arrays.asList(1,2,3,4,5));

    }

    private void createObservableAndObserver() {
        myObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("au");
                e.onComplete();
            }
        });

        myObserver = new Observer<String>() {

            @Override
            public void onComplete() {
                Toast.makeText(MainActivityJava.this, "onCOmpleted",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(String text) {
                //textView.setText(text);
            }
        };

    }
}
