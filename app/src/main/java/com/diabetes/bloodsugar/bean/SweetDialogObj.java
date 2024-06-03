package com.diabetes.bloodsugar.bean;

import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetDialogObj {

    SweetAlertDialog sweetAlertDialog;
    RoundedProgressBar roundedProgressBar;

    public SweetDialogObj() {

    }

    public SweetDialogObj(RoundedProgressBar roundedProgressBar, SweetAlertDialog sweetAlertDialog) {
        this.roundedProgressBar = roundedProgressBar;
        this.sweetAlertDialog = sweetAlertDialog;
    }

    public RoundedProgressBar getRoundedProgressBar() {
        return roundedProgressBar;
    }

    public void setRoundedProgressBar(RoundedProgressBar roundedProgressBar) {
        this.roundedProgressBar = roundedProgressBar;
    }

    public SweetAlertDialog getSweetAlertDialog() {
        return sweetAlertDialog;
    }

    public void setSweetAlertDialog(SweetAlertDialog sweetAlertDialog) {
        this.sweetAlertDialog = sweetAlertDialog;
    }
}
