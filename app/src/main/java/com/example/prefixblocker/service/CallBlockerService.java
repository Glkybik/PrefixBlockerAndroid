package com.example.prefixblocker.service;

import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import androidx.annotation.RequiresApi;

import com.example.prefixblocker.data.BlockedCall;
import com.example.prefixblocker.data.PrefsManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class CallBlockerService extends CallScreeningService {

    @Override
    public void onScreenCall(Call.Details callDetails) {
        String phoneNumber = "";
        if (callDetails.getHandle() != null) {
            phoneNumber = callDetails.getHandle().getSchemeSpecificPart();
        }

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");

        if (!isRussianNumber(cleanNumber)) {
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        String prefix = getPrefix(cleanNumber);
        if (prefix == null) {
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        PrefsManager prefsManager = new PrefsManager(this);
        List<String> blockedPrefixes = prefsManager.getActivePrefixes();

        if (blockedPrefixes.contains(prefix)) {
            CallResponse response = new CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(true)
                    .setSkipNotification(true)
                    .build();

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            BlockedCall blockedCall = new BlockedCall(cleanNumber, prefix, timestamp);
            prefsManager.addBlockedCall(blockedCall);

            respondToCall(callDetails, response);
        } else {
            respondToCall(callDetails, new CallResponse.Builder().build());
        }
    }

    private boolean isRussianNumber(String number) {
        if (number == null || number.isEmpty()) return false;
        if (!number.startsWith("+7")) return false;
        String digitsOnly = number.replace("+", "");
        return digitsOnly.length() == 11;
    }

    private String getPrefix(String number) {
        if (number == null || !number.startsWith("+7")) return null;
        String withoutCountry = number.replace("+7", "");
        if (withoutCountry.length() < 3) return null;
        return withoutCountry.substring(0, 3);
    }
}