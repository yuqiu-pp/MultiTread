package com.thread.gk;

public class Add10k {
    private long count;

    public void add10k(){
        System.out.println("add10k");
        int idx = 0;
        while (idx++ < 10000){
            // 锁住共享变量才能得到正确结果
            // synchronized (this)
            {
                count += 1;
            }
        }
    }

    public long getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        final Add10k test = new Add10k();

        Thread th1 = new Thread(()->{
            test.add10k();
        });
        Thread th2 = new Thread(()->{
            test.add10k();
        });

        th1.start();
        th2.start();
        // 等待线程结束
        th1.join();
        th2.join();

        System.out.println(test.getCount());
    }
}
