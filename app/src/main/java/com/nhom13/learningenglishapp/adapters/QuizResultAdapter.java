package com.nhom13.learningenglishapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.models.QuizResult;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class QuizResultAdapter extends RecyclerView.Adapter<QuizResultAdapter.QuizResultViewHolder> {

    private List<QuizResult> quizResultList;
    private Context context;

    public QuizResultAdapter(Context context, List<QuizResult> quizResultList) {
        this.context = context;
        this.quizResultList = quizResultList;
    }

    @NonNull
    @Override
    public QuizResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_result, parent, false);
        return new QuizResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizResultViewHolder holder, int position) {
        QuizResult result = quizResultList.get(position);


        holder.tvGameNumber.setText("Ván " + (position + 1));
        holder.tvScore.setText("Điểm: " + result.getScore());
        holder.tvCorrectAnswers.setText("Đúng: " + result.getCorrectAnswers() + "/" + result.getTotalQuestions());


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText("Ngày chơi: " + sdf.format(new Date(result.getDate())));


    }

    @Override
    public int getItemCount() {
        return quizResultList != null ? quizResultList.size() : 0;
    }

    public void updateData(List<QuizResult> newQuizResultList) {
        this.quizResultList = newQuizResultList;
        notifyDataSetChanged();
    }

    public static class QuizResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameNumber, tvScore, tvCorrectAnswers, tvDate;

        public QuizResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameNumber = itemView.findViewById(R.id.tvGameNumber);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvCorrectAnswers = itemView.findViewById(R.id.tvCorrectAnswers);
            tvDate = itemView.findViewById(R.id.tvDate);

        }
    }
}
