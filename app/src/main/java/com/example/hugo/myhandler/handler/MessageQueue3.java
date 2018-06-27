package com.example.hugo.myhandler.handler;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**这个是有问题的   会出现最后一个读不出来的问题*/
public class MessageQueue3 implements IMessageQueue {
    /**进入队列的锁*/
    private final Lock putLock = new ReentrantLock();
    private final Condition notFull = putLock.newCondition();
    /**出队列的锁*/
    private final Lock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();
    /**队头*/
    private Node head;
    /**队尾*/
    private Node lase;
    /**记录大小*/
    private AtomicInteger count = new AtomicInteger(0);
    /**容量 ， 默认为10*/
    private int capacity = 10;

    public MessageQueue3(int capacity){
        this.capacity = capacity;
    }

    @Override
    public Message next() throws InterruptedException {
        Node node;
        int c = -1;
        takeLock.lock();
        try {
            while (count.get() == 0){
                notEmpty.await();
            }
            node = head;
            head = head.next;
            //原子性自减
            c = count.getAndDecrement();
            System.out.println(c);
            if(c > 0){
                notEmpty.signal();
            }
        }finally {
            takeLock.unlock();
        }
        if(count.get() < capacity){
            signalNotFull();
        }
        return node.date;
    }

    @Override
    public void enqueueMessage(Message message) throws InterruptedException {
        System.out.println("enqueueMessage:" + message.getMsg());
        Node node = new Node(message);
        int c = -1;
        putLock.lock();
        try {
            while (count.get() == capacity){
                notFull.await();
            }
            //初始状态
            if(head == null && lase == null){
                head = lase = node;
            }else{
                lase.next = node;
                lase = lase.next;
            }
            //原子性 自加
            c = count.getAndIncrement();
            System.out.println("aaaaa:"+c+"   "+count.get());
            if(c < capacity){
                notFull.signal();
            }
        }finally {
            putLock.unlock();
        }
        if(c > 0){
            signalNotEmpty();
        }
    }

    /**给取message的发送信号 有message了*/
    private void signalNotEmpty(){
        takeLock.lock();
        try {
            notEmpty.signal();
        }finally {
            takeLock.unlock();
        }
    }

    /**发送队列有空位的信号给进入队列 */
    private void signalNotFull(){
        putLock.lock();
        try {
            notFull.signal();
        }finally {
            putLock.unlock();
        }
    }

    static class Node{
        Message date;
        Node next;
        public Node(Message date){
            this.date = date;
        }
    }
}
