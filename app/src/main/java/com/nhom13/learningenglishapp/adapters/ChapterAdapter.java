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
import com.nhom13.learningenglishapp.database.models.Chapter;

import java.io.File;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private List<Chapter> chapterList;
    private OnChapterListener listener;

    public interface OnChapterListener {
        void onChapterClick(Chapter chapter);
        void onChapterDeleteClick(Chapter chapter);
    }

    public ChapterAdapter(Context context, List<Chapter> chapterList, OnChapterListener listener) {
        this.context = context;
        this.chapterList = chapterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        
        // Hiển thị ID và tên chapter
        holder.tvChapterId.setText("ID: " + chapter.getId());
        holder.tvChapterName.setText(chapter.getName());
        
        // Hiển thị hình ảnh nếu có
        if (chapter.getImagePath() != null && !chapter.getImagePath().isEmpty()) {
            File imgFile = new File(chapter.getImagePath());
            if (imgFile.exists()) {
                holder.imgChapter.setImageURI(Uri.fromFile(imgFile));
            } else {
                holder.imgChapter.setImageResource(R.drawable.abc);
            }
        } else {
            holder.imgChapter.setImageResource(R.drawable.abc);
        }
        
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChapterClick(chapter);
            }
        });
        
        // Xử lý sự kiện click nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChapterDeleteClick(chapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList != null ? chapterList.size() : 0;
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        ImageView imgChapter;
        TextView tvChapterId, tvChapterName;
        ImageButton btnDelete;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChapter = itemView.findViewById(R.id.imgChapter);
            tvChapterId = itemView.findViewById(R.id.tvChapterId);
            tvChapterName = itemView.findViewById(R.id.tvChapterName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
