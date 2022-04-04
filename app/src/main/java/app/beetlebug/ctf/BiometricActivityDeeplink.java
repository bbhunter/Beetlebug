package app.beetlebug.ctf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.Executor;

import app.beetlebug.FlagCaptured;
import app.beetlebug.R;

public class BiometricActivityDeeplink extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private ImageView mImageView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);

        mImageView = findViewById(R.id.fingerPrintImageView);

        sharedPreferences = getSharedPreferences("flag_scores", Context.MODE_PRIVATE);


        if(Build.VERSION.SDK_INT>=21){
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        // init the values
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(BiometricActivityDeeplink.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(BiometricActivityDeeplink.this, "Exploit deeplinks to bypass login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        // setup title, description and auth dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using Fingerprint or Face")
                .setNegativeButtonText("Cancel")
                .build();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show auth dialog
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    public void signIn(View view) {
        EditText sign = findViewById(R.id.editTextPassword);
        sign.setError("Wrong password");
    }

    public void flg(View view) {
        EditText m_flg = (EditText) findViewById(R.id.flag);
        String result = m_flg.getText().toString();
        if((result.equals("0x43J1230"))) {
            int ctf_score_auth = 5;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ctf_score_auth", ctf_score_auth);
            editor.apply();
            Intent secret_intent = new Intent(BiometricActivityDeeplink.this, FlagCaptured.class);
            secret_intent.putExtra("ctf_score_auth", ctf_score_auth);
            startActivity(secret_intent);
        } else if (result.isEmpty()) {
            m_flg.setError("Enter flag");
        }
    }
}