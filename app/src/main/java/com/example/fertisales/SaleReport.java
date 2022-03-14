package com.example.fertisales;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.fertisales.databinding.ActivitySaleReportBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class SaleReport extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    int check = 0;
    ActivitySaleReportBinding saleReportBinding;
    String sM = "", sY = "", sD = "", sMT = "", sYT = "", sDT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saleReportBinding = ActivitySaleReportBinding.inflate(getLayoutInflater());
        View view = saleReportBinding.getRoot();
        setContentView(view);
    }

    public void fromDate(View view) {
        check = 1;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    public void toDate(View view) {
        check = 2;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        sM = String.format("%02d", month);
        sD = String.format("%02d", dayOfMonth);
        sY = Integer.toString(year);
        if (check == 1)
            saleReportBinding.fromDate.setText(sD + "/" + sM + "/" + year);
        else if (check == 2) {
            sDT = sD;
            sMT = sM;
            sYT = sY;
            saleReportBinding.toDate.setText(sD + "/" + sM + "/" + year);
        }
    }


    public void getReport(View view) throws ParseException {
        if (sM.equals("") || sY.equals("") || sD.equals("")) {
            Toast.makeText(this, "Fill 'From' what date you want to see reports.", Toast.LENGTH_SHORT).show();
        } else if (sMT.equals("") || sYT.equals("") || sDT.equals("")) {
            Toast.makeText(this, "Fill 'To' what date you want to see reports.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, SaleReportList.class);

            String fromDate = saleReportBinding.fromDate.getText().toString() + " 00:00:00";
            String toDate = saleReportBinding.toDate.getText().toString() + " 24:00:00";

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            Date dateT = sdf.parse(toDate);
            Date dateF = sdf.parse(fromDate);

            long millisT = dateT.getTime();
            long millisF = dateF.getTime();

            if (millisF >= millisT) {
                Toast.makeText(this, "Wrong dates filled.", Toast.LENGTH_SHORT).show();
            } else {
                intent.putExtra("dateT", millisT);
                intent.putExtra("dateF", millisF);
                startActivity(intent);
            }
        }
    }
}