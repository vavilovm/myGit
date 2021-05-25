package ru.itmo.mit.git.command;

import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;
import ru.itmo.mit.git.dataHolders.IndexInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class Checkout extends AbstractCommand {
    public Checkout(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsAtLeast1();
    }

    @Override
    protected void execute() throws GitException {
        String firstArg = arguments.get(0);
        if (firstArg.equals("--")) {
            for (int i = 1; i < arguments.size(); i++) {
                String argument = arguments.get(i);
                checkoutFile(argument);
            }
        } else if (storage.isBranch(firstArg)) {
            checkoutBranch(firstArg);
        } else {
            checkout(storage.getCommitHash(firstArg));
        }

    }

    @Override
    protected void printResults() throws GitException {
        out.println("Checkout completed successful");
    }

    public void checkoutBranch(String branch) throws GitException {
        CommitHolder headCommit = storage.getHeadCommit();
        storage.updateHead(headCommit.hash);

        new Reset(storage).reset(storage.getBranches().get(branch));
        storage.updateHead(branch);
    }


    public void checkout(String commitHash) throws GitException {
        if (!storage.getIndexMap().isEmpty()) {
            throw new GitException("Staging area must be empty");
        }
        // detach head
        storage.updateHead(storage.getHeadCommit().hash);
        new Reset(storage).reset(commitHash);
    }

    public void checkoutFile(String path) throws GitException {
        checkoutFile(storage.getAbsolutePath(path));
    }

    private void checkoutFile(Path path) throws GitException {
        String relative = storage.relativeFileName(path);
        HashMap<String, IndexInfo> indexMap = storage.getIndexMap();

        CommitHolder headCommit = storage.getHeadCommit();

        if (relative == null || !headCommit.files.containsKey(relative)) return;

        indexMap.remove(relative);

        try {
            storage.copyFile(Path.of(headCommit.files.get(relative).path), path);
        } catch (IOException e) {
            throw new GitException(e);
        }
    }


}
