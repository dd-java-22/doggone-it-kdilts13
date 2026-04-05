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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

public final class DogBreedInference {

  private static final String MODEL_ASSET = "breed_model.tflite";
  private static final String LABELS_ASSET = "breed_names.json";
  private static final int INPUT_CHANNELS = 3;
  private static final int FLOAT_BYTES = 4;
  private static final int TOP_K = 5;

  private static final Type LABEL_LIST_TYPE = new TypeToken<List<String>>() {}.getType();
  private static final Gson gson = new Gson();

  private DogBreedInference() {
    // Utility class.
  }

  public static DogBreedInferenceResult run(Context context, Uri sourceUri) throws IOException {
    if (context == null) {
      throw new IllegalArgumentException("Context must not be null.");
    }
    if (sourceUri == null) {
      throw new IllegalArgumentException("Source Uri must not be null.");
    }

    List<String> labels = loadLabels(context);
    if (labels.isEmpty()) {
      throw new IOException("Labels list is empty from asset " + LABELS_ASSET);
    }

    Bitmap bitmap = decodeBitmap(context, sourceUri);
    if (bitmap == null) {
      throw new IOException("Unable to decode image from Uri: " + sourceUri);
    }

    MappedByteBuffer model = loadModel(context);
    try (Interpreter interpreter = new Interpreter(model)) {
      Tensor inputTensor = interpreter.getInputTensor(0);
      int[] inputShape = inputTensor.shape();
      if (inputShape.length != 4) {
        throw new IllegalStateException("Unexpected input tensor shape length: "
            + shapeToString(inputShape));
      }
      int inputHeight = inputShape[1];
      int inputWidth = inputShape[2];
      int inputChannels = inputShape[3];
      if (inputChannels != INPUT_CHANNELS) {
        throw new IllegalStateException("Expected " + INPUT_CHANNELS
            + " input channels but got " + inputChannels);
      }
      if (inputTensor.dataType() != DataType.FLOAT32) {
        throw new IllegalStateException("Expected FLOAT32 input tensor but got "
            + inputTensor.dataType());
      }

      Bitmap resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true);
      ByteBuffer input = toModelInput(resized, inputWidth, inputHeight, inputChannels);

      Tensor outputTensor = interpreter.getOutputTensor(0);
      int[] outputShape = outputTensor.shape();
      if (outputShape.length != 2 || outputShape[0] != 1) {
        throw new IllegalStateException("Unexpected output tensor shape: "
            + shapeToString(outputShape));
      }
      if (outputTensor.dataType() != DataType.FLOAT32) {
        throw new IllegalStateException("Expected FLOAT32 output tensor but got "
            + outputTensor.dataType());
      }
      int outputCount = outputShape[1];
      if (outputCount != labels.size()) {
        throw new IllegalStateException("Output size " + outputCount
            + " does not match label count " + labels.size());
      }

      float[][] output = new float[1][outputCount];
      interpreter.run(input, output);

      List<DogBreedPrediction> topPredictions = toTopPredictions(labels, output[0], TOP_K);
      return new DogBreedInferenceResult(
          sourceUri.toString(),
          inputWidth,
          inputHeight,
          topPredictions
      );
    } catch (RuntimeException e) {
      throw new IOException("Inference failed for Uri: " + sourceUri, e);
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

  private static Bitmap decodeBitmap(Context context, Uri sourceUri) throws IOException {
    try (InputStream input = context.getContentResolver().openInputStream(sourceUri)) {
      if (input == null) {
        throw new IOException("Content resolver returned null stream for Uri: " + sourceUri);
      }
      Bitmap decoded = BitmapFactory.decodeStream(input);
      if (decoded == null) {
        return null;
      }
      if (decoded.getConfig() == Config.ARGB_8888) {
        return decoded;
      }
      return decoded.copy(Config.ARGB_8888, false);
    }
  }

  private static ByteBuffer toModelInput(Bitmap bitmap, int width, int height, int channels) {
    if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
      throw new IllegalArgumentException("Expected bitmap size " + width + "x" + height
          + " but got " + bitmap.getWidth() + "x" + bitmap.getHeight());
    }
    ByteBuffer input = ByteBuffer.allocateDirect(1 * height * width * channels * FLOAT_BYTES);
    input.order(ByteOrder.nativeOrder());

    int[] pixels = new int[width * height];
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

    int pixelIndex = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
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

  private static List<DogBreedPrediction> toTopPredictions(
      List<String> labels,
      float[] scores,
      int topK
  ) {
    int k = Math.max(1, Math.min(topK, Math.min(labels.size(), scores.length)));
    List<DogBreedPrediction> predictions = new ArrayList<>(scores.length);
    for (int i = 0; i < scores.length && i < labels.size(); i++) {
      predictions.add(new DogBreedPrediction(labels.get(i), scores[i]));
    }
    predictions.sort(Comparator.comparingDouble(DogBreedPrediction::score).reversed());
    return Collections.unmodifiableList(predictions.subList(0, k));
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
