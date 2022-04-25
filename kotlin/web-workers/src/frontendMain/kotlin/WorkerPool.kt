import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.RequestResult
import org.w3c.dom.Worker
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

/*class WorkerPool(size: Int, private val workerScript: String) {

    data class WorkerJob<R : RequestResult>(val request: Request<R>, val continuation: Continuation<R>) {
        suspend fun execute(worker: Worker) {
            try {
                val response = worker.request(request)
                continuation.resume(response)
            } catch (t: Throwable) {
                continuation.resumeWithException(t)
            }
        }
    }

    private val availableWorkers = ArrayDeque<Worker>()
    private val queue = ArrayDeque<WorkerJob<*>>()

    init {
        repeat(size) { nr ->
            availableWorkers.addLast(Worker("$workerScript?id=Worker-$nr"))
        }
    }

    suspend fun <R : RequestResult> request(request: Request<R>) = suspendCoroutine<R> { continuation ->
        queue.addLast(WorkerJob(request, continuation))
        checkAvailableWork()
    }

    private fun checkAvailableWork() {
        if (queue.isEmpty() || availableWorkers.isEmpty()) return
        val noOfJobs = min(queue.size, availableWorkers.size)
        val work = (0 until noOfJobs).map { queue.removeFirst() to availableWorkers.removeFirst() }
        work.forEach { (job, worker) ->
            GlobalScope.launch {
                job.execute(worker)
                availableWorkers.addLast(worker)
                checkAvailableWork()
            }
        }
    }
}*/

class WorkerPool(size: Int, private val workerScript: String) {

    data class Job(val data: String, val continuation: Continuation<String>) {
        suspend fun execute(worker: Worker) {
            try {
                val response = worker.send(data)
                continuation.resume(response.data.toString())
            } catch (t: Throwable) {
                continuation.resumeWithException(t)
            }
        }
    }

    private val availableWorkers = ArrayDeque<Worker>()
    private val jobs = ArrayDeque<Job>()

    init {
        repeat(size) { nr ->
            availableWorkers.addLast(Worker("$workerScript?id=Worker-$nr"))
        }
    }

    suspend fun send(data: String) = suspendCoroutine<String> { continuation ->
        jobs.addLast(Job(data, continuation))
        checkAvailableWork()
    }

    private fun checkAvailableWork() {
        if (jobs.isEmpty() || availableWorkers.isEmpty()) return
        val noOfMessages = min(jobs.size, availableWorkers.size)
        val work = (0 until noOfMessages).map { jobs.removeFirst() to availableWorkers.removeFirst() }
        work.forEach { (job, worker) ->
            GlobalScope.launch {
                job.execute(worker)
                availableWorkers.addLast(worker)
                checkAvailableWork()
            }
        }
    }
}