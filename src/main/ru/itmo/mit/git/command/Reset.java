package ru.itmo.mit.git.command;

import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;
import ru.itmo.mit.git.dataHolders.IndexInfo;

import java.io.IOException;
import java.util.HashMap;

public class Reset extends AbstractCommand {
    public Reset(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(1);
    }

    @Override
    protected void execute() throws GitException {
        reset(storage.getCommitHash(arguments.get(0)));
    }

    @Override
    protected void printResults() throws GitException {
        out.println("Reset successful");
    }

    public void reset(String commitHash) throws GitException {
        CommitHolder commit = storage.getCommit(commitHash);
        HashMap<String, IndexInfo> files = commit.files;

        CommitHolder headCommit = storage.getHeadCommit();

        // restore
        try {
            storage.workingDirectoriesWalk().
                    filter(path -> !path.equals(storage.workingDirectory) && !path.startsWith(storage.gitDirectory)).
                    map(storage::relativeFileName).
                    filter(relative -> files.containsKey(relative) || // only tracked
                            (headCommit != null && headCommit.files.containsKey(relative))).
                    forEach(relative -> {
                        try {
                            if (!files.containsKey(relative)) {
                                storage.deleteFile(storage.workingDirectory.resolve(relative));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            files.forEach((relative, info) -> {
                try{
                    storage.copyFileToWorkingDir(relative, files.get(relative).path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException | RuntimeException e) {
            throw new GitException(e);
        }


        // clear index
        storage.updateIndexMap(new HashMap<>());

        // change branch
        String branch = storage.getHeadBranch();
        if (branch != null) {
            HashMap<String, String> branches = storage.getBranches();
            branches.put(branch, commitHash);
            storage.updateBranches(branches);
        } else {
            // detached head
            storage.updateHead(commitHash);
        }
    }

}
