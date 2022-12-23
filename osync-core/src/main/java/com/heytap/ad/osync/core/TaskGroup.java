package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.task.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskGroup是指需要并发调度的一组任务，
 * 如：a,b,c -> d -> e,f 中，a, b, c构成一个TaskGroup，d构成一个TaskGroup，e, f构成一个TaskGroup
 */
public class TaskGroup {

    private final TaskAdj taskAdj;
    /**
     * 此taskGroup下面的任务列表
     */
    private List<AsyncTask> taskList;

    TaskGroup(TaskAdj taskAdj, List<AsyncTask> taskList) {
        synchronized (taskAdj) {
            this.taskAdj = taskAdj;
            this.taskList = new ArrayList<>(taskList);
            for (AsyncTask task : taskList) {
                taskAdj.addDependency(task, null);
            }
        }
    }

    /**
     * 此TaskGroup下面所有的任务执行完后，汇总后继续执行 asyncTask
     *
     * @param clear
     * @param asyncTask
     * @return
     */
    public TaskGroup then(boolean clear, AsyncTask asyncTask) {
        synchronized (taskAdj) {
            for (AsyncTask from : this.taskList) {
                taskAdj.addDependency(from, asyncTask);
            }
            taskAdj.addDependency(asyncTask, null);

            if (clear) {
                this.taskList = new ArrayList<AsyncTask>();
                this.taskList.add(asyncTask);
            }
            return this;
        }
    }


    public void refresh(List<AsyncTask> cacheTaskList) {
        this.taskList.clear();
        this.taskList.addAll(cacheTaskList);
    }

}
