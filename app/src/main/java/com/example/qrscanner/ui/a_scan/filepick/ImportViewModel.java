package com.example.qrscanner.ui.a_scan.filepick;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cherrydev.file.FileData;


public class ImportViewModel extends ViewModel {
    public ImportViewModel() {
        this.uri = new MutableLiveData<>();
        this.fileData = new MutableLiveData<>();
    }

    private MutableLiveData<Uri> uri;
    private MutableLiveData<FileData> fileData;


    public void setFileData(FileData fileData) {
        this.fileData.setValue(fileData);
    }

    public void setUri(Uri uri) {
        this.uri.setValue(uri);
    }




    public MutableLiveData<Uri> getUri() {
        return uri;
    }

    public MutableLiveData<FileData> getFileData() {
        return fileData;
    }


}
