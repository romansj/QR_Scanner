package com.example.qrscanner;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import net.glxn.qrgen.android.QRCode;

public class BarcodeHelper {

    public MutableLiveData<Bitmap> getBitmap(String text) {
        MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();

        MyBitmapThread myBitmapThread = new MyBitmapThread(bitmapMutableLiveData, text);
        myBitmapThread.start();

        return bitmapMutableLiveData;
    }

    private class MyBitmapThread extends Thread {
        private MutableLiveData<Bitmap> bitmapMutableLiveData;
        private String ip;

        public MyBitmapThread(MutableLiveData<Bitmap> bitmapMutableLiveData, String ip) {
            this.bitmapMutableLiveData = bitmapMutableLiveData;
            this.ip = ip;
        }

        @Override
        public void run() {
            Bitmap myBitmap = QRCode.from(ip).withSize(500, 500).bitmap();
            bitmapMutableLiveData.postValue(myBitmap);
        }
    }


}
