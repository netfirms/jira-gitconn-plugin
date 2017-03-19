package jigit.indexer.api.github;

import jigit.client.github.GitHubRepositoryAPI;
import jigit.client.github.dto.GitHubBranch;
import jigit.client.github.dto.GitHubCommit;
import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.RequestsCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class GithubAPIAdapter implements APIAdapter {
    @NotNull
    private final GitHubRepositoryAPI repository;
    @NotNull
    private final RequestsCounter requestsCounter = new RequestsCounter();

    public GithubAPIAdapter(@NotNull GitHubRepositoryAPI repository) {
        this.repository = repository;
    }

    @NotNull
    @Override
    public CommitAdapter getCommit(@NotNull String commitSha1) throws IOException {
        final GitHubCommit commit = repository.getCommit(commitSha1);
        if (commit == null) {
            throw new IllegalStateException("Something goes wrong. Got null commit for sha1 = " + commitSha1);
        }
        final GithubCommitAdapter commitAdapter = new GithubCommitAdapter(commit);
        requestsCounter.increase();
        return commitAdapter;
    }

    @Nullable
    @Override
    public String getHeadCommitSha1(@NotNull String branch) throws IOException {
        final GitHubBranch gitHubBranch = repository.getBranch(branch);
        requestsCounter.increase();
        if (gitHubBranch == null) {
            return null;
        }
        return gitHubBranch.getSha();
    }

    @Override
    public long getRequestsQuantity() {
        return requestsCounter.value();
    }
}
