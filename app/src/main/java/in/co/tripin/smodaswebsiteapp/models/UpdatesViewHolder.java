package in.co.tripin.smodaswebsiteapp.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.tripin.smodaswebsiteapp.R;


/**
 * Created by Shubham on 1/10/2018.
 */

public class UpdatesViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView title;
    public TextView description;
    public TextView time;

    public UpdatesViewHolder(View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.imageViewicon);
        title = itemView.findViewById(R.id.textViewtitle);
        description = itemView.findViewById(R.id.textViewDiscription);
        time = itemView.findViewById(R.id.textViewago);
    }
}
