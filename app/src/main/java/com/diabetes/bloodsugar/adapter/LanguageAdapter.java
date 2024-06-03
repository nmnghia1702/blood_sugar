package com.diabetes.bloodsugar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mnpemods.mcpecenter.model.LanguageModel;
import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.model.IClickLanguage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<LanguageModel> lists;
    private IClickLanguage iClickLanguage;

    public LanguageAdapter(Context context, List<LanguageModel> lists, IClickLanguage iClickLanguage) {
        this.context = context;
        this.lists = lists;
        this.iClickLanguage = iClickLanguage;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_language, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        try {
            LanguageModel data = lists.get(position);
            if (holder instanceof LanguageViewHolder) {
                ((LanguageViewHolder) holder).bind(data);
                ((LanguageViewHolder) holder).relayEnglish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iClickLanguage.onClick(data);
                    }
                });

                if (data.isCheck()) {
                    ((LanguageViewHolder) holder).relayEnglish.setBackgroundResource(R.drawable.border_item_language_select);
//                    ((LanguageViewHolder) holder).tvTitle.setTextColor(Color.parseColor("#FEFEFE"));
                    ((LanguageViewHolder) holder).tvTitle.setTextColor(context.getResources().getColor(R.color.color_FEFEFE));
                } else {
                    ((LanguageViewHolder) holder).relayEnglish.setBackgroundResource(R.drawable.border_item_language);
//                    ((LanguageViewHolder) holder).tvTitle.setTextColor(Color.parseColor("#111111"));
                    ((LanguageViewHolder) holder).tvTitle.setTextColor(context.getResources().getColor(R.color.color_111111));
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvTitle;
        RadioButton rbBtn;
        RelativeLayout relayEnglish;

        public LanguageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.img_avatar);
            tvTitle = itemView.findViewById(R.id.tv_title);
            rbBtn = itemView.findViewById(R.id.rb_language);
            relayEnglish = itemView.findViewById(R.id.relay_english);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bind(LanguageModel data) {
            ivAvatar.setImageDrawable(context.getDrawable(data.getImage()));
            tvTitle.setText(data.getLanguageName());
            rbBtn.setChecked(data.isCheck());
        }
    }

    public void setSelectLanguage(LanguageModel model) {
        for (LanguageModel data : lists) {
            if (data.getLanguageName().equals(model.getLanguageName())) {
                data.setCheck(true);
            } else {
                data.setCheck(false);
            }
        }
        notifyDataSetChanged();
    }
}