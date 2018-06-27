package com.example.hugo.myhandler.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息队列的实习类
 * */
public class MessageQueue implements IMessageQueue {
    /**用阻塞队列保存message*/
    private final BlockingQueue<Message> queue;

    /**
     *
     * @param capacity  阻塞队列容量
     */
    public MessageQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public Message next() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void enqueueMessage(Message message)  {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
