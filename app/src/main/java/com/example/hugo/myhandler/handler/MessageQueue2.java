package com.example.hugo.myhandler.handler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue2 implements IMessageQueue {
    private final Queue<Message> queue;
    /**message 队列容量*/
    private int capacity = 0;
    /**ReentrantLock 这个锁 有可重入、可中断、可限时、公平锁等特点。*/
    private final Lock lock = new ReentrantLock();
    private final Condition BUFFER_CONDITION = lock.newCondition();
    public MessageQueue2(int capacity){
        this.capacity = capacity;
        queue = new LinkedList<>();
    }

    @Override
    public Message next() throws InterruptedException {
        try{
            lock.lock();
            while (queue.size() == 0){
                BUFFER_CONDITION.await();
            }
            Message message = queue.poll();
            BUFFER_CONDITION.signalAll();
            return message;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void enqueueMessage(Message message) throws InterruptedException {
        try {
            lock.lock();
            while (queue.size() == capacity) {
                BUFFER_CONDITION.await();
            }
            queue.offer(message);
            BUFFER_CONDITION.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
