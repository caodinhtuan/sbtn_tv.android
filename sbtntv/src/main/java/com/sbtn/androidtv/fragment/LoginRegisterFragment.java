package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.WebViewActivity;
import com.sbtn.androidtv.eventbus.ReloadDataEvent;
import com.sbtn.androidtv.request.NetworkService;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.Code;
import com.sbtn.androidtv.request.datacallback.UserInfoDataCallback;
import com.sbtn.androidtv.request.datarequest.FacebookRegisterResource;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.PermissionFacebook;
import com.sbtn.androidtv.utils.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by hoanguyen on 5/19/16.
 */
public class LoginRegisterFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "LoginRegisterFragment";
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btLogInSubmit;
    private Button btSignUpNext;
    private View btFBLogIn;
    private Button btForgotPass;

    private EditText edtEmailSignUp;
    private EditText edtPasswordSignUp;
    private EditText edtPasswordSignUpConfirm;
    private Button btLogInBack;
    private Button btSignUpSubmit;

    private View layoutLogin;
    private View layoutSignUp;

    private LoginManager loginManager;
    private CallbackManager facebookCallbackManager;

    public interface OnActivityResultCallback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public static LoginRegisterFragment newInstance() {

//        Bundle args = new Bundle();

        LoginRegisterFragment fragment = new LoginRegisterFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_register, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        //Login
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        btLogInSubmit = (Button) v.findViewById(R.id.btLogInSubmit);
        btSignUpNext = (Button) v.findViewById(R.id.btSignUpNext);
        btFBLogIn = v.findViewById(R.id.btFBLogIn);
        btForgotPass = (Button) v.findViewById(R.id.btForgotPass);

        //SignUp
        edtEmailSignUp = (EditText) v.findViewById(R.id.edtEmailSignUp);
        edtPasswordSignUp = (EditText) v.findViewById(R.id.edtPasswordSignUp);
        edtPasswordSignUpConfirm = (EditText) v.findViewById(R.id.edtPasswordConfirmSignUp);
        btLogInBack = (Button) v.findViewById(R.id.btLogInBack);
        btSignUpSubmit = (Button) v.findViewById(R.id.btSignUpSubmit);

        layoutLogin = v.findViewById(R.id.layout_login);
        layoutSignUp = v.findViewById(R.id.layout_sign_up);

        btLogInSubmit.setOnClickListener(this);
        btSignUpNext.setOnClickListener(this);
        btFBLogIn.setOnClickListener(this);
        btForgotPass.setOnClickListener(this);
        btLogInBack.setOnClickListener(this);
        btSignUpSubmit.setOnClickListener(this);

        //TODO temp input for login register
//        edtEmail.setText("hoa.onworldtv+1@gmail.com");
//        edtPassword.setText("123457");

//        edtEmailSignUp.setText("hoa.onworldtv+4@gmail.com");
//        edtPasswordSignUp.setText("123456");
//        edtPasswordSignUpConfirm.setText("123456");

        initFacebookSDK(v);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogInSubmit:
                handleClickLoginSubmit();
                break;

            case R.id.btSignUpNext:
                showSignUpForm();
                break;

            case R.id.btForgotPass:
                handleForgotPass();
                break;

            case R.id.btLogInBack:
                showLoginForm();
                break;

            case R.id.btSignUpSubmit:
                handleClickSignUpSubmit();
                break;
        }
    }


    private void showLoginForm() {
        if (layoutLogin.getVisibility() == View.GONE) {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutSignUp.setVisibility(View.GONE);
            edtEmail.requestFocus();
        }
    }

    private void showSignUpForm() {
        if (layoutSignUp.getVisibility() == View.GONE) {
            layoutSignUp.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
            edtEmailSignUp.requestFocus();
        }
    }

    private void handleForgotPass() {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkService.getForgotPassWeb()));
        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        browserIntent.putExtra(WebViewActivity.EXTRA_URL, NetworkService.getForgotPassWeb());
        startActivity(browserIntent);
    }

    private void handleClickLoginSubmit() {
        final String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            showDialogConfirm(getString(R.string.warning), getString(R.string.dialog_msg_missing_email_pass), null);
            return;
        }

        if (!ValidateUtils.validateEmail(email) || !ValidateUtils.validatePassword(pass)) {
            showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_pass), null);
            return;
        }

        showLoadingDialog();
        RequestFramework.login(getActivity(), email, pass, new RequestFramework.DataCallBack<UserInfoDataCallback>() {
            @Override
            public void onResponse(UserInfoDataCallback dataResult) {
                if (dataResult == null) {
                    ALog.e(TAG, "login error: dataResult == null");
                    showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_login_unknown), null);
                    return;
                }

                switch (dataResult.getCode()) {
                    case Code.CODE_ERROR_LOGIN_INVALID_EMAIL_OR_PASS:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_pass), null);
                        break;

                    case Code.CODE_OK:
                        String name;
                        if (dataResult.getData() != null && !TextUtils.isEmpty(dataResult.getData().getFullName())) {
                            name = dataResult.getData().getFullName();
                        } else {
                            name = email;
                        }

                        showDialogConfirm(getString(R.string.congratulation), getString(R.string.dialog_msg_ok_login, name), new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                handleLoginOK();
                            }
                        });
                        break;

                    case Code.CODE_ERROR_ACCOUNT_NOT_ACTIVED:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_login_not_active), null);
                        break;

                    default:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_login_unknown), null);
                        ALog.e(TAG, "login error: code" + dataResult.getCode() + " - " + dataResult.getMessage());
                }

                hideLoadingDialog();
            }

            @Override
            public void onFailure() {
                ALog.e(TAG, "login error: onFailure");
                showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_login_unknown), null);
                hideLoadingDialog();
            }
        });
    }

    private void handleClickSignUpSubmit() {
        final String email = edtEmailSignUp.getText().toString();
        final String pass1 = edtPasswordSignUp.getText().toString();
        String pass2 = edtPasswordSignUpConfirm.getText().toString();

        if (TextUtils.isEmpty(email) || (TextUtils.isEmpty(pass1) && (TextUtils.isEmpty(pass2)))) {
            showDialogConfirm(getString(R.string.warning), getString(R.string.dialog_msg_missing_email_pass), null);
            return;
        }

        if (!ValidateUtils.validateEmail(email)) {
            showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email), null);
            return;
        }

        if (!ValidateUtils.validatePassword(email)) {
            showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_pass), null);
            return;
        }

        if (!pass1.equals(pass2)) {
            showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_pass_not_match), null);
            return;
        }

        showLoadingDialog();
        RequestFramework.register(getActivity(), email, pass1, new RequestFramework.DataCallBack<UserInfoDataCallback>() {
            @Override
            public void onResponse(UserInfoDataCallback dataResult) {
                if (dataResult == null) {
                    ALog.e(TAG, "login error: dataResult == null");
                    showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_register_unknown), null);
                    return;
                }

                switch (dataResult.getCode()) {
                    case Code.CODE_OK:
                        showDialogConfirm(getString(R.string.congratulation), getString(R.string.dialog_msg_ok_register), new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                edtEmail.setText(email);
                                edtPassword.setText(pass1);
                                btLogInSubmit.requestFocus();
                                showLoginForm();
                            }
                        });
                        break;

                    case Code.CODE_ERROR_ACCOUNT_NOT_ACTIVED:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_register_not_active), null);
                        break;

                    case Code.CODE_ERROR_REGISTER_EMAIL_EXITS:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_register_exits), null);
                        break;

                    default:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_register_unknown), null);
                        ALog.e(TAG, "login error: code" + dataResult.getCode() + " - " + dataResult.getMessage());
                }

                hideLoadingDialog();
            }

            @Override
            public void onFailure() {
                ALog.e(TAG, "login error: onFailure");
                showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_register_unknown), null);
                hideLoadingDialog();
            }
        });
    }

    private void showLoadingDialog() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof AccountManagerActivity) {
            ((AccountManagerActivity) activity).showLoading();
        }
    }

    private void hideLoadingDialog() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof AccountManagerActivity) {
            ((AccountManagerActivity) activity).hideLoading();
        }
    }

    private void showDialogConfirm(String title, String msg, MaterialDialog.SingleButtonCallback buttonOKCallback) {
        MaterialDialog dialog = new MaterialDialog.Builder(this.getActivity()).backgroundColorRes(R.color.MDGray)
                .title(title)
                .content(msg)
                .positiveText(R.string.ok)
                .onPositive(buttonOKCallback)
                .show();
        MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
        actionButton.requestFocus();
        actionButton.setBackground(getResources().getDrawable(R.drawable.statelist_button));
    }

    private void initFacebookSDK(View view) {
        View buttonFacebookLogin = view.findViewById(R.id.btFBLogIn);
        if (buttonFacebookLogin != null) {
            buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginManager == null) {
                        loginManager = LoginManager.getInstance();
                        loginManager.setDefaultAudience(loginManager.getDefaultAudience());
                        loginManager.setLoginBehavior(loginManager.getLoginBehavior());
                    }

                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken != null) {
                        loginManager.logOut();
                    }

                    loginManager.logInWithReadPermissions(getActivity(), PermissionFacebook.getPermission());
                }
            });
        }

        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        ALog.d(TAG, "FB callback:onSuccess()");

                        showLoadingDialog();
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        ALog.d(TAG, "GraphRequest.newMeRequest(email) -> " + response.toString());

                                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                        if (accessToken == null) {
                                            FacebookException exception = new FacebookException("Cannot retrieve Facebook access token!");
                                            onError(exception);
                                            return;
                                        }
                                        FacebookRegisterResource resource = new FacebookRegisterResource();
                                        Profile profile = Profile.getCurrentProfile();
                                        if (profile != null) {
                                            resource.setAvatar(profile.getProfilePictureUri(100, 100).toString());
                                            resource.setFirstName(profile.getFirstName());
                                            resource.setLastName(profile.getLastName());
//                                            fullName = profile.getName();
                                            resource.setFacebookID(profile.getId());
                                            resource.setFacebookToken(accessToken.getToken());
                                            resource.setEmailAddress(object.optString("email"));
                                        }
//                                        if (!resource.isValid()) {
//                                            FacebookException exception = new FacebookException("Cannot retrieve Facebook first name and last name");
//                                            onError(exception);
//                                            return;
//                                        }

                                        handleLoginWithFacebookAccount(resource);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        ALog.d(TAG, "FB callback:onCancel()");
                        hideLoadingDialog();
                        //TODO show dialog error cancel
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        ALog.d(TAG, "FB callback:onError() @msg=" + exception.getMessage());
                        hideLoadingDialog();
                        //TODO show dialog error
                    }
                });
    }

    private void handleLoginWithFacebookAccount(final FacebookRegisterResource resource) {
        showLoadingDialog();
        RequestFramework.loginByFacebook(getActivity(), resource, new RequestFramework.DataCallBack<UserInfoDataCallback>() {
            @Override
            public void onResponse(UserInfoDataCallback dataResult) {
                if (dataResult == null) {
                    ALog.e(TAG, "handleLoginWithFacebookAccount error: dataResult == null");
                    showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_login_unknown), null);
                    return;
                }

                switch (dataResult.getCode()) {
//                    case Code.CODE_ERROR_LOGIN_INVALID_EMAIL_OR_PASS:
//                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_pass), null);
//                        break;

                    case Code.CODE_OK:
                        String name;
                        if (dataResult.getData() != null && !TextUtils.isEmpty(dataResult.getData().getFullName())) {
                            name = dataResult.getData().getFullName();
                        } else {
                            name = resource.getFullName();
                        }

                        showDialogConfirm(getString(R.string.congratulation), getString(R.string.dialog_msg_ok_login, name), new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                handleLoginOK();
                            }
                        });
                        break;

                    case Code.CODE_ERROR_ACCOUNT_NOT_ACTIVED:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_email_login_not_active), null);
                        break;

                    default:
                        showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_login_unknown), null);
                        ALog.e(TAG, "login error: code" + dataResult.getCode() + " - " + dataResult.getMessage());
                }

                hideLoadingDialog();
            }

            @Override
            public void onFailure() {
                ALog.e(TAG, "login error: onFailure");
                showDialogConfirm(getString(R.string.error), getString(R.string.dialog_msg_error_login_unknown), null);
                hideLoadingDialog();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleLoginOK() {
        EventBus.getDefault().post(new ReloadDataEvent(TAG));
        Activity activity = getActivity();
        if (activity != null && activity instanceof AccountManagerActivity) {
            ((AccountManagerActivity) activity).handleShowFragment();
        }
    }

    /**
     * Phục vụ cho việc login nhanh khi debug
     *
     * @param email
     * @param pass
     */
    public void forceLogin(String email, String pass) {

        showLoginForm();
        edtEmail.setText(email);
        edtPassword.setText(pass);

        btLogInSubmit.performClick();
    }

}
