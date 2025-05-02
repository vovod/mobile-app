package com.nhom13.learningenglishapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private Context context;
    private List<String> answerOptions;
    private OnAnswerClickListener listener;

    public interface OnAnswerClickListener {
        void onAnswerClick(String answer);
    }

    public AnswerAdapter(Context context, List<String> answerOptions, OnAnswerClickListener listener) {
        this.context = context;
        this.answerOptions = answerOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        String answer = answerOptions.get(position);
        holder.tvAnswer.setText(answer);
        
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnswerClick(answer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return answerOptions != null ? answerOptions.size() : 0;
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvAnswer;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardAnswer);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
        }
    }
}
