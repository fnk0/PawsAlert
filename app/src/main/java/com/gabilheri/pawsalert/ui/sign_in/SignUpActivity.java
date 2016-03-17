package com.gabilheri.pawsalert.ui.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.EditText;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.ValidationUtils;
import com.gabilheri.pawsalert.ui.home.HomeActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import in.anshul.libray.PasswordEditText;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/8/16.
 */
public class SignUpActivity extends BaseActivity
        implements SignUpCallback, LogInCallback {

    @Bind(R.id.name)
    AppCompatEditText mNameET;

    @Bind(R.id.phoneNumber)
    AppCompatEditText mPhoneET;

    @Bind(R.id.email)
    AppCompatEditText mEmailET;

    @Bind(R.id.password)
    PasswordEditText mPasswordET;

    @Bind(R.id.confirmPassword)
    PasswordEditText mConfirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        setTitle(getResources().getString(R.string.sign_up));
        if (getIntent().getExtras() != null &&
                getIntent().getExtras().getBoolean(Const.TRANSITION_LAYOUT)) {
            enableActivityTransition();
        }
    }

    @OnClick(R.id.createAccount)
    public void createAccount(View v) {
        HashMap<EditText, String> validationMap = new HashMap<>();
        String phoneNumber = mPhoneET.getText().toString();
        String email = mEmailET.getText().toString();
        String password = mPasswordET.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String name = mNameET.getText().toString();
        validationMap.put(mNameET, ValidationUtils.validateName(name));
        validationMap.put(mPhoneET, ValidationUtils.isValidPhoneNumber(phoneNumber));
        validationMap.put(mEmailET, ValidationUtils.isValidEmailAddress(email));
        validationMap.put(mPasswordET, ValidationUtils.isValidPassword(password));
        validationMap.put(mConfirmPassword, ValidationUtils.passwordsMatch(password, confirmPassword));

        boolean isValid = true;

        for(Map.Entry<EditText, String> entry : validationMap.entrySet()) {
            if (entry.getValue() != null) {
                isValid = false;
                entry.getKey().setError(getStringWithColor(WHITE_COLOR, entry.getValue()));
            }
        }
        if (isValid) {
            showProgressDialog("Creating Account...", null);
            User user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setUsername(email);
            user.setPassword(password);
            user.setPhoneNumber(phoneNumber);

            user.signUpInBackground(this);
        }
    }

    @Override
    public void done(ParseException e) {
        if (e != null) {
            dismissDialog();
            showSnackbar("Error creating account. Try another email.");
        } else {
            ParseUser.logInInBackground(mEmailET.getText().toString(), mPasswordET.getText().toString(), this);
        }
    }

    @Override
    public void done(ParseUser user, ParseException e) {
        dismissDialog();
        if (e != null) {
            showSnackbar("Oops! Something went wrong. Try again in a few moments.");
        } else {
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_sign_up;
    }
}
