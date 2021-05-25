package ru.itmo.mit.git.command;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.dataHolders.GitStorage;
import ru.itmo.mit.git.dataHolders.IndexInfo;
import ru.itmo.mit.git.dataHolders.IndexStatus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.itmo.mit.git.dataHolders.GitStorage.getHashByPath;

public class Add extends AbstractCommand {
    private List<Path> paths;

    public Add(GitStorage storage) {
        super(storage);
    }

    @Override
    protected void validateArguments() {
        paths = arguments.stream().map(storage::getAbsolutePath).filter(storage::fileExists).collect(Collectors.toList());
    }

    @Override
    protected void execute() throws GitException {
        add(paths);
    }

    @Override
    protected void printResults() {
        out.println("Add completed successful");
    }


    public void add(List<Path> paths) throws GitException {
        HashMap<String, IndexInfo> indexMap = storage.getIndexMap();
        CommitHolder headCommit = storage.getHeadCommit();

        for (Path path : paths) {
            String relative = storage.relativeFileName(path);
            IndexInfo indexInfo = indexMap.get(relative);
            String indexHash = indexInfo == null ? null : indexInfo.hash;

            String newHash = getHashByPath(path);


            String committedHash = null;
            if (headCommit != null && headCommit.files.containsKey(relative)) {
                committedHash = headCommit.files.get(relative).hash;
            }
            if (Objects.equals(newHash, indexHash) || Objects.equals(newHash, committedHash)) {
                // everything is up-to-date
                return;
            }

            Path copyPath = storage.files.resolve(relative).resolve(newHash);
            try {
                storage.copyFile(path, copyPath);
            } catch (IOException e) {
                throw new GitException(e);
            }

            IndexStatus status = IndexStatus.ADDED;
            if (committedHash != null) {
                status = IndexStatus.MODIFIED;
            }

            indexMap.put(relative, new IndexInfo(status, copyPath.toString(), newHash));
        }

        storage.updateIndexMap(indexMap);
    }
}
