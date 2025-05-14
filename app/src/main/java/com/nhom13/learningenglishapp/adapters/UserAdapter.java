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
import android.util.Log; // Import Log

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
    private OnUserClickListener onUserClickListener;

    private static final String TAG = "UserAdapter"; // Tag cho Log

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public interface OnUserDeleteListener {
        void onUserDeleted();
    }

    public UserAdapter(Context context, List<User> userList, OnUserDeleteListener deleteListener, OnUserClickListener clickListener) {
        this.context = context;
        // Khởi tạo userList rỗng nếu được truyền null
        this.userList = userList != null ? userList : new java.util.ArrayList<>();
        this.userDao = new UserDao(context);
        this.onUserDeleteListener = deleteListener;
        this.onUserClickListener = clickListener;
    }

    // Constructor cũ
    public UserAdapter(Context context, List<User> userList, OnUserDeleteListener listener) {
        this(context, userList, listener, null);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // --- THÊM LOG VÀO ĐÂY ---
        Log.d(TAG, "onBindViewHolder called for position: " + position + " with list size: " + userList.size());
        if (position >= userList.size()) {
            Log.e(TAG, "onBindViewHolder called with invalid position: " + position);
            return; // Tránh crash nếu có lỗi
        }
        // ------------------------

        User user = userList.get(position);

        // --- THÊM LOG ĐỂ KIỂM TRA DỮ LIỆU CỦA TỪNG ITEM ---
        Log.d(TAG, "Binding user: " + user.getUsername() + " with score: " + user.getScore() + " at position: " + position);
        // -------------------------------------------------


        holder.tvUsername.setText(user.getUsername());
        holder.tvScore.setText(String.valueOf(user.getScore()));

        // imgUser không còn trong layout item_user.xml bạn gửi, nhưng giữ lại khai báo biến
        // Nếu bạn muốn hiển thị ảnh user, bạn cần thêm ImageView vào item_user.xml và logic load ảnh ở đây
        // holder.imgUser.setImageResource(R.drawable.usermanager); // Ảnh mặc định


        if (onUserDeleteListener != null) {
            holder.btnDeleteUser.setVisibility(View.VISIBLE);
            holder.btnDeleteUser.setOnClickListener(v -> {
                showDeleteConfirmDialog(user.getUsername(), position);
            });
        } else {
            holder.btnDeleteUser.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });
    }

    private void showDeleteConfirmDialog(String username, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + username + "\" không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = userDao.deleteUser(username);
            if (success) {
                // Cập nhật UI sau khi xóa
                // Trước tiên, kiểm tra xem position còn hợp lệ không
                if (position >= 0 && position < userList.size()) {
                    userList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, userList.size());
                    Toast.makeText(context, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                    if (onUserDeleteListener != null) {
                        onUserDeleteListener.onUserDeleted();
                    }
                } else {
                    Log.e(TAG, "Attempted to remove item with invalid position: " + position);
                    // Có thể cần tải lại toàn bộ dữ liệu nếu position không hợp lệ
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
        // --- THÊM LOG VÀO ĐÂY ---
        int count = userList != null ? userList.size() : 0;
        Log.d(TAG, "getItemCount returns: " + count);
        return count;
        // -----------------------
    }

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        // --- THÊM LOG VÀO ĐÂY ---
        Log.d(TAG, "updateData called with new list size: " + (newUserList != null ? newUserList.size() : 0));
        // -----------------------
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
