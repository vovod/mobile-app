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
import com.nhom13.learningenglishapp.database.models.Quiz;

import java.io.File;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private Context context;
    private List<Quiz> quizList;
    private OnQuizListener listener;

    public interface OnQuizListener {
        void onQuizClick(Quiz quiz);
        void onQuizDeleteClick(Quiz quiz);
    }

    public QuizAdapter(Context context, List<Quiz> quizList, OnQuizListener listener) {
        this.context = context;
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        

        holder.tvCorrectAnswer.setText("Đáp án đúng: " + quiz.getCorrectAnswer());
        

        holder.tvWrongAnswer.setText("Đáp án sai: " + quiz.getWrongAnswer());
        

        if (quiz.getImagePath() != null && !quiz.getImagePath().isEmpty()) {
            File imgFile = new File(quiz.getImagePath());
            if (imgFile.exists()) {
                holder.imgQuiz.setImageURI(Uri.fromFile(imgFile));
            } else {
                holder.imgQuiz.setImageResource(R.drawable.question);
            }
        } else {
            holder.imgQuiz.setImageResource(R.drawable.question);
        }
        

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
        

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizDeleteClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList != null ? quizList.size() : 0;
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView imgQuiz;
        TextView tvCorrectAnswer, tvWrongAnswer;
        ImageButton btnDelete;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            imgQuiz = itemView.findViewById(R.id.imgQuiz);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            tvWrongAnswer = itemView.findViewById(R.id.tvWrongAnswer);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
