package ru.itmo.mit.git.dataHolders;

import java.io.Serializable;

public class IndexInfo implements Serializable {
    public IndexStatus status;
    public String path;
    public String hash;

    public IndexInfo(IndexStatus status, String path, String hash) {
        this.status = status;
        this.path = path;
        this.hash = hash;
    }
}
