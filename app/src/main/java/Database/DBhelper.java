package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.Category;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Models.AccountModel;
import Models.CategoryModel;
import Models.SavingModel;
import Models.Transaction;
import Util.Util;

public class DBhelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "monefy.db";

    public DBhelper(Context context) {
        super(context, DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_ct_transaction = "CREATE TABLE "+ DBConfig.Transactions.TABLE_NAME  + " ( " +
                DBConfig.Transactions.COLUMN_NAME_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "+
                DBConfig.Transactions.Column_NAME_AMOUNT + " DOUBLE,"+
                DBConfig.Transactions.Column_NAME_CATEGORY_ID + " INT , "+
                DBConfig.Transactions.Column_NAME_DESCRIPTION + " TEXT , "+
                DBConfig.Transactions.Column_NAME_DATE + " DATE , "+
                DBConfig.Transactions.Column_NAME_ACCOUNT_ID + " INT , "
                + "FOREIGN KEY(" + DBConfig.Transactions.Column_NAME_CATEGORY_ID + ") REFERENCES "
                + DBConfig.Categories.TABLE_NAME + "(CID) , "
                + "FOREIGN KEY(" + DBConfig.Transactions.Column_NAME_ACCOUNT_ID  + ") REFERENCES "
                + DBConfig.Accounts.TABLE_NAME  + "(AID) "  + ");";

        String sql_ct_categories = "CREATE TABLE "+ DBConfig.Categories.TABLE_NAME  + " ( " +
                DBConfig.Categories.COLUMN_NAME_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "+
                DBConfig.Categories.COLUMN_NAME_CNAME + " TEXT , "+
                DBConfig.Categories.COLUMN_NAME_DESCRIPTION + " TEXT , " +
                DBConfig.Categories.COLUMN_NAME_TYPE + " TEXT , " +
                DBConfig.Categories.COLUMN_NAME_ICON + " TEXT " + " );";

        String sql_ct_accounts = "CREATE TABLE "+ DBConfig.Accounts.TABLE_NAME  + " ( " +
                DBConfig.Accounts.COLUMN_NAME_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "+
                DBConfig.Accounts.COLUMN_NAME_ANAME + " TEXT , "+
                DBConfig.Accounts.COLUMN_NAME_AMOUNT + " DOUBLE , " +
                DBConfig.Accounts.COLUMN_NAME_TYPE + " TEXT , " +
                DBConfig.Accounts.COLUMN_NAME_NUMBER + " TEXT, " +
                DBConfig.Accounts.COLUMN_NAME_DESCRIPTION +" TEXT " +");";

        String sql_ct_savings = "CREATE TABLE " + DBConfig.Savings.TABLE_NAME + " ( " +
                DBConfig.Savings.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "+
                DBConfig.Savings.COLUMN_NAME_SAVINGNAME + " TEXT , "+
                DBConfig.Savings.COLUMN_NAME_SAVINGDISCRIPTION + " TEXT , "+
                DBConfig.Savings.COLUMN_NAME_TARGETAMOUNT + " DOUBLE ,"+
                DBConfig.Savings.COLUMN_NAME_STARTAMOUNT + " DOUBLE  );";

        String sql_ct_savings_trans = "CREATE TABLE " + DBConfig.SavingsTransaction.TABLE_NAME + " ( " +
                DBConfig.SavingsTransaction.COLUMN_NAME_SID + " INTEGER NOT NULL , "+
                DBConfig.SavingsTransaction.COLUMN_NAME_TID + " INTEGER  NOT NULL ,"+
                "PRIMARY KEY ( "+DBConfig.SavingsTransaction.COLUMN_NAME_TID+" , "+ DBConfig.SavingsTransaction.COLUMN_NAME_SID+" ) "+ " );";


        db.execSQL(sql_ct_savings);
        db.execSQL(sql_ct_categories);
        db.execSQL(sql_ct_accounts);
        db.execSQL(sql_ct_transaction);
        db.execSQL(sql_ct_savings_trans );
        setDefaultCategories(db);
        setDefaultAccount(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    /*
     ---------------------------Vishvi Wathasha---------------------------------------------
     */

    public boolean insertCategory(CategoryModel categoryModel){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put( DBConfig.Categories.COLUMN_NAME_CNAME , categoryModel.getName() );
        contentValues.put( DBConfig.Categories.COLUMN_NAME_DESCRIPTION , categoryModel.getDescription() );
        contentValues.put( DBConfig.Categories.COLUMN_NAME_TYPE , categoryModel.getType() );
        contentValues.put( DBConfig.Categories.COLUMN_NAME_ICON , categoryModel.getIcon() );

        long result = db.insert( DBConfig.Categories.TABLE_NAME ,null, contentValues );

        if (result > 0){
            //succes msg
            return true;
        }
        else{
            return false;
        }
    }

    public CategoryModel readSingleCategory( String ID ) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { DBConfig.Categories.COLUMN_NAME_ID , DBConfig.Categories.COLUMN_NAME_CNAME , DBConfig.Categories.COLUMN_NAME_DESCRIPTION ,
                DBConfig.Categories.COLUMN_NAME_TYPE , DBConfig.Categories.COLUMN_NAME_ICON};

        String Selection = DBConfig.Categories.COLUMN_NAME_ID + " = ? ";
        String selectionArgs[]  = { ID };

        Cursor values = db.query(DBConfig.Categories.TABLE_NAME ,projection,Selection ,selectionArgs ,null,null, null);
        CategoryModel c = new CategoryModel();


        while (values.moveToNext()){
            String name = values.getString(values.getColumnIndexOrThrow(DBConfig.Categories.COLUMN_NAME_CNAME));
            int id = values.getInt( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ID ));
            c.setID(id);
            c.setName(name);
            c.setDescription( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_DESCRIPTION)));
            c.setType( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_TYPE)));
            c.setIcon( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ICON )));

        }
        return c;
    }

    public ArrayList<CategoryModel> readAllCategories(String type) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { DBConfig.Categories.COLUMN_NAME_ID , DBConfig.Categories.COLUMN_NAME_CNAME , DBConfig.Categories.COLUMN_NAME_DESCRIPTION ,
        DBConfig.Categories.COLUMN_NAME_TYPE , DBConfig.Categories.COLUMN_NAME_ICON};

        String Selection = DBConfig.Categories.COLUMN_NAME_TYPE + " = ? ";
        String selectionArgs[]  = { type };

        Cursor values = db.query(DBConfig.Categories.TABLE_NAME ,projection,Selection ,selectionArgs ,null,null, null);

        ArrayList<CategoryModel> categoryModels = new ArrayList<CategoryModel>();

        while (values.moveToNext()){
            String name = values.getString(values.getColumnIndexOrThrow(DBConfig.Categories.COLUMN_NAME_CNAME));
            int id = values.getInt( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ID ));
            CategoryModel c = new CategoryModel();
            c.setID(id);
            c.setName(name);
            c.setDescription( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_DESCRIPTION)));
            c.setType( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_TYPE)));
            c.setIcon( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ICON )));
            categoryModels.add(c);

        }
        return categoryModels;
    }

    public boolean deleteCategory(String id) {

        SQLiteDatabase db = getReadableDatabase();
        String selection = DBConfig.Categories.COLUMN_NAME_ID +" = ? ";
        String args[] = { id };
        long result = db.delete( DBConfig.Categories.TABLE_NAME , selection, args);
        if (result > 0){
            return true;
        }else {
            return false;
        }
     }

    public boolean updateCategory(CategoryModel newCategory) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put( DBConfig.Categories.COLUMN_NAME_CNAME , newCategory.getName());
        contentValues.put(DBConfig.Categories.COLUMN_NAME_DESCRIPTION, newCategory.getDescription());
        contentValues.put(DBConfig.Categories.COLUMN_NAME_TYPE, newCategory.getType());
        //contentValues.put(DBConfig.Categories.COLUMN_NAME_ICON, newCategory.getIcon());

        String selection = DBConfig.Categories.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { String.valueOf( newCategory.getID() ) };

        long result = db.update( DBConfig.Categories.TABLE_NAME , contentValues , selection , selectionArgs);

        if (result > 0){

            return true;
        }
        else{
            return false;
        }

    }

    public ArrayList<Transaction>  latestTransactions( String CID ){
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DBConfig.Transactions.TABLE_NAME + " , " + DBConfig.Categories.TABLE_NAME +
                " WHERE " + DBConfig.Transactions.TABLE_NAME + ".CID = " + DBConfig.Categories.TABLE_NAME + ".CID AND "+
                DBConfig.Categories.TABLE_NAME + ".CID = " + CID + " ORDER BY "+ DBConfig.Transactions.TABLE_NAME
                +"."+DBConfig.Transactions.Column_NAME_DATE +" DESC LIMIT 10";

        Cursor values = db.rawQuery( sql , null );

        ArrayList<Transaction> arrayList = new ArrayList<>();

        while (values.moveToNext()){
            try {
            Transaction transaction = new Transaction();
            CategoryModel category = new CategoryModel();

            transaction.setId( values.getInt( values.getColumnIndexOrThrow( DBConfig.Transactions.COLUMN_NAME_ID ) ) );
            transaction.setAmount( values.getDouble( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_AMOUNT )));
            transaction.setDescription( values.getString( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_DESCRIPTION )));
            transaction.setAccountId( values.getInt( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_ACCOUNT_ID )));

            Date datex = null;
            String date = values.getString( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_DATE ));
            SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                datex = formatter.parse(date);


            transaction.setDate( new SimpleDateFormat("dd-MM-yyyy").format(datex ));
            category.setID( values.getInt( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ID )));
            category.setName( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_CNAME )));
            category.setType( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_TYPE )));
            category.setIcon( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ICON )));

            transaction.setCategoryModel(category);
            arrayList.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }



    //-----------------------------------------------Amoda Sasmitha-------------------------------------------------------

    public long insertTransaction(Transaction transaction ){

        Date date = null;
        try {
             date = new SimpleDateFormat("EEEE , dd MMMM yyyy").parse(transaction.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( DBConfig.Transactions.Column_NAME_AMOUNT , transaction.getAmount() );
        values.put( DBConfig.Transactions.Column_NAME_DESCRIPTION , transaction.getDescription());
        values.put( DBConfig.Transactions.Column_NAME_DATE , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) );
        values.put( DBConfig.Transactions.Column_NAME_CATEGORY_ID , transaction.getCategoryModel().getID() );
        values.put( DBConfig.Transactions.Column_NAME_ACCOUNT_ID , transaction.getAccountId() );


        long result = db.insert( DBConfig.Transactions.TABLE_NAME , null , values );
       return result;

    }
    public ArrayList<Transaction> readAllTransactions( String from , String to ){
        SQLiteDatabase db = getReadableDatabase();
        to = Util.addDays( to , 1);
        try {
            from = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("dd-MM-yyyy").parse(from) );
            to = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("dd-MM-yyyy").parse(to) );

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sql = "SELECT * FROM " + DBConfig.Transactions.TABLE_NAME + " , " + DBConfig.Categories.TABLE_NAME +
                     " WHERE " + DBConfig.Transactions.TABLE_NAME + ".CID = " + DBConfig.Categories.TABLE_NAME + ".CID AND "+
                " transactions.Date BETWEEN '"+from+"' AND  '"+to+"' ";

        Cursor values = db.rawQuery( sql , null );

        ArrayList<Transaction> arrayList = new ArrayList<>();

        while (values.moveToNext()){
            Transaction transaction = new Transaction();
            CategoryModel category = new CategoryModel();

            transaction.setId( values.getInt( values.getColumnIndexOrThrow( DBConfig.Transactions.COLUMN_NAME_ID ) ) );
            transaction.setAmount( values.getDouble( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_AMOUNT )));
            transaction.setDescription( values.getString( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_DESCRIPTION )));
            transaction.setAccountId( values.getInt( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_ACCOUNT_ID )));

            Date datex = null;
            String date = values.getString( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_DATE ));
            SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                datex = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            transaction.setDate( new SimpleDateFormat("dd-MM-yyyy").format(datex ));

            category.setID( values.getInt( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ID )));
            category.setName( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_CNAME )));
            category.setType( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_TYPE )));
            category.setIcon( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ICON )));

            transaction.setCategoryModel(category);
            arrayList.add(transaction);

        }

        return arrayList;
    }


    public ArrayList<Transaction> transactionGroupByCategories( String from , String to ){
        SQLiteDatabase db = getReadableDatabase();
        to = Util.addDays( to , 1);
        try {
            from = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("dd-MM-yyyy").parse(from) );
            to = new SimpleDateFormat("yyyy-MM-dd").format( new SimpleDateFormat("dd-MM-yyyy").parse(to) );

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sql = "SELECT "+DBConfig.Categories.TABLE_NAME+".* , SUM(transactions.Amount) AS total FROM " + DBConfig.Transactions.TABLE_NAME + " , " + DBConfig.Categories.TABLE_NAME +
                " WHERE " + DBConfig.Transactions.TABLE_NAME + ".CID = " + DBConfig.Categories.TABLE_NAME + ".CID AND "+
                " transactions.Date BETWEEN '"+from+"' AND  '"+to+"' GROUP BY " + DBConfig.Categories.TABLE_NAME + ".CID";

        Cursor values = db.rawQuery( sql , null );

        ArrayList<Transaction> arrayList = new ArrayList<>();

        while (values.moveToNext()){
            Transaction transaction = new Transaction();
            CategoryModel category = new CategoryModel();

            transaction.setAmount( values.getDouble( values.getColumnIndexOrThrow( "total" )));

            category.setID( values.getInt( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ID )));
            category.setName( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_CNAME )));
            category.setType( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_TYPE )));
            category.setIcon( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ICON )));


            transaction.setCategoryModel(category);
            arrayList.add(transaction);

        }

        return arrayList;
    }


    public boolean updateTransaction(Transaction transaction ){

        Date date = null;
        try {
            date = new SimpleDateFormat("EEEE , dd MMMM yyyy").parse(transaction.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put( DBConfig.Transactions.Column_NAME_AMOUNT , transaction.getAmount() );
        values.put( DBConfig.Transactions.Column_NAME_DESCRIPTION , transaction.getDescription());
        values.put( DBConfig.Transactions.Column_NAME_DATE , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) );
        values.put( DBConfig.Transactions.Column_NAME_CATEGORY_ID , transaction.getCategoryModel().getID() );
        values.put( DBConfig.Transactions.Column_NAME_ACCOUNT_ID , transaction.getAccountId() );

        String selection = DBConfig.Transactions.COLUMN_NAME_ID + " = ? ";
        String args[] = { String.valueOf( transaction.getId() ) };

        long result =  db.update( DBConfig.Transactions.TABLE_NAME , values , selection , args);
        if( result > 0){
            return true;
        }else{
            return false;
        }
    }
    public boolean deleteTransaction( int id ){
        SQLiteDatabase db = getReadableDatabase();
        String selection = DBConfig.Transactions.COLUMN_NAME_ID + " = ?";
        String Args[] = { String.valueOf( id ) };
        long result = db.delete( DBConfig.Transactions.TABLE_NAME , selection , Args );
        if( result > 0 ){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteAllTransactionInAccount( int AID ){
        SQLiteDatabase db = getReadableDatabase();
        String selection = DBConfig.Transactions.Column_NAME_ACCOUNT_ID + " = ?";
        String Args[] = { String.valueOf( AID ) };
        long result = db.delete( DBConfig.Transactions.TABLE_NAME , selection , Args );
        if( result > 0 ){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteAllTransactionInCategory( String CID ){
        SQLiteDatabase db = getReadableDatabase();
        String selection = DBConfig.Transactions.Column_NAME_CATEGORY_ID + " = ?";
        String Args[] = { CID  };
        long result = db.delete( DBConfig.Transactions.TABLE_NAME , selection , Args );
        if( result > 0 ){
            return true;
        }else{
            return false;
        }
    }



    //----------------------------------------------Padula Guruge ------------------------------------------------------

    //add account
    public boolean addAccount(AccountModel accountModel){
        SQLiteDatabase db = getWritableDatabase();   //get writable db
        ContentValues contentValues = new ContentValues();

        contentValues.put( DBConfig.Accounts.COLUMN_NAME_ANAME, accountModel.getAccountName());
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_TYPE, accountModel.getAccountType());
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_AMOUNT, accountModel.getAmount());
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_NUMBER, accountModel.getAccountNumber());
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_DESCRIPTION, accountModel.getAccountDescription());
        long result = db.insert( DBConfig.Accounts.TABLE_NAME ,null, contentValues );

        if (result > 0){

            return true;
        }
        else{
            return false;
        }
    }
    //edit account

    public boolean updateAccount(AccountModel accountModel){
        SQLiteDatabase db = getReadableDatabase();  //get readable db for update
        ContentValues contentValues = new ContentValues();

        contentValues.put( DBConfig.Accounts.COLUMN_NAME_ANAME, accountModel.getAccountName());
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_TYPE, accountModel.getAccountType());
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_DESCRIPTION, accountModel.getAccountDescription());

        String selection = DBConfig.Accounts.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { String.valueOf( accountModel.getId() ) };

        long result = db.update( DBConfig.Accounts.TABLE_NAME , contentValues , selection , selectionArgs);

        if (result > 0){

            return true;
        }
        else{
            return false;
        }
    }


    //view all accounts

    public ArrayList<AccountModel> readAllAccounts(){
        SQLiteDatabase db = getReadableDatabase(); //get readable db for update

        String[] projection = { DBConfig.Accounts.COLUMN_NAME_ID , DBConfig.Accounts.COLUMN_NAME_ANAME , DBConfig.Accounts.COLUMN_NAME_DESCRIPTION
        , DBConfig.Accounts.COLUMN_NAME_AMOUNT , DBConfig.Accounts.COLUMN_NAME_NUMBER , DBConfig.Accounts.COLUMN_NAME_TYPE };

        ArrayList<AccountModel> arrayList = new ArrayList<>();

        Cursor values = db.query(DBConfig.Accounts.TABLE_NAME , projection , null, null , null ,null , null  );
        while(values.moveToNext() ){

            AccountModel account = new AccountModel();
                account.setId( values.getInt( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_ID )));
                account.setAccountName( values.getString( values.getColumnIndexOrThrow( DBConfig.Accounts.COLUMN_NAME_ANAME )));
                account.setAccountDescription( values.getString( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_DESCRIPTION )));
                account.setAccountType( values.getString( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_TYPE )));
                account.setAccountNumber( values.getString( values.getColumnIndexOrThrow( DBConfig.Accounts.COLUMN_NAME_NUMBER )));
                account.setAmount( values.getDouble( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_AMOUNT )));

            arrayList.add(account);

        }

        return arrayList;
    }
// view all account with balance
    public ArrayList<AccountModel> readAllAccountsWithBalance(){
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT A." + DBConfig.Accounts.COLUMN_NAME_ID + " , A." + DBConfig.Accounts.COLUMN_NAME_ANAME + " , A." +
                     DBConfig.Accounts.COLUMN_NAME_TYPE + " , A." + DBConfig.Accounts.COLUMN_NAME_AMOUNT + " , A." +
                     DBConfig.Accounts.COLUMN_NAME_DESCRIPTION + " , A." + DBConfig.Accounts.COLUMN_NAME_NUMBER + " , " +
                    " SUM( CASE WHEN C.type = 'Income' THEN T.Amount ELSE -1 * T.Amount END) AS balance " +
                     "FROM  accounts A LEFT OUTER JOIN transactions T  ON  A.AID = T.AID  LEFT OUTER JOIN categories C ON C.CID = T.CID " +
                     " GROUP BY A.AID";

//balance will calculate by using transactions , accounts, categories tables
// case - if the trans  income then add and if it expns then deducted
        ArrayList<AccountModel> arrayList = new ArrayList<>();

        Cursor values = db.rawQuery( sql , null );
        while(values.moveToNext() ){

            AccountModel account = new AccountModel();
            account.setId( values.getInt( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_ID )));
            account.setAccountName( values.getString( values.getColumnIndexOrThrow( DBConfig.Accounts.COLUMN_NAME_ANAME )));
            account.setAccountDescription( values.getString( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_DESCRIPTION )));
            account.setAccountType( values.getString( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_TYPE )));
            account.setAccountNumber( values.getString( values.getColumnIndexOrThrow( DBConfig.Accounts.COLUMN_NAME_NUMBER )));
            account.setAmount( values.getDouble( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_AMOUNT )));
            account.setBalance( values.getDouble( values.getColumnIndexOrThrow(DBConfig.Accounts.COLUMN_NAME_AMOUNT )) + values.getDouble( values.getColumnIndexOrThrow( "balance")) );

            arrayList.add(account);


        }

        return arrayList;
    }
//delete account
    public boolean deleteAccount( int id ){
        SQLiteDatabase db = getReadableDatabase();
        String selection = DBConfig.Accounts.COLUMN_NAME_ID + " = ?";
        String Args[] = { String.valueOf( id ) };
        long result = db.delete( DBConfig.Accounts.TABLE_NAME , selection , Args );
        if( result > 0 ){
            return true;
        }else{
            return false;
        }
    }
//get lastes  transactoisn
    public ArrayList<Transaction>  AccountlatestTransactions( String AID ){
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DBConfig.Transactions.TABLE_NAME + " , " + DBConfig.Categories.TABLE_NAME +
                " WHERE " + DBConfig.Transactions.TABLE_NAME + ".CID = " + DBConfig.Categories.TABLE_NAME + ".CID AND "+
                DBConfig.Transactions.TABLE_NAME + ".AID = " + AID + " ORDER BY "+ DBConfig.Transactions.TABLE_NAME
                +"."+DBConfig.Transactions.Column_NAME_DATE +" DESC LIMIT 5";

        Cursor values = db.rawQuery( sql , null );

        ArrayList<Transaction> arrayList = new ArrayList<>();

        while (values.moveToNext()){
            try {
                Transaction transaction = new Transaction();
                CategoryModel category = new CategoryModel();

                transaction.setId( values.getInt( values.getColumnIndexOrThrow( DBConfig.Transactions.COLUMN_NAME_ID ) ) );
                transaction.setAmount( values.getDouble( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_AMOUNT )));
                transaction.setDescription( values.getString( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_DESCRIPTION )));
                transaction.setAccountId( values.getInt( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_ACCOUNT_ID )));

                Date datex = null;
                String date = values.getString( values.getColumnIndexOrThrow( DBConfig.Transactions.Column_NAME_DATE ));
                SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                datex = formatter.parse(date);


                transaction.setDate( new SimpleDateFormat("dd-MM-yyyy").format(datex ));
                category.setID( values.getInt( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ID )));
                category.setName( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_CNAME )));
                category.setType( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_TYPE )));
                category.setIcon( values.getString( values.getColumnIndexOrThrow( DBConfig.Categories.COLUMN_NAME_ICON )));

                transaction.setCategoryModel(category);
                arrayList.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }

    //-------------------------------Pubudu Arosha----------------------------------------------------------------
    //add account
    public boolean addSaving(SavingModel savingModel ){
        SQLiteDatabase db = getWritableDatabase();   //get writable db
        ContentValues contentValues = new ContentValues();

        contentValues.put( DBConfig.Savings.COLUMN_NAME_SAVINGNAME  , savingModel.getSavingName()  );
        contentValues.put( DBConfig.Savings.COLUMN_NAME_SAVINGDISCRIPTION , savingModel.getSavingDescription()  );
        contentValues.put( DBConfig.Savings.COLUMN_NAME_STARTAMOUNT , savingModel.getStartAmount()  );
        contentValues.put( DBConfig.Savings.COLUMN_NAME_TARGETAMOUNT , savingModel.getTargetAmount()  );

        long result = db.insert( DBConfig.Savings.TABLE_NAME ,null, contentValues );

        if (result > 0){

            return true;
        }
        else{
            return false;
        }
    }

    public boolean addSavingTransaction( int SID , int TID ){
        SQLiteDatabase db = getWritableDatabase();   //get writable db
        ContentValues contentValues = new ContentValues();

        contentValues.put( DBConfig.SavingsTransaction.COLUMN_NAME_SID  ,  SID  );
        contentValues.put( DBConfig.SavingsTransaction.COLUMN_NAME_TID , TID  );

        long result = db.insert( DBConfig.SavingsTransaction.TABLE_NAME  ,null, contentValues );

        if (result > 0){

            return true;
        }
        else{
            return false;
        }
    }



    public ArrayList<SavingModel>  readAllSavingsWithAmount(){
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT ST." +  DBConfig.Savings.COLUMN_NAME_ID  + " , ST." + DBConfig.Savings.COLUMN_NAME_SAVINGNAME + " , ST." +
                DBConfig.Savings.COLUMN_NAME_SAVINGDISCRIPTION + " , ST." + DBConfig.Savings.COLUMN_NAME_TARGETAMOUNT + " , ST." +
                DBConfig.Savings.COLUMN_NAME_STARTAMOUNT + " , " +
                " SUM( T.Amount ) AS balance " +
                "FROM savings ST LEFT OUTER JOIN  savingsTrans S ON  ST.SID = S.SID  LEFT OUTER JOIN transactions T ON S.TID = T.TID " +
                " GROUP BY S.SID";


        ArrayList<SavingModel> arrayList = new ArrayList<>();
        Cursor values = db.rawQuery( sql , null );

        while(values.moveToNext() ){
            SavingModel saving = new SavingModel();
            saving.setID( values.getInt( values.getColumnIndexOrThrow( DBConfig.Savings.COLUMN_NAME_ID )));
            saving.setSavingName( values.getString( values.getColumnIndexOrThrow( DBConfig.Savings.COLUMN_NAME_SAVINGNAME )));
            saving.setSavingDescription( values.getString( values.getColumnIndexOrThrow( DBConfig.Savings.COLUMN_NAME_SAVINGDISCRIPTION )));
            saving.setTargetAmount( values.getDouble( values.getColumnIndexOrThrow( DBConfig.Savings.COLUMN_NAME_TARGETAMOUNT )));
            saving.setStartAmount( values.getDouble( values.getColumnIndexOrThrow( DBConfig.Savings.COLUMN_NAME_STARTAMOUNT )));
            saving.setCurrentAmount( values.getDouble( values.getColumnIndexOrThrow("balance")));

            arrayList.add(saving);

        }

        return arrayList;
    }

    public boolean updateSaving(SavingModel savingModel){
        SQLiteDatabase db = getReadableDatabase();  //get readable db for update
        ContentValues contentValues = new ContentValues();

        contentValues.put( DBConfig.Savings.COLUMN_NAME_SAVINGNAME, savingModel.getSavingName());
        contentValues.put( DBConfig.Savings.COLUMN_NAME_SAVINGDISCRIPTION, savingModel.getSavingName());
        contentValues.put( DBConfig.Savings.COLUMN_NAME_TARGETAMOUNT, savingModel.getSavingName());


        String selection = DBConfig.Savings.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { String.valueOf( savingModel.getID() ) };

        long result = db.update( DBConfig.Savings.TABLE_NAME , contentValues , selection , selectionArgs);

        if (result > 0){

            return true;
        }
        else{
            return false;
        }
    }

    public boolean deleteSaving( int id ){
        SQLiteDatabase db = getReadableDatabase();

        String selection = DBConfig.Savings.COLUMN_NAME_ID + " = ?";

        String Args[] = { String.valueOf( id ) };
        long result = db.delete( DBConfig.Savings.TABLE_NAME , selection , Args );
        if( result > 0 ){
            return true;
        }else{
            return false;
        }
    }



    //-------------------------------default values---------------------------------------------------------------
    public void setDefaultCategories(SQLiteDatabase db){
        ArrayList<CategoryModel> categories = new ArrayList<>();
        categories.add( new CategoryModel( "Transportation" , "Transportation expenses are specific costs incurred by an employee or self-employed taxpayer while traveling away from home for business purposes. Transportation expenses are a subset of travel expenses, which include all of the costs associated with business travel, such as taxi fare, fuel, parking fees, lodging, meals, tips, and cleaning." , "Expense" , "bus"    ));
        categories.add( new CategoryModel( "Bills and Utilities" , "An expense or an expenditure is an amount owed to someone else, be it for services, products, or any other goods. For people who don’t run a business, expenses don’t mean anything more than that, at least in most cases. However, for business owners and self-employed people, some business spending impacts the amount of tax owed to the state." , "Expense" , "bill"    ));
        categories.add( new CategoryModel( "Investments" , "Investment  is a component of your taxable expenses — it expenses added in with your income from employment and other sources, such as a pension. You can earn investment expenses from a variety of sources, including stocks, bonds and earnings on a life insurance policy." , "Expense" , "invest"    ));
        categories.add( new CategoryModel( "Food and Beverages" , "Monthly food costs are determined by taking a monthly physical inventory of food stock, evaluating the inventory, and then adjusting the valuation to more accurately reflect the cost of food consumed." , "Expense" , "food"    ));
        categories.add( new CategoryModel( "Health and Fitness" , "Medical expenses are any costs incurred in the prevention or treatment of injury or disease. Medical expenses include health and dental insurance premiums, doctor and hospital visits, co-pays, prescription" , "Expense" , "health"    ));
        categories.add( new CategoryModel( "Family" , "Home expenses. In addition to the cost of the housing, whether it is rent, a mortgage payment, or real estate taxes, fees for utilities such as electricity and gas as well as insurance for the property are also part of household expenses." , "Expense" , "family"    ));
        categories.add( new CategoryModel( "Entertainment" , "Home expenses. In addition to the cost of the housing, whether it is rent, a mortgage payment, or real estate taxes, fees for utilities such as electricity and gas as well as insurance for the property are also part of household expenses." , "Expense" , "entertainment"    ));
        categories.add( new CategoryModel( "Gifts and Donations" , "Giving gifts is a great way to show your appreciation for special clients during the holidays, You can also give a gift that qualifies as an entertainment expense " , "Expense" , "gift"    ));
        categories.add( new CategoryModel( "Education" , "Qualified expenses are amounts paid for tuition, fees and other related expense for an eligible student that are required for enrollment or attendance at an eligible educational institution." , "Expense" , "education"    ));
        categories.add( new CategoryModel( "Savings" , "The definition of fixed expenses is any expense that does not change from period to period,such as mortgage or rent payments, utility bills, and loan payments. " , "Expense" , "savings"    ));

        categories.add( new CategoryModel( "Awards" , "The incomes for the wallet when receiving gifts and awards from relatives and friends especially the cash awards other than non cash awards, you can add those cash awards and calculate the expense." , "Income" , "award"    ));
        categories.add( new CategoryModel( "Selling" , "Selling stuff on the internet or some where else might get an income, so adding them to the daily income lets you calculate the rest of the money left " , "Income" , "sell"    ));
        categories.add( new CategoryModel( "Interest Money" , "With the interest money the amount is getting increased as the interest money being added to the expense amount,Interest money method is a huge method in increasing the income " , "Income" , "interest"    ));
        categories.add( new CategoryModel( "Gifts" , "The incomes for the wallet when receiving gifts and awards from relatives and friends especially the cash awards and calculate the expense." , "Income" , "get"    ));
        categories.add( new CategoryModel( "Salary" , "If you are paid an annual salary, the calculation is fairly easy. Since gross income refers to the total amount you earn before tax, and so does your annual salary, simply take the total amount of money (salary) you're paid for the year, and then divide this amount by 12.10" , "Income" , "salary"    ));

        db.beginTransaction();
        try {
            for (CategoryModel item : categories) {
                ContentValues contentValues = new ContentValues();
                contentValues.put( DBConfig.Categories.COLUMN_NAME_CNAME , item.getName() );
                contentValues.put( DBConfig.Categories.COLUMN_NAME_DESCRIPTION , item.getDescription() );
                contentValues.put( DBConfig.Categories.COLUMN_NAME_TYPE , item.getType() );
                contentValues.put( DBConfig.Categories.COLUMN_NAME_ICON , item.getIcon() );
                db.insert( DBConfig.Categories.TABLE_NAME ,null, contentValues );
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.i( "DB" , "Default Categories Created" );
    }

    public void setDefaultAccount( SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_ANAME , "Wallet"  );
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_TYPE , "Wallet" );
        contentValues.put( DBConfig.Accounts.COLUMN_NAME_AMOUNT , 0 );

        db.insert( DBConfig.Accounts.TABLE_NAME ,null, contentValues );
    }



}
