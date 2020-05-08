package com.thread.wx;


import sun.misc.IOUtils;
import sun.nio.ch.IOUtil;

import java.util.Scanner;

public class TestInBlockIOState {
    Scanner in = new Scanner(System.in);

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                // 命令行中的阻塞读
                String input = in.nextLine();
                System.out.println(input);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                // IOUtils.closeQuietly(in);
            }

        }
    }, "输入输出");

    // 启动
    t.start();

    // 确保run已经得到执行
    Thread.sleep(100);

    // 状态为RUNNABLE
    assertThat(t.getState()).isEqualTo(Thread.State.RUNNABLE);
}
