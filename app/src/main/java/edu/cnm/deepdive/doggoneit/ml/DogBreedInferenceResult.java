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

import java.util.Collections;
import java.util.List;

/**
 * Immutable value object containing top dog-breed predictions for one analyzed image.
 */
public final class DogBreedInferenceResult {

  private final String sourceUri;
  private final int inputWidth;
  private final int inputHeight;
  private final List<DogBreedPrediction> topPredictions;

  /**
   * Creates a result bundle for one inference run.
   *
   * @param sourceUri Image URI string used as inference input.
   * @param inputWidth Model input width in pixels.
   * @param inputHeight Model input height in pixels.
   * @param topPredictions Top-ranked predictions from the model.
   */
  public DogBreedInferenceResult(String sourceUri, int inputWidth, int inputHeight,
      List<DogBreedPrediction> topPredictions) {
    this.sourceUri = sourceUri;
    this.inputWidth = inputWidth;
    this.inputHeight = inputHeight;
    this.topPredictions = (topPredictions == null)
        ? Collections.emptyList()
        : Collections.unmodifiableList(topPredictions);
  }

  /**
   * @return Source image URI string.
   */
  public String sourceUri() {
    return sourceUri;
  }

  /**
   * @return Model input width used during inference.
   */
  public int inputWidth() {
    return inputWidth;
  }

  /**
   * @return Model input height used during inference.
   */
  public int inputHeight() {
    return inputHeight;
  }

  /**
   * @return Unmodifiable top prediction list.
   */
  public List<DogBreedPrediction> topPredictions() {
    return topPredictions;
  }

}
