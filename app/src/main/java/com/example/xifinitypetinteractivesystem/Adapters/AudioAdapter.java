package com.example.xifinitypetinteractivesystem.Adapters;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xifinitypetinteractivesystem.R;

import java.util.ArrayList;


public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> implements Filterable{

    private ArrayList<DataModel> audiofiles;
    private final OnItemClickListener onItemClickListener;
    private final ArrayList<DataModel> filteredList;
    private SearchFilter searchFilter;




    public AudioAdapter(ArrayList<DataModel> audiofiles, OnItemClickListener onItemClickListener)
    {
        this.audiofiles = audiofiles;
        filteredList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commands_layout,parent,false);

        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {

        holder.title.setText(audiofiles.get(position).getTitle());
        holder.pos = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return audiofiles.size();
    }

    @Override
    public Filter getFilter() {
        if(searchFilter ==null)
        {
            filteredList.clear();
            filteredList.addAll(this.audiofiles);
            searchFilter = new SearchFilter(filteredList);
        }
        return searchFilter;
    }


    class SearchFilter extends Filter{
        private final ArrayList<DataModel> listFilter;

        public SearchFilter(ArrayList<DataModel> listFilter) {
            this.listFilter = listFilter;
        }

        void updateList(DataModel dataModel)
        {
            listFilter.remove(dataModel);
        }



        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String seach_string = charSequence.toString().toLowerCase().trim();

            FilterResults results = new FilterResults();

            if(TextUtils.isEmpty(charSequence))
            {
                results.values = listFilter;
            }
            else
            {
                ArrayList<DataModel> filtered = new ArrayList<>();

                for(DataModel d :listFilter)
                {
                    if(d.getTitle().toLowerCase().contains(seach_string))
                    {
                        filtered.add(d);
                    }
                }
                results.values = filtered;
            }
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                audiofiles = (ArrayList<DataModel>) filterResults.values;
                notifyDataSetChanged();

        }
    }
    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        int pos;
        ImageButton delete;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_audio);
            delete = itemView.findViewById(R.id.delete_command);
            itemView.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

           if(view.getId()==R.id.delete_command)
           {
               final int pos = getAdapterPosition();
               DataModel dataModel = audiofiles.get(pos);

               onItemClickListener.OnItemDeleted(view,pos,dataModel);
               if(dataModel.getFile().exists())
               {
                   dataModel.getFile().delete();
               }

               audiofiles.remove(pos);
               notifyItemRemoved(pos);
               notifyDataSetChanged();

               if(searchFilter!=null)
               {
                   searchFilter.updateList(dataModel);
               }

           }
           else
           {
               final int pos = getAdapterPosition();
               DataModel dataModel = audiofiles.get(pos);


               onItemClickListener.OnItemSelected(dataModel);
           }


        }
    }

    public interface OnItemClickListener {
        void OnItemSelected(DataModel dataModel);

        void OnItemDeleted(View view, int pos, DataModel dataModel);
    }

    public void removeItem(int position,DataModel dataModel) {
        audiofiles.remove(position);
        notifyItemRemoved(position);

        if(searchFilter!=null)
        {
            searchFilter.updateList(dataModel);
        }
    }


}
