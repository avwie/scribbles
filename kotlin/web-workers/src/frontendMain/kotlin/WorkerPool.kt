import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

    @Suppress("UNCHECKED_CAST")
    suspend fun <R : RequestResult> request(request: Request<R>): R {
        val data = Json.encodeToString(request as Request<RequestResult>)
        val response = send(data)
        val deserialized = Json.decodeFromString<Response>(response)

        if (deserialized.error != null) throw WorkerException(deserialized.error)
        return deserialized.result as R
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