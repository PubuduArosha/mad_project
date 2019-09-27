package com.example.myapplication;

import android.accounts.Account;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import Adapters.Account_SpinnerAdapter;
import Database.DBhelper;
import Models.AccountModel;
import Models.CategoryModel;
import Models.SavingModel;
import Models.Transaction;


public class AddExpense extends Fragment {
    private Button save_btn;
   private EditText select_date, category_select ,amount ,Description ;
   private ImageView categoryIcon;
   private Boolean fromSaving;
   private SavingModel saving;
   private Spinner spinner;
    ArrayList<AccountModel> Acc_arrayList;
    CategoryModel currentCategory;
   DBhelper db;

    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense ,container , false);

        fromSaving = false;
        save_btn = view.findViewById(R.id.save_btn);
        amount = view.findViewById(R.id.editText);
        category_select = view.findViewById(R.id.category_select_Text);
        Description = view.findViewById( R.id.edit_description);
        select_date = view.findViewById(R.id.select_date);
        categoryIcon = view.findViewById(R.id.category_icon);
         spinner = view.findViewById(R.id.select_account);

         //if request coming from saving
        if( getArguments() != null ){
            Bundle savingBundle = getArguments();
            if( savingBundle.getSerializable("Saving") != null ){
                Log.i("DB" , "fromSaving : " + fromSaving);
                saving = (SavingModel) savingBundle.getSerializable("Saving");
                fromSaving = true;
                Log.i("DB" , "fromSaving : " + fromSaving);
            }
        }

         db = new DBhelper(getContext());
        Acc_arrayList =  db.readAllAccounts();
        Account_SpinnerAdapter Spinner_adapter  = new Account_SpinnerAdapter( getContext() , Acc_arrayList );
        spinner.setAdapter(Spinner_adapter);

        updateDate(view);
        UpdateCategory();

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Description.length() == 0) {
                    Description.setError("Enter Description");
                } else   if(category_select.length() == 0){
                    category_select.setError("Select Category");
                }
                else   if(amount.length() == 0){
                    amount.setError("Enter Amount");
                }
                else{

                    String amounttemp =   amount.getText().toString().trim();
                    double amountx  = (amounttemp.length() > 0 ) ? Double.valueOf(amounttemp) : 0;
                    double balance = 0;
                 ArrayList<AccountModel> accountlist = db.readAllAccountsWithBalance();
                    for (AccountModel model : accountlist) {
                        if(model.getId() == Acc_arrayList.get( spinner.getSelectedItemPosition() ).getId()){
                            balance = model.getBalance();
                            break;
                        }
                    }
                    //inflate layout
                    View layout = getLayoutInflater().inflate(R.layout.toast_message, (ViewGroup) view.findViewById(R.id.toastRoot));
                    TextView text = layout.findViewById(R.id.textMsg);
                    CardView background = layout.findViewById(R.id.back);
                    Toast toast = new Toast(getContext());
                    toast.setDuration(Toast.LENGTH_LONG);


                if(  ( balance >= amountx && currentCategory.getType().equals("Expense")  ) || currentCategory.getType().equals("Income")  ) {

                    Transaction current = new Transaction();
                    current.setAmount(amountx);
                    current.setCategoryModel(currentCategory);
                    current.setDescription(Description.getText().toString().trim());
                    current.setDate(select_date.getText().toString().trim());
                    current.setAccountId(Acc_arrayList.get(spinner.getSelectedItemPosition()).getId());
                    long id = db.insertTransaction(current);
                    boolean result;

                    if (id > 0) {
                        result = true;
                    } else {
                        result = false;
                    }

                    if (fromSaving) {
                        db.addSavingTransaction(saving.getID(), (int) id);
                    }




                    if (result == false) {
                        text.setText("Transaction Added Unsuccessfully");
                        background.setCardBackgroundColor(getResources().getColor(R.color.red));
                        text.setTextColor(getResources().getColor(R.color.white));
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 230);
                        toast.setView(layout);
                        toast.show();


                    } else {
                        background.setCardBackgroundColor(getResources().getColor(R.color.green));
                        text.setTextColor(getResources().getColor(R.color.white));
                        text.setText("Transaction Added Successfully");
                        toast.setView(layout);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 230);
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        toast.show();

                    }
                }else{
                    text.setText("Transaction must be less or equal than Account balance, Current balance = " + balance );
                    background.setCardBackgroundColor(getResources().getColor(R.color.red));
                    text.setTextColor(getResources().getColor(R.color.white));
                    toast.setView(layout);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 120);
                    toast.show();

                }
              }
            }
        });

        return view;
    }


    public void updateDate( View view){
        select_date = view.findViewById(R.id.select_date);

        select_date.setText(  new SimpleDateFormat("EEEE , dd MMMM yyyy").format( new Date())  );
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog dialog = new DatePickerDialog( getActivity() , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        try {
                            mMonth++;
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(   mYear +"-"+ mMonth +"-"+ mDay);

                            select_date.setText( new SimpleDateFormat("EEEE , dd MMMM yyyy").format(date) );
                        } catch (ParseException e) {
                            select_date.setText( e+"");
                        }
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH) );
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });
    }

    public void UpdateCategory( ){


        if( fromSaving ){
            currentCategory = new CategoryModel();
            currentCategory.setIcon("savings");
            currentCategory.setID(10);
            currentCategory.setName("Savings");
            category_select.setText( currentCategory.getName());
            categoryIcon.setImageResource(getContext().getResources().getIdentifier( currentCategory.getIcon(),
                    "drawable",
                    getContext().getPackageName()));

            Description.setText( saving.getSavingName() );


        }else {

            if (getArguments() != null &&  getArguments().getSerializable("expenseData") != null ) {
                Bundle bundle = getArguments();

                Transaction current = (Transaction) bundle.getSerializable("expenseData");
                amount.setText(String.valueOf(current.getAmount()));
                category_select.setText(current.getCategoryModel().getName());
                Description.setText(current.getDescription());
                select_date.setText(current.getDate());
                currentCategory = current.getCategoryModel();
                categoryIcon.setImageResource(getContext().getResources().getIdentifier(current.getCategoryModel().getIcon(),
                        "drawable",
                        getContext().getPackageName()));

                for (int position = 0; position < Acc_arrayList.size(); position++) {
                    if (Acc_arrayList.get(position).getId() == current.getAccountId()) {
                        spinner.setSelection(position);
                        break;
                    }
                }

            }
            category_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String amounttemp = amount.getText().toString().trim();
                    double amountx = (amounttemp.length() > 0) ? Double.valueOf(amounttemp) : 0;

                    Category category = new Category();
                    Bundle dataBundle = new Bundle();
                    Transaction current = new Transaction();

                    current.setAmount(amountx);
                    current.setDescription(Description.getText().toString().trim());
                    current.setDate(select_date.getText().toString().trim());
                    current.setAccountId(Acc_arrayList.get(spinner.getSelectedItemPosition()).getId());
                    dataBundle.putSerializable("expenseData", current);

                    category.setArguments(dataBundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, category).commit();

                }
            });
        }
    }
}
