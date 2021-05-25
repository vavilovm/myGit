package ru.itmo.mit.git.dataHolders;

import org.apache.commons.io.FileUtils;
import ru.itmo.mit.git.GitException;

import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CommitHolder implements Serializable {

    private static final String DEFAULT_BRANCH = "master";
    private static final SecureRandom random = new SecureRandom();
    private static final String symbols = ("0123456789abcdef");
    private static final int LENGTH = 40;
    public final String hash = generateString();
    public final String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    public final String author = System.getProperty("user.name");
    public final String message;
    public final HashMap<String, IndexInfo> files = new HashMap<>(); // <relative path, Info>
    public String prev = null; // prev commits
    public String mergedPrev = null;

    public CommitHolder(String message, GitStorage storage, String mergeBranch) throws GitException, IOException {
        this.message = message;

        HashMap<String, CommitHolder> commitsMap = storage.getCommits();
        HashMap<String, String> branches = storage.getBranches();
        String branch = null;
        if (!storage.fileExists(storage.head)) {
            // Initial commit
            FileUtils.touch(storage.head.toFile());
            branch = DEFAULT_BRANCH;
        } else {
            String headVal = storage.getHead();

            String commit = headVal;
            if (branches.containsKey(headVal)) {
                branch = headVal;
                commit = branches.get(branch);
            }

            files.putAll(commitsMap.get(commit).files);
            prev = commit;
            mergedPrev = branches.get(mergeBranch);
        }
        HashMap<String, IndexInfo> indexMap = storage.getIndexMap();

        indexMap.forEach((s, info) -> {
            if (info.status.equals(IndexStatus.REMOVED)) {
                files.remove(s);
            } else {
                files.put(s, info);
            }
        });

        if (branch != null) {
            branches.put(branch, hash);
            storage.updateBranches(branches);
            storage.updateHead(branch);
        } else {
            storage.updateHead(hash);
        }

        commitsMap.put(hash, this);
        storage.updateCommitMap(commitsMap);
        storage.updateIndexMap(new HashMap<>());
    }

    @Override
    public String toString() {
        String newline = System.lineSeparator();
        return "Commit " + hash + newline +
                "Author: " + author + newline +
                "Date: " + date + newline +
                newline +
                message;
    }

    private String generateString() {
        char[] buf = new char[LENGTH];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
        }
        return new String(buf);
    }
}
