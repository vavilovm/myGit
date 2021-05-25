package ru.itmo.mit.git.command;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.util.HashMap;

public class BranchCreate extends AbstractCommand {
    String branch;

    public BranchCreate(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsExactly(1);
        branch = arguments.get(0);
    }

    @Override
    protected void execute() throws GitException {
        HashMap<String, String> branches = storage.getBranches();
        branches.put(branch, storage.getHeadCommit().hash);
        storage.updateBranches(branches);
        new Checkout(storage).checkoutBranch(branch);
    }

    @Override
    protected void printResults() throws GitException {
        out.println("Branch " + branch + " created successfully");
        out.println("You are on branch " + branch);
    }

}
