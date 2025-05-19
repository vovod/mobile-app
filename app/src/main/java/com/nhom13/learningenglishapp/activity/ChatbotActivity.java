package com.nhom13.learningenglishapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.nhom13.learningenglishapp.R;
import com.nhom13.learningenglishapp.activity.user.UserHomePageActivity;
import com.nhom13.learningenglishapp.adapters.MessageAdapter;
import com.nhom13.learningenglishapp.database.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatbotActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "ChatbotActivity";

    private RecyclerView rvChatMessages;
    private EditText etChatMessage;
    private ImageButton btnSendMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private TextToSpeech textToSpeech;
    private boolean isTtsInitialized = false;

    private GenerativeModel gm;
    private GenerativeModelFutures model;
    private Executor mainExecutor;

    private ImageButton btnHomeChatbot, btnSettingChatbot;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }

        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        btnHomeChatbot = findViewById(R.id.btnHomeChatbot);
        btnSettingChatbot = findViewById(R.id.btnSettingChatbot);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(messageAdapter);

        textToSpeech = new TextToSpeech(this, this);
        mainExecutor = Executors.newSingleThreadExecutor();

        String apiKey = "API_key";


        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY_HERE") || apiKey.equals("null")) {
            Log.e(TAG, "API Key is missing or not replaced.");
            Toast.makeText(this, "Lỗi cấu hình: Thiếu API Key", Toast.LENGTH_LONG).show();
            btnSendMessage.setEnabled(false);
        } else {
            GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
            gm = new GenerativeModel("gemini-1.5-flash-latest", apiKey, configBuilder.build());
            model = GenerativeModelFutures.from(gm);
        }

        btnSendMessage.setOnClickListener(v -> {
            String userQuery = etChatMessage.getText().toString().trim();
            if (!userQuery.isEmpty()) {
                addUserMessage(userQuery);
                etChatMessage.setText("");
                if (model != null) {
                    sendQueryToGemini(userQuery);
                } else {
                    addBotMessage("Lỗi: Chatbot chưa được cấu hình đúng cách (API Key).");
                }
            } else {
                Toast.makeText(ChatbotActivity.this, "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
            }
        });

        btnHomeChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(ChatbotActivity.this, UserHomePageActivity.class);
            if (username != null && !username.isEmpty()) {
                intent.putExtra("username", username);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnSettingChatbot.setOnClickListener(v -> {
            showSettingDialog();
        });

        addBotMessage("Xin chào! Tôi là trợ lý học tiếng Anh. Bạn muốn hỏi gì về từ vựng (nghĩa, phát âm)?");
    }

    private void addUserMessage(String text) {
        Message userMessage = new Message(text, Message.TYPE_USER);
        messageList.add(userMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    private void addBotMessage(String text) {
        Message botMessage = new Message(text, Message.TYPE_BOT);
        messageList.add(botMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    private void sendQueryToGemini(String query) {
        addBotMessage("Bot đang suy nghĩ...");

        String prompt = "Bạn là một trợ lý học tiếng Anh thân thiện cho ứng dụng LearningEnglishApp. " +
                "Nhiệm vụ của bạn là trả lời các câu hỏi của người dùng liên quan đến nghĩa và cách phát âm của từ tiếng Anh. " +
                "Khi người dùng hỏi nghĩa một từ, hãy cung cấp nghĩa tiếng Việt của từ đó. " +
                "Khi người dùng hỏi cách phát âm một từ, hãy xác nhận lại từ đó và nói rằng bạn sẽ phát âm nó, ví dụ: 'Okay, để tôi phát âm từ [tên từ] cho bạn nghe nhé.' Sau đó, ứng dụng sẽ dùng Text-To-Speech để phát âm. " +
                "Nếu câu hỏi không rõ ràng hoặc không liên quan đến từ vựng, hãy trả lời một cách lịch sự rằng bạn chưa hiểu hoặc chỉ có thể giúp về nghĩa và phát âm từ. " +
                "Câu hỏi của người dùng là: \"" + query + "\"";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = "";
                try {
                    if (result != null && result.getText() != null) {
                        resultText = result.getText();
                        Log.i(TAG, "Gemini Response: " + resultText);
                    } else {
                        resultText = "Xin lỗi, tôi không thể xử lý yêu cầu này ngay bây giờ.";
                        Log.e(TAG, "Gemini response or text is null.");
                    }
                } catch (Exception e) {
                    resultText = "Đã có lỗi xảy ra khi xử lý phản hồi từ Gemini.";
                    Log.e(TAG, "Error processing Gemini response: ", e);
                }

                final String finalResultText = resultText;
                runOnUiThread(() -> {
                    updateBotMessage(finalResultText);
                    Pattern pattern = Pattern.compile("phát âm từ ['\"]?([a-zA-Z\\s]+)['\"]?", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(finalResultText);
                    if (matcher.find()) {
                        String wordToPronounce = matcher.group(1);
                        if (wordToPronounce != null && !wordToPronounce.trim().isEmpty()) {
                            speakWord(wordToPronounce.trim());
                        }
                    } else if (query.toLowerCase().startsWith("phát âm từ ")) {
                        String wordFromQuery = query.substring("phát âm từ ".length()).trim();
                        if (!wordFromQuery.isEmpty()) {
                            speakWord(wordFromQuery);
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "Error calling Gemini API: ", t);
                runOnUiThread(() -> {
                    updateBotMessage("Xin lỗi, đã có lỗi xảy ra khi kết nối với trợ lý AI.");
                });
            }
        }, mainExecutor);
    }

    private void updateBotMessage(String newText) {
        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).getMessageType() == Message.TYPE_BOT) {
            messageList.get(messageList.size() - 1).setMessageText(newText);
            messageAdapter.notifyItemChanged(messageList.size() - 1);
        } else {
            addBotMessage(newText);
        }
        rvChatMessages.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true;
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "US English not supported or missing. Trying UK English.");
                result = textToSpeech.setLanguage(Locale.UK);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "UK English not supported or missing. Trying general English.");
                    result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "General English also not supported or missing.");
                        Toast.makeText(this, "Ngôn ngữ tiếng Anh không được TTS hỗ trợ đầy đủ.", Toast.LENGTH_SHORT).show();
                        isTtsInitialized = false;
                    } else {
                        Log.i(TAG, "TextToSpeech set to general English.");
                    }
                } else {
                    Log.i(TAG, "TextToSpeech set to UK English.");
                }
            } else {
                Log.i(TAG, "TextToSpeech set to US English.");
            }
        } else {
            isTtsInitialized = false;
            Log.e(TAG, "Khởi tạo TextToSpeech thất bại! Status: " + status);
            Toast.makeText(this, "Khởi tạo TextToSpeech thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakWord(String word) {
        if (!isTtsInitialized) {
            Log.e(TAG, "TTS is not initialized. Cannot speak.");
            Toast.makeText(this, "TTS chưa sẵn sàng để phát âm.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            Locale currentLang = textToSpeech.getLanguage();
            if (currentLang == null || (!currentLang.equals(Locale.US) && !currentLang.equals(Locale.UK) && !currentLang.equals(Locale.ENGLISH))) {
                Log.w(TAG, "TTS language was not set to English. Attempting to set again.");
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = textToSpeech.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            }
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, word.hashCode() + "");
            Log.i(TAG, "TTS speaking word: " + word + " with language: " + textToSpeech.getLanguage());
        } else if (textToSpeech == null) {
            Log.e(TAG, "TextToSpeech is null (should not happen if isTtsInitialized is true).");
        } else if (textToSpeech.isSpeaking()) {
            Log.w(TAG, "TextToSpeech is already speaking.");
        }
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_setting, null);
        builder.setView(dialogView);

        Button logoutButton = dialogView.findViewById(R.id.igbLogOut);
        ImageButton igbHomeDialog = dialogView.findViewById(R.id.igbHome);
        ImageButton igbMusicDialog = dialogView.findViewById(R.id.igbMusic);
        ImageButton igbExitDialog = dialogView.findViewById(R.id.igbExit);

        AlertDialog dialog = builder.create();
        dialog.show();

        logoutButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ChatbotActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
            Toast.makeText(ChatbotActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });

        igbHomeDialog.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ChatbotActivity.this, UserHomePageActivity.class);
            if (username != null && !username.isEmpty()) {
                intent.putExtra("username", username);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        igbMusicDialog.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Nhạc đang phát triển", Toast.LENGTH_SHORT).show();
        });

        igbExitDialog.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity();
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            Log.i(TAG, "TextToSpeech đã được giải phóng.");
        }
        super.onDestroy();
    }
}