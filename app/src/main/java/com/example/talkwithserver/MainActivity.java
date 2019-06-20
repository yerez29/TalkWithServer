package com.example.talkwithserver;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String USER_TOKEN = "user_token";
    private static final String CURRENT_USER_NAME = "user_name";
    private static final String PRETTY_NAME = "pretty_name";
    private static final String IMAGE_URL = "image_url";
    private static final String DEFAULT_TOKEN = "TOKEN";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private EditText userName;
    private Button finishedUserName;
    private String userNameStr = "";
    private String prettyNme = "";
    private String imageUrl = "";
    private String token = "";
    private ProgressDialog progress;
    private EditText prettyNameText;
    private Button prettyNameSubmission;
    private Spinner dropdown;
    private ImageView image;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewsInitialization();
        generateUserToken();
        getUserDataFromServer();
        editPrettyName();
        editImageUrl();
    }

    private void editImageUrl() {
        if (!token.equals(DEFAULT_TOKEN))
        {
            dropdown.setVisibility(View.VISIBLE);
            String[] items = new String[]{"crab", "unicorn", "alien", "robot", "octopus", "frog"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = parent.getItemAtPosition(position).toString();
                    imageUrl = "/images/" + selection + ".png";
                    MyServerInreface serverInterface = ServerHolder.getInstance().serverInterface;
                    SetUserProfileImageRequest request = new SetUserProfileImageRequest(imageUrl);
                    Call<UserResponse> call = serverInterface.chooseProfileImage(request, "token " + token);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "code: " + String.valueOf(response.code() + ", try again!"),
                                        Toast.LENGTH_LONG).show();
                                imageUrl = "";
                                editor.putString(IMAGE_URL, imageUrl);
                                editor.apply();
                            }
                            else
                            {
                                editor.putString(IMAGE_URL, imageUrl);
                                editor.apply();
                                image.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + imageUrl).into(image);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            imageUrl = "";
                            editor.putString(IMAGE_URL, imageUrl);
                            editor.apply();
                        }
                    });
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void editPrettyName() {
        if (!token.equals(DEFAULT_TOKEN)) {
            prettyNameText.setVisibility(View.VISIBLE);
            prettyNameSubmission.setVisibility(View.VISIBLE);
            prettyNameSubmission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newPrettyName = prettyNameText.getText().toString();
                    if (newPrettyName.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter a valid pretty name and hit the submit button!",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                        {
                        MyServerInreface serverInterface = ServerHolder.getInstance().serverInterface;
                        SetUserPrettyNameRequest request = new SetUserPrettyNameRequest(newPrettyName);
                        Call<UserResponse> call = serverInterface.postPrettyName(request, "token " + token);
                        call.enqueue(new Callback<UserResponse>() {
                            @Override
                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "code: " + String.valueOf(response.code() + ", try again!"),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    User data = response.body().getData();
                                    prettyNme = data.getPretty_name();
                                    imageUrl = data.getImage_url();
                                    editor.putString(PRETTY_NAME, prettyNme);
                                    editor.putString(IMAGE_URL, imageUrl);
                                    editor.apply();
                                    userNameStr = sp.getString(CURRENT_USER_NAME, "");
                                    text.setVisibility(View.VISIBLE);
                                    if (prettyNme == null || prettyNme.equals("")) {
                                        text.setText("welcome, " + userNameStr + "!");
                                    } else {
                                        text.setText("welcome again, " + prettyNme + "!");
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<UserResponse> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void getUserDataFromServer() {
        if (!token.equals(DEFAULT_TOKEN)) {
            MyServerInreface serverInterface = ServerHolder.getInstance().serverInterface;
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
            Call<UserResponse> call = serverInterface.getUserResponse("token " + token);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (!response.isSuccessful()) {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "code: " + String.valueOf(response.code() + ", try again!"),
                                Toast.LENGTH_LONG).show();
                    } else {
                        progress.dismiss();
                        User data = response.body().getData();
                        prettyNme = data.getPretty_name();
                        imageUrl = data.getImage_url();
                        editor.putString(PRETTY_NAME, prettyNme);
                        editor.putString(IMAGE_URL, imageUrl);
                        editor.apply();
                        userNameStr = sp.getString(CURRENT_USER_NAME, "");
                        userName.setVisibility(View.GONE);
                        finishedUserName.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                        if (prettyNme == null || prettyNme.equals("")) {
                            text.setText("welcome, " + userNameStr + "!");
                        } else {
                            text.setText("welcome again, " + prettyNme + "!");
                        }
                    }
                }
                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void generateUserToken() {
        if (token.equals(DEFAULT_TOKEN)) {
            userName.setVisibility(View.VISIBLE);
            finishedUserName.setVisibility(View.VISIBLE);
            finishedUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyServerInreface serverInterface = ServerHolder.getInstance().serverInterface;
                    final String newUser = userName.getText().toString();
                    if (newUser.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter a valid username and hit the submit button!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Call<TokenResponse> call = serverInterface.getUserToken(newUser);
                        call.enqueue(new Callback<TokenResponse>() {
                            @Override
                            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "code: " + String.valueOf(response.code() + ", try again!"),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    String data = response.body().getData();
                                    token = data;
                                    editor.putString(USER_TOKEN, data);
                                    editor.putString(CURRENT_USER_NAME, newUser);
                                    editor.apply();
                                    userName.setVisibility(View.GONE);
                                    finishedUserName.setVisibility(View.GONE);
                                    getUserDataFromServer();
                                    editPrettyName();
                                    editImageUrl();
                                }
                            }
                            @Override
                            public void onFailure(Call<TokenResponse> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void viewsInitialization() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        token = sp.getString(USER_TOKEN, DEFAULT_TOKEN);
        userName = findViewById(R.id.userName);
        finishedUserName = findViewById(R.id.finished_user_name);
        prettyNameText = findViewById(R.id.prretyName);
        prettyNameSubmission = findViewById(R.id.finished_pretty_name);
        text = findViewById(R.id.user_name_text);
        dropdown = findViewById(R.id.url_images);
        image = findViewById(R.id.profileImage);
        userName.setVisibility(View.INVISIBLE);
        finishedUserName.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        prettyNameText.setVisibility(View.INVISIBLE);
        prettyNameSubmission.setVisibility(View.INVISIBLE);
        dropdown.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);
    }
}
