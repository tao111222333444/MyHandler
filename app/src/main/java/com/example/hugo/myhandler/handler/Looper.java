package com.example.hugo.myhandler.handler;

public class Looper {
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
    IMessageQueue messageQueue;
    private static Looper sMainLooper;
    public Looper(){
//        messageQueue = new MessageQueue(10);
        messageQueue = new MessageQueue3(10);
//        messageQueue = new MessageQueue1(10);
//        messageQueue = new MessageQueue2(10);
    }

    public static void prepare(){
        if(sThreadLocal.get() != null){
            throw new RuntimeException("Only one Looper may be created per thread.");
        }
        sThreadLocal.set(new Looper());
    }

    public static void prepareMainLooper(){
        prepare();
        synchronized (Looper.class){
            if(sMainLooper != null){
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }

    public static Looper getsMainLooper(){
        return sMainLooper;
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop(){
        final Looper me = myLooper();
        if(me == null){
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        while(true){
            // 消费Message,如果MessageQueue为null,则等待
            Message message = null;

            try {
                message = me.messageQueue.next();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (message != null){
                message.target.handleMessage(message);
            }
        }
    }
}
