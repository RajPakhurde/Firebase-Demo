package com.rajpakhurde.firebasedemo.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
