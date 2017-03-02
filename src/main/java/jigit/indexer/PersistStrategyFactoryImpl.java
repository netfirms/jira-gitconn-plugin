package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Collection;

public final class PersistStrategyFactoryImpl implements PersistStrategyFactory {
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final PersistStrategy persistAllDataStrategy;
    @NotNull
    private final PersistStrategy persistDependentDataOnlyStrategy;

    public PersistStrategyFactoryImpl(@NotNull CommitManager commitManager) {
        this.commitManager = commitManager;
        persistAllDataStrategy = new PersistAllDataStrategy();
        persistDependentDataOnlyStrategy = new PersistDependentDataOnlyStrategy();
    }

    @NotNull
    @Override
    public PersistStrategy getStrategy(@NotNull String repoName, @NotNull String commitSha1, boolean fetchedFirstTime) {
        final boolean commitAlreadyIndexed = commitManager.isExists(repoName, commitSha1);
        if (commitAlreadyIndexed && fetchedFirstTime) {
            return PersistStrategy.DO_NOTHING;
        }

        if (commitAlreadyIndexed) {
            return persistDependentDataOnlyStrategy;
        }

        return persistAllDataStrategy;
    }

    private final class PersistAllDataStrategy implements PersistStrategy {
        @NotNull
        @Override
        public Collection<String> persist(@NotNull String repoName,
                                          @NotNull String branch,
                                          @NotNull CommitAdapter commitAdapter,
                                          @NotNull Collection<String> issueKeys,
                                          @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException {
            commitManager.persist(commitAdapter, repoName, branch, issueKeys, commitFileAdapters);
            return commitAdapter.getParentSha1s();
        }
    }

    /*Strategy looks strange. But it needs to save dependent data again because persist method is not transactional.*/
    private final class PersistDependentDataOnlyStrategy implements PersistStrategy {
        @NotNull
        @Override
        public Collection<String> persist(@NotNull String repoName,
                                          @NotNull String branch,
                                          @NotNull CommitAdapter commitAdapter,
                                          @NotNull Collection<String> issueKeys,
                                          @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException {
            commitManager.persistDependent(commitAdapter, repoName, branch, issueKeys, commitFileAdapters);
            return commitAdapter.getParentSha1s();
        }
    }
}