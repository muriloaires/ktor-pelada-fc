package web.request

import com.google.gson.Gson
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.io.errors.IOException
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object MultipartHandler {
    private val uploadDir: File = File("C:\\ktor\\uploads")

    init {
        if (!uploadDir.mkdirs() && !uploadDir.exists()) {
            throw IOException("Failed to create directory ${uploadDir.absolutePath}")
        }
    }

    suspend fun saveMultipartFile(part: PartData.FileItem): String? {
        return part.originalFileName?.let { originalFileName ->
            val ext = File(originalFileName).extension
            val file = File(
                uploadDir,
                "${System.currentTimeMillis()}.$ext"
            )
            part.streamProvider()
                .use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
            file.name
        }
    }

    fun <T> getFormItem(part: PartData.FormItem, clazz: Class<T>): T {
        return Gson().fromJson(part.value, clazz)
    }

    /**
     * Utility boilerplate method that suspending,
     * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
     *
     * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
     * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
     */
    private suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Long {
        return withContext(dispatcher) {
            val buffer = ByteArray(bufferSize)
            var bytesCopied = 0L
            var bytesAfterYield = 0L
            while (true) {
                val bytes = read(buffer).takeIf { it >= 0 } ?: break
                out.write(buffer, 0, bytes)
                if (bytesAfterYield >= yieldSize) {
                    yield()
                    bytesAfterYield %= yieldSize
                }
                bytesCopied += bytes
                bytesAfterYield += bytes
            }
            return@withContext bytesCopied
        }
    }

}