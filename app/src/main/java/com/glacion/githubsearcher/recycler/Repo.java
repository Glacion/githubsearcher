package com.glacion.githubsearcher.recycler;

/**
 * This object will be used for the ViewHolders on the RecyclerView.
 */
public class Repo {
    final private String fullName;
    final private String description;
    final private String url;
    final private int forks;
    final private int stars;

    public Repo(String fullName, String description, String url, int forks, int stars) {
        this.fullName = fullName;
        this.description = description;
        this.url = url;
        this.forks = forks;
        this.stars = stars;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public int getForks() {
        return forks;
    }

    public int getStars() {
        return stars;
    }
}
