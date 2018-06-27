package com.example.hugo.myhandler.handler;


public abstract class Handler {
    private IMessageQueue messageQueue;
    public Handler(Looper looper){
        messageQueue = looper.messageQueue;
    }
    public Handler(){
         Looper.myLooper();
    }

    public void sendMessage(Message message){
        // 指定发送Message的Handler,方便回调
        message.target = this;
        try {
            messageQueue.enqueueMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public abstract void handleMessage(Message message);

}
