package com.thread.gk;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 *  资源分配器：如何解决一次性申请转出和转入账户的问题
 *  通知-等待 考虑4个要素：
 *  1.互斥锁：Alloctor需要的是单例，用this做互斥锁
 *  2.线程要求的条件：出和入都没分配过
 *  3.何时等待：线程要的条件不满足
 *  4.何时通知：当有线程释放账户时就通知。释放的账户不一定是收到通知的线程需要的账户，所以尽量用notifyAll
 */

public class MyLock {

    public static void main(String[] args) throws InterruptedException {
        Account src = new Account(10000);
        Account tag = new Account(10000);
        final CountDownLatch countDownLatch = new CountDownLatch(9999);

        for (int i = 0; i < 9999; i++) {
            new Thread(()->{
                src.transferToTarget(1, tag);
                countDownLatch.countDown();
            }).start();
        }

        // 等待所有线程结束
        countDownLatch.await();

        // 打印结果
        System.out.println("src=" + src.getBalance());
        System.out.println("tag=" + tag.getBalance());
    }


    // 账户类
    static class Account{
        private Integer balance;

        public Account(Integer balance){
            this.balance = balance;
        }

        public void transferToTarget(Integer amount, Account target){
            Allocator.getInstance().apply(this, target);
            this.balance -= amount;
            target.setBalance(target.getBalance() + amount);
            Allocator.getInstance().release(this, target);
        }

        public Integer getBalance() {
            return balance;
        }

        public void setBalance(Integer balance) {
            this.balance = balance;
        }
    }


    // 单例锁类
    static class Allocator{
        private List<Account> locks = new ArrayList();

        private Allocator(){}

        // synchronized保证apply和release的互斥
        // synchronized修饰非静态函数，锁的对象是this
        public synchronized void apply(Account src, Account tag){
            // 要用wait()的范式，不能用if
            while (locks.contains(src) || locks.contains(tag)){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            locks.add(src);
            locks.add(tag);
        }

        public synchronized void release(Account src, Account tag){
            locks.remove(src);
            locks.remove(tag);
            // 不用notify的原因
            this.notifyAll();
        }

        public static Allocator getInstance(){
            return AllocatorSingle.install;
        }

        static class AllocatorSingle{
            public static Allocator install = new Allocator();
        }
    }
}
