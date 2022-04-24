package com.example.qrscanner.ui.b_generate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("www.google.com");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String result) {
        this.mText.setValue(result);
    }
}