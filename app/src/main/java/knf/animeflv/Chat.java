package knf.animeflv;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Recyclers.ChatListAdapter;

/**
 * Created by Jordy on 29/10/2015.
 */
public class Chat extends AppCompatActivity implements LoginServer.callback {
    private static String sUserId;
    public static final String USER_ID_KEY = "userId";
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 100;
    private ListView lvChat;
    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;
    private boolean mFirstLoad;
    private EditText etMessage;
    private Button btSend;
    private Handler handler = new Handler();
    String email_coded;
    String pass_coded;
    Context context;
    EditText etusername;
    MaterialDialog dialog;
    MaterialDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(Color.parseColor("#FFC219"));
            getWindow().setStatusBarColor(Color.parseColor("#FFC219"));
        }
        context = this;
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
            pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                login();
            } else {
                signUp(true);
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 500);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }

    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
        setupMessagePosting();
    }

    private void signUp(Boolean isnew) {
        String username = getSharedPreferences("data", MODE_PRIVATE).getString("username", "null");
        String login_email = PreferenceManager.getDefaultSharedPreferences(context).getString("login_email", "null");
        if (!isnew) {
            ParseUser nlogin = new ParseUser();
            nlogin.setUsername(email_coded);
            nlogin.setPassword(pass_coded);
            nlogin.setEmail(login_email);
            nlogin.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseUser.logInInBackground(email_coded, pass_coded, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    startWithCurrentUser();
                                }
                            }
                        });
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Error al crear cuenta", Toast.LENGTH_SHORT).show();
                        Log.d("Error Parse", e.getMessage());
                        finish();
                    }
                }
            });
        } else {
            new Login().show(getSupportFragmentManager(), "Login");
        }
    }

    private void login() {
        ParseUser.logInInBackground(email_coded, pass_coded, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    dialog = new MaterialDialog.Builder(context)
                            .title("Escribe tu apodo")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.username, false)
                            .positiveText("OK")
                            .autoDismiss(false)
                            .cancelable(false)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    String user = etusername.getText().toString();
                                    if (user.length() > 4) {
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("username", user).apply();
                                        signUp(false);
                                    } else {
                                        etusername.setError("El apodo debe ser mas largo");
                                    }
                                }
                            }).build();
                    etusername = (EditText) dialog.getCustomView().findViewById(R.id.etUsername);
                    etusername.setTextColor(getResources().getColor(R.color.black));
                    dialog.show();
                }
            }
        });
    }

    private void setupMessagePosting() {
        etMessage = (EditText) findViewById(R.id.etMessage);
        etMessage.setTextColor(getResources().getColor(R.color.black));
        btSend = (Button) findViewById(R.id.btSend);
        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<Message>();
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        mAdapter = new ChatListAdapter(Chat.this, sUserId, mMessages);
        lvChat.setAdapter(mAdapter);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (body.length() > 0) {
                    Message message = new Message();
                    message.setUserId(sUserId);
                    message.setBody(body);
                    message.setAndroidID(androidID);
                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            receiveMessage();
                        }
                    });
                    etMessage.setText("");
                }
            }
        });
        handler.postDelayed(runnable, 500);
    }

    private void receiveMessage() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    if (mFirstLoad) {
                        lvChat.setSelection(mAdapter.getCount() - 1);
                        mFirstLoad = false;
                    }
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    @Override
    public void response(String data, TaskType taskType) {
        if (data.trim().contains("OK")) {
            dialog1 = new MaterialDialog.Builder(context)
                    .title("Escribe tu apodo")
                    .titleGravity(GravityEnum.CENTER)
                    .customView(R.layout.username, false)
                    .positiveText("OK")
                    .autoDismiss(false)
                    .cancelable(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String user = etusername.getText().toString();
                            if (user.length() > 4) {
                                getSharedPreferences("data", MODE_PRIVATE).edit().putString("username", user).apply();
                                signUp(false);
                            } else {
                                etusername.setError("El apodo debe ser mas largo");
                            }
                        }
                    }).build();
            etusername = (EditText) dialog1.getCustomView().findViewById(R.id.etUsername);
            etusername.setTextColor(getResources().getColor(R.color.black));
            dialog1.show();
        }
    }
}
