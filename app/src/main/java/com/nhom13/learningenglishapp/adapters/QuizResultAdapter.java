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

import java.text.SimpleDateFormat; // Import SimpleDateFormat
import java.util.List;
import java.util.Locale; // Import Locale
import java.util.Date; // Import Date

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_result, parent, false); // Sẽ tạo layout item này sau
        return new QuizResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizResultViewHolder holder, int position) {
        QuizResult result = quizResultList.get(position);

        // Hiển thị thông tin kết quả quiz
        holder.tvGameNumber.setText("Ván " + (position + 1)); // Số thứ tự ván chơi
        holder.tvScore.setText("Điểm: " + result.getScore());
        holder.tvCorrectAnswers.setText("Đúng: " + result.getCorrectAnswers() + "/" + result.getTotalQuestions());

        // Định dạng và hiển thị ngày chơi
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText("Ngày chơi: " + sdf.format(new Date(result.getDate())));

        // (Tùy chọn) Hiển thị thêm thông tin nếu cần
        // Ví dụ: Tỷ lệ đúng
        // holder.tvPercentage.setText(String.format(Locale.getDefault(), "Tỷ lệ đúng: %.1f%%", result.getPercentageCorrect()));
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
        TextView tvGameNumber, tvScore, tvCorrectAnswers, tvDate; // Thêm các TextView cần thiết

        public QuizResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameNumber = itemView.findViewById(R.id.tvGameNumber); // Sẽ tạo ID này trong item_quiz_result
            tvScore = itemView.findViewById(R.id.tvScore); // Sẽ tạo ID này trong item_quiz_result
            tvCorrectAnswers = itemView.findViewById(R.id.tvCorrectAnswers); // Sẽ tạo ID này trong item_quiz_result
            tvDate = itemView.findViewById(R.id.tvDate); // Sẽ tạo ID này trong item_quiz_result
            // Ánh xạ thêm các view khác nếu có
        }
    }
}
