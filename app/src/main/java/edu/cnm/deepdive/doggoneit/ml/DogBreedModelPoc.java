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

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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

}

