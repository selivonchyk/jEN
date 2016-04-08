/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.v4.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paul
 */
public class StatsManager {
	private Integer lastXRateLimit;
	private Integer lastXRateLimitRemaining;
	private Integer lastXRateLimitUsed;

    public static class Tracker {

        String name;
        long startTime;
        long endTime = 0L;

        Tracker(String name, long start) {
            this.name = name;
            this.startTime = start;
        }
    }
    private Map<String, OpData> map = new HashMap<String, OpData>();

    public Tracker start(String name) {
        return new Tracker(name, System.currentTimeMillis());
    }

    public void end(Tracker tracker) {
        tracker.endTime = System.currentTimeMillis();
        long delta = tracker.endTime - tracker.startTime;
        OpData opData = get(tracker.name);
        opData.count++;
        opData.sumTime += delta;

        if (delta > opData.maxTime) {
            opData.maxTime = delta;
        }
        if (delta < opData.minTime) {
            opData.minTime = delta;
        }
    }

    public void close(Tracker tracker) {
        if (tracker.endTime == 0L) {
            OpData opData = get(tracker.name);
            opData.count++;
            opData.error++;
        }
    }

    private OpData get(String op) {
        OpData opData = map.get(op);
        if (opData == null) {
            opData = new OpData(op);
            map.put(op, opData);
        }
        return opData;
    }

    public PerformanceStats getOverallPerformanceStats() {
        int total = 0;
        int errs = 0;
        int sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        PerformanceStats ps = new PerformanceStats();
        List<OpData> opList = new ArrayList<OpData>(map.values());
        for (OpData opData : opList) {
            total += opData.count;
            errs += opData.error;
            sum += opData.sumTime;

            if (opData.minTime < min) {
                min = opData.minTime;
            }
            if (opData.maxTime > max) {
                max = opData.maxTime;
            }
        }
        ps.setCalls(total);
        ps.setFailures(errs);
        ps.setTotalCallTime(sum);
        ps.setMinCallTime(min);
        ps.setMinCallTime(max);
        return ps;
    }

    public String dump() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(String.format("|| %7s || %6s || %6s || %6s || %6s || %15s ||", "Calls", "Fail", "Avg", "Min", "Max", "Method")).append("\n");
        int total = 0;
        int errs = 0;
        int sum = 0;

        List<OpData> opList = new ArrayList<OpData>(map.values());
        Collections.sort(opList);
        for (OpData opData : opList) {
        	sb.append(opData).append("\n");
            total += opData.count;
            errs += opData.error;
            sum += opData.sumTime;
        }

        int successCount = total - errs;
        sb.append("\n");
        sb.append(" Total calls : ").append(total).append(" \n");
        sb.append(" Total errors: ").append(errs).append(" \n");
        if (total > 0) {
        	sb.append(" Success Rate: ").append(100 * (total - errs) / total).append(" \n");
        }

        if (successCount > 0) {
        	sb.append(" Average Time: ").append(sum / successCount).append(" \n");
        }
        if (lastXRateLimitRemaining != null) {
        	sb.append(" Rate limit status: ").append("\n");
        	sb.append("     X-RateLimit-Limit    : ").append(lastXRateLimit).append("\n");
        	sb.append("     X-RateLimit-Used     : ").append(lastXRateLimitUsed).append("\n");
        	sb.append("     X-RateLimit-Remaining: ").append(lastXRateLimitRemaining).append("\n");
        }
        sb.append("\n");
        String dump = sb.toString();
        System.out.println(dump);
        return dump;
    }

	public Integer getLastXRateLimit() {
		return lastXRateLimit;
	}

	public void setLastXRateLimit(Integer lastXRateLimit) {
		this.lastXRateLimit = lastXRateLimit;
	}

	public Integer getLastXRateLimitRemaining() {
		return lastXRateLimitRemaining;
	}

	public void setLastXRateLimitRemaining(Integer lastXRateLimitRemaining) {
		this.lastXRateLimitRemaining = lastXRateLimitRemaining;
	}

	public Integer getLastXRateLimitUsed() {
		return lastXRateLimitUsed;
	}

	public void setLastXRateLimitUsed(Integer lastXRateLimitUsed) {
		this.lastXRateLimitUsed = lastXRateLimitUsed;
	}
}

class OpData implements Comparable<OpData> {

    String name;
    int count = 0;
    int error = 0;
    long minTime = Long.MAX_VALUE;
    long maxTime = -Long.MAX_VALUE;
    long sumTime = 0;

    OpData(String name) {
        this.name = name;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getError() {
        return error;
    }

    public long getSumTime() {
        return sumTime;
    }

    public long getAvgTime() {
        int successCount = count - error;
        if (successCount > 0) {
            return sumTime / successCount;
        } else {
            return 0;
        }
    }

    public String toString() {
        return String.format("|| %7d || %6d || %6d || %6d || %6d || %15s ||",
                getCount(), getError(), getAvgTime(), getMinTime(), getMaxTime(), getName());

    }

    public int compareTo(OpData other) {
        return name.compareTo(other.name);
    }
}
