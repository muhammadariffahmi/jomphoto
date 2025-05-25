package com.example.jomphoto;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jomphoto.databinding.FragmentTransformBinding;
import com.example.jomphoto.imagemanip.Rotate;
import com.example.jomphoto.imagemanip.Scale;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.Objects;

public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;
    private ImageViewModel imageViewModel;
    private final Rotate r = new Rotate();

    private final Scale s = new Scale();

    private Bitmap currentBitmap;
    private Mat lastProcessedMat;

    private Bitmap originalBitmap;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);

        imageViewModel.getProcessedImage().observe(getViewLifecycleOwner(), processedImage -> {
            if (processedImage != null) {
                lastProcessedMat = processedImage;
                currentBitmap = convertMatToBitmap(processedImage);
                binding.imageView.setImageBitmap(currentBitmap);
            }

            if (originalBitmap == null) {
                originalBitmap = currentBitmap.copy(Objects.requireNonNull(currentBitmap.getConfig()), true);
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
        });

        binding.applyCrop.setOnClickListener(v -> {
            Bitmap croppedBitmap = binding.cropImageView.getCroppedImage();
            binding.imageView.setImageBitmap(croppedBitmap);
            currentBitmap = croppedBitmap;

            Mat croppedMat = new Mat();
            Utils.bitmapToMat(croppedBitmap, croppedMat);
            imageViewModel.setProcessedImage(croppedMat);
            lastProcessedMat = croppedMat;

            binding.cropImageView.setVisibility(View.GONE);
            binding.applyCrop.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.VISIBLE);
        });


        new EditText(super.requireContext());
        EditText scaleFactorInput = binding.scaleFactorNumberDecimal;
        scaleFactorInput.setText("1.0");


        float scaleFactor = Float.parseFloat(scaleFactorInput.getText().toString());


        binding.undo.setOnClickListener(v -> {
            if (originalBitmap != null) {
                binding.imageView.setImageBitmap(originalBitmap);
                currentBitmap = originalBitmap;

                Mat mat = new Mat();
                Utils.bitmapToMat(originalBitmap, mat);
                imageViewModel.setProcessedImage(mat);
            }
        });

        binding.apply.setOnClickListener(v -> {
            if (lastProcessedMat != null) {
                imageViewModel.setOriginalImage(lastProcessedMat);
                originalBitmap = currentBitmap.copy(Objects.requireNonNull(currentBitmap.getConfig()), true);

                scale(scaleFactor);

                Toast.makeText(super.requireContext(), "Changes Applied", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private Bitmap convertMatToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private void rotateLeft() {

        Mat source = imageViewModel.getProcessedImage().getValue();
        if (source == null) {
            source = imageViewModel.getOriginalImage().getValue();
            if (source == null) return;
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

        source = r.rotateRight(source);

        imageViewModel.setProcessedImage(source);
    }

    private void scale(float scaleFactor) {

        Mat source = imageViewModel.getProcessedImage().getValue();
        if (source == null) {
            source = imageViewModel.getOriginalImage().getValue();
            if (source == null) return;
        }

        source = s.scale(source, scaleFactor);

        imageViewModel.setProcessedImage(source);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
