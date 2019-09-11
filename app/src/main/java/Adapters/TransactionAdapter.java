package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

import Models.Transaction;

class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>  {

    private ArrayList<Transaction> arrayList;
    private Context context;

    public TransactionAdapter(ArrayList<Transaction> arrayList , Context context ) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.expense_row , parent , false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        Transaction t = arrayList.get(position);
        int resID =    context.getResources().getIdentifier( t.getCategoryModel().getIcon() , "drawable", context.getPackageName());
        holder.category.setText( t.getCategoryModel().getName() );
        holder.description.setText( t.getDescription() );
        holder.amount.setText( String.valueOf(t.getAmount() ));
        holder.icon.setImageResource(resID);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView category , description , amount ;
        ImageView icon;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.type);
            amount = itemView.findViewById(R.id.amount);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
