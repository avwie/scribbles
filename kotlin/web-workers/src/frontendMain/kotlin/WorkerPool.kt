import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import nl.avwie.webworkers.Initialize
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.RequestResult
import nl.avwie.webworkers.Response
import org.w3c.dom.Worker
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

class WorkerPool(size: Int, private val workerScript: String) {

    class InitializedWorker(val workerId: String, val worker: Worker)

    data class WorkerJob<R : RequestResult>(val request: Request<R>, val continuation: Continuation<R>) {
        suspend fun execute(worker: InitializedWorker) {
            try {
                val response = worker.worker.request(request)
                continuation.resume(response)
            } catch (t: Throwable) {
                continuation.resumeWithException(t)
            }
        }
    }

    private val availableWorkers = ArrayDeque<InitializedWorker>()
    private val queue = ArrayDeque<WorkerJob<*>>()

    init {
        with(GlobalScope) {
            launch {
                val initializations = (0 until size).map { nr ->
                    launch {
                        val worker = Worker(workerScript)
                        val workerId = "Worker [$workerScript-$nr]"
                        worker.request(Initialize(workerId))
                        console.log("$workerId initialized")
                        availableWorkers.addLast(InitializedWorker(workerId, worker))
                    }
                }

                initializations.joinAll()
                console.log("Fully initialized")
                checkAvailableWork()
            }
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
}