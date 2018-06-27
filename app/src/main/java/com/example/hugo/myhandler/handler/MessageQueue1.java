package com.example.hugo.myhandler.handler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageQueue1 implements IMessageQueue {
    /**保存message的队列*/
    private Queue<Message> queue;
    /**原子性整数  初始值为0*/
    private final AtomicInteger integer = new AtomicInteger(0);
    /**volatile设置count的可见性  当这个值改变时会立即更新  其他线程需要读取时  会去内存读取新值
     * 　1）保证了不同线程对这个变量进行操作时的可见性，即一个线程修改了某个变量的值，这新值对其他线程来说是立即可见的。
     *　 2）禁止进行指令重排序。
     * */
    private volatile int count;
    /**缓冲区锁*/
    private final Object BUFFER_LOCK = new Object();
    public MessageQueue1(int capacity){
        this.count = capacity;
        queue = new LinkedList<>();
    }

    /**
     * 如果queue的size 为0，说明现在没有消息待处理，因此执行BUFFER_LOCK.wait()挂起线程，
     * 当queue.poll();时，queue里就有了消息，需要唤醒因为没有消息而挂起的线程，
     * 所以执行BUFFER_LOCK.notifyAll();。
     * */
    @Override
    public Message next() throws InterruptedException {
        synchronized ( BUFFER_LOCK ){
            while (queue.size() == 0){
                BUFFER_LOCK.wait();
            }
            Message message = queue.poll();
            BUFFER_LOCK.notifyAll();
            return message;
        }
    }

    /**
     *如果queue.size() == count说明消息满了，如果Handler继续sendMessage，queue无法继续装下，
     * 因此该线程需要挂起： BUFFER_LOCK.wait();。当执行queue.offer(message);时，
     * queue里头保存的Message就少了一个，可以插入新的Message，因此BUFFER_LOCK.notifyAll();
     * 唤醒因为queue满了而挂起的线程。
     */
    @Override
    public void enqueueMessage(Message message) throws InterruptedException {
        synchronized (BUFFER_LOCK) {
            while (queue.size() == count){
                BUFFER_LOCK.wait();
            }
            queue.offer(message);
            BUFFER_LOCK.notifyAll();
        }
    }
}
