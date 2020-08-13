package com.example.tokmanniexpirysystem2.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.entities.History;
import com.example.tokmanniexpirysystem2.utilities.TimeStampConverter;

import java.util.List;

public class HistoryRecyclerviewAdapter extends RecyclerView.Adapter<HistoryRecyclerviewAdapter.ViewHolder>{
    private List<History> historyList;
    private Context context;
    public HistoryRecyclerviewAdapter(Context context, List<History> dataArgs){
        historyList = dataArgs;
        this.context = context;
    }

    @Override
    public HistoryRecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_recyclerview_element, parent, false);
        HistoryRecyclerviewAdapter.ViewHolder viewHolder= new HistoryRecyclerviewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryRecyclerviewAdapter.ViewHolder holder, int position) {
        holder.historyDateTextView.setText(TimeStampConverter.dateToTimestamp(historyList.get(position).getModificationDate()));
        holder.historySummaryTextView.setText(historyList.get(position).getSummary());
        if(position%2 == 0) {
            holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_even_color));
        } else {
            holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_odd_color));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }




    /**
     * A Simple ViewHolder for the RecyclerView
     */
    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView historyDateTextView;
        public TextView historySummaryTextView;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            historyDateTextView = (TextView) itemView.findViewById(R.id.history_list_element_date);
            historySummaryTextView = (TextView) itemView.findViewById(R.id.history_list_element_summary);
        }
    }
}
