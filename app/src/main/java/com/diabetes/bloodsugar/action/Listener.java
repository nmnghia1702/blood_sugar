package com.diabetes.bloodsugar.action;


import android.content.Intent;

public class Listener {

    public interface OnDialogYesNoListener {
        public void onYesClick();

        public void onNoClick();
    }

    public interface OnDialogAddCoverListener {
        public void onAddClick();

        public void onCoverClick();
    }

    public interface OnIntentReceived {
        void onIntent(Intent i, int resultCode);
    }
}
