package edu.cnm.deepdive.doggoneit.model.entity;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class ScanWithPredictions {

  @Embedded
  private Scan scan;

  @Relation(
      entity = BreedPrediction.class,
      parentColumn = "scan_id",
      entityColumn = "scan_id"
  )
  private List<BreedPrediction> predictions;

  public Scan getScan() {
    return scan;
  }

  public void setScan(Scan scan) {
    this.scan = scan;
  }

  public List<BreedPrediction> getPredictions() {
    return predictions;
  }

  public void setPredictions(List<BreedPrediction> predictions) {
    this.predictions = predictions;
  }
}
