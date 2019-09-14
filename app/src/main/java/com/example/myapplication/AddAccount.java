package com.example.myapplication;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import Database.DBhelper;
import Models.AccountModel;

public class AddAccount extends AppCompatActivity {

    DBhelper db;
    EditText accountName,  accountIniAmount, accountNumber, accountDescription;
    Spinner accountType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerAccounts);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerAccounts, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        accountName = (EditText) findViewById(R.id.textAddAcountName);
        accountType = (Spinner) findViewById(R.id.spinnerAccounts);
        accountIniAmount = (EditText) findViewById(R.id.add_account_initial_account_txt);
        accountNumber = (EditText) findViewById(R.id.account_no_txt2);
        accountDescription = (EditText) findViewById(R.id.account_des);

    db = new DBhelper(this);


    }
    public void addAccountDetails(View view){
        AccountModel am = new AccountModel();
        am.setAccountName( accountName.getText().toString().trim() );
        am.setAccountType( accountType.getItemAtPosition( accountType.getSelectedItemPosition()  ).toString().trim() );
        am.setAmount( Double.valueOf( accountIniAmount.getText().toString().trim() ) );
        am.setAccountNumber( accountNumber.getText().toString().trim() );
        am.setAccountDescription( accountDescription.getText().toString().trim() );

        boolean result = db.addAccount( am);

        //inflate layout
        View layout = getLayoutInflater().inflate( R.layout.toast_message , (ViewGroup) view.findViewById(R.id.toastRoot) );
        TextView text = layout.findViewById(R.id.textMsg);
        CardView background = layout.findViewById(R.id.back);

        //creat toast
        Toast toast = new Toast( this );
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER , 0 , 230 );


        if( result == false ){
            text.setText("Account Added Unsuccessfully");
            background.setCardBackgroundColor( getResources().getColor(R.color.red));
            text.setTextColor( getResources().getColor( R.color.white ));
            toast.setView(layout);
            toast.show();

        }else{
            background.setCardBackgroundColor( getResources().getColor(R.color.green));
            text.setTextColor( getResources().getColor( R.color.white ));
            text.setText("Account Added Successfully");
            toast.setView(layout);
            this.finish();
            toast.show();

        }

    }
}
