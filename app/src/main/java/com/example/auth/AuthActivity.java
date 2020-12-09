package com.example.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demochatfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Check if user is signed in (non-null) and update UI accordingly.
        if (user == null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void onSignOut(View view) {
        mAuth.signOut();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void getUserProfile(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("display name:");
            sb.append(user.getDisplayName());
            sb.append("\n");
            sb.append("email:");
            sb.append(user.getEmail());
            sb.append("\n");
            sb.append("photo url:");
            sb.append(user.getPhotoUrl());
            sb.append("\n");
            sb.append("is email verified:");
            sb.append(user.isEmailVerified());
            sb.append("\n");
            sb.append("uid:");
            sb.append(user.getUid());

            TextView textUserInfo = findViewById(R.id.text_user_info);
            textUserInfo.setText(sb.toString());
        }
    }

    public void sendVerifyEmail(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AuthActivity.this, "驗證Email已寄出", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AuthActivity.this, "驗證Email寄送失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void updateUserInfo(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName("Ryan Hsueh")
                    .setPhotoUri(Uri.parse("https://avatars1.githubusercontent.com/u/10694648?s=460&v=4"))
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AuthActivity.this, "User information updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AuthActivity.this, "User information update failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /*
        官網強調當你刪除帳號、設定主要 email 以及改變密碼的時候
        都必須做 Re-authenticate 否則有機會會丟出 FirebaseAuthRecentLoginRequiredException

        首先必須透過 AuthCredential 拿到 EmailAuthProvider 的憑證
        接著透過 FirebaseUser 物件的 reauthenticate 方法將憑證傳入

        因此我們在呼叫 FirebaseUser 物件的 updateEmail
        要寫在 FirebaseUser 物件的 reauthenticate callback 完成以後才可以改變 Email
     */
    public void reauthenticate(View view) {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            final View layout = LayoutInflater.from(this).inflate(R.layout.dlg_user_account, null);
            new AlertDialog.Builder(this)
                    .setView(layout)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextInputLayout tl_username = layout.findViewById(R.id.tl_username);
                            TextInputLayout tl_password = layout.findViewById(R.id.tl_password);

                            String email = tl_username.getEditText().getText().toString();
                            String password = tl_password.getEditText().getText().toString();

                            // 取得用戶憑證
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AuthActivity.this, "重新認證成功", Toast.LENGTH_SHORT).show();

                                        // 重新認證成功後，才可修改用戶Email or Password
                                        // add code to update Email/Password

                                    } else {
                                        Toast.makeText(AuthActivity.this, "重新認證失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
