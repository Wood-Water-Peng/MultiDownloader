# MultiDownloader

### 为什么要做这个项目

最近一直在听喜马拉雅(绝对不是广告)，经常离线下载音频节目，觉得这一块以前并没有认真的做过，算是一块短板，遂起了这个念头。

思路：

很简单的一个小项目，分为`全部任务`和`下载完成`两个模块，一单任务下载完成，通知栏进行通知。

### 简单分析

**下载的实现**

为了记录每一个文件的下载状态，所以必须要用Sqlite保存下载的状态，如文件的总大小、已下载大小、下载状态(完成？暂停？)等等信息。在下载过程中，有两个异常需要考虑：

1. 网络中断
2. 进程被杀死

下载类DownloadManager为DownloadService的静态类，所以他的生命周期和DownloadService相同，当DownloadService被销毁时，我们手动地清理DownloadManager。

DownloadService提供下载接口，DownloadManager具体实现。

**RecyclerView中的复用问题**

Adapter为Activity提供界面，而Adapter中的数据来自于下载线程，在下载线程中肯定会有一个监听器，监听器拿到实时数据，进而在adapter中更新数据。现在的问题是，怎样来设置这个监听器？我是这么理解的：当用户点击下载时，便有了一条下载记录，这个记录和一个下载线程是一一对应的，所以，我们可以将监听器和下载记录绑定起来。这样，监听器拿到的就是正确的下载记录，然后再adapter中更新界面。

**数据库的使用**

[GreenDao](https://github.com/greenrobot/greenDAO) 这个看文档就可以了



 

