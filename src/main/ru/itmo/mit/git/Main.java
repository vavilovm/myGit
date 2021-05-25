package ru.itmo.mit.git;

public class Main {
    public static void main(String[] args) {
        try {
            GitCliImpl gitCli = new GitCliImpl(System.getProperty("user.dir"));
            gitCli.setOutputStream(System.out);
            gitCli.runCommand(args);
        } catch (GitException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
