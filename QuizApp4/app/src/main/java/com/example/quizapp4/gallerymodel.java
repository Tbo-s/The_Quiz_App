package com.example.quizapp4;

import android.net.Uri;

public class gallerymodel {
    private String nameOfDog;
    private int imageResource; // Voor vooraf gedefinieerde drawable resources
    private Uri imageUri;      // Voor afbeeldingen die de gebruiker toevoegt

    // Constructor voor vooraf gedefinieerde afbeeldingen (drawable resources)
    public gallerymodel(String nameOfDog, int imageResource) {
        this.nameOfDog = nameOfDog;
        this.imageResource = imageResource;
        this.imageUri = null;
    }

    // Constructor voor afbeeldingen uit de galerij (via URI)
    public gallerymodel(String nameOfDog, Uri imageUri) {
        this.nameOfDog = nameOfDog;
        this.imageUri = imageUri;
        this.imageResource = 0; // Of een andere standaardwaarde
    }

    public String getNameOfDog() {
        return nameOfDog;
    }

    // Retourneert de drawable resource ID (gebruik dit wanneer imageUri == null)
    public int getImageResource() {
        return imageResource;
    }

    // Retourneert de URI (gebruik dit wanneer niet-null)
    public Uri getImageUri() {
        return imageUri;
    }
}
