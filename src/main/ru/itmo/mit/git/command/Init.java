package ru.itmo.mit.git.command;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.io.IOException;
import java.util.HashMap;

public class Init extends AbstractCommand {

    public Init(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(0);
    }

    @Override
    protected void execute() throws GitException {
        try {
            storage.createDirectories(storage.gitDirectory);
            storage.createDirectories(storage.files);

            storage.updateIndexMap(new HashMap<>());
            storage.updateBranches(new HashMap<>()); // <String name, String commit hash>
            storage.updateCommitMap(new HashMap<>());

            new Commit(storage).createCommit("Initial commit");
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    @Override
    protected void printResults() {
        out.println("Project initialized");
    }
}
