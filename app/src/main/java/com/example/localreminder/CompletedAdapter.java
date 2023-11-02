package com.example.localreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.CompletedDataViewHolder> {

    Context context;
    List<MyDbDataModel> completedDataModelList;
    MyDataBaseHandler myDataBaseHandler;
    MyDbDataModel myDbDataModel;

    public CompletedAdapter(Context context, List<MyDbDataModel> completedDataModelList) {
        this.context = context;
        this.completedDataModelList = completedDataModelList;
    }

    @NonNull
    @Override
    public CompletedDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.completed_list_item,parent,false);
        return new CompletedDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedDataViewHolder holder, int position) {
        myDbDataModel = completedDataModelList.get(position);
        holder.txtDateCF.setText(myDbDataModel.getDate());
        holder.txtTimeCF.setText(myDbDataModel.getTime());
        holder.txtDescCF.setText(myDbDataModel.getDescription());

        holder.imgDeleteCF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete Item?");
                builder.setMessage("Do you want to Delete this Item?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (dialogInterface, which) -> {

                    myDataBaseHandler= new MyDataBaseHandler(holder.itemView.getContext());
                    myDataBaseHandler.deleteDataCompleted(myDbDataModel.getId());
                    completedDataModelList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(view.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                });

                builder.setNegativeButton("No", (dialogInterface, which) ->
                        dialogInterface.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });
    }

    @Override
    public int getItemCount() {
        return completedDataModelList.size();
    }

    public static class CompletedDataViewHolder extends RecyclerView.ViewHolder{

        TextView txtDateCF,txtTimeCF,txtDescCF;
        ImageView imgDeleteCF;

        public CompletedDataViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDateCF = itemView.findViewById(R.id.txtDateCF);
            txtTimeCF = itemView.findViewById(R.id.txtTimeCF);
            txtDescCF = itemView.findViewById(R.id.txtDescCF);
            imgDeleteCF = itemView.findViewById(R.id.imgDeleteCF);

        }
    }
}
