package com.glacion.githubsearcher.recycler;

public class Repo {
    final private String fullName;
    final private String description;
    final private String url;
    final private int forks;
    final private int stars;

    /**
     * This object will be used for the ViewHolders on the RecyclerView.
     * @param fullName Full name of the repository (eg. Glacion/githubsearcher)
     * @param description Description for the repository. Defaults to "No Description Provided"
     *                    if the repository does not have a description.
     * @param url URL of the repository.
     * @param forks Number of how many this repo was forked.
     * @param stars Number of how many this repo was starred.
     */
    public Repo(String fullName, String description, String url, int forks, int stars) {
        this.fullName = fullName;
        // If the repo doesn't have a description, use default string.
        this.description = description.equals("null") ? "No Description Provided" : description;
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
