package ru.itmo.mit.git.command;

import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.io.IOException;

public class Commit extends AbstractCommand {
    public Commit(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(1);
    }

    @Override
    protected void execute() throws GitException {
        createCommit(arguments.get(0));
    }

    @Override
    protected void printResults() {
        out.println("Files committed");
    }


    public void createCommit(String message) throws GitException {
        createCommit(message, null);
    }

    public void createCommit(String message, String mergeBranch) throws GitException {
        try {
            new CommitHolder(message, storage, mergeBranch);
        } catch (IOException e) {
            throw new GitException(e);
        }
    }
}
