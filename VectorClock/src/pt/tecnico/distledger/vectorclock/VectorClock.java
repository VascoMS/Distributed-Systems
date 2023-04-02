package pt.tecnico.distledger.server.domain;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class VectorClock {
    private final List<Integer> timestamps;

    public VectorClock(){
        timestamps = new ArrayList<>();
    }

    public VectorClock(List<Integer> vc){timestamps = new ArrayList<>(vc);}

    public List<Integer> getTimestamps() {
        return timestamps;
    }

    public Integer getTimestamp(Integer i){
        return timestamps.get(i);
    }

    public void setTimestamp(Integer i, Integer value){
        timestamps.set(i, value);
    }

    public void increment(Integer i) {timestamps.set(i, timestamps.get(i) + 1); }

    public boolean greaterEqual(List<Integer> v){
        return IntStream.range(0, timestamps.size())
                .allMatch(i -> timestamps.get(i) >= v.get(i));
    }

    public boolean lessEqual(List<Integer> v){
        return IntStream.range(0, timestamps.size())
                .allMatch(i -> v.get(i) <= timestamps.get(i));
    }

    public void merge(VectorClock v){
        for(int i = 0; i < timestamps.size(); i++){
            if(v.getTimestamp(i) > timestamps.get(i))
                timestamps.set(i, v.getTimestamp(i));
        }
    }

}
