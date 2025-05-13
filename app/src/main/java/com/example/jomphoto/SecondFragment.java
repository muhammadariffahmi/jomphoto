package com.example.jomphoto;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jomphoto.databinding.FragmentSecondBinding;

import org.opencv.android.Utils;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageViewModel imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);

        imageViewModel.getProcessedImage().observe(getViewLifecycleOwner(), processedImage -> {
            if (processedImage != null) {
                Bitmap bitmap = Bitmap.createBitmap(processedImage.width(), processedImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(processedImage, bitmap);
                binding.imageView.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
