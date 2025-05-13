package com.nhom13.learningenglishapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.models.Video;
import com.nhom13.learningenglishapp.utils.YouTubeHelper;

public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = "PlayVideoActivity";
    private WebView webView;
    private ProgressBar progressBar;
    private String videoId;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        // Ánh xạ view
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        // Cấu hình WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // Cho phép tự động phát

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(PlayVideoActivity.this, "Lỗi khi tải video", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "WebView error: " + error);
            }
        });

        // Lấy dữ liệu video từ intent
        if (getIntent().hasExtra("video")) {
            Video video = (Video) getIntent().getSerializableExtra("video");
            String rawVideoUrl = video.getVideoUrl();

            // Trích xuất ID video từ URL hoặc sử dụng trực tiếp nếu đã là ID
            videoId = YouTubeHelper.extractYouTubeId(rawVideoUrl);

            // Log để debug
            Log.d(TAG, "Raw Video URL: " + rawVideoUrl);
            Log.d(TAG, "Extracted Video ID: " + videoId);

            // Tải video YouTube trong WebView
            loadYouTubeVideo(videoId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin video", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadYouTubeVideo(String videoId) {
        // Kiểm tra videoId
        if (videoId == null || videoId.isEmpty()) {
            Toast.makeText(this, "ID video không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị ProgressBar trong khi tải
        progressBar.setVisibility(View.VISIBLE);

        // Log để debug
        Toast.makeText(this, "Đang tải video ID: " + videoId, Toast.LENGTH_SHORT).show();

        try {
            // Phương pháp 1: Sử dụng iframe nhúng
            String html = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n" +
                    "    <style>\n" +
                    "        body { margin: 0; padding: 0; background-color: #000; }\n" +
                    "        .container { position: relative; width: 100%; height: 100vh; overflow: hidden; }\n" +
                    "        iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <iframe src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0\" frameborder=\"0\" allow=\"autoplay; encrypted-media; fullscreen\" allowfullscreen></iframe>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            // Tải HTML vào WebView - sử dụng loadDataWithBaseURL thay vì loadData
            webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null);

            // Đặt lắng nghe lỗi
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Log.e(TAG, "WebView error, trying fallback method");
                    // Phương pháp 2 (dự phòng): Nếu phương pháp 1 không hoạt động, hãy thử mở trực tiếp URL
                    webView.loadUrl("https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0");
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading YouTube video: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi tải video: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Phương pháp 3 (dự phòng cuối cùng): Mở trong ứng dụng YouTube hoặc trình duyệt
            showFallbackOptions(videoId);
        }
    }

    private void showFallbackOptions(final String videoId) {
        // Tạo dialog hiển thị các tùy chọn dự phòng
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Không thể phát video trong ứng dụng");
        builder.setMessage("Bạn muốn mở video trong ứng dụng YouTube hoặc trình duyệt?");

        // Nút mở trong YouTube
        builder.setPositiveButton("YouTube", (dialog, which) -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                startActivity(intent);
                finish();
            } catch (Exception e) {
                // Nếu không có ứng dụng YouTube, mở trong trình duyệt
                openInBrowser(videoId);
            }
        });

        // Nút mở trong trình duyệt
        builder.setNegativeButton("Trình duyệt", (dialog, which) -> {
            openInBrowser(videoId);
        });

        // Nút hủy
        builder.setNeutralButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void openInBrowser(String videoId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
