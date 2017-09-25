package com.kotiln.test.manager

import java.util.*

interface IDownloadListener {
    fun progress(url: String, progress: Int)
    fun newTask(url: String)
    fun complete(url: String)
    fun failed(url: String)
}

open class DownloadListenerImp : IDownloadListener {
    private var _progress: ((url: String, progress: Int) -> Unit)? = null
    private var _newTask: ((url: String) -> Unit)? = null
    private var _complete: ((url: String) -> Unit)? = null
    private var _failed: ((url: String) -> Unit)? = null
    override fun progress(url: String, progress: Int) {
        _progress?.invoke(url, progress)
    }

    override fun newTask(url: String) {
        _newTask?.invoke(url)
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

    fun onNewTask(block: (url: String) -> Unit) {
        _newTask = block
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
                DownloadTask.STATUS_PAUSE -> {
                    downloadThread?.let {
                        if (it.isAlive) it.interrupt()
                    }
                    println("$url paused")
                }
                STATUS_WAITING -> println("$url resume")
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
    }

    override fun run() {
        status = STATUS_RUNNING
        listener?.newTask(url)
        var index = 0
        while (index++ < 10) {
            if (status == STATUS_RUNNING) {
                try {
                    Thread.sleep(1000)
                    listener?.progress(url, index * 10)
                } catch (e: Exception) {
                    println("${Thread.currentThread().name} ${e.message}")
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
        downloadThread = Thread(this)
        downloadThread?.start()
    }

    fun pause() {
        status = STATUS_PAUSE
    }

    fun resume() {
        status = STATUS_WAITING
    }
}


class DownloadManager {
    private var listener: DownloadListenerImp? = null
    private val taskList: LinkedList<DownloadTask> by lazy {
        LinkedList<DownloadTask>()
    }

    private constructor()

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

                override fun newTask(url: String) {
                    super.newTask(url)
                    listener?.newTask(url)
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
        var currentTask = taskList.filter {
            it.url.equals(url)
        }.getOrNull(0)
        currentTask?.let {
            taskList.remove(currentTask)
        }
    }

    @Synchronized
    private fun schedule() {
        var t = taskList.firstOrNull { it.status == DownloadTask.STATUS_RUNNING }
        if (t != null) {
            //if current has task is running then do nothing
        } else {
            taskList.firstOrNull {
                it.status == DownloadTask.STATUS_WAITING
            }?.let {
                it.start()
            }
        }
    }

    fun start() {
        schedule()
    }

    fun pause(url: String) {
        taskList.filter { it.url.equals(url) }.forEach {
            it.pause()
        }
        schedule()
    }

    fun resume(url: String) {
        taskList.filter { it.url.equals(url) }.forEach {
            it.resume()
        }
        schedule()
    }
}

fun main(args: Array<String>) {
    DownloadManager.getInstance().registerListener {
        onNewTask { url ->
            println("${Thread.currentThread().name} new task: $url")
        }
        onProgress { url, progress ->
            println("${Thread.currentThread().name} progress:$progress")
        }
        onComplete { url ->
            println("${Thread.currentThread().name} complete!")
        }
    }.addTask("http://www.google.com/download.zip").addTask("http://www.baidu.com/download.zip").start()

    Thread.sleep(2000)
    DownloadManager.getInstance().pause("http://www.google.com/download.zip")
    Thread.sleep(2000)
    DownloadManager.getInstance().resume("http://www.google.com/download.zip")
}