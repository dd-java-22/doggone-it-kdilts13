/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.doggoneit.ml;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

/**
 * Minimal proof-of-concept helper for verifying on-device model + label asset loading and basic
 * TensorFlow Lite/LiteRT interpreter initialization. This intentionally does not run inference.
 */
public final class DogBreedModelPoc {

  private static final String TAG = "DogBreedModelPoc";
  private static final String MODEL_ASSET = "breed_model.tflite";
  private static final String LABELS_ASSET = "breed_names.json";

  private static final int INPUT_WIDTH = 260;
  private static final int INPUT_HEIGHT = 260;
  private static final int INPUT_CHANNELS = 3;
  private static final int FLOAT_BYTES = 4;

  private static final Type LABEL_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

  private static final Gson gson = new Gson();

  private DogBreedModelPoc() {
    // Utility class.
  }

  /**
   * Loads labels and model from assets, initializes an interpreter, and logs the input/output tensor
   * metadata and label count. Any failure is logged; exceptions are not thrown to the caller.
   *
   * @param context Android context used to access assets.
   */
  public static void initAndLog(Context context) {
    try {
      List<String> labels = loadLabels(context);
      Log.i(TAG, "Loaded labels: count=" + labels.size());

      MappedByteBuffer model = loadModel(context);
      try (Interpreter interpreter = new Interpreter(model)) {
        logTensorDetails(interpreter, labels.size());
      }
    } catch (Exception e) {
      Log.e(TAG, "Model POC init failed: " + e.getMessage(), e);
    }
  }

  /**
   * Runs one inference using a bundled drawable resource as input and logs top predictions.
   *
   * @param context Android context used to access assets/resources.
   * @param drawableResId resource ID for the input image (e.g., {@code R.drawable.dog}).
   */
  public static void runOnceAndLog(Context context, int drawableResId) {
    try {
      List<String> labels = loadLabels(context);
      if (labels.isEmpty()) {
        Log.e(TAG, "Inference POC failed: labels list is empty");
        return;
      }

      Bitmap bitmap = decodeBitmap(context, drawableResId);
      if (bitmap == null) {
        Log.e(TAG, "Inference POC failed: could not decode drawable resId=" + drawableResId);
        return;
      }

      Bitmap resized = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, true);
      ByteBuffer input = toModelInput(resized);
      float[][] output = new float[1][labels.size()];

      MappedByteBuffer model = loadModel(context);
      try (Interpreter interpreter = new Interpreter(model)) {
        logTensorDetails(interpreter, labels.size());
        interpreter.run(input, output);
      }

      logTopPredictions(labels, output[0], 5);
      Log.i(TAG, "Inference POC completed successfully.");
    } catch (Exception e) {
      Log.e(TAG, "Inference POC failed: " + e.getMessage(), e);
    }
  }

  private static List<String> loadLabels(Context context) throws IOException {
    String json = new String(
        context.getAssets().open(LABELS_ASSET).readAllBytes(),
        StandardCharsets.UTF_8
    );
    try {
      List<String> labels = gson.fromJson(json, LABEL_LIST_TYPE);
      return (labels != null) ? labels : Collections.emptyList();
    } catch (JsonParseException e) {
      throw new IOException("Failed to parse " + LABELS_ASSET, e);
    }
  }

  private static MappedByteBuffer loadModel(Context context) throws IOException {
    try (AssetFileDescriptor afd = context.getAssets().openFd(MODEL_ASSET);
        FileInputStream inputStream = new FileInputStream(afd.getFileDescriptor())) {
      FileChannel channel = inputStream.getChannel();
      long startOffset = afd.getStartOffset();
      long declaredLength = afd.getDeclaredLength();
      return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
  }

  private static Bitmap decodeBitmap(Context context, int drawableResId) {
    Bitmap decoded = BitmapFactory.decodeResource(context.getResources(), drawableResId);
    if (decoded == null) {
      return null;
    }
    if (decoded.getConfig() == Config.ARGB_8888) {
      return decoded;
    }
    return decoded.copy(Config.ARGB_8888, false);
  }

  private static ByteBuffer toModelInput(Bitmap bitmap) {
    // Preprocessing assumption (matches common Keras EfficientNet behavior):
    // Convert RGB channel values from 0..255 to 0..1 by dividing by 255.
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    if (width != INPUT_WIDTH || height != INPUT_HEIGHT) {
      throw new IllegalArgumentException(
          "Expected bitmap size " + INPUT_WIDTH + "x" + INPUT_HEIGHT + " but got " + width + "x"
              + height);
    }

    ByteBuffer input = ByteBuffer.allocateDirect(
        1 * INPUT_HEIGHT * INPUT_WIDTH * INPUT_CHANNELS * FLOAT_BYTES);
    input.order(ByteOrder.nativeOrder());

    int[] pixels = new int[INPUT_WIDTH * INPUT_HEIGHT];
    bitmap.getPixels(pixels, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT);

    int pixelIndex = 0;
    for (int y = 0; y < INPUT_HEIGHT; y++) {
      for (int x = 0; x < INPUT_WIDTH; x++) {
        int color = pixels[pixelIndex++];
        float r = Color.red(color);
        float g = Color.green(color);
        float b = Color.blue(color);
        input.putFloat(r);
        input.putFloat(g);
        input.putFloat(b);
      }
    }

    input.rewind();
    return input;
  }

  private static void logTopPredictions(List<String> labels, float[] scores, int topK) {
    int k = Math.max(1, Math.min(topK, Math.min(labels.size(), scores.length)));
    List<Prediction> predictions = new ArrayList<>(scores.length);
    for (int i = 0; i < scores.length && i < labels.size(); i++) {
      predictions.add(new Prediction(i, labels.get(i), scores[i]));
    }
    predictions.sort(Comparator.comparingDouble((Prediction p) -> p.score).reversed());

    Log.i(TAG, "Top " + k + " predictions:");
    for (int rank = 1; rank <= k; rank++) {
      Prediction p = predictions.get(rank - 1);
      Log.i(TAG, rank + ") " + p.label + " = " + p.score);
    }
  }

  private static void logTensorDetails(Interpreter interpreter, int labelCount) {
    Tensor input = interpreter.getInputTensor(0);
    Tensor output = interpreter.getOutputTensor(0);

    int[] inputShape = input.shape();
    int[] outputShape = output.shape();
    DataType inputType = input.dataType();
    DataType outputType = output.dataType();

    Log.i(TAG, "Input tensor: index=0 shape=" + shapeToString(inputShape) + " type=" + inputType);
    Log.i(TAG, "Output tensor: index=0 shape=" + shapeToString(outputShape) + " type=" + outputType);
    Log.i(TAG, "Label count: " + labelCount);
  }

  private static String shapeToString(int[] shape) {
    if (shape == null) {
      return "null";
    }
    StringBuilder builder = new StringBuilder();
    builder.append('[');
    for (int i = 0; i < shape.length; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(shape[i]);
    }
    builder.append(']');
    return builder.toString();
  }

  private static final class Prediction {

    final int index;
    final String label;
    final float score;

    private Prediction(int index, String label, float score) {
      this.index = index;
      this.label = label;
      this.score = score;
    }

  }

}
