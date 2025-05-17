package com.nhom13.learningenglishapp.adapters;

import android.content.Context;
import android.graphics.Bitmap; // Thêm dòng này
import android.graphics.BitmapFactory; // Thêm dòng này
import android.util.Log; // Thêm dòng này (nếu chưa có, để dùng Log.e)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.models.Vocabulary;

import java.io.File; // Dòng này bạn đã có nhưng không còn cần thiết nếu tải từ assets
import java.io.IOException; // Thêm dòng này
import java.io.InputStream; // Thêm dòng này
import java.util.List;

public class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.AlphabetViewHolder> {

    private Context context;
    private List<Vocabulary> vocabularyList;
    private OnVocabularyClickListener listener;

    public interface OnVocabularyClickListener {
        void onVocabularyClick(Vocabulary vocabulary);
    }

    public AlphabetAdapter(Context context, List<Vocabulary> vocabularyList, OnVocabularyClickListener listener) {
        this.context = context;
        this.vocabularyList = vocabularyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlphabetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alphabet, parent, false);
        return new AlphabetViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlphabetViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.tvAlphabet.setText(vocabulary.getWord());

        if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            InputStream inputStream = null;
            try {

                inputStream = context.getAssets().open(vocabulary.getImagePath());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                holder.imgAlphabet.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("AlphabetAdapter", "Error loading image from assets: " + vocabulary.getImagePath(), e);
                holder.imgAlphabet.setImageResource(R.drawable.abc);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("AlphabetAdapter", "Error closing InputStream", e);
                    }
                }
            }
        } else {
            holder.imgAlphabet.setImageResource(R.drawable.abc);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVocabularyClick(vocabulary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vocabularyList != null ? vocabularyList.size() : 0;
    }

    public static class AlphabetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgAlphabet;
        TextView tvAlphabet;

        public AlphabetViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardAlphabet);
            imgAlphabet = itemView.findViewById(R.id.imgAlphabet);
            tvAlphabet = itemView.findViewById(R.id.tvAlphabet);
        }
    }
}