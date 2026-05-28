package com.example.cmms.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getErrorMessage(int httpCode) {
        switch (httpCode) {
            case 401:
                return "Sesja wygasła. Zaloguj się ponownie.";
            case 403:
                return "Brak uprawnień do tej operacji";
            case 404:
                return "Nie znaleziono zasobu";
            case 409:
                return "Konflikt danych. Odśwież i spróbuj ponownie.";
            case 422:
                return "Nieprawidłowe dane formularza";
            default:
                if (httpCode >= 500) {
                    return "Błąd serwera. Spróbuj ponownie później.";
                }
                return "Wystąpił nieoczekiwany błąd";
        }
    }
}
