package com.company;
import java.io.PrintStream;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static class Storage{
        Integer product = 0;
        Integer maxVolume;
        public Storage(Integer maxVolume)
        {
            this.maxVolume = maxVolume;
        }
        public Integer getProduct()
        {
            return product;
        }
        public void addProduct()
        {
            product = product+1;
        }
        public void removeProduct()
        {
            product = product-1;
        }
        public Boolean isEmpty()
        {
            return product < 1;
        }
        public Boolean isFull()
        {
            return product == maxVolume;
        }

    }

    public static class Generator extends Thread {
        Storage box;
        Boolean pause;
        Random random = new Random();
        public Generator(Storage box)
        {
            pause = false;
            this.box = box;
        }
        public void run() {
            while(true)
            {
                while(box.isFull() || pause)
                {
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    gen();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("Произведен товар. Количество товара на складе %d%n",box.getProduct());
            }
        }
        public void gen() throws InterruptedException
        {
            TimeUnit.SECONDS.sleep(random.nextInt(4));
            box.addProduct();
        }
        public void setPause(Boolean pause)
        {
            this.pause = pause;
        }
    }

    public static class Consumer extends Thread {
        Storage box;
        Boolean pause;
        Random random = new Random();
        public Consumer(Storage box)
        {
            pause = false;
            this.box = box;
        }
        public void run()
        {
            while(true)
            {
                while (box.isEmpty() || pause) {
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    collect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("Потреблен товар. Количество товара на складе %d%n", box.getProduct());
            }
        }
        public void collect() throws InterruptedException
        {
            TimeUnit.SECONDS.sleep(random.nextInt(5));
            box.removeProduct();
        }
        public void setPause(Boolean pause)
        {
            this.pause = pause;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        System.out.printf("Enter volume of storage%n");
        int volume = in.nextInt();
        Storage storage = new Storage(volume);
        Generator generator = new Generator(storage);
        Consumer consumer = new Consumer(storage);
        generator.start();
        consumer.start();
        while(true)
        {
            generator.setPause(storage.getProduct() > volume / 2);
            consumer.setPause(storage.getProduct() <= volume / 2);
        }

    }
}
