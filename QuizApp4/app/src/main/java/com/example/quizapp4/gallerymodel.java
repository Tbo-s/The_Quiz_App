package com.example.quizapp4;

import android.net.Uri;

import java.io.Serializable;

public class gallerymodel implements Serializable {
    private String nameOfDog;
    private int imageResource; // Niet-0 als het een drawable betreft
    private String imageUriString; // Niet null als het een galerij-afbeelding betreft

    // Constructor voor drawable resources
    public gallerymodel(String nameOfDog, int imageResource) {
        this.nameOfDog = nameOfDog;
        this.imageResource = imageResource;
        this.imageUriString = null;
    }

    // Constructor voor afbeeldingen uit de galerij
    public gallerymodel(String nameOfDog, Uri imageUri) {
        this.nameOfDog = nameOfDog;
        this.imageResource = 0; // Geen drawable resource
        this.imageUriString = imageUri.toString();
    }

    public String getNameOfDog() {
        return nameOfDog;
    }

    public int getImageResource() {
        return imageResource;
    }

    // Retourneer de URI (of null als er geen URI is)
    public Uri getImageUri() {
        return (imageUriString != null) ? Uri.parse(imageUriString) : null;
    }
}
