package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TaskAdj {
    private final IdentityHashMap<AsyncTask, List<AsyncTask>> dependTasks = new IdentityHashMap<>();

    synchronized TaskGroup after(final AsyncTask... asyncTasks) {
        for (AsyncTask asyncTask : asyncTasks) {
            if (!dependTasks.containsKey(asyncTask)) {
                throw new IllegalStateException(
                        "should call TaskAdj.begin first");
            }
        }
        return new TaskGroup(this, Arrays.asList(asyncTasks));
    }

    synchronized TaskGroup begin(List<AsyncTask> asyncTasks) {
        TaskGroup taskGroup = new TaskGroup(this, asyncTasks);
        return taskGroup;
    }

    synchronized Map<AsyncTask, List<AsyncTask>> getDependsTasks() {
        return dependTasks;
    }

    void addDependency(AsyncTask from, AsyncTask to) {
        List<AsyncTask> asyncTasks = dependTasks.computeIfAbsent(from, v -> new ArrayList<>(0));

        if (to != null && !asyncTasks.contains(to)) {
            asyncTasks.add(to);
        }
    }
}
