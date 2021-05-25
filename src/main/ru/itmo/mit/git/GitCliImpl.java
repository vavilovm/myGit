package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GitCliImpl implements GitCli {
    private final GitCommands gitCommandsManager;
    private PrintStream out = System.out;

    public GitCliImpl(String folder) {
        Path workingDirectory = Paths.get(folder);
        gitCommandsManager = new GitCommands(workingDirectory);
    }

    
    private final String help = 
            "init - initialization of the repository" + System.lineSeparator() + 
            "add <files> - add a file" + System.lineSeparator() + 
            "rm <files> - the file is removed from the repository, physically remains" + System.lineSeparator() + 
            "status - changed / deleted / not added files" + System.lineSeparator() + 
            "commit <message> with date and time stamp" + System.lineSeparator() + 
            "reset <to_revision>. Reset behavior is the same as git reset --hard" + System.lineSeparator() + 
            "log [from_revision]" + System.lineSeparator() + 
            "checkout <revision>" + System.lineSeparator() + 
            "Possible revision values:" + System.lineSeparator() + 
            "commit hash - hash of the commit" + System.lineSeparator() + 
            "master - revert a branch to its original state" + System.lineSeparator() + 
            "HEAD ~ N, where N is a non-negative integer. HEAD ~ N means _Nth commit before HEAD (HEAD ~ 0 == HEAD)" + System.lineSeparator() + 
            "checkout - <files> - flushes changes in files" + System.lineSeparator() + 
            "branch-create <branch> - create a branch named <branch>" + System.lineSeparator() + 
            "branch-remove <branch> - remove a branch <branch>" + System.lineSeparator() + 
            "show-branches - show all existing branches" + System.lineSeparator() + 
            "merge <branch> - merge a branch <branch> into the current one";
    
    public void runCommand(String[] args) throws GitException {
        if (args.length == 0) {
            out.println("specify command");
            out.println(help);
        } else {
            runCommand(args[0],
                    Arrays.asList(args).subList(1, args.length));
        }
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        gitCommandsManager.commands.get(command).get().run(out, arguments);
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        out = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return gitCommandsManager.getRelativeRevisionFromHead(n);
    }
}
