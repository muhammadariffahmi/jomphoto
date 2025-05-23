package com.example.jomphoto;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.jomphoto.databinding.FragmentColourAdjustBinding;
import com.example.jomphoto.imagemanip.BrightnessContrast;
import com.example.jomphoto.imagemanip.Saturation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ColourAdjustFragment extends Fragment {

    private FragmentColourAdjustBinding binding;
    private ImageViewModel imageViewModel;
    private final BrightnessContrast bc = new BrightnessContrast();
    private final Saturation sat = new Saturation();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = FragmentColourAdjustBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);

        imageViewModel.getProcessedImage().observe(getViewLifecycleOwner(), processedImage -> {
            if (processedImage != null) {
                Bitmap bitmap = Bitmap.createBitmap(processedImage.width(), processedImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(processedImage, bitmap);
                binding.imageView.setImageBitmap(bitmap);
            }
        });

        binding.brightnessSlider.addOnChangeListener((slider, value, fromUser) -> {
            imageViewModel.setBrightness(value);
            applyTransformations();
        });

        binding.contrastSlider.addOnChangeListener((slider, value, fromUser) -> {
            imageViewModel.setContrast(value);
            applyTransformations();
        });

        binding.saturationSlider.addOnChangeListener((slider, value, fromUser) -> {
            imageViewModel.setSaturation(value);
            applyTransformations();
        });

        binding.transform.setOnClickListener(v ->
                NavHostFragment.findNavController(ColourAdjustFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        binding.addBorder.setOnClickListener(v ->
                NavHostFragment.findNavController(ColourAdjustFragment.this)
                        .navigate(R.id.action_ColourAdjustFragment_to_ImageBorderFragment)
        );

    }


    private void applyTransformations() {
        Mat originalImage = imageViewModel.getOriginalImage().getValue();  // Get the original image
        if (originalImage == null) return;

        float brightness = imageViewModel.getBrightness().getValue();
        float contrast = imageViewModel.getContrast().getValue();
        float saturation = imageViewModel.getSaturation().getValue();

        Mat transformedImage = originalImage.clone();

        Mat rgbImage = new Mat();
        Imgproc.cvtColor(transformedImage, rgbImage, Imgproc.COLOR_BGR2RGB);

        rgbImage = bc.changeBrightnessAndContrast(rgbImage, contrast, brightness);
        rgbImage = sat.changeSaturation(rgbImage, saturation);

        imageViewModel.setProcessedImage(rgbImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
