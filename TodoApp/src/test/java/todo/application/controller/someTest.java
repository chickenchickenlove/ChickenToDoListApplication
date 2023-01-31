package todo.application.controller;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class someTest {


    @Test
    void test1() {
        HashMap<String, String> myMap = new HashMap<>();
        System.out.println("someTest.test1 " + (myMap.get("abc") == null));
    }

    @Test
    void test2() {


        HashMap<String, Integer> map = new HashMap<>();



        log.info("main start");
        Runnable userA = () -> {
            if (map.get("name") == null) {
                map.put("name",2);
                log.info("now Map is Free! Thread A");
//                sleep(1);
                log.info("threadA input!");
            }else{
                log.info("now Map is busy! this request is rejected");
            }

            if (map.get("name") != null) {
                map.remove("name");
                log.info("threadA remove");
            }
        };

        Runnable userB = () -> {
            if (map.get("name") == null) {
                map.put("name",3);
                log.info("now Map is Free! Thread B");
                log.info("threadB input!");
            }else{
                log.info("now Map is busy! this request is rejected");
            }
            if (map.get("name") != null) {
                map.remove("name");
                log.info("threadB remove");
            }
        };


        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start(); // A 실행
        threadB.start(); // B 실행





        sleep(3000);
        log.info("main exit");
    }



    @Test
    void test3() {


        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();


        log.info("main start");
        Runnable userA = () -> {
            if (map.get("name") == null) {
                map.put("name",2);
                log.info("now Map is Free! Thread A : {}", map.get("name"));
                log.info("Finally threadA input!");
            }else{
                log.info("now Map is busy! ThreadA request is rejected");
            }
        };

        Runnable userB = () -> {
            if (map.get("name") == null) {
                map.put("name",3);
                log.info("now Map is Free! Thread B : {}", map.get("name"));
                log.info("Finally threadB input!");
            }else{
                log.info("now Map is busy! ThreadB request is rejected");
            }
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start(); // A 실행
        threadB.start(); // B 실행

//        log.info("final Value = {}", map.get("name"));


        sleep(3000);
        log.info("main exit");
    }


    @Test
    void test4() {


        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        synchronized (map) {

        }


        log.info("main start");
        Runnable userA = () -> {
                map.put("name",2);
                log.info("now Map is Free! Thread A : {}", map.get("name"));
        };

        Runnable userB = () -> {
                map.put("name",3);
                log.info("now Map is Free! Thread B : {}", map.get("name"));
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");

        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start(); // A 실행
        threadB.start(); // B 실행

//        log.info("final Value = {}", map.get("name"));


        sleep(3000);
        log.info("main exit");
    }


    public void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
