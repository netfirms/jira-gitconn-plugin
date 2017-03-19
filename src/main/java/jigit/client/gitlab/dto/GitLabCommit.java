package jigit.client.gitlab.dto;

import jigit.client.gitlab.GitLab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public final class GitLabCommit {
    @NotNull
    private final String id;
    @NotNull
    private final String message;
    @NotNull
    private final String author_name;
    @NotNull
    private final String created_at;
    @Nullable
    private final List<String> parent_ids;

    public GitLabCommit(@NotNull String id,
                        @NotNull String message,
                        @NotNull String author_name,
                        @NotNull String created_at,
                        @Nullable List<String> parent_ids) {
        this.id = id;
        this.message = message;
        this.author_name = author_name;
        this.created_at = created_at;
        this.parent_ids = parent_ids;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public String getAuthorName() {
        return author_name;
    }

    @NotNull
    public Date getCreatedAt() {
        return GitLab.parseDate(created_at);
    }

    @Nullable
    public List<String> getParentIds() {
        return parent_ids;
    }
}
