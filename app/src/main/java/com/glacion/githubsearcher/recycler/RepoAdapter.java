package com.glacion.githubsearcher.recycler;

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

    private List<Repo> repoList;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Repo repo = repoList.get(position);
        holder.fullName.setText(repo.getFullName());
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
        TextView fullName;

        ViewHolder(View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.full_name);
        }
    }
}
