package com.gabilheri.pawsalert.ui.sign_in;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.ValidationUtils;
import com.gabilheri.pawsalert.ui.home.HomeActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import butterknife.Bind;
import butterknife.OnClick;
import in.anshul.libray.PasswordEditText;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/8/16.
 */
public class SignInActivity extends BaseActivity implements LogInCallback{

    @Bind(R.id.email)
    AppCompatEditText mEmailEditText;

    @Bind(R.id.password)
    PasswordEditText mPasswordEditText;

    @Bind(R.id.signUp)
    Button mSignUpButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        setTitle(getResources().getString(R.string.sign_in));
        if (getIntent().getExtras() != null &&
                getIntent().getExtras().getBoolean(Const.TRANSITION_LAYOUT)) {
            enableActivityTransition();
        }
    }

    @OnClick(R.id.signInBtn)
    public void signIn(View v) {
        String emailValidation = ValidationUtils.isValidEmailAddress(mEmailEditText.getText().toString());
        String passwordValidation = ValidationUtils.isValidPassword(mPasswordEditText.getText().toString());
        boolean isValid = emailValidation == null && passwordValidation == null;
        if (isValid) {
            hideKeyboard();
            showProgressDialog("Signing In....", null);
            ParseUser.logInInBackground(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString(), this);
        } else {
            showErrorIfNecessary(emailValidation, mEmailEditText);
            showErrorIfNecessary(passwordValidation, mPasswordEditText);
        }
    }

    public void showErrorIfNecessary(String error, EditText editText) {
        if (error != null) {
            editText.setError(getStringWithColor(WHITE_COLOR, error));
        }
    }

    @Override
    public void done(ParseUser user, ParseException e) {
        dismissDialog();
        if (user != null) {
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else {
            Timber.e(e, e.getLocalizedMessage());
            showSnackbar("Error signing in. Email or password invalid.");
        }
    }

    @OnClick(R.id.forgotPassword)
    public void forgotPassword(View v) {
        new MaterialDialog.Builder(this)
                .title("Forgot Password")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Email", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String validation = ValidationUtils.isValidEmailAddress(input.toString());
                        if (validation == null) {
                            ParseUser.requestPasswordResetInBackground(input.toString(), new RequestPasswordResetCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        showSnackbar("Successfully reset the password. Check your email.");
                                    } else {
                                        showSnackbar("Oops! Something went wrong. Try again later.");
                                    }
                                }
                            });
                        } else {
                            showSnackbar(validation);
                        }
                    }
                }).show();
    }

    @OnClick(R.id.signUp)
    public void openSignUp(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(Const.TRANSITION_LAYOUT, true);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, mSignUpButton, Const.TRANSITION_LAYOUT);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_sign_in;
    }
}