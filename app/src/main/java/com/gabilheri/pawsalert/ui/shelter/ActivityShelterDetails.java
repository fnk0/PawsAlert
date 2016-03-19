package com.gabilheri.pawsalert.ui.shelter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.home.PetListFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/12/16.
 */
public class ActivityShelterDetails extends BaseActivity
        implements GetCallback<AnimalShelter>, OnMapReadyCallback, View.OnClickListener {

    @Bind(R.id.shelterImage)
    AppCompatImageView mShelterImageIV;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.locationTV)
    AppCompatTextView mLocationTV;

    @Bind(R.id.details)
    AppCompatTextView mDetailsTV;

    @Bind(R.id.shelterHours)
    AppCompatTextView mShelterHours;

    @Bind(R.id.map)
    MapView mMapView;

    @Bind(R.id.detailsTitle)
    AppCompatTextView mDetailsTitleTV;

    @Bind(R.id.fabCall)
    FloatingActionButton mActionCall;

    @Bind(R.id.actionShare)
    LinearLayout mActionShare;

    @Bind(R.id.actionEmail)
    LinearLayout mActionEmail;

    @Bind(R.id.actionWeb)
    LinearLayout mActionWeb;

    @Bind(R.id.actionDonate)
    LinearLayout mActionDonate;

    GoogleMap mGoogleMap;
    AnimalShelter mAnimalShelter;

    String pObjectID;
    PayPalConfiguration mPayPalConfiguration;

    AppCompatTextView mDonate25;
    AppCompatTextView mDonate50;
    AppCompatTextView mDonate100;
    AppCompatTextView mDonateOther;
    AppCompatEditText mDonateEditText;

    List<AppCompatTextView> moneyTVs;

    MaterialDialog mDonateDialog;
    BigDecimal mDonateAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        Bundle extras = getIntent().getExtras();
        mAnimalShelter = new AnimalShelter();
        mActionEmail.setOnClickListener(this);
        mActionCall.setOnClickListener(this);
        mActionShare.setOnClickListener(this);
        mActionWeb.setOnClickListener(this);
        mActionDonate.setOnClickListener(this);

        if (extras != null) {
            pObjectID = extras.getString(Const.OBJECT_ID);

            String pictureUrl = extras.getString(Const.IMAGE_EXTRA);
            loadImage(pictureUrl, mShelterImageIV);

            ParseQuery<AnimalShelter> query = AnimalShelter.getQuery();
            query.include("owner");
            query.getInBackground(pObjectID, this);

            mPayPalConfiguration = new PayPalConfiguration()
                    .merchantName(getString(R.string.app_name))
                    .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                    .clientId(Const.PAYPAL_CLIENT_ID);

            Intent intent = new Intent(this, PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
            startService(intent);

            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabCall:
                makePhoneCall(mAnimalShelter.getPhoneNumber());
                break;
            case R.id.actionShare:
                shareURL("http://www.stillwaterpaws.com/shelter.html?id=" + mAnimalShelter.getObjectId());
                break;
            case R.id.actionEmail:
                sendEmail(mAnimalShelter.getEmail());
                break;
            case R.id.actionWeb:
                String url = mAnimalShelter.getWebsite();
                if (!url.contains("http")) {
                    url = "http://" + url;
                }
                openURL(url);
                break;
            case R.id.money_25:
                changeMoneySelection((AppCompatTextView) v, "25.00");
                break;
            case R.id.money_50:
                changeMoneySelection((AppCompatTextView) v, "50.00");
                break;
            case R.id.money_100:
                changeMoneySelection((AppCompatTextView) v, "100.00");
                break;
            case R.id.moneyOther:
                changeMoneySelection((AppCompatTextView) v, "");
                break;
            case R.id.actionDonate:
                if (mDonateDialog == null) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                            .customView(R.layout.donate_dialog, false)
                            .positiveColor(getResources().getColor(R.color.primary))
                            .negativeColor(getResources().getColor(R.color.red_500))
                            .negativeText("Cancel")
                            .positiveText("Donate")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dismissDonateDialog();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (mDonateAmount != null) {
                                        dismissDonateDialog();
                                        donate();
                                    } else {
                                        showSnackbar("Please select a donation amount.");
                                    }
                                }
                            });
                    mDonateDialog = builder.build();
                    if (mDonateDialog.getCustomView() != null) {
                        mDonate25 = ButterKnife.findById(mDonateDialog.getCustomView(), R.id.money_25);
                        mDonate50 = ButterKnife.findById(mDonateDialog.getCustomView(), R.id.money_50);
                        mDonate100 = ButterKnife.findById(mDonateDialog.getCustomView(), R.id.money_100);
                        mDonateOther = ButterKnife.findById(mDonateDialog.getCustomView(), R.id.moneyOther);
                        mDonateEditText = ButterKnife.findById(mDonateDialog.getCustomView(), R.id.donateAmount);

                        moneyTVs = new ArrayList<>();
                        moneyTVs.add(mDonate25);
                        moneyTVs.add(mDonate50);
                        moneyTVs.add(mDonate100);
                        moneyTVs.add(mDonateOther);

                        mDonate25.setOnClickListener(this);
                        mDonate50.setOnClickListener(this);
                        mDonate100.setOnClickListener(this);
                        mDonateOther.setOnClickListener(this);

                        mDonateEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.toString().isEmpty()) {
                                    mDonateAmount = null;
                                } else {
                                    mDonateAmount = new BigDecimal(s.toString());
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    }
                }
                mDonateDialog.show();

                break;
        }
    }

    public void changeMoneySelection(AppCompatTextView tv, String text) {

        for(AppCompatTextView v : moneyTVs) {
            if (v.equals(tv)) {
                v.setTextColor(getResources().getColor(R.color.accent_color));
            } else {
                v.setTextColor(getResources().getColor(R.color.primary));
            }
        }

        mDonateEditText.setEnabled(tv.getId() == R.id.moneyOther);
        mDonateEditText.setText(text);
    }

    void dismissDonateDialog() {
        if (mDonateDialog != null && mDonateDialog.isShowing()) {
            mDonateDialog.dismiss();
        }
    }

    public void donate() {
        PayPalPayment payment = new PayPalPayment(mDonateAmount, "USD", "Donation for: " + mAnimalShelter.getShelterName(),
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.seeMoreAnimals)
    public void seeMoreAnimals(View v) {
        Intent i = new Intent(this, ActivityShelterAnimals.class);
        i.putExtra(PetListFragment.SHELTER_ID, mAnimalShelter.getObjectId());
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Timber.i(confirm.toJSONObject().toString(4));
                    String approved = confirm.toJSONObject().getJSONObject("response").getString("state");
                    if (approved.equals("approved")) {
                        showSnackbar("Thank you for your donation!!");
                    } else {
                        showSnackbar("Oops! Something went wrong. Try again later");
                    }
                } catch (JSONException e) {
                    Timber.e(e, "an extremely unlikely failure occurred: " + e.getLocalizedMessage());
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            showSnackbar("The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            showSnackbar("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    @Override
    public void done(AnimalShelter object, ParseException e) {
        mAnimalShelter = object;
        mAnimalShelter = mAnimalShelter.fromParseObject(mAnimalShelter);
        mLocationTV.setText(mAnimalShelter.getAddress());
        mCollapsingToolbarLayout.setTitle(mAnimalShelter.getShelterName());
        mShelterHours.setText(String.format(
                Locale.getDefault(), "%s - %s", mAnimalShelter.getOpenTime(), mAnimalShelter.getCloseTime()));
        mDetailsTitleTV.setText(getString(R.string.about));
        mDetailsTV.setText(mAnimalShelter.getShelterDescription());
        setLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setLocation();
    }

    public void setLocation() {
        if (mAnimalShelter != null && mGoogleMap != null) {
            try {
                LatLng loc = new LatLng(mAnimalShelter.getLatitude(), mAnimalShelter.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(loc));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, mGoogleMap.getCameraPosition().zoom));
            } catch (Exception ex) {
                Timber.d("Error parsing lat/lng");
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_shelter_details;
    }
}
