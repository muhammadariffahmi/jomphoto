package com.example.jomphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jomphoto.databinding.FragmentImageBorderBinding;
import com.example.jomphoto.imagemanip.ImageBorder;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.Objects;

public class ImageBorderFragment extends Fragment {

    private FragmentImageBorderBinding binding;
    private ImageViewModel imageViewModel;

    private final ImageBorder ib = new ImageBorder();

    private Bitmap currentBitmap;
    private Mat lastProcessedMat;

    private Bitmap originalBitmap;

    private Color color;
    private View selectedFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentImageBorderBinding.inflate(inflater, container, false);
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

        binding.black.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.black));
            updateColourPreview(color);
        });
        binding.white.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.white));
            updateColourPreview(color);
        });

        binding.red.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.red));
            updateColourPreview(color);

        });

        binding.blue.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.blue));
            updateColourPreview(color);

        });

        binding.green.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.green));
            updateColourPreview(color);

        });

        binding.magenta.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.magenta));
            updateColourPreview(color);

        });

        binding.yellow.setOnClickListener(v -> {
            color = Color.valueOf(ContextCompat.getColor(requireContext(), R.color.yellow));
            updateColourPreview(color);

        });

        binding.divider2.setVisibility(View.GONE);
        binding.colorPresetRow.setVisibility(View.GONE);

        new EditText(super.requireContext());
        EditText borderSizeInput = binding.borderSizeNumberDecimal;
        borderSizeInput.setText("0");

        binding.button2.setOnClickListener(v -> {

            if (binding.colorPresetRow.getVisibility() == View.GONE) {
                binding.divider2.setVisibility(View.VISIBLE);
                binding.colorPresetRow.setVisibility(View.VISIBLE);


            }
            else {
                binding.divider2.setVisibility(View.GONE);
                binding.colorPresetRow.setVisibility(View.GONE);

            }
                });

        binding.applyBorder.setOnClickListener(v -> {

            if (lastProcessedMat != null) {
                int size = Integer.parseInt(borderSizeInput.getText().toString());

                if (color != null) {
                    addBorder(color, size);

                    imageViewModel.setOriginalImage(lastProcessedMat);
                    originalBitmap = currentBitmap.copy(Objects.requireNonNull(currentBitmap.getConfig()), true);

                } else {
                    Toast.makeText(getContext(), "Please select a color", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.undoBorder.setOnClickListener(v -> {
            if (originalBitmap != null) {
                binding.imageView.setImageBitmap(originalBitmap);
                currentBitmap = originalBitmap;

                Mat mat = new Mat();
                Utils.bitmapToMat(originalBitmap, mat);
                imageViewModel.setProcessedImage(mat);
            }
        });

    }

    private Bitmap convertMatToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }


    private void addBorder(Color color, int size) {

        Mat source = imageViewModel.getProcessedImage().getValue();
        if (source == null) {
            source = imageViewModel.getOriginalImage().getValue();
            if (source == null) return;
        }

        source = ib.addImageBorder(source, color, size);

        imageViewModel.setProcessedImage(source);
    }


    private void updateColourPreview(Color color) {
        Drawable drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.color_preview_shape);

        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable.mutate();
            gradientDrawable.setColor(color.toArgb());
            binding.button2.setCompoundDrawablesWithIntrinsicBounds(null, null, gradientDrawable, null);
        }
    }

}