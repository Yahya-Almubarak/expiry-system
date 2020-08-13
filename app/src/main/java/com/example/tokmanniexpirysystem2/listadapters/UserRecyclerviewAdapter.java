package com.example.tokmanniexpirysystem2.listadapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.utilities.LocalizedRoleConverter;
import com.example.tokmanniexpirysystem2.utilities.TimeStampConverter;

import java.util.List;

public class UserRecyclerviewAdapter extends RecyclerView.Adapter<UserRecyclerviewAdapter.ViewHolder> {
    private List<User> usersList;
    private Context context;
    public UserRecyclerviewAdapter(Context context, List<User> dataArgs){
        usersList = dataArgs;
        this.context = context;
    }

    @Override
    public UserRecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_recyclerview_element, parent, false);
        UserRecyclerviewAdapter.ViewHolder viewHolder= new UserRecyclerviewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserRecyclerviewAdapter.ViewHolder holder, int position) {
        holder.userFirstNameTextView.setText(usersList.get(position).getFirstName());
        holder.userLastNameTextView.setText(usersList.get(position).getLastName());
        holder.userRoleTextView.setText(LocalizedRoleConverter.getInstance(context).localizedStringFromRole(usersList.get(position).getRole()));
        holder.userCreationDateTextView.setText(TimeStampConverter.dateToTimestamp(usersList.get(position).getCreationDate()));
        if(position%2 == 0) {
            holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_even_color));
        } else {
            holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_odd_color));
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }




    /**
     * A Simple ViewHolder for the RecyclerView
     */
    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userFirstNameTextView;
        public TextView userLastNameTextView;
        public TextView userRoleTextView;
        public TextView userCreationDateTextView;
        public View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userFirstNameTextView = (TextView) itemView.findViewById(R.id.users_list_element_first_name);
            userLastNameTextView = (TextView) itemView.findViewById(R.id.users_list_element_last_name);
            userRoleTextView = (TextView) itemView.findViewById(R.id.users_list_element_role);
            userCreationDateTextView = (TextView) itemView.findViewById(R.id.users_list_element_creation_date);
            itemView.findViewById(R.id.users_list_element_delete_btn).setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.users_list_element_delete_btn){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.users_delete_alert_title));
                builder.setPositiveButton(context.getResources().getString(R.string.users_delete_alert_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = getAdapterPosition();
                        deleteUser(usersList.get(position));
                        usersList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, usersList.size());
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.users_delete_alert_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }

        }
        private void deleteUser(final User user) {
            class DeleteUser extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {
                    DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                            .userDao()
                            .delete(user);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Toast.makeText(context, context.getResources().getString(R.string.users_deleted_toast), Toast.LENGTH_LONG).show();

                }
            }

            DeleteUser du = new DeleteUser();
            du.execute();

        }
    }
}

