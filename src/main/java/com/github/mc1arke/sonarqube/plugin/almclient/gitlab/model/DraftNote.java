package com.github.mc1arke.sonarqube.plugin.almclient.gitlab.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DraftNote {

    private final long id;
    private final long authorId;

    @JsonCreator
    public DraftNote(@JsonProperty("id") long id, @JsonProperty("author_id") long authorId) {
        this.id = id;
        this.authorId = authorId;
    }

    public long getId() {
        return id;
    }

    public long getAuthorId() {
        return authorId;
    }

}
