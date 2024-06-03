package com.diabetes.bloodsugar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.diabetes.bloodsugar.R;

import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    int[] images;
    String[] type;
    String[] content;
    LayoutInflater mLayoutInflater;

    public ViewPagerAdapter(Context context, int[] images, String[] type, String[] content) {
        this.context = context;
        this.images = images;
        this.type = type;
        this.content = content;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_slide, container, false);
        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);
        // setting the image in the imageView
        imageView.setImageResource(images[position]);
        TextView tvType = itemView.findViewById(R.id.tvType);
        Log.d("DucQv", "type: " + type[position]);
        Log.d("DucQv", "content: " + content[position]);
        tvType.setText(type[position]);
        TextView tvContent = itemView.findViewById(R.id.tvContent);
        tvContent.setText(content[position]);
        // Adding the View
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
