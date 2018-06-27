package com.example.hugo.myhandler.handler;


import java.util.Random;

public class Main {

    public static void main(String[] args){
        MainThread mainThread = new MainThread();
        mainThread.start();
        // 确保MainLooper构建完成
        while(Looper.getsMainLooper() == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Handler handler = new Handler(Looper.getsMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                System.out.println("execute in : " + Thread.currentThread().getName());
                switch (message.getCode()){
                    case 0 :
                        System.out.println("code 0 : " + message.getMsg());
                        break;
                    case 1 :
                        System.out.println("code 1 :" + message.getMsg());
                        break;
                        default:
                            System.out.println("other code : " + message.getMsg());
                }
            }
        };

        Message message1 = new Message(0,"I an the first message!");
        WorkThread workThread1 = new WorkThread(handler,message1);

        Message message2 = new Message(1,"I am the second message!");
        WorkThread workThread2 = new WorkThread(handler,message2);

        Message message3 = new Message(66,"I am a message!");
        WorkThread workThread3 = new WorkThread(handler,message3);

        workThread1.start();
        workThread2.start();
        workThread3.start();
}

    /***
     * 模拟工作线程
     */
    public static class WorkThread extends Thread{
        private Handler handler;
        private Message message;

        public WorkThread(Handler handler,Message message){
            setName("WorkThread");
            this.handler = handler;
            this.message = message;
        }

        @Override
        public void run() {
            super.run();
            // 模拟耗时操作
            Random random = new Random();
            try {
                Thread.sleep(random.nextInt(10) * 300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 任务执行完，sendMessage
            handler.sendMessage(message);
        }
    }

    /**模拟主线程*/
    public static class MainThread extends Thread{
        public MainThread(){
            setName("MainThread");
        }

        @Override
        public void run() {
            super.run();
            // 这里与系统的调用一样
            Looper.prepareMainLooper();
            System.out.println(getName() + " the Looper is prepared.");
            Looper.loop();
        }
    }
}
