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

class DownloadManager {
    private var listener: DownloadListenerImp? = null
    private val taskList: LinkedList<DownloadTask> by lazy {
        LinkedList<DownloadTask>()
    }

    private var isRunning: Boolean = false
    private var downloadThread: Thread? = null

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

    class DownloadTask(var url: String, var listener: DownloadListenerImp?) : Runnable {

        override fun run() {
            listener?.newTask(url)
            var index = 0
            while (index++ < 10) {
                Thread.sleep(1000)
                listener?.progress(url, index * 10)
            }
            listener?.complete(url)
        }

    }

    @Synchronized
    fun addTask(url: String): DownloadManager {
        taskList.add(DownloadTask(url, object : DownloadListenerImp() {
            override fun progress(url: String, progress: Int) {
                super.progress(url, progress)
                listener?.progress(url, progress)
            }

            override fun newTask(url: String) {
                super.newTask(url)
                isRunning = true
                listener?.newTask(url)
            }

            override fun failed(url: String) {
                super.failed(url)
                isRunning = false
                listener?.failed(url)
            }

            override fun complete(mUrl: String) {
                super.complete(url)
                isRunning = false
                var currentTask = taskList.filter {
                    with(it) {
                        url.equals(mUrl)
                    }
                }.getOrNull(0)
                currentTask?.let {
                    taskList.remove(currentTask)
                }

                listener?.complete(mUrl)
                schedule()
            }
        }))
        return this
    }

    @Synchronized
    private fun schedule() {
        if (!isRunning) {
            var runnable = taskList.getOrNull(0)
            runnable?.let {
                downloadThread = Thread(runnable)
                downloadThread!!.start()
            }
        }
    }

    fun start() {
        schedule()
    }
}

fun main(args: Array<String>) {
    DownloadManager.getInstance().registerListener {
        onNewTask { url ->
            println("new task: $url")
        }
        onProgress { url, progress ->
            println("progress:$progress")
        }
        onComplete { url ->
            println("complete!")
        }
    }.addTask("http://www.google.com/download.zip").addTask("http://www.baidu.com/download.zip").start()
}