package com.example.quizapp4;

import android.net.Uri;

import java.io.Serializable;

public class gallerymodel implements Serializable {
    private String nameOfDog;
    private int imageResource; // Not 0 if it is a drawable resource
    private String imageUriString; // Not null if it is a gallery image

    // Constructor for drawable resources
    public gallerymodel(String nameOfDog, int imageResource) {
        this.nameOfDog = nameOfDog;
        this.imageResource = imageResource;
        this.imageUriString = null;
    }

    // Constructor for images from the gallery
    public gallerymodel(String nameOfDog, Uri imageUri) {
        this.nameOfDog = nameOfDog;
        this.imageResource = 0; // No drawable resource
        this.imageUriString = imageUri.toString();
    }

    public String getNameOfDog() {
        return nameOfDog;
    }

    public int getImageResource() {
        return imageResource;
    }

    // Returns the URI (or null if there is no URI)
    public Uri getImageUri() {
        return (imageUriString != null) ? Uri.parse(imageUriString) : null;
    }
}
