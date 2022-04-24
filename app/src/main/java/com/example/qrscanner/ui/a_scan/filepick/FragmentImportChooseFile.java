package com.example.qrscanner.ui.a_scan.filepick;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cherrydev.common.MimeTypes;
import com.cherrydev.file.FileData;
import com.cherrydev.file.FileUtils;
import com.example.qrscanner.databinding.FragmentBackupChooseFileBinding;


public class FragmentImportChooseFile extends Fragment {
    public static FragmentImportChooseFile newInstance() {

        Bundle args = new Bundle();

        FragmentImportChooseFile fragment = new FragmentImportChooseFile();
        fragment.setArguments(args);
        return fragment;
    }

    FragmentBackupChooseFileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBackupChooseFileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

  ImportViewModel importVM;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        importVM = new ViewModelProvider(requireActivity()).get(ImportViewModel.class);
        importVM.getFileData().observe(getViewLifecycleOwner(), fileData -> {
            binding.cardViewInfo.setVisibility(View.VISIBLE);
            binding.nameView.setText(fileData.getFileName());
            binding.sizeView.setText(""+fileData.getSize());
            binding.infoView.setText(String.valueOf(fileData.getLastModified()));
        });


        binding.buttonChooseFile.setOnClickListener(v -> {
            mGetContent.launch(MimeTypes.Image.ALL);
        });


        mutableLiveData.observe(getViewLifecycleOwner(), uri -> {
            if (uri == null) {
                Toast.makeText(requireContext(), "URI NULL", Toast.LENGTH_LONG).show();
                return;
            }

           System.out.println("uri " + uri);


            ContentResolver contentResolver = requireActivity().getContentResolver();


            FileData fileData = FileUtils.getFileData(contentResolver, uri);
            importVM.setUri(uri);
            importVM.setFileData(fileData);
        });
    }

    MutableLiveData<Uri> mutableLiveData = new MutableLiveData<>();

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> mutableLiveData.postValue(uri));

}
