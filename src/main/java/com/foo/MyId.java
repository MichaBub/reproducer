package com.foo;

import dev.morphia.annotations.Entity;

import java.io.Serializable;

@Entity(useDiscriminator = false)
public class MyId implements Serializable {
    private static final long serialVersionUID = -1L;
    protected long myId;
    public MyId() {
    }
    public MyId(long myId) {
        this.myId = myId;
    }
    public MyId(String myId) {
        this.myId = Long.parseLong(myId);
    }
}