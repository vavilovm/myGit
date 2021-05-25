package ru.itmo.mit.git.dataHolders;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.dataHolders.CommitHolder;
import ru.itmo.mit.git.dataHolders.IndexInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.stream.Stream;

public class GitStorage {
    public final Path workingDirectory;
    public final Path gitDirectory;
    public final Path commitsPath;
    public final Path files;
    public final Path head;
    public final Path indexPath; // changes
    public final Path branchesPath;

    public GitStorage(Path directory) {
        this.workingDirectory = directory.toAbsolutePath().normalize();
        gitDirectory = workingDirectory.resolve(".myGit");
        commitsPath = gitDirectory.resolve("commits");
        indexPath = gitDirectory.resolve("index");
        files = gitDirectory.resolve("files");
        head = gitDirectory.resolve("HEAD");
        branchesPath = gitDirectory.resolve("branches");
    }

    public static String getHashByPath(Path path) throws GitException {
        try (InputStream is = Files.newInputStream(path)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readObjectByPath(Path path) throws GitException {
        try (FileInputStream file = new FileInputStream(path.toFile());
             ObjectInputStream in = new ObjectInputStream(file)) {
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GitException(e);
        }
    }

    private static void writeObjectByPath(Path path, Object obj) throws GitException {
        // clear file
        //noinspection EmptyTryBlock
        try (PrintWriter ignored = new PrintWriter(path.toFile())) {
        } catch (FileNotFoundException e) {
            throw new GitException(e);
        }

        try (FileOutputStream file = new FileOutputStream(path.toFile());
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(obj);
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    public HashMap<String, IndexInfo> getIndexMap() throws GitException {
        return readObjectByPath(indexPath);
    }

    public HashMap<String, String> getBranches() throws GitException {
        return readObjectByPath(branchesPath);
    }

    public String getHead() throws GitException {
        return readObjectByPath(head);
    }

    public CommitHolder getHeadCommit() throws GitException {
        HashMap<String, CommitHolder> commits = getCommits();
        if (commits.isEmpty()) return null;

        String headVal = getHead();
        HashMap<String, String> branches = getBranches();

        String hash = headVal;
        if (branches.containsKey(headVal)) {
            hash = branches.get(headVal);
        }

        return commits.get(hash);
    }

    public HashMap<String, CommitHolder> getCommits() throws GitException {
        return readObjectByPath(commitsPath);
    }

    public void updateCommitMap(HashMap<String, CommitHolder> map) throws GitException {
        writeObjectByPath(commitsPath, map);
    }

    public void updateIndexMap(HashMap<String, IndexInfo> indexMap) throws GitException {
        writeObjectByPath(indexPath, indexMap);
    }

    public void updateBranches(HashMap<String, String> branches) throws GitException {
        writeObjectByPath(branchesPath, branches);
    }

    public void updateHead(String newHead) throws GitException {
        writeObjectByPath(head, newHead);
    }

    public String relativeFileName(Path file) {
        String s = file.toAbsolutePath().normalize().toString();
        if (!s.startsWith(workingDirectory.toString())) {
            return null;
        }
        return s.substring(workingDirectory.toString().length() + 1);
    }

    public Path getAbsolutePath(String s) {
        Path path = Path.of(s);
        if (!path.isAbsolute()) {
            path = workingDirectory.resolve(s);
        }
        return path.normalize();
    }

    public boolean fileExists(String s) {
        return Files.exists(getAbsolutePath(s));
    }

    public boolean fileExists(Path s) {
        return Files.exists(s);
    }


    public String getHeadBranch() throws GitException {
        String headVal = getHead();
        if (getBranches().containsKey(headVal)) {
            return headVal;
        } else {
            return null;
        }
    }

    public CommitHolder getCommit(String commitHash) throws GitException {
        HashMap<String, CommitHolder> commits = getCommits();
        return commits.get(commitHash);
    }

    // commit hash -- хеш коммита
    // master -- вернуть ветку в исходное состояние
    // HEAD~N
    public String getCommitHash(String s) throws GitException {
        HashMap<String, CommitHolder> commits = getCommits();
        if (commits.containsKey(s)) {
            return s;
        }
        HashMap<String, String> branches = getBranches();
        if (branches.containsKey(s)) {
            return branches.get(s);
        }
        if (s.startsWith("HEAD~")) {
            int n = Integer.parseInt(s.substring("HEAD~".length()));
            if (n < 0) {
                throw new GitException("number must be non-negative");
            }
            return getRelativeRevisionFromHead(n);
        }

        throw new GitException("wrong format");
    }


    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        CommitHolder commit = getHeadCommit();
        HashMap<String, CommitHolder> commits = getCommits();
        for (int i = 0; i < n; i++) {
            if (commit == null) {
                throw new GitException("there are not enough commits");
            }
            commit = commits.get(commit.prev);
        }

        if (commit == null) {
            throw new GitException("there are not enough commits");
        }

        return commit.hash;
    }

    public boolean isBranch(String s) throws GitException {
        return getBranches().containsKey(s);
    }

    public void copyFile(Path path, Path copyPath) throws IOException {
        FileUtils.touch(copyPath.toFile());
        Files.copy(path, copyPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void copyFileToWorkingDir(String relative, String path) throws IOException {
        Path copyPath = workingDirectory.resolve(relative);
        copyFile(Path.of(path), copyPath);
    }

    public void deleteFile(Path p) throws IOException {
        Files.deleteIfExists(p);
    }

    public void createDirectories(Path p) throws IOException {
        Files.createDirectories(p);
    }

    public Stream<Path> workingDirectoriesWalk() throws IOException {
        return Files.walk(workingDirectory);
    }
}
