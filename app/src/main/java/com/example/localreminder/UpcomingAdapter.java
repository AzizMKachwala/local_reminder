package com.example.localreminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingDataViewHolder> {

    private ArrayList<MyDbDataModel> dataModelList;
    MyDataBaseHandler myDataBaseHandler;
    private OnEditItemClickListener editItemClickListener;
    private OnDeleteItemClickListener deleteItemClickListener;

    public UpcomingAdapter(ArrayList<MyDbDataModel> dataModelList, OnEditItemClickListener editItemClickListener, OnDeleteItemClickListener deleteItemClickListener) {
        this.dataModelList = dataModelList;
        this.editItemClickListener = editItemClickListener;
        this.deleteItemClickListener = deleteItemClickListener;
    }

    public interface OnEditItemClickListener {
        void onEditClick(int position);
    }

    public interface OnDeleteItemClickListener {
        void onDeleteClick(int position);
    }

    public void updateData(ArrayList<MyDbDataModel> newDataModelArrayList) {
        dataModelList.clear();
        dataModelList.addAll(newDataModelArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpcomingDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.upcoming_list_item, parent, false);
        return new UpcomingDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingDataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        myDataBaseHandler=new MyDataBaseHandler(holder.itemView.getContext());
        MyDbDataModel myDbDataModel = dataModelList.get(position);
        holder.txtDateUF.setText(myDbDataModel.getDate());
        holder.txtTimeUF.setText(myDbDataModel.getTime());
        holder.txtDescUF.setText(myDbDataModel.getDescription());

        holder.imgEditUF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editItemClickListener != null) {
                    editItemClickListener.onEditClick(position);
                }
            }
        });

        holder.imgDeleteUF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteItemClickListener != null) {
                    deleteItemClickListener.onDeleteClick(position);
                }
            }
        });

        holder.imgDoneUF.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Mark as Done?");
            builder.setMessage("Do you want to Mark " + myDbDataModel.getDescription() + "Item as Completed?");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", (dialogInterface, which) -> {
                myDataBaseHandler.insertDataCompleted(myDbDataModel.getDate(), myDbDataModel.getTime(), myDbDataModel.getDescription());
                myDataBaseHandler.deleteData(myDbDataModel.getId());
                dataModelList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, dataModelList.size());
                Toast.makeText(view.getContext(), "Item Marked as Completed", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    public static class UpcomingDataViewHolder extends RecyclerView.ViewHolder {

        TextView txtDateUF, txtTimeUF, txtDescUF;
        ImageView imgDoneUF, imgEditUF, imgDeleteUF;

        public UpcomingDataViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDateUF = itemView.findViewById(R.id.txtDateUF);
            txtTimeUF = itemView.findViewById(R.id.txtTimeUF);
            txtDescUF = itemView.findViewById(R.id.txtDescUF);
            imgDoneUF = itemView.findViewById(R.id.imgDoneUF);
            imgEditUF = itemView.findViewById(R.id.imgEditUF);
            imgDeleteUF = itemView.findViewById(R.id.imgDeleteUF);

        }
    }
}
