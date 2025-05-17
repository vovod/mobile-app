package com.nhom13.learningenglishapp.activity.user;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.database.dao.VideoDao; // <<<<<<<<<< THÊM IMPORT
import com.nhom13.learningenglishapp.database.models.Video;
import com.nhom13.learningenglishapp.utils.YouTubeHelper;

public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = "PlayVideoActivity";
    private WebView webView;
    private ProgressBar progressBar;
    private String videoId;
    private VideoDao videoDao;
    private Video currentVideo;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoDao = new VideoDao(this);


        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });


        if (getIntent().hasExtra("video")) {
            currentVideo = (Video) getIntent().getSerializableExtra("video");
            if (currentVideo == null) {
                Toast.makeText(this, "Lỗi: Dữ liệu video không hợp lệ.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String rawVideoUrl = currentVideo.getVideoUrl();


            videoId = YouTubeHelper.extractYouTubeId(rawVideoUrl);


            Log.d(TAG, "Raw Video URL: " + rawVideoUrl);
            Log.d(TAG, "Extracted Video ID: " + videoId);


            videoDao.incrementViewCount(currentVideo.getId());
            Log.d(TAG, "Incremented view count for video ID: " + currentVideo.getId() + ", Title: " + currentVideo.getTitle());



            loadYouTubeVideo(videoId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin video", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadYouTubeVideo(String videoId) {

        if (videoId == null || videoId.isEmpty()) {
            Toast.makeText(this, "ID video không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        try {

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

                    "        <iframe src=\"https://www.youtube.com/embed/" + (videoId != null ? videoId : "") + "?autoplay=1\" frameborder=\"0\" allowfullscreen></iframe>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";


            webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null);


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Log.e(TAG, "WebView error, trying fallback method. Error: " + error.getDescription());

                    if (videoId != null && !videoId.isEmpty()) {
                        webView.loadUrl("https://www.youtube.com/watch?v=" + videoId + "&autoplay=1&rel=0");
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading YouTube video: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi tải video: " + e.getMessage(), Toast.LENGTH_SHORT).show();


            if (videoId != null && !videoId.isEmpty()) {
                showFallbackOptions(videoId);
            }
        }
    }

    private void showFallbackOptions(final String videoId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Không thể phát video trong ứng dụng");
        builder.setMessage("Bạn muốn mở video trong ứng dụng YouTube hoặc trình duyệt?");


        builder.setPositiveButton("YouTube", (dialog, which) -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                startActivity(intent);
                finish();
            } catch (Exception e) {

                openInBrowser(videoId);
            }
        });


        builder.setNegativeButton("Trình duyệt", (dialog, which) -> {
            openInBrowser(videoId);
        });


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
