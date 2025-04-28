package com.nhom13.learningenglishapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.dao.UserDao;
import com.nhom13.learningenglishapp.database.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;
    private UserDao userDao;
    private OnUserDeleteListener onUserDeleteListener;

    public interface OnUserDeleteListener {
        void onUserDeleted();
    }

    public UserAdapter(Context context, List<User> userList, OnUserDeleteListener listener) {
        this.context = context;
        this.userList = userList;
        this.userDao = new UserDao(context);
        this.onUserDeleteListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvScore.setText(String.valueOf(user.getScore()));

        // Không cho phép xóa tài khoản admin
        if ("admin".equals(user.getUsername())) {
            holder.btnDeleteUser.setVisibility(View.INVISIBLE);
        } else {
            holder.btnDeleteUser.setVisibility(View.VISIBLE);
            holder.btnDeleteUser.setOnClickListener(v -> {
                showDeleteConfirmDialog(user.getUsername(), position);
            });
        }
    }

    private void showDeleteConfirmDialog(String username, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + username + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = userDao.deleteUser(username);
            if (success) {
                userList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, userList.size());
                Toast.makeText(context, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                if (onUserDeleteListener != null) {
                    onUserDeleteListener.onUserDeleted();
                }
            } else {
                Toast.makeText(context, "Xóa người dùng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView tvUsername, tvScore;
        ImageButton btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvScore = itemView.findViewById(R.id.tvScore);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
