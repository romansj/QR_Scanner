package com.example.qrscanner.ui.a_scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cherrydev.sharing.DialogShare;
import com.example.qrscanner.CameraThread;
import com.example.qrscanner.MyApp;
import com.example.qrscanner.R;
import com.example.qrscanner.databinding.FragmentScanBinding;
import com.example.qrscanner.messages.CameraMessage;
import com.example.qrscanner.messages.ObservableMessage;

import org.jetbrains.annotations.NotNull;

@SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
public class HomeFragment extends Fragment {

    private ObservableMessage<CameraMessage> observableMessage;
    private FragmentScanBinding binding;
    private SurfaceView surfaceView;
    private String lastText = "";
    private boolean isLink = false;


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            Toast.makeText(requireContext(), "got perm " + isGranted, Toast.LENGTH_SHORT).show();
            if (isGranted) initViews();
        });
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);


        initViews();
    }


    private void initViews() {
        surfaceView = binding.surfaceView;
        Log.i("initViews", "null: " + (surfaceView.getHolder() == null));


        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("surfaceCreated", "holder:" + holder.toString());
                observableMessage.setValue(new CameraMessage(CameraMessage.Type.START, surfaceView.getHolder()));
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i("surfChanged", "form:" + format + ", w:" + width + " h:" + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("surfaceDestroyed", "holder:" + holder.toString());
                observableMessage.setValue(new CameraMessage(CameraMessage.Type.STOP));
            }

        };

        surfaceView.getHolder().addCallback(callback);

        //to get this shit working after onResume of fragment recreation :). Without it "surfaceCreated" is never called.
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);


        binding.btnShare.setOnClickListener(v -> {
            if (lastText.isEmpty()) return;

            DialogShare dialogShare = DialogShare.newInstance(lastText, new DialogShare.ShareClickListener() {
                @Override
                public void onShareItemClick(View view, int position) {

                }
            });

            dialogShare.show(getChildFragmentManager(), null);
        });

        binding.btnOpen.setOnClickListener(v -> {
            if (lastText.isEmpty()) return;
            if (!isLink) return;

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lastText)));
        });


        ClipboardManager clipboard = (ClipboardManager) MyApp.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);

        binding.btnCopy.setOnClickListener(v -> {
            if (lastText.isEmpty()) return;

            ClipData clip = ClipData.newPlainText("null", lastText);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(requireActivity(), "Copied!", Toast.LENGTH_SHORT).show();
        });


        CameraThread.CameraCallback cameraCallback = list -> {
            requireActivity().runOnUiThread(() -> {
                //without binding==null check -> NPE happens after exiting fragment
                if (binding == null) return;


                String joinedStrings = String.join(",", list);
                binding.txtBarcodeValue.setText(joinedStrings);
                binding.txtBarcodeValueCount.setText("" + list.size());


                lastText = joinedStrings;
                isLink = Patterns.WEB_URL.matcher(lastText).matches();

                binding.btnOpen.setEnabled(isLink);
                if (isLink) binding.btnOpen.setIcon(requireActivity().getDrawable(R.drawable.open_in_new));
            });
        };


        observableMessage = new ObservableMessage<>();

        CameraThread cameraThread = new CameraThread(cameraCallback, observableMessage);
        cameraThread.start();


        binding.btnFilePick.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_pick_file);
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (surfaceView != null) initViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}