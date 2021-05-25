package ru.itmo.mit.git.dataHolders;

import java.util.LinkedList;
import java.util.List;

public class GitStatus {
    public final String branch;
    public final List<String> added = new LinkedList<>();
    public final List<String> modified = new LinkedList<>();
    public final List<String> removed = new LinkedList<>();
    public final List<String> untrackedNew = new LinkedList<>();
    public final List<String> untrackedRemoved = new LinkedList<>();
    public final List<String> untrackedModified = new LinkedList<>();

    public GitStatus(String branch) {
        this.branch = branch;
    }
}
