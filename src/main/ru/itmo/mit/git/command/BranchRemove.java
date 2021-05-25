package ru.itmo.mit.git.command;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.util.HashMap;

public class BranchRemove extends AbstractCommand {
    String branch;

    public BranchRemove(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(1);
        branch = arguments.get(0);
        if (branch.equals(storage.getHeadBranch())) {
            throw new GitException(branch + " is the current branch");
        }
    }

    @Override
    protected void execute() throws GitException {
        HashMap<String, String> branches = storage.getBranches();
        branches.remove(branch);
        storage.updateBranches(branches);

    }

    @Override
    protected void printResults() throws GitException {
        out.println("Branch " + branch + " removed successfully");
    }
}
