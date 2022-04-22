import nl.avwie.webworkers.Initialize
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.Response
import org.w3c.dom.Worker
import kotlin.math.min

class WorkerPool(size: Int, private val workerScript: String) {

    class Job<R : Response>(private val request: Request<R>, private val callback: (worker: Worker, response: R) -> Unit) {
        fun execute(worker: Worker) {
            worker.request(request) { response ->
                callback(worker, response)
            }
        }
    }

    private val availableWorkers = ArrayDeque<Worker>()
    private val queue = ArrayDeque<Job<*>>()

    init {
        repeat(size) { nr ->
            val worker = Worker(workerScript)
            val workerId = "Worker [$workerScript-$nr]"
            worker.request(Initialize(workerId)) {
                availableWorkers.addLast(worker)
                console.log("$workerId initialized")
                availableWorkers.addLast(worker)
                checkAvailableWork()
            }
        }
    }

    fun <R : Response> request(request: Request<R>, callback: (response: R) -> Unit) {
        val job = Job(request) { worker, response ->
            availableWorkers.addLast(worker)
            checkAvailableWork()
            callback(response)
        }
        queue.addLast(job)
        checkAvailableWork()
    }

    private fun checkAvailableWork() {
        if (queue.isEmpty() || availableWorkers.isEmpty()) return
        val noOfJobs = min(queue.size, availableWorkers.size)

        val work = (0 until noOfJobs).map { queue.removeFirst() to availableWorkers.removeFirst() }
        work.forEach { (job, worker) ->
            job.execute(worker)
        }
    }
}