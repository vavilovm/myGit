package ru.itmo.mit.git.command;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.dataHolders.GitStorage;
import ru.itmo.mit.git.dataHolders.IndexInfo;
import ru.itmo.mit.git.dataHolders.IndexStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Remove extends AbstractCommand {
    private List<Path> paths;

    public Remove(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() {
        paths = arguments.stream().map(storage::getAbsolutePath).filter(Files::exists).collect(Collectors.toList());
    }

    @Override
    protected void execute() throws GitException {
        remove(paths);
    }

    @Override
    protected void printResults() {
        out.println("Rm completed successful");
    }

    private void remove(List<Path> paths) throws GitException {
        HashMap<String, IndexInfo> indexMap = storage.getIndexMap();
        CommitHolder headCommit = storage.getHeadCommit();

        for (Path path : paths) {
            String relative = storage.relativeFileName(path);

            if (headCommit != null) {
                if (headCommit.files.containsKey(relative)) {
                    indexMap.put(relative, new IndexInfo(IndexStatus.REMOVED, null, null));
                } else {
                    indexMap.remove(relative);
                }
                storage.updateIndexMap(indexMap);
            }
        }
    }
}