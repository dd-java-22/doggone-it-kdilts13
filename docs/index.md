---
title: Overview
description: "Summary of in-progress or completed project."
order: 0
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc}

- ToC
{:toc}

## Summary

Doggone It – Dog Breed Identifier is a mobile application that allows users to identify dog breeds using a photo taken with their device camera or selected from the device gallery. The application performs on-device image classification to generate likely breed matches and presents the results in a clear, user-friendly interface.

In addition to identifying breeds, the application allows users to save scans locally, attach notes, and view historical results. For each identified breed, the application retrieves additional breed information from a public dog-breed information API, providing educational details such as temperament, origin, and other characteristics.

The application combines device integration, on-device machine learning inference, local persistence, and external API consumption while maintaining a clean, multiscreen architecture.

## Intended users and user stories

**1. Dog owner:**
  - As a curious dog owner, I want to take or upload a photo of a dog and see likely breed matches so that I can quickly satisfy my curiosity.

**2. Dog park visitor:**
  - As a dog park visitor, I want to save identified dogs with notes and timestamps so that I can remember which dogs I have met.

**3. Prospective adopter:**
  - As a prospective adopter, I want to view detailed breed information and mark favorite scans so that I can compare options later.

**4. Casual learner:**
  - As a casual learner, I want to browse and filter my scan history so that I can review previously identified breeds.

### Stretch Goal
**5. Dog lover:**
  - As a dog lover, I want to generate a stylized cartoon version of a saved photo so that I can create a fun keepsake image.

## Functionality

### Core functionality

- Capture a photo using the device camera

- Select an existing photo from the device gallery

- Perform on-device breed classification using a TensorFlow Lite model

- Display top predicted breeds with confidence values

- Save scan results to a local SQLite database

- Attach optional notes to saved scans

- View scan history in a searchable and sortable list

- Mark scans as favorites

- Retrieve breed details from an external dog-breed API

- Cache retrieved breed information locally for offline viewing

- Manage user preferences such as sorting behavior and display options

### Stretch goals

- Generating a locally processed “cartoon-style” portrait when saving a scan

- Expanding classification support to include cats

## Persistent data

The following information will be stored in persistent device storage so that it remains available across multiple sessions of the application. The app does not rely on a remote server for data persistence; instead, it uses local Android storage (Room database and DataStore preferences).

- **Scan History**
    - Images that the user has previously scanned.
    - The predicted dog breeds and associated confidence scores for each scan.
    - Timestamp of when each scan was performed.

- **Breed Information Cache**
    - Basic information about dog breeds returned from the prediction system (such as breed name and description).
    - Stored locally to reduce repeated processing and improve app responsiveness.


- **User Preferences**
    - App settings - display preferences, default sort, and number of columns to display in galleries options.
    - Stored using Android DataStore so preferences persist between sessions.


- **Model Metadata**
    - Information about the TensorFlow Lite model version currently installed on the device.
    - Allows the app to detect when the model needs to be updated.

## Device/external services

| Service / Source | Documentation | How the App Uses It | Can the App Function Without It? |
|---|---|---|---|
| TheDogAPI (Breed Information API) | [TheDogAPI Documentation](https://docs.thedogapi.com/) | Retrieves detailed information about dog breeds such as temperament, origin, breed group, and other descriptive attributes to display alongside prediction results. | Yes. If the API is unavailable, the app can still perform breed predictions using the local TensorFlow Lite model. Previously retrieved breed information cached in the local database will remain available offline. |
| Dog CEO Dog API (Image / Breed List API) | [Dog CEO API Documentation](https://dog.ceo/dog-api/documentation/) | Provides breed lists and dog images that can be used for browsing breeds or supplementing UI elements such as breed galleries. | Yes. The core functionality of the app (breed prediction) does not depend on this API, and cached or locally stored data can still be displayed offline. |
| TensorFlow Lite (On-Device ML) | [TensorFlow Lite Android Guide](https://www.tensorflow.org/lite/android) | Runs the dog breed classification model locally on the device. The app converts captured or uploaded images into a format suitable for the TensorFlow Lite model and receives predicted breed probabilities. | No. Without TensorFlow Lite the application cannot perform breed prediction. |
| Camera (CameraX) | [CameraX Developer Guide](https://developer.android.com/training/camerax) | Used to capture photos of dogs directly inside the app so the image can be analyzed for breed prediction. | The app cannot capture new dog photos without camera access, but existing photos can still be analyzed, and previously saved scans can still be viewed. |
| Media Gallery Access | [Android Shared Media Storage](https://developer.android.com/training/data-storage/shared/media) | Allows users to select existing photos of dogs from their device gallery instead of taking a new picture. | The app can still function using the camera, but users will not be able to analyze existing photos. |
| Local Database (SQLite / Room) | [Room Persistence Library](https://developer.android.com/training/data-storage/room) | Stores scan history, saved predictions, and possibly cached breed information so users can review past scans. | The app could still perform breed predictions, but scan history and stored results would not persist between sessions. |
| Preferences Storage (DataStore) | [Android DataStore Guide](https://developer.android.com/topic/libraries/architecture/datastore) | Stores small configuration settings such as user preferences, onboarding flags, or display settings. | The app would still function but user settings would not persist between sessions. |

### External API Usage Notes

Both TheDogAPI and Dog CEO Dog API provide free access suitable for a student project within their rate limits. TheDogAPI requires an API key, while the Dog CEO API is openly accessible.

Breed information retrieved from these APIs will be cached locally in the application's Room/SQLite database. Caching reduces repeated network requests and allows previously viewed breed information to remain available when the device is offline.

The application does not require user accounts or authentication. Core functionality such as capturing images and performing breed prediction with the on-device TensorFlow Lite model will continue to function without an internet connection, though new breed information may not be retrieved until connectivity is restored.

## Stretch goals and possible enhancements 

- Add a feature to create a cartoon style portrait of saved dog photos

- Expand classification support to include cats