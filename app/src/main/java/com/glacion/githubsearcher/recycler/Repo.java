package com.glacion.githubsearcher.recycler;

/**
 * This object will be used for the ViewHolders on the RecyclerView.
 */
public class Repo {
    private String fullName;

    public Repo(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
