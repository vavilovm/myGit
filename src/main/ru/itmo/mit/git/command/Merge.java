package ru.itmo.mit.git.command;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.dataHolders.GitStorage;
import ru.itmo.mit.git.dataHolders.IndexInfo;
import ru.itmo.mit.git.dataHolders.IndexStatus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Merge extends AbstractCommand {
    public Merge(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(1);
    }

    @Override
    protected void execute() throws GitException {
        merge(arguments.get(0));
    }

    @Override
    protected void printResults() throws GitException {

    }

    public void merge(String branch) throws GitException {
        if (!storage.isBranch(branch)) {
            throw new GitException("It is not a branch");
        }
        String headBranch = storage.getHeadBranch();
        if (headBranch == null) {
            throw new GitException("Head is in detached state");
        }
        CommitHolder mergeCommit = storage.getCommit(storage.getBranches().get(branch));
        HashMap<String, IndexInfo> files = mergeCommit.files;

        // add changes from mergeCommit
        List<Path> paths = new ArrayList<>();
        files.forEach((relative, info) -> {
            try {
                if (files.containsKey(relative) && !info.status.equals(IndexStatus.REMOVED)) {
                    storage.copyFileToWorkingDir(relative, info.path);
                    paths.add(storage.getAbsolutePath(relative));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        new Add(storage).add(paths);

        new Commit(storage).createCommit("merged '" + headBranch + "' with '" + branch + "'", branch);
    }


}
