---

title: Licenses & Attributions
description: "Third-party libraries, services, and assets used in the project"
order: 70
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc}

- ToC
{:toc}

# Licenses & Attributions

This document lists all third-party libraries, services, and assets used in **Doggone It**, along with their sources and licensing information.

---

## Open Source Libraries

The application relies on several widely used open-source Android libraries:

### AndroidX Libraries

* Source: https://developer.android.com/jetpack/androidx
* License: Apache License 2.0

### Dagger / Hilt

* Source: https://dagger.dev/
* License: Apache License 2.0

### Room (Android Jetpack)

* Source: https://developer.android.com/jetpack/androidx/releases/room
* License: Apache License 2.0

### Retrofit / OkHttp

* Source: https://square.github.io/retrofit/
* License: Apache License 2.0

### Glide

* Source: https://github.com/bumptech/glide
* License: BSD-style / Apache License 2.0

### TensorFlow Lite (LiteRT)

* Source: https://www.tensorflow.org/lite
* License: Apache License 2.0

---

## External Services

### The Dog API

* Source: https://thedogapi.com/
* License: Terms of Service (not open-source)

**Usage in this project:**

* Breed data is retrieved dynamically via API requests
* A limited mapping dataset (breed name to API ID) is derived from API data for internal use

---

## Machine Learning Model

### Dog Breed Classification Model

* Source:

    * https://huggingface.co/spaces/nithin521/Dog-Breed-Classification
    * https://huggingface.co/spaces/nithin521/Dog-Breed-Classification/blob/656e6f973e33b38070727c857ac625312d14df8c/breed_model.tflite
* License: Not explicitly specified by the source

**Usage in this project:**

* The model is included as a `.tflite` file for on-device inference
* Used to generate predicted dog breeds from input images
* Included strictly for educational purposes

### Model Label Data (`breed_names.json`)

* Source: Included with the model
* License: Not explicitly specified

**Usage:**

* Provides mapping between model output indices and breed names
* Used during prediction result processing

---

## Third-Party Media

### Unsplash Image

* Source: https://unsplash.com/photos/a-dog-sitting-in-the-grass-with-its-tongue-out-rFao8fcvTdY
* License: Unsplash License

**Usage:**

* Used for wireframe and design purposes only
* Not used in model training or production features

---

## Summary

All third-party components used in this project are either:

* Open-source libraries under permissive licenses (primarily Apache 2.0), or
* External services used under their Terms of Service, or
* Third-party assets used for educational purposes with appropriate attribution.

This project is developed as part of a course assignment and is not intended for commercial distribution.
