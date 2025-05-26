package com.example.jomphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jomphoto.databinding.FragmentPreviewBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.Objects;


public class PreviewFragment extends Fragment {

    private FragmentPreviewBinding binding;

    private ImageViewModel imageViewModel;

    private Bitmap currentBitmap;
    private Mat lastProcessedMat;

    private Bitmap originalBitmap;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentPreviewBinding.inflate(inflater, container, false);
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


        TextInputEditText annotationInput = binding.annotationEditText;
        String savedAnnotation = imageViewModel.getAnnotation().getValue();
        annotationInput.setText(savedAnnotation != null ? savedAnnotation : "");

        annotationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String annotation = Objects.requireNonNull(annotationInput.getText()).toString();
                imageViewModel.setAnnotation(annotation);
            }
        });

        binding.adjustColourButton.setOnClickListener(v -> {
//            String annotation = Objects.requireNonNull(annotationInput.getText()).toString();
//            imageViewModel.setAnnotation(annotation);
//

            // Navigate
            NavHostFragment.findNavController(PreviewFragment.this)
                    .navigate(R.id.action_PreviewFragment_to_ColourAdjustFragment);
        });

        binding.saveAnnotation.setOnClickListener(v -> {
            String annotation = Objects.requireNonNull(annotationInput.getText()).toString();
            imageViewModel.setAnnotation(annotation);

            String uri = imageViewModel.getPhotoUri() != null ? imageViewModel.getPhotoUri().toString() : "null";
            Toast.makeText(requireContext(), "Saving for URI: " + uri, Toast.LENGTH_LONG).show();

            if (uri.equals("null")) {
                Toast.makeText(requireContext(), "Photo URI is null. Cannot save annotation.", Toast.LENGTH_SHORT).show();
                return;
            }

            PhotoDatabaseHelper dbHelper = new PhotoDatabaseHelper(requireContext());
            dbHelper.updateAnnotate(uri, annotation); // This probably doesn't match any existing photo

            Intent intent = new Intent(requireContext(), FullscreenActivity.class);
            intent.putExtra("photo_uri", uri);
            startActivity(intent);
        });


    }

        private Bitmap convertMatToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
        }

}