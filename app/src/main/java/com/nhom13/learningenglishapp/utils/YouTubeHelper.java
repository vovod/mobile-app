package com.nhom13.learningenglishapp.utils;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeHelper {
    private static final String TAG = "YouTubeHelper";
    
    /**
     * Trích xuất ID video từ URL YouTube hoặc trả về chuỗi gốc nếu đã là ID
     * Hỗ trợ các định dạng URL:
     * - https://www.youtube.com/watch?v=VIDEO_ID
     * - https://youtu.be/VIDEO_ID
     * - https://www.youtube.com/embed/VIDEO_ID
     * - VIDEO_ID (nếu đã là ID)
     */
    public static String extractYouTubeId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        
        String videoId = url.trim();
        
        // Mẫu regex để trích xuất ID video từ URL YouTube
        String pattern = "(?:youtube\\.com\\/(?:[^\\/]+\\/.+\\/|(?:v|e(?:mbed)?)\\/|.*[?&]v=)|youtu\\.be\\/)([^\"&?\\/ ]{11})";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videoId);
        
        if (matcher.find()) {
            // Trích xuất ID từ URL
            videoId = matcher.group(1);
            Log.d(TAG, "Extracted YouTube ID: " + videoId);
        } else if (videoId.length() == 11) {
            // Có thể đã là ID (11 ký tự)
            Log.d(TAG, "Using as YouTube ID: " + videoId);
        } else {
            Log.w(TAG, "Could not extract YouTube ID from: " + url);
        }
        
        return videoId;
    }
}
