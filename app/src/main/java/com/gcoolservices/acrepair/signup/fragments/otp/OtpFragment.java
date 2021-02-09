package com.gcoolservices.acrepair.signup.fragments.otp;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.OTPModel;

public class OtpFragment extends Fragment {

    private OtpViewModel mViewModel;
    FloatingActionButton floatingActionButton;
    Pinview pinview;
    private OTPModel data;
    private  String mobileNumber;

    public static OtpFragment newInstance() {
        return new OtpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_fragment, container, false);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        pinview = view.findViewById(R.id.otp_view);

        if (getArguments() != null) {
            data = (OTPModel) getArguments().getSerializable("data");
            mobileNumber = getArguments().getString("mobile", "");
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinview.getPinLength()<4){
                    Toast.makeText(getActivity(), "Enter Pin", Toast.LENGTH_SHORT).show();
                    return;
                }

                matchOtp(v);
            }
        });
        return view;
    }

    private void matchOtp(View v) {

        if (data.getOtp().equals(pinview.getValue().toString())){
            Bundle bundle = new Bundle();
            bundle.putString("mobile", mobileNumber);
            Navigation.findNavController(v).navigate(R.id.action_OTPFragment_to_signupFragment, bundle);
        } else {
            pinview.setValue("");
            Toast.makeText(requireActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OtpViewModel.class);
        // TODO: Use the ViewModel
    }

}