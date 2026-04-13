package edu.cnm.deepdive.doggoneit.ui;

import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.ItemSavedScanBinding;
import edu.cnm.deepdive.doggoneit.viewmodel.ScanGalleryItem;
import java.util.Objects;

/**
 * RecyclerView adapter for the saved-scans gallery grid.
 */
public class SavedScanGridAdapter
    extends ListAdapter<ScanGalleryItem, SavedScanGridAdapter.ViewHolder> {

  private static final int DEFAULT_GRID_COLUMN_COUNT = 3;
  private static final int GRID_SIDE_PADDING_DP = 32;
  private static final int TILE_MARGIN_DP = 12;

  private final ScanClickListener scanClickListener;
  private int gridColumnCount = DEFAULT_GRID_COLUMN_COUNT;

  public SavedScanGridAdapter(ScanClickListener scanClickListener) {
    super(new DiffCallback());
    this.scanClickListener = scanClickListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    ItemSavedScanBinding binding = ItemSavedScanBinding.inflate(inflater, parent, false);
    return new ViewHolder(binding, scanClickListener);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(getItem(position), gridColumnCount);
  }

  /**
   * Sets displayed grid column count and refreshes cell sizing.
   *
   * @param columnCount Requested column count; values outside 2-4 are normalized.
   */
  public void setGridColumnCount(int columnCount) {
    int normalizedCount = (columnCount >= 2 && columnCount <= 4)
        ? columnCount : DEFAULT_GRID_COLUMN_COUNT;
    if (gridColumnCount != normalizedCount) {
      gridColumnCount = normalizedCount;
      notifyDataSetChanged();
    }
  }

  /**
   * Callback contract for gallery tile selection.
   */
  public interface ScanClickListener {

    void onScanClick(long scanId);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private final ItemSavedScanBinding binding;
    private final ScanClickListener scanClickListener;

    ViewHolder(ItemSavedScanBinding binding, ScanClickListener scanClickListener) {
      super(binding.getRoot());
      this.binding = binding;
      this.scanClickListener = scanClickListener;
    }

    void bind(ScanGalleryItem item, int gridColumnCount) {
      long scanId = (item != null) ? item.getScanId() : 0;
      binding.getRoot().setOnClickListener(v -> scanClickListener.onScanClick(scanId));
      Glide.with(binding.savedScanImage).clear(binding.savedScanImage);
      if (item == null || item.getImagePath().isBlank()) {
        showPlaceholder();
        return;
      }
      Glide.with(binding.savedScanImage)
          .load(Uri.parse(item.getImagePath()))
          .placeholder(R.drawable.dog)
          .error(R.drawable.dog)
          .centerCrop()
          .override(getThumbnailSizePx(gridColumnCount), getThumbnailSizePx(gridColumnCount))
          .thumbnail(0.25f)
          .into(binding.savedScanImage);
    }

    private void showPlaceholder() {
      binding.savedScanImage.setImageResource(R.drawable.dog);
    }

    private int getThumbnailSizePx(int gridColumnCount) {
      int normalizedCount = (gridColumnCount >= 2 && gridColumnCount <= 4)
          ? gridColumnCount : DEFAULT_GRID_COLUMN_COUNT;
      float density = binding.savedScanImage.getResources().getDisplayMetrics().density;
      int screenWidthPx = binding.savedScanImage.getResources().getDisplayMetrics().widthPixels;
      int horizontalSpacingPx = Math.round(
          (GRID_SIDE_PADDING_DP + (normalizedCount * TILE_MARGIN_DP)) * density
      );
      int availableWidthPx = Math.max(screenWidthPx - horizontalSpacingPx, 0);
      return Math.max(availableWidthPx / normalizedCount,
          Math.round(TypedValue.applyDimension(
              TypedValue.COMPLEX_UNIT_DIP,
              120,
              binding.savedScanImage.getResources().getDisplayMetrics()
          )));
    }
  }

  static class DiffCallback extends DiffUtil.ItemCallback<ScanGalleryItem> {

    @Override
    public boolean areItemsTheSame(@NonNull ScanGalleryItem oldItem,
        @NonNull ScanGalleryItem newItem) {
      return oldItem.getScanId() == newItem.getScanId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull ScanGalleryItem oldItem,
        @NonNull ScanGalleryItem newItem) {
      return oldItem.getScanId() == newItem.getScanId()
          && Objects.equals(oldItem.getImagePath(), newItem.getImagePath())
          && Objects.equals(oldItem.getTimestamp(), newItem.getTimestamp())
          && Objects.equals(oldItem.getTopBreedLabel(), newItem.getTopBreedLabel())
          && Objects.equals(oldItem.getTopBreedConfidence(), newItem.getTopBreedConfidence());
    }
  }
}
