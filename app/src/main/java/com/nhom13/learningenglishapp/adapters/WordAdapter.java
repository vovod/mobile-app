package com.nhom13.learningenglishapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.models.Vocabulary;
import com.nhom13.learningenglishapp.utils.FileUtils;

import java.io.File;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Vocabulary> vocabularyList;
    private Context context;
    private OnWordListener onWordListener;

    public interface OnWordListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public WordAdapter(Context context, List<Vocabulary> vocabularyList, OnWordListener onWordListener) {
        this.context = context;
        this.vocabularyList = vocabularyList;
        this.onWordListener = onWordListener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.tvWord.setText(vocabulary.getWord());

        if (vocabulary.getChapter() != null) {
            holder.tvChapter.setText(vocabulary.getChapter().getName());
        } else {
            holder.tvChapter.setText("Chương " + vocabulary.getChapterId());
        }

        // Hiển thị hình ảnh nếu có
        if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            File imgFile = new File(vocabulary.getImagePath());
            if (imgFile.exists()) {
                holder.imgWord.setImageURI(Uri.fromFile(imgFile));
            } else {
                holder.imgWord.setImageResource(R.drawable.abc);
            }
        } else {
            holder.imgWord.setImageResource(R.drawable.abc);
        }

        // Thiết lập sự kiện click cho các nút
        holder.btnEditWord.setOnClickListener(v -> {
            if (onWordListener != null) {
                onWordListener.onEditClick(position);
            }
        });

        holder.btnDeleteWord.setOnClickListener(v -> {
            if (onWordListener != null) {
                onWordListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vocabularyList != null ? vocabularyList.size() : 0;
    }

    public void updateData(List<Vocabulary> newVocabularyList) {
        this.vocabularyList = newVocabularyList;
        notifyDataSetChanged();
    }

    public Vocabulary getItem(int position) {
        return vocabularyList.get(position);
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        ImageView imgWord;
        TextView tvWord, tvChapter;
        ImageButton btnEditWord, btnDeleteWord;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            imgWord = itemView.findViewById(R.id.imgWord);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvChapter = itemView.findViewById(R.id.tvChapter);
            btnEditWord = itemView.findViewById(R.id.btnEditWord);
            btnDeleteWord = itemView.findViewById(R.id.btnDeleteWord);
        }
    }
}
