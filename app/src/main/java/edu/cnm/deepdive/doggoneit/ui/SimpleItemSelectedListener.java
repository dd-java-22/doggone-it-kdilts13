package edu.cnm.deepdive.doggoneit.ui;

import android.view.View;
import android.widget.AdapterView;

/**
 * Thin spinner/item-selection listener that forwards selected position to a callback.
 */
public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

  private final SelectionConsumer consumer;

  /**
   * @param consumer Callback invoked with selected position.
   */
  public SimpleItemSelectedListener(SelectionConsumer consumer) {
    this.consumer = consumer;
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    consumer.accept(position);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
  }

  /**
   * Callback contract for selection events.
   */
  public interface SelectionConsumer {

    void accept(int position);
  }
}
