---
title: Build Instructions
description: "Build Instructions"
order: 30
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc}

- ToC
{:toc}

# Build Instructions

## Overview

These instructions explain how to build, run, and test the submitted version of **Doggone It**. They also include the required setup for external content that is **not** committed to the repository, specifically the Dog API key used for breed facts.

## Prerequisites

To build and run this project, you should have:

- **IntelliJ IDEA (Ultimate Edition recommended for Android support)**
- A recent **Android SDK** installation configured in IntelliJ
- **JDK 21** or the version required by the Gradle/IntelliJ project configuration
- An **Android emulator** or a physical Android device
- Internet access for Dog API requests used by the breed facts feature

## Android version requirements

- **Minimum SDK:** 33
- **Target SDK:** 36

## Clone the repository

```bash
git clone https://github.com/dd-java-22/doggone-it-kdilts13
cd doggone-it-kdilts13
```

## Required external content: Dog API key

This project requires a Dog API key for the breed facts feature. The key is **not** stored in the repository and must be added locally before building/running the app.

### Step 1: Create the file

Create this file if it does not already exist:

```text
app/local.properties
```

### Step 2: Add the property

Add the following line to `app/local.properties`:

```properties
dogApiKey=YOUR_DOG_API_KEY_HERE
```

Replace `YOUR_DOG_API_KEY_HERE` with a valid API key.
You can get an API key for free at [The Dog API Website](https://thedogapi.com/signup).

### Important note

- Do **not** commit `app/local.properties` or your real API key to Git.
- If the Dog API key is missing, the app may still build, but breed facts requests will not work correctly.

## Open the project in IntelliJ

1. Launch IntelliJ IDEA.
2. Choose **Open**.
3. Select the root project folder (`doggone-it-kdilts13`).
4. Allow Gradle sync to complete.

If prompted to configure the Android SDK, follow IntelliJ’s setup prompts.

## Build the project in IntelliJ

After Gradle sync completes:

1. Select **Build > Build Project**  
   or
2. Use the build button in the toolbar.

This should compile the app and confirm that the project is configured correctly.

## Run the app in IntelliJ

1. Start an Android emulator, or connect a physical Android device with developer mode enabled.
2. In IntelliJ, select the app run configuration.
3. Click **Run**.

The app should install and launch on the selected device.

## Command-line build (optional)

From the project root, you can also build with Gradle:

### Windows

```bash
gradlew assembleDebug
```

### macOS / Linux

```bash
./gradlew assembleDebug
```

## Permissions and device behavior

This app may use Android device features and system flows related to:

- Camera capture
- Gallery/photo picker access
- Local image storage
- Network access for Dog API breed facts

Depending on device version and system behavior, permission prompts may appear when testing camera or media-related features.

## Recommended test flow after installation

After launching the app, verify the following:

1. Log in to the app.
2. Open the **Home** screen.
3. Use **Launch Camera** or **Analyze from Gallery**.
4. Confirm that the analysis screen appears and displays prediction results.
5. Select a prediction and save the scan.
6. Open the saved scan and confirm:
   - breed facts are shown
   - notes can be edited/saved
   - favorite status can be toggled
7. Open the saved scans gallery and confirm:
   - scans appear
   - sorting works
   - favorites filtering works
   - column count reflects saved settings
8. Open **Settings** and confirm preference changes persist.

## Javadoc generation

Generated Javadocs are located under:

```text
docs/api
```

If regeneration is needed, run the project Javadoc task from the project root:

### Windows

```bash
gradlew app:javadoc
```

### macOS / Linux

```bash
./gradlew app:javadoc
```

## Troubleshooting

### Gradle sync fails

- Confirm IntelliJ is using the expected JDK version.
- Confirm required Android SDK packages are installed.
- Re-sync the project after installing missing components.

### Breed facts do not load

- Confirm `app/local.properties` exists.
- Confirm the file contains a valid `dogApiKey`.
- Confirm the device/emulator has internet access.

### Camera or gallery flow does not work

- Confirm the device supports the required camera/media flows.
- Check and allow any runtime permissions if prompted.
- Re-test on both emulator and physical device if needed.

## Notes for graders/testers

- The Dog API key must be supplied locally in `app/local.properties`.
- The breed facts feature depends on network connectivity and a valid API key.
- The app’s persistent local data is stored on-device using Room/SQLite.
