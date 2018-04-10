package com.glacion.githubsearcher.recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glacion.githubsearcher.R;

import java.util.List;

/**
 * Our adapter for the RecyclerView
 */
public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {

    final private List<Repo> repoList;

    public RepoAdapter(List<Repo> repoList) {
        this.repoList = repoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the specified layout file for Views in RecyclerView.
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.repo_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Get the data from the list to apply in the Views.
     * Modify this should you want to add more attributes to the each view.
     * OnClick actions can be performed here as well.
     * @param holder A single item on the RecyclerView.
     * @param position Position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Repo repo = repoList.get(position);
        holder.fullName.setText(repo.getFullName());
        holder.description.setText(repo.getDescription());
        holder.forks.setText(String.valueOf(repo.getForks()));
        holder.stars.setText(String.valueOf(repo.getStars()));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRepo(repo.getUrl(), v.getContext());
            }
        };
        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return repoList.size();
    }

    /**
     * Inner class for the ViewHolders of the RecyclerView.
     * Modify the constructor when you'll add new attributes to your views.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView fullName;
        final TextView description;
        final TextView forks;
        final TextView stars;
        ViewHolder(View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.full_name);
            description = itemView.findViewById(R.id.description);
            forks = itemView.findViewById(R.id.forks);
            stars = itemView.findViewById(R.id.stars);
        }
    }

    private void openRepo (String url, Context context) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
