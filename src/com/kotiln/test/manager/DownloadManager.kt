package com.kotiln.test.manager

import java.io.File
import java.net.URL
import java.util.*
import java.util.regex.Pattern


object Log {
    fun d(msg: String) {
        println("${Thread.currentThread().name} $msg")
    }
}

interface IDownloadListener {
    fun progress(url: String, progress: Int)
    fun startTask(url: String)
    fun complete(url: String)
    fun failed(url: String)
}

open class DownloadListenerImp : IDownloadListener {
    private var _progress: ((url: String, progress: Int) -> Unit)? = null
    private var _startTask: ((url: String) -> Unit)? = null
    private var _complete: ((url: String) -> Unit)? = null
    private var _failed: ((url: String) -> Unit)? = null
    override fun progress(url: String, progress: Int) {
        _progress?.invoke(url, progress)
    }

    override fun startTask(url: String) {
        _startTask?.invoke(url)
    }

    override fun complete(url: String) {
        _complete?.invoke(url)
    }

    override fun failed(url: String) {
        _failed?.invoke(url)
    }

    /**
     * support user imp
     */
    fun onProgress(block: (url: String, progress: Int) -> Unit) {
        _progress = block
    }

    fun onStartTask(block: (url: String) -> Unit) {
        _startTask = block
    }

    fun onComplete(block: (url: String) -> Unit) {
        _complete = block
    }

    fun onFailed(block: (url: String) -> Unit) {
        _failed = block
    }
}

class DownloadTask(var url: String, var listener: DownloadListenerImp?) : Runnable {
    var status: Int? = STATUS_WAITING
        set(value) {
            when (value) {
                DownloadTask.STATUS_PAUSE or DownloadTask.STATUS_STOP
                -> {
                    downloadThread?.let {
                        if (it.isAlive) it.interrupt()
                    }
                    Log.d("$url interrupted")
                }
                STATUS_WAITING -> Log.d("$url resume")
            }
            field = value
        }
    private var downloadThread: Thread? = null
    private var currentSize: Long? = 0
    private var totalSize: Long? = 0


    companion object {
        val STATUS_WAITING = 0
        val STATUS_RUNNING = 1
        val STATUS_PAUSE = 2
        val STATUS_FAILED = 3
        val STATUS_COMPLETE = 4
        val STATUS_STOP = 5
    }

    private fun readFromNet() {
        var fileName = getFileName()
        fileName?.length ?: throw DownloadException("fileName is empty")
        var engine = URL(url)
        var file = File(System.getProperty("user.dir") + File.separator + "out", fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.createNewFile()
        }
        if (file.exists()) file.delete()
        totalSize = engine.openConnection().contentLengthLong

        var byteread: Int = -1
        var buffer = ByteArray(1024 * 8)
        engine.openStream().use {
            do {
                byteread = it.read(buffer)
                if (byteread != -1) {
                    currentSize = currentSize!!.plus(byteread)
                    file.outputStream().use { it.write(buffer, 0, byteread) }
                    var progress = Integer.parseInt((currentSize!! * 100 / totalSize!!).toString())
                    listener?.progress(url, progress)
                }
            } while (byteread != -1)
        }
    }

    private class DownloadException(override val message: String) : Throwable() {

    }

    private fun getFileName(): String? {
        val pattern = Pattern.compile("[^/\\\\]+$")
        val matcher = pattern.matcher(url)
        var fileName: String? = null
        if (matcher.find()) {
            fileName = matcher.group()
        }
        return fileName
    }

    override fun run() {
        status = STATUS_RUNNING
        listener?.startTask(url)

        try {
            readFromNet()
        } catch (e: Exception) {
            Log.d("download error: $e")
            if (status == STATUS_RUNNING) {
                listener?.failed(url)
            }
            return
        }

        status = STATUS_COMPLETE
        listener?.complete(url)
    }

    fun start() {
        downloadThread = Thread(this, "Thread-$url")
        downloadThread?.start()
    }

    fun pause() {
        status = STATUS_PAUSE
    }

    fun resume() {
        status = STATUS_WAITING
    }

    fun stop() {
        status = STATUS_STOP
    }
}


class DownloadManager private constructor() {
    private var listener: DownloadListenerImp? = null
    private val taskList: LinkedList<DownloadTask> by lazy {
        LinkedList<DownloadTask>()
    }

    companion object {
        private var instance: DownloadManager? = null

        fun getInstance(tag: String = ""): DownloadManager {
            if (instance == null) {
                instance = DownloadManager()
            }
            return instance!!
        }
    }

    fun registerListener(init: (DownloadListenerImp.() -> Unit)): DownloadManager {
        var f = DownloadListenerImp()
        f.init()
        this.listener = f
        return this
    }

    @Synchronized
    fun addTask(url: String): DownloadManager {
        if (taskList.none { it.url.equals(url) }) {
            taskList.add(DownloadTask(url, object : DownloadListenerImp() {
                override fun progress(url: String, progress: Int) {
                    super.progress(url, progress)
                    listener?.progress(url, progress)
                }

                override fun startTask(url: String) {
                    super.startTask(url)
                    listener?.startTask(url)
                }

                override fun failed(url: String) {
                    super.failed(url)
                    listener?.failed(url)
                }

                override fun complete(url: String) {
                    super.complete(url)
                    removeTask(url)
                    listener?.complete(url)
                    schedule()
                }
            }))
        }
        return this
    }

    @Synchronized
    private fun removeTask(url: String) {
        taskList.dropWhile { it.url.equals(url) }
    }

    @Synchronized
    private fun schedule() {
        var t = taskList.firstOrNull { it.status == DownloadTask.STATUS_RUNNING }
        if (t != null) {
            //do nothing when the taskList contains running task
        } else {
            taskList.firstOrNull {
                it.status == DownloadTask.STATUS_WAITING
            }?.let {
                it.start()
            }
        }
    }

    fun start() {
        Log.d("start")
        schedule()
    }

    fun pause(url: String) {
        Log.d("pause")
        taskList.filter { it.url.equals(url) }.forEach {
            it.pause()
        }
        schedule()
    }

    fun resume(url: String) {
        Log.d("resume")
        taskList.filter { it.url.equals(url) }.forEach {
            it.resume()
        }
        schedule()
    }

    fun stop(url: String) {
        Log.d("stop")
        taskList.filter { it.url.equals(url) }.forEach {
            it.stop()
        }
        removeTask(url)
        schedule()
    }
}

fun main(args: Array<String>) {
    DownloadManager.getInstance().registerListener {
        onStartTask { url ->
            Log.d("start task: $url")
        }
        onProgress { url, progress ->
            Log.d(" progress:$progress")
        }
        onComplete { url ->
            Log.d(" complete!")
        }
    }.addTask("http://ucdl.25pp.com/fs01/union_pack/Wandoujia_180581_web_seo_baidu_homepage.apk").start()

//    Thread.sleep(2000)
//    DownloadManager.getInstance().pause("http://www.google.com/download.zip")
//    Thread.sleep(2000)
//    DownloadManager.getInstance().resume("http://www.google.com/download.zip")
//    Thread.sleep(2000)
//    DownloadManager.getInstance().stop("http://www.google.com/download.zip")
}