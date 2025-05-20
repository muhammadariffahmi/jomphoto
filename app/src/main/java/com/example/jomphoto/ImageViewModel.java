package com.example.jomphoto;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.opencv.core.Mat;

public class ImageViewModel extends ViewModel {
    private final MutableLiveData<Mat> originalImage = new MutableLiveData<>();
    private final MutableLiveData<Mat> processedImage = new MutableLiveData<>();
    private final MutableLiveData<Float> brightness = new MutableLiveData<>(0.0f);
    private final MutableLiveData<Float> contrast = new MutableLiveData<>(1.0f);
    private final MutableLiveData<Float> saturation = new MutableLiveData<>(1.0f);

    public MutableLiveData<Mat> getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(Mat image) {
        originalImage.setValue(image);
    }

    public MutableLiveData<Mat> getProcessedImage() {
        return processedImage;
    }

    public void setProcessedImage(Mat image) {
        processedImage.setValue(image);
    }

    public MutableLiveData<Float> getBrightness() {
        return brightness;
    }

    public void setBrightness(float value) {
        brightness.setValue(value);
    }

    public MutableLiveData<Float> getContrast() {
        return contrast;
    }

    public void setContrast(float value) {
        contrast.setValue(value);
    }

    public MutableLiveData<Float> getSaturation() {
        return saturation;
    }

    public void setSaturation(float value) {
        saturation.setValue(value);
    }
}
