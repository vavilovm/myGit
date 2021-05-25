package ru.itmo.mit.git.command;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

public class ShowBranches extends AbstractCommand {

    public ShowBranches(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(0);
    }

    @Override
    protected void execute() throws GitException {
    }

    @Override
    protected void printResults() throws GitException {
        out.println("Available branches:");
        for (String s : storage.getBranches().keySet()) {
            out.println(s);
        }
    }
}
