package edu.cnm.deepdive.doggoneit.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.ItemSavedScanBinding;
import edu.cnm.deepdive.doggoneit.viewmodel.ScanGalleryItem;
import java.util.Objects;

public class SavedScanGridAdapter
    extends ListAdapter<ScanGalleryItem, SavedScanGridAdapter.ViewHolder> {

  private final ScanClickListener scanClickListener;

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
    holder.bind(getItem(position));
  }

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

    void bind(ScanGalleryItem item) {
      long scanId = (item != null) ? item.getScanId() : 0;
      binding.getRoot().setOnClickListener(v -> scanClickListener.onScanClick(scanId));
      binding.savedScanImage.setImageDrawable(null);
      if (item == null || item.getImagePath().isBlank()) {
        showPlaceholder();
        return;
      }
      try {
        binding.savedScanImage.setImageURI(Uri.parse(item.getImagePath()));
      } catch (RuntimeException e) {
        showPlaceholder();
        return;
      }
      if (binding.savedScanImage.getDrawable() == null) {
        showPlaceholder();
      }
    }

    private void showPlaceholder() {
      binding.savedScanImage.setImageResource(R.drawable.dog);
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
          && Objects.equals(oldItem.getTopBreedLabel(), newItem.getTopBreedLabel());
    }
  }
}
