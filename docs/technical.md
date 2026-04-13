---

title: Technical Requirements & Dependencies
description: "Technical requirements, dependencies, and environment setup"
order: 60
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc}

- ToC
{:toc}

# Technical Requirements & Dependencies

## Overview

This document lists the technical requirements, dependencies, and configuration needed to build and run the **Doggone It** application.

---

## Development Environment

The project was developed and tested using the following environment:

* **IDE:** IntelliJ IDEA (Ultimate Edition recommended for Android support)
* **Build System:** Gradle (via Android Gradle Plugin)
* **Language(s):**

    * Java (primary)
    * Kotlin (partial usage in supporting components)

---

## Platform Requirements

* **Minimum SDK:** 33
* **Target SDK:** 36
* **Android OS:** Android 13 (API 33) or higher recommended

---

## Required Software

To build and run the project, the following must be installed:

* **Java Development Kit (JDK):** Version 21 (or compatible with project configuration)
* **Android SDK:** Installed and configured through IntelliJ
* **Gradle:** Included via Gradle Wrapper (`gradlew`)

---

## Device Requirements

The application can be run on:

* Android Emulator (recommended for testing)
* Physical Android device with developer mode enabled

The device must support:

* Camera functionality (for image capture)
* Media access (for gallery image selection)
* Internet connectivity (for API requests)

---

## External Services

### The Dog API

* **URL:** https://thedogapi.com/
* **Purpose:** Provides breed information and metadata
* **Authentication:** API key required

**Usage in this project:**

* Fetches breed facts for selected predictions
* Supports mapping between model output and API data

---

## Local Configuration

### Dog API Key

A local configuration file is required:

```text
app/local.properties
```

This file must contain:

```properties
dogApiKey=YOUR_API_KEY
```

This file is not committed to version control and must be created manually.

---

## Data Storage

The application uses:

### Room (SQLite)

* Stores:

    * Saved scans
    * Predictions
    * User notes
    * Favorite flags
* Provides persistent local storage on the device

---

## Major Libraries and Dependencies

The following key libraries are used in the project:

### AndroidX / Jetpack

* UI components and lifecycle management
* Navigation support
* RecyclerView for dynamic UI

### Dagger / Hilt

* Dependency injection framework
* Manages application components and services

### Room

* SQLite abstraction layer
* Handles local database operations

### Retrofit / OkHttp

* HTTP client for API communication
* Used to access Dog API endpoints

### Glide

* Image loading and caching library
* Displays images efficiently in the UI

### TensorFlow Lite (LiteRT)

* On-device machine learning inference
* Used to classify dog breeds from images

---

## Machine Learning Model

* Format: `.tflite`
* Location: bundled within application assets
* Purpose: classify dog breeds from images
* Input: image data
* Output: ranked list of breed predictions

A corresponding label file (`breed_names.json`) is used to map output indices to breed names.

---

## Permissions

The application may request the following permissions:

* **Camera** – to capture new images
* **Media / Storage Access** – to select images from the gallery

Permissions are requested at runtime as needed.

---

## Networking Requirements

* Internet access is required for:

    * Fetching breed data from The Dog API
* The application will still function offline for:

    * Image analysis (ML inference)
    * Viewing previously saved scans

---

## Javadoc Documentation

Generated API documentation is included in:

```text
docs/api
```

These docs are generated using the Gradle `javadoc` task and include documentation for the primary Java source files.

---

## Summary

The project uses a standard modern Android stack with:

* Local persistence via Room
* Dependency injection via Hilt
* Network communication via Retrofit
* Image handling via Glide
* On-device ML via TensorFlow Lite

All required tools and dependencies are either included in the project or documented above for setup.
