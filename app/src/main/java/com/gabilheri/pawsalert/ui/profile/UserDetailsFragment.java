package com.gabilheri.pawsalert.ui.profile;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.base.BaseFragment;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.ValidationUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.OnClick;
import in.anshul.libray.PasswordEditText;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class UserDetailsFragment extends BaseFragment implements SaveCallback {

    private static final int UPDATE_PASSWORD = 0;
    private static final int UPDATE_ACCOUNT = 1;

    @Bind(R.id.userName)
    AppCompatEditText mUserNameET;

    @Bind(R.id.userEmail)
    AppCompatEditText mUserEmail;

    @Bind(R.id.editPassword)
    PasswordEditText mPasswordET;

    @Bind(R.id.editConfirmPassword)
    PasswordEditText mConfirmPasswordET;

    boolean isEditingAccount;
    User mCurrentUser;
    int currentUpdateType = -1;

    public static UserDetailsFragment newInstance() {
        Bundle args = new Bundle();
        UserDetailsFragment fragment = new UserDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentUser = (User) ParseUser.getCurrentUser();
        mUserEmail.setText(mCurrentUser.getEmail());
        mUserNameET.setText(mCurrentUser.getFullName());
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_user_details;
    }

    @OnClick(R.id.btnChangePassword)
    public void changePassword(View v) {

        String password = mPasswordET.getText().toString();
        String passwordCheck = ValidationUtils.isValidPassword(password);
        String confirmCheck = ValidationUtils.passwordsMatch(password, mConfirmPasswordET.getText().toString());

        if (passwordCheck == null) {
            if (confirmCheck == null) {
                // Valid passwords!
                mCurrentUser.setPassword(password);
                updateUser(UPDATE_PASSWORD);
            } else {
                mConfirmPasswordET.setError(confirmCheck);
            }
        } else {
            mPasswordET.setError(passwordCheck);
        }

    }

    @OnClick(R.id.btnEditAccount)
    public void editAccount(View v) {
        isEditingAccount = !isEditingAccount;
        mUserEmail.setEnabled(isEditingAccount);
        mUserNameET.setEnabled(isEditingAccount);
        ((Button)v).setText(getString(isEditingAccount ? R.string.save_changes : R.string.edit_account));
        if (!isEditingAccount) {
            String name = ValidationUtils.validateName(mUserNameET.getText().toString());
            String email = ValidationUtils.isValidEmailAddress(mUserEmail.getText().toString());

            if (name == null) {
                if (email == null) {
                    // both are valid
                    mCurrentUser.setFullName(mUserNameET.getText().toString());
                    mCurrentUser.setEmail(mUserEmail.getText().toString());
                    updateUser(UPDATE_ACCOUNT);
                } else {
                    mUserEmail.setError(email);
                }
            } else {
                mUserNameET.setError(name);
            }
        }
    }

    public void updateUser(int updateType) {
        currentUpdateType = updateType;
        if (updateType == UPDATE_ACCOUNT) {
            ((BaseActivity) getActivity()).showProgressDialog(null, "Updating user...");
        } else {
            ((BaseActivity) getActivity()).showProgressDialog(null, "Updating password...");
        }
        mCurrentUser.saveInBackground(this);
    }

    @Override
    public void done(ParseException e) {
        ((BaseActivity) getActivity()).dismissDialog();
        if (e == null) {
            if (currentUpdateType == UPDATE_ACCOUNT) {
                ((BaseActivity) getActivity()).showSnackbar("Successfully updated user.");
            } else {
                ((BaseActivity) getActivity()).showSnackbar("Successfully updated password.");
            }
        } else {
            ((BaseActivity) getActivity()).showSnackbar("Oops! Something went wrong. Try again later.");
            Timber.e(e, "Error updating user: " + e.getLocalizedMessage());
        }
    }
}
