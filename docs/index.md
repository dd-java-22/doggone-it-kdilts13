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

As a curious dog owner, I want to take or upload a photo of a dog and see likely breed matches so that I can quickly satisfy my curiosity.

As a dog park visitor, I want to save identified dogs with notes and timestamps so that I can remember which dogs I have met.

As a prospective adopter, I want to view detailed breed information and mark favorite breeds so that I can compare options later.

As a casual learner, I want to browse and filter my scan history so that I can review previously identified breeds.

Stretch Goal:
As a dog lover, I want to generate a stylized cartoon version of a saved photo so that I can create a fun keepsake image.

## Functionality

### Core functionality includes:

- Capture a photo using the device camera

- Select an existing photo from the device gallery

- Perform on-device breed classification using a TensorFlow Lite model

- Display top predicted breeds with confidence values

- Save scan results to a local SQLite database

- Attach optional notes to saved scans

- View scan history in a searchable and sortable list

- Mark scans or breeds as favorites

- Retrieve breed details from an external dog-breed API

- Cache retrieved breed information locally for offline viewing

- Manage user preferences such as sorting behavior and display options

### Stretch goals include:

- Generating a locally processed “cartoon-style” portrait when saving a scan

- Expanding classification support to include cats

## Persistent data

[//]: # (TODO Using a bullet list, list what content will be maintained in server-side storage. This should include any information that users of your app would expect to be maintained &#40;i.e., without connection to a server&#41; across multiple sessions of use.)

## Device/external services

### Device Services
- Camera (photo capture)

- Device gallery access (photo selection)

- Local storage for image references

- SQLite database for persistent data storage

- SharedPreferences or DataStore for user settings

### External Services
- **TheDogAPI (primary source for breed facts)**: Used to retrieve dog breed details (e.g., temperament, origin, breed group, and other descriptive attributes). TheDogAPI describes itself as a free service; it requires an API key, and the free plan is rate-limited (e.g., 10 requests per minute).


- **Dog CEO Dog API (optional / supplemental)**: Used for breed lists and/or breed images if needed for browsing or UI presentation. Dog CEO’s API is openly accessible and provides endpoints for random dog images and breed-based image retrieval.

Both APIs provide free access appropriate for a student project within their rate limits. Retrieved breed information will be cached locally in the application’s SQLite database to minimize repeated network requests and ensure that previously viewed breed details remain available offline.

The application will not require user accounts and will function offline for previously saved scans and cached breed data.

## Stretch goals and possible enhancements 

[//]: # (TODO If you can identify functional elements of the software that you think might not be achievable in the scope of the project, but which would nonetheless add significant value if you were able to include them, list them here. For now, we recommend listing them in order of complexity/amount of work, from the least to the most.)
