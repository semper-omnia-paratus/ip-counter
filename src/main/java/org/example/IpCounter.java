package org.example;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class IpCounter {
    private BitSet positiveRange = new BitSet(Integer.MAX_VALUE);
    private BitSet negativeRange = new BitSet(Integer.MAX_VALUE);


    private long countDistinctFromFile(String file) {
        final AtomicLong counter = new AtomicLong();

        try (Stream<String> stream = Files.lines(Paths.get(file))) {
            stream.forEach(e -> {
                String[] chunks = e.split("\\.");
                if (chunks.length < 4) {
                    return;
                }
                int ip = Integer.valueOf(chunks[0]) << 24;
                ip = ip ^ Integer.valueOf(chunks[1]) << 16;
                ip = ip ^ Integer.valueOf(chunks[2]) << 8;
                ip = ip ^ Integer.valueOf(chunks[3]);
                if (ip > 0 ) {
                    if (!positiveRange.get(ip)){
                        positiveRange.set(ip);
                        counter.getAndIncrement();
                    }
                } else {
                    if (!negativeRange.get(-ip)){
                        negativeRange.set(-ip);
                        counter.getAndIncrement();
                    }
                }
            }
            );
        } catch (IOException e) {
            System.out.printf("I/O Error: %s", e);
            return 0;
        }
        return counter.get();
    }

    public static void main(String[] args) {
        IpCounter instance = new IpCounter();
        String file = "input.txt";
        if (args.length > 0) {
            file = args[0];
        }
        System.out.println(instance.countDistinctFromFile(file));

    }
}