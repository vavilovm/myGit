package ru.itmo.mit.git.command;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.io.PrintStream;
import java.util.List;

public abstract class AbstractCommand {
    protected final GitStorage storage;
    protected PrintStream out;
    protected List<String> arguments;

    public AbstractCommand(GitStorage storage) {
        this.storage = storage;
    }

    public void run(PrintStream out1, List<String> arguments1) throws GitException {
        out = out1;
        arguments = arguments1;

        validateArguments();
        execute();
        printResults();
    }

    protected abstract void validateArguments() throws GitException;

    protected abstract void execute() throws GitException;

    protected abstract void printResults() throws GitException;

    protected void validateNumberOfArgumentsExactly(int n) throws GitException {
        if (arguments.size() != n) {
            throw new GitException("Supported number of arguments: " + n);
        }
    }

    protected void validateNumberOfArgumentsRange(int lowerbound, int upperbound) throws GitException {
        if (arguments.size() < lowerbound || upperbound < arguments.size()) {
            String range = "from " + lowerbound + " to " + upperbound;
            throw new GitException("Supported number of arguments: " + range);
        }
    }

    protected void validateNumberOfArgumentsAtLeast1() throws GitException {
        if (arguments.size() < 1) {
            String range = "at least " + 1;
            throw new GitException("Supported number of arguments: " + range);
        }
    }
}