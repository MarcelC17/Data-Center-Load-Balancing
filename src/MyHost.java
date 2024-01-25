    /* Implement this class. */
    import java.util.*;


    public class MyHost extends Host {

        //Taskul care ruleaza (running)
        Task running_task = null;

        //Timpul de start, folosit la calcularea timpului de rulare a unui task
        Long start_time = 0L;

        //Coada de prioritati
        PriorityQueue<Task> queue = new PriorityQueue<Task>(new ComparatorTasks());

        //Defineste logica de sortare din priority queue, comparand prioritatile si id
        class ComparatorTasks implements Comparator<Task> {
            @Override
            public int compare(Task t1, Task t2){
                //Compara prioritatile
                int compare_priority = Integer.compare(t2.getPriority(), t1.getPriority());
                //Daca sunt egale compara ID
                if (compare_priority == 0) {
                    return Integer.compare(t1.getId(), t2.getId());
                }

                return compare_priority;
            }
        }


        //Rularea taskului
        @Override
        public void run() {
            //Citeste din coada, opreste daca toate taskurile au fost rulate si a primit semnalul de shutdown
            while (!Thread.interrupted() || !queue.isEmpty()) {

                synchronized (this) {
                    if (!queue.isEmpty()) {
                        //Seteaza taskul cu prioritatea cea mai mare ca running
                        running_task = queue.poll();
                    }
                }


                if (running_task != null) {
                    try {
                        //Pastreaza timpul la care a inceput rularea taskului
                        start_time = System.currentTimeMillis();
                        //Executa taskului
                        Thread.sleep(running_task.getLeft());
                        //Semnaleaza ca sa finisat executia
                        running_task.finish();
                        running_task = null;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {;
                        running_task = null;
                    }
                }
            }
        }

        @Override
        public void addTask(Task task){
            synchronized (this) {
                //Compara prioritatea taskului primit cu a celui care ruleaza
                if (running_task != null &&
                        running_task.isPreemptible() && running_task.getPriority() < task.getPriority()) {

                    //Calculeaza timpul de rulare ramas pentru taskul running
                    running_task.setLeft(running_task.getLeft() - (long) (System.currentTimeMillis() - start_time));
                    //Adauga taskul cu timpul nou in coada
                    queue.add(running_task);
                    //Intrerupe rullarea taskului running
                    interrupt();
                }
                //Adauga taskul nou in coada
                queue.add(task);
            }
        }

        @Override
        public int getQueueSize() {
            //Returneaza dimensiunea cozii de asteptare tinand cont de taskul care ruleaza
            synchronized (this) {
                if (running_task == null)
                    return queue.size();
                else return queue.size()+1;
            }

        }

        @Override
        public long getWorkLeft() {
            //Calculeaza tmipul de rulare ramas, iterand prin coada
            long total_work =0;
            for (Task task : queue){
                total_work += task.getLeft();
            }
            //Adauga timpul taskului running
            if (running_task != null)
                return total_work + running_task.getLeft();
            else return total_work;
        }

        @Override
        public void shutdown() {
            //Intrerupe executia
            interrupt();
        }
    }