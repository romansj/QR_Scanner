package com.example.qrscanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.qrscanner.messages.CameraMessage;
import com.example.qrscanner.messages.ObservableMessage;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class CameraThread extends Thread {
    public static final String SEPARATOR = ", ";
    CameraCallback cameraCallback;
    ObservableMessage<CameraMessage> observableMessage;
    Disposable disposable;


    public CameraThread(CameraCallback cameraCallback, ObservableMessage<CameraMessage> observableMessage) {
        this.cameraCallback = cameraCallback;
        this.observableMessage = observableMessage;
    }

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    public void run() {
        Log.i("CameraThread", "run, thread name:" + Thread.currentThread().getName());


        initialiseDetectorsAndSources();


        disposable = observableMessage.getObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(message -> {

            switch (message.getType()) {
                case START:
                    Log.i("CameraThread newMssg", "START");
                    if (ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("TAG", "run: ");
                        return;
                    }
                    //can see image in camera
                    cameraSource.start(message.getHolder());
                    break;

                case PAUSE:
                    break;

                case STOP:
                    Log.i("CameraThread newMssg", "STOP");
                    doDisposal();
                    if (cameraSource != null) cameraSource.release();
                    interrupt();
                    break;

                default:
                    //do nothin
                    break;
            }


        }, error -> {
            Log.e("cameraThread", "error:");
            error.printStackTrace();
        });
    }

    void doDisposal() {
        if (disposable != null) disposable.dispose();
    }

    private void initialiseDetectorsAndSources() {
        //System.out.println("Barcode scanner started");


        barcodeDetector = new BarcodeDetector.Builder(MyApp.getInstance().getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(MyApp.getInstance().getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(1000, 1000)
                .setAutoFocusEnabled(true)
                .build();


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                System.out.println("Released barcode scanner resources");
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() == 0) return;

                int maxResults = barcodes.size();

                List<String> results=new ArrayList<>();
                for (int i = 0; i < maxResults; i++) results.add(barcodes.valueAt(i).displayValue);
                cameraCallback.sendData(results);
            }
        });


    }

    public interface CameraCallback {
        void sendData(List<String> text);
    }
}