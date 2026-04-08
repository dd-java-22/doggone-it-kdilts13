package edu.cnm.deepdive.doggoneit.ui;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

  private final SelectionConsumer consumer;

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

  public interface SelectionConsumer {

    void accept(int position);
  }
}
