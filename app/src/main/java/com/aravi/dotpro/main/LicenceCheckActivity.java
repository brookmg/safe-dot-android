package com.aravi.dotpro.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.aravi.dotpro.R;
import com.github.javiersantos.piracychecker.PiracyChecker;
import com.github.javiersantos.piracychecker.callbacks.DoNotAllowCallback;
import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.Display;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.github.javiersantos.piracychecker.enums.PirateApp;

public class LicenceCheckActivity extends AppCompatActivity {

    private int checkCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence_check);

        new Handler().postDelayed(this::checkLicense, 1000);
    }

    private void checkLicense(){
        checkCount = 1;
        new PiracyChecker(this)
                .enableGooglePlayLicensing("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3fCjy2J/SZsvZ0ZSx83DprNinAvjqJ8e9rR3q7iATBcovsNRZa9GZXFQvxiNs7Gfhr70ZfOyHq7cGxUQEsEkEuyE0A8XSvESePbCp9pRjHLhJ5oFIOMtY6u/ouhHY9ayxD5JDistqXhzIKsl6ZTHHIJAITW42psKabSLLKV2MnpeE1IkchPglW6WMW4mG8lc6rFihkG35jyeT6vQkAttnW9o08Cw8IYOZguqq8kvL01T3+eg5jKuQ4cUxz7JHZURrdCkExONDnoFNMtWYJUb7uxT0MSkgg1VTqYk8jDmTQmFGXfb4pZQsuPSHGAm+mA2G6217tUolaB/MljW/iun1QIDAQAB")
                .callback(callback)
                .start();
    }

    private void checkSigningCertificates(){
        checkCount = 2;
        new PiracyChecker(this)
                .enableSigningCertificates("48b69792562c0a22dfc1164cbdf315101b89cc6b")
                .callback(callback)
                .start();
    }

    private void checkInstaller(){
        checkCount = 3;
        new PiracyChecker(this)
                .enableInstallerId(InstallerID.GOOGLE_PLAY)
                .callback(callback)
                .start();
    }


    private PiracyCheckerCallback callback = new PiracyCheckerCallback() {
        @Override
        public void allow() {
            switch (checkCount){
                case 1:
                    new Handler().postDelayed(() -> checkSigningCertificates(), 1000);
                    break;
                case 2:
                    new Handler().postDelayed(() -> checkInstaller(), 1000);
                    break;
                case 3:
                    break;
                default:
                    break;
            }

        }
        @Override
        public void doNotAllow(PiracyCheckerError piracyCheckerError, PirateApp pirateApp) {
            switch (checkCount){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    };
}