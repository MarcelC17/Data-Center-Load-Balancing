/* Implement this class. */

import java.util.List;
import java.util.*;

public class MyDispatcher extends Dispatcher {

    private int lastAssignedNodeId = 0;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task){
        //Determina algoritmul rulat
        switch (this.algorithm){

            case ROUND_ROBIN:
                //calculeaza Id host
                lastAssignedNodeId = (lastAssignedNodeId + 1) % (hosts.size());
                hosts.get(lastAssignedNodeId).addTask(task);
                break;

            case SHORTEST_QUEUE:
                //Cel mai mic queue
                long least = 100;
                //Pozitia hostului cu cel mai mic queue
                int host_no = 100;

                //Gaseste id hostului cu cel mai mic queue
                for (Host  host : hosts) {
                    if (host.getQueueSize() < least) {
                        least = host.getQueueSize();
                        host_no = hosts.indexOf(host);
                    }
                }
                hosts.get(host_no).addTask(task);
                break;

            case SIZE_INTERVAL_TASK_ASSIGNMENT:
                //Adauga nodurile in dependenta de tip
                switch (task.getType()){
                    case SHORT -> hosts.get(0).addTask(task);
                    case MEDIUM -> hosts.get(1).addTask(task);
                    case LONG -> hosts.get(2).addTask(task);
                }
                break;

            case LEAST_WORK_LEFT:
                //Cel mai putin timp de lucru ramas
                long least_work = 1000000;
                //Pozitia hostului cu cel mai putin timp de lucru
                int host_no_lwl = 100;

                for (Host  host : hosts) {
                    if (host.getWorkLeft() < least_work) {
                        least_work = host.getWorkLeft();
                        host_no_lwl = hosts.indexOf(host);
                    }
                }

                hosts.get(host_no_lwl).addTask(task);
                break;
        }

    }
}

