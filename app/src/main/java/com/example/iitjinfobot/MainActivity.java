package com.example.iitjinfobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    private TextView helloUser;
    private LinearLayout chat_layout;
    private EditText message;
    private ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        helloUser = findViewById(R.id.user_hello);
        chat_layout = findViewById(R.id.chat_layout);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send_button);

        if (acct != null) {
//            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();

            String greetingText = helloUser.getText().toString();
            greetingText += " Hello\n" + personGivenName;
            helloUser.setText(greetingText);

            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(480, LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.setMargins(6, 6, 6, 6);
            tv.setLayoutParams(lParams);
            tv.setText("Hello " + personGivenName + ". I am your IITJ Assistant. How can I help you?");
            tv.setTextSize(16);
            tv.setPadding(6, 6, 6, 6);
            Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.logo);
            img.setBounds(0, 0, 60, 60);
            tv.setCompoundDrawables(img, null, null, null);

            tv.setBackgroundResource(R.drawable.bot_text);
            chat_layout.addView(tv);

        } else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.side_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dev_info) {
            Intent developerIntent = new Intent(MainActivity.this, DeveloperInfoActivity.class);
            startActivity(developerIntent);
        }
        if (item.getItemId() == R.id.signout) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return true;
    }
}