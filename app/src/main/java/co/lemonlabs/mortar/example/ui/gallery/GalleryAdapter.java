package co.lemonlabs.mortar.example.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import co.lemonlabs.mortar.example.R;
import co.lemonlabs.mortar.example.data.api.model.Image;
import co.lemonlabs.mortar.example.ui.misc.BindableAdapter;

public class GalleryAdapter extends BindableAdapter<Image> {
    private final Picasso picasso;
    private List<Image> images = Collections.emptyList();

    public GalleryAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Image getItem(int position) {
        return images.get(position);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.gallery_view_image, container, false);
    }

    @Override
    public void bindView(Image item, int position, View view) {
        ((GalleryItemView) view).bindTo(item, picasso);
    }

    public void replaceWith(List<Image> images) {
        this.images = images;
        notifyDataSetChanged();
    }
}
