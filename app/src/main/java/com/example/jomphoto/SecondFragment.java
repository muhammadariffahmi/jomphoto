package com.example.jomphoto;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jomphoto.databinding.FragmentSecondBinding;
import com.example.jomphoto.imagemanip.Rotate;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ImageViewModel imageViewModel;
    private final Rotate r = new Rotate();



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

        imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);

        imageViewModel.getProcessedImage().observe(getViewLifecycleOwner(), processedImage -> {
            if (processedImage != null) {
                Bitmap bitmap = Bitmap.createBitmap(processedImage.width(), processedImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(processedImage, bitmap);
                binding.imageView.setImageBitmap(bitmap);
            }
        });

        binding.rotateLeft.setOnClickListener(v -> rotateLeft());
        binding.rotateRight.setOnClickListener(v -> rotateRight());

        binding.applyCrop.setVisibility(View.GONE);

        binding.crop.setOnClickListener(v -> {
            binding.imageView.setVisibility(View.GONE);
            binding.cropImageView.setVisibility(View.VISIBLE);
            binding.applyCrop.setVisibility(View.VISIBLE);

            binding.cropImageView.setImageBitmap(((BitmapDrawable) binding.imageView.getDrawable()).getBitmap());

            // TODO cropping tool
        });

        binding.applyCrop.setOnClickListener(v -> {
            Bitmap croppedBitmap = binding.cropImageView.getCroppedImage();
            binding.imageView.setImageBitmap(croppedBitmap);

            binding.cropImageView.setVisibility(View.GONE);
            binding.applyCrop.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.VISIBLE);
        });
    }




    boolean isRGB = false;

    private void rotateLeft() {

        Mat source = imageViewModel.getProcessedImage().getValue();
        if (source == null) {
            source = imageViewModel.getOriginalImage().getValue();
            if (source == null) return;
        }

        if (!isRGB) {
            Mat rgbImage = new Mat();
            Imgproc.cvtColor(source, rgbImage, Imgproc.COLOR_BGR2RGB);
            source = rgbImage;
            isRGB = true;
        }

        source = r.rotateLeft(source);

        imageViewModel.setProcessedImage(source);
    }

    private void rotateRight() {

        Mat source = imageViewModel.getProcessedImage().getValue();
        if (source == null) {
            source = imageViewModel.getOriginalImage().getValue();
            if (source == null) return;
        }

        if (!isRGB) {
            Mat rgbImage = new Mat();
            Imgproc.cvtColor(source, rgbImage, Imgproc.COLOR_BGR2RGB);
            source = rgbImage;
            isRGB = true;
        }

        source = r.rotateRight(source);

        imageViewModel.setProcessedImage(source);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
