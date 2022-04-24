package com.example.qrscanner.ui.b_generate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrscanner.BarcodeHelper;
import com.example.qrscanner.EditTextDebounce;
import com.example.qrscanner.SaveHelper;
import com.example.qrscanner.databinding.FragmentGenerateBinding;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentGenerateBinding binding;
    Bitmap currentBitmap;


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarcodeHelper barcodeHelper = new BarcodeHelper();
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            if (s.isEmpty()) return;

            barcodeHelper.getBitmap(s).observe(getViewLifecycleOwner(), bitmap -> {
                ImageView myImage = binding.imageView;
                myImage.setImageBitmap(bitmap);
                currentBitmap=bitmap;
            });
        });


        EditTextDebounce.create(binding.inputLayout.getEditText(), 350).watch(result -> dashboardViewModel.setText(result));



        binding.btnSave.setOnClickListener(v -> {
            new SaveHelper().saveImageExternal(currentBitmap, uri -> {
                Snackbar mySnackbar = Snackbar.make(binding.getRoot(), "Image saved", Snackbar.LENGTH_LONG);
                mySnackbar.setAction("Open", v1 -> startActivity(new Intent(Intent.ACTION_VIEW, uri)));
                mySnackbar.show();
            });
        });


        binding.btnShare.setOnClickListener(v ->{
            if(currentBitmap==null)return;
            new SaveHelper().saveImage(currentBitmap, uri -> shareImageUri(uri));
        });
    }

    /**
     * Shares the PNG image from Uri.
     * @param uri Uri of image to share.
     */
    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Select"));
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentGenerateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}