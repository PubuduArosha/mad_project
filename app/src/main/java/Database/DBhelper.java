package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.Category;

import java.util.ArrayList;

import Models.AccountModel;
import Models.CategoryModel;
import Models.Transaction;

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


        db.execSQL(sql_ct_categories);
        db.execSQL(sql_ct_accounts);
        db.execSQL(sql_ct_transaction);
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

    //-----------------------------------------------Amoda Sasmitha-------------------------------------------------------

    public ArrayList<Transaction> readAllTransactions( String from , String to , String accout ){


        return null;
    }

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

    //----------------------------------------------Padula Guruge ------------------------------------------------------

    public boolean addAccount(AccountModel accountModel){
        SQLiteDatabase db = getWritableDatabase();
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

    public ArrayList<AccountModel> readAllAccounts(){
        SQLiteDatabase db = getReadableDatabase();

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
                Log.i( "DB" , "-----------------------------------------------------  " );
                Log.i( "DB" , account.getId()+"" );
                Log.i( "DB" , account.getAccountName() );
                Log.i( "DB" , account.getAccountDescription()+"" );
                Log.i( "DB" , account.getAccountType()+"" );
                Log.i( "DB" , account.getAccountNumber()+"" );
                Log.i( "DB" , account.getAmount()+"" );

        }

        return arrayList;
    }

}
