package com.kotiln.test.manager

import java.util.*

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


    companion object {
        val STATUS_WAITING = 0
        val STATUS_RUNNING = 1
        val STATUS_PAUSE = 2
        val STATUS_FAILED = 3
        val STATUS_COMPLETE = 4
        val STATUS_STOP = 5
    }

    override fun run() {
        status = STATUS_RUNNING
        listener?.startTask(url)
        var index = 0
        while (index++ < 10) {
            if (status == STATUS_RUNNING) {
                try {
                    Thread.sleep(1000)
                    listener?.progress(url, index * 10)
                } catch (e: Exception) {
                    Log.d("${e.message}")
                    return
                }
            } else {
                return
            }
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
    }.addTask("http://www.google.com/download.zip").addTask("http://www.baidu.com/download.zip").start()

    Thread.sleep(2000)
    DownloadManager.getInstance().pause("http://www.google.com/download.zip")
    Thread.sleep(2000)
    DownloadManager.getInstance().resume("http://www.google.com/download.zip")
    Thread.sleep(2000)
    DownloadManager.getInstance().stop("http://www.google.com/download.zip")
}