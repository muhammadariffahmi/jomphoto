package com.example.jomphoto;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.jomphoto.databinding.FragmentColourAdjustBinding;
import com.example.jomphoto.imagemanip.BrightnessContrast;
import com.example.jomphoto.imagemanip.Saturation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ColourAdjustFragment extends Fragment {

    private FragmentColourAdjustBinding binding;
    private ImageViewModel imageViewModel;
    private final BrightnessContrast bc = new BrightnessContrast();
    private final Saturation sat = new Saturation();

    private Bitmap currentBitmap;
    private Mat lastProcessedMat;

    private Bitmap originalBitmap;

    private PhotoDatabaseHelper helper;

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

        helper = new PhotoDatabaseHelper(requireContext());

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

        binding.undoAll.setOnClickListener(v -> {
            if (originalBitmap != null) {
                binding.imageView.setImageBitmap(originalBitmap);
                currentBitmap = originalBitmap;

                Mat mat = new Mat();
                Utils.bitmapToMat(originalBitmap, mat);
                imageViewModel.setProcessedImage(mat);
            }

            binding.brightnessSlider.setValue(0.0f);
            binding.contrastSlider.setValue(1.0f);
            binding.saturationSlider.setValue(1.0f);

        });

        binding.applyAll.setOnClickListener(v -> {
            if (currentBitmap == null) return;

            // get uri from parent activity intent
            String originalUri = null;


            if (getActivity() != null && getActivity().getIntent() != null) {
                originalUri = getActivity().getIntent().getStringExtra("photo_uri");

                System.out.println(originalUri);

            }

            if (originalUri == null) {
                Toast.makeText(getContext(), "Original photo URI not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // save edited bitmap to file
            Uri newUri = saveBitmapToFile(currentBitmap, originalUri);

            System.out.println(newUri);

            if (newUri != null) {
                boolean success = helper.updatePhotoUri(originalUri, newUri.toString());

                if (success) {
                    Toast.makeText(getContext(), "Photo saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update photo in database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to save edited photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap convertMatToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private void applyTransformations() {
        Mat originalImage = imageViewModel.getOriginalImage().getValue();  // Get the original image
        if (originalImage == null) return;

        float brightness = imageViewModel.getBrightness().getValue();
        float contrast = imageViewModel.getContrast().getValue();
        float saturation = imageViewModel.getSaturation().getValue();

        Mat transformedImage = originalImage.clone();

        transformedImage = bc.changeBrightnessAndContrast(transformedImage, contrast, brightness);
        transformedImage = sat.changeSaturation(transformedImage, saturation);

        imageViewModel.setProcessedImage(transformedImage);
    }

    private Uri saveBitmapToFile(Bitmap bitmap, String originalUri) {
        try {
            File file = new File(requireContext().getCacheDir(), "edited_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
