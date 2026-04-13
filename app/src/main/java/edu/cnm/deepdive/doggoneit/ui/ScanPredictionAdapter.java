package edu.cnm.deepdive.doggoneit.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.doggoneit.databinding.ItemScanPredictionBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying and selecting top breed predictions.
 */
public class ScanPredictionAdapter
    extends RecyclerView.Adapter<ScanPredictionAdapter.ViewHolder> {

  private final OnPredictionSelectedListener listener;
  private final List<ScanPredictionItem> items = new ArrayList<>();
  private int selectedPosition = RecyclerView.NO_POSITION;

  public ScanPredictionAdapter(OnPredictionSelectedListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    ItemScanPredictionBinding binding = ItemScanPredictionBinding.inflate(inflater, parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ScanPredictionItem item = items.get(position);
    boolean selected = position == selectedPosition;
    holder.bind(item, selected);
    holder.binding.getRoot().setOnClickListener(v -> select(position));
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  /**
   * Replaces current prediction items and applies default selection.
   *
   * @param newItems New prediction list.
   * @param defaultSelection Position selected after replacement.
   */
  public void submitItems(List<ScanPredictionItem> newItems, int defaultSelection) {
    items.clear();
    if (newItems != null) {
      items.addAll(newItems);
    }
    selectedPosition = (items.isEmpty())
        ? RecyclerView.NO_POSITION
        : Math.max(0, Math.min(defaultSelection, items.size() - 1));
    notifyDataSetChanged();
    if (listener != null && selectedPosition != RecyclerView.NO_POSITION) {
      listener.onPredictionSelected(items.get(selectedPosition));
    }
  }

  /**
   * @return Currently selected prediction item, or {@code null}.
   */
  public ScanPredictionItem getSelectedItem() {
    if (selectedPosition == RecyclerView.NO_POSITION || selectedPosition >= items.size()) {
      return null;
    }
    return items.get(selectedPosition);
  }

  /**
   * @return Read-only view of displayed prediction items.
   */
  public List<ScanPredictionItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  private void select(int position) {
    if (position < 0 || position >= items.size() || position == selectedPosition) {
      return;
    }
    int previous = selectedPosition;
    selectedPosition = position;
    if (previous != RecyclerView.NO_POSITION) {
      notifyItemChanged(previous);
    }
    notifyItemChanged(selectedPosition);
    if (listener != null) {
      listener.onPredictionSelected(items.get(position));
    }
  }

  /**
   * Callback contract for prediction selection.
   */
  public interface OnPredictionSelectedListener {

    void onPredictionSelected(ScanPredictionItem item);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private final ItemScanPredictionBinding binding;

    ViewHolder(ItemScanPredictionBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    void bind(ScanPredictionItem item, boolean selected) {
      binding.predictionName.setText(item.displayName());
      binding.predictionConfidence.setText(
          String.format(Locale.US, "%d%%", item.confidencePercent())
      );
      int selectedStrokeColor = ContextCompat.getColor(binding.getRoot().getContext(),
          android.R.color.holo_blue_dark);
      int normalStrokeColor = ContextCompat.getColor(binding.getRoot().getContext(),
          android.R.color.darker_gray);
      int selectedCardColor = ContextCompat.getColor(binding.getRoot().getContext(),
          android.R.color.holo_blue_light);
      int normalCardColor = ContextCompat.getColor(binding.getRoot().getContext(),
          android.R.color.white);
      binding.getRoot().setStrokeWidth(selected ? 4 : 1);
      binding.getRoot().setStrokeColor(selected ? selectedStrokeColor : normalStrokeColor);
      binding.getRoot().setCardBackgroundColor(selected ? selectedCardColor : normalCardColor);
    }
  }

  public record ScanPredictionItem(String rawLabel, String displayName, float score,
                                   int confidencePercent) {
  }
}
