package com.example.hugo.myhandler.handler;

public interface IMessageQueue {
    Message next() throws InterruptedException;
    void enqueueMessage(Message message) throws InterruptedException;
}
