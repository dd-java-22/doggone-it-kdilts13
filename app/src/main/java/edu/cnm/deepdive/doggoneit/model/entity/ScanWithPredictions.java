package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

/**
 * Room relation projection combining a saved scan with its prediction rows.
 */
public class ScanWithPredictions {

  @Embedded
  private Scan scan;

  @Relation(
      entity = BreedPrediction.class,
      parentColumn = "scan_id",
      entityColumn = "scan_id"
  )
  private List<BreedPrediction> predictions;

  /**
   * @return Parent scan entity.
   */
  public Scan getScan() {
    return scan;
  }

  /**
   * @param scan Parent scan entity.
   */
  public void setScan(Scan scan) {
    this.scan = scan;
  }

  /**
   * @return Predictions associated with the scan.
   */
  public List<BreedPrediction> getPredictions() {
    return predictions;
  }

  /**
   * @param predictions Prediction rows for the scan.
   */
  public void setPredictions(List<BreedPrediction> predictions) {
    this.predictions = predictions;
  }
}
