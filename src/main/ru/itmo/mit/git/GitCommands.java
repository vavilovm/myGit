package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.command.*;
import ru.itmo.mit.git.dataHolders.GitStorage;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class GitCommands {
    public Map<String, Supplier<AbstractCommand>> commands;
    GitStorage storage;

    public GitCommands(Path directory) {
        storage = new GitStorage(directory);
        commands = Map.ofEntries(Map.entry("init", () -> new Init(storage)),
                Map.entry("add", () -> new Add(storage)),
                Map.entry("rm", () -> new Remove(storage)),
                Map.entry("status", () -> new Status(storage)),
                Map.entry("commit", () -> new Commit(storage)),
                Map.entry("reset", () -> new Reset(storage)),
                Map.entry("log", () -> new Log(storage)),
                Map.entry("checkout", () -> new Checkout(storage)),
                Map.entry("branch-create", () -> new BranchCreate(storage)),
                Map.entry("branch-remove", () -> new BranchRemove(storage)),
                Map.entry("show-branches", () -> new ShowBranches(storage)),
                Map.entry("merge", () -> new Merge(storage)));
    }

    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return storage.getRelativeRevisionFromHead(n);
    }
}
