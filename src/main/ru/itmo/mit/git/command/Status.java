package ru.itmo.mit.git.command;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.dataHolders.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static ru.itmo.mit.git.dataHolders.GitStorage.getHashByPath;

public class Status extends AbstractCommand {
    GitStatus gitStatus;

    public Status(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(0);
    }

    @Override
    protected void execute() throws GitException {
        gitStatus = new GitStatus(storage.getHeadBranch());

        HashMap<String, IndexInfo> indexMap = storage.getIndexMap();
        CommitHolder headCommit = storage.getHeadCommit();
        try {
            storage.workingDirectoriesWalk().
                    filter(path -> !path.equals(storage.workingDirectory) && !path.startsWith(storage.gitDirectory)).
                    map(storage::relativeFileName).
                    filter(relative -> headCommit == null || !headCommit.files.containsKey(relative)).
                    forEach(relative -> {
                        if ((headCommit == null || !headCommit.files.containsKey(relative)) && !indexMap.containsKey(relative)) {
                            gitStatus.untrackedNew.add(relative);
                        } else {
                            IndexInfo indexInfo = indexMap.get(relative);

                            if (indexInfo.status.equals(IndexStatus.ADDED)) {
                                gitStatus.added.add(relative);
                            } else if (indexInfo.status.equals(IndexStatus.REMOVED)) {
                                gitStatus.removed.add(relative);
                            } else if (indexInfo.status.equals(IndexStatus.MODIFIED)) {
                                gitStatus.modified.add(relative);
                            }
                        }
                    });
            if (headCommit != null) {
                headCommit.files.forEach((relative, info) -> {
                    try {
                        Path path = storage.getAbsolutePath(relative);
                        if (!storage.fileExists(path)) {
                            gitStatus.untrackedRemoved.add(relative);
                        } else if (!info.hash.equals(getHashByPath(path))) {
                            gitStatus.untrackedModified.add(relative);
                        }
                    } catch (GitException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new GitException(e);
        }
    }


    @Override
    protected void printResults() {
        if (gitStatus.branch != null) {
            out.println("Current branch is '" + gitStatus.branch + "'");
        } else {
            out.println("Error while performing status: Head is detached");
            return;
        }

        boolean untrackedEmpty = gitStatus.untrackedNew.isEmpty() &&
                gitStatus.untrackedRemoved.isEmpty() && gitStatus.untrackedModified.isEmpty();

        if (!(gitStatus.modified.isEmpty() && gitStatus.removed.isEmpty() && gitStatus.added.isEmpty())) {
            out.println("Ready to commit:");
            out.println();

            printByStatus(gitStatus.added, "New files:");

            printByStatus(gitStatus.modified, "Modified files:");

            printByStatus(gitStatus.removed, "Deleted files:");
        } else if (untrackedEmpty) {
            out.println("Everything up to date");
            return;
        }

        if (!untrackedEmpty) {
            out.println("Untracked files:");
            out.println();
            printByStatus(gitStatus.untrackedNew, "New files:");
            printByStatus(gitStatus.untrackedModified, "Modified files:");
            printByStatus(gitStatus.untrackedRemoved, "Removed files:");
        }
    }

    private void printByStatus(List<String> status, String category) {
        final String tab = "    ";

        if (!status.isEmpty()) {
            out.println(category);
            for (String file : status) {
                out.println(tab + file);
            }
            out.println();
        }
    }


}
