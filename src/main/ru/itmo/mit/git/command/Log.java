package ru.itmo.mit.git.command;

import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.util.Map;
import java.util.StringJoiner;

public class Log extends AbstractCommand {
    private Map<String, CommitHolder> commitsMap;
    private CommitHolder startCommit;

    public Log(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() throws GitException {
        validateNumberOfArgumentsRange(0, 1);
    }

    @Override
    protected void execute() throws GitException {
        commitsMap = storage.getCommits();
        if (arguments.size() == 0) {
            startCommit = storage.getHeadCommit();
        } else {
            startCommit = storage.getCommit(storage.getCommitHash(arguments.get(0)));
        }
    }

    @Override
    protected void printResults() throws GitException {
        StringJoiner stringJoiner = new StringJoiner(System.lineSeparator() + System.lineSeparator());
        for (CommitHolder commit = startCommit; commit != null; commit = commitsMap.get(commit.prev)) {
            stringJoiner.add(commit.toString());
        }
        out.println(stringJoiner);
    }
}
