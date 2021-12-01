# netty-demo
netty demo

## 1 IO请求过程
第一个阶段为I/O 调用阶段，即用户进程向内核发起系统调用。

第二个阶段为I/O 执行阶段。此时，内核等待 I/O 请求处理完成返回。该阶段分为两个过程：首先等待数据就绪，并写入内核缓冲区；随后将内核缓冲区数据拷贝至用户态缓冲区。

为了方便大家理解，可以看一下这张图：

![Drawing 0.png](https://s0.lgstatic.com/i/image/M00/60/29/Ciqc1F-NAZ6Ae3bPAAHigveMsIQ514.png)

## 2  Linux 的 5 种主要 I/O 模式

1. 同步阻塞 I/O（BIO B-Blocking）

![1.png](https://s0.lgstatic.com/i/image/M00/61/05/CgqCHl-OnUKAeEELAAEnHU3FHGA343.png)

如上图所表现的那样，应用进程向内核发起 I/O 请求，发起调用的线程一直等待内核返回结果。一次完整的 I/O 请求称为BIO（Blocking IO，阻塞 I/O），所以 BIO 在实现异步操作时，只能使用多线程模型，一个请求对应一个线程。但是，线程的资源是有限且宝贵的，创建过多的线程会增加线程切换的开销。



2. 同步非阻塞 I/O（NIO N-Non-blocked）

![2.png](https://s0.lgstatic.com/i/image/M00/60/F9/Ciqc1F-OnTeAFLNhAAFptS-OxRY266.png)

如上图所示，应用进程向内核发起 I/O 请求后不再会同步等待结果，而是会立即返回，通过轮询的方式获取请求结果。NIO 相比 BIO 虽然大幅提升了性能，但是轮询过程中大量的系统调用导致上下文切换开销很大。所以，单独使用非阻塞 I/O 时效率并不高，而且随着并发量的提升，非阻塞 I/O 会存在严重的性能浪费。

3. I/O 多路复用

![3.png](https://s0.lgstatic.com/i/image/M00/61/05/CgqCHl-OnV2ADXBhAAFUZ6oiz6U529.png)

多路复用实现了一个线程处理多个 I/O 句柄的操作。多路指的是多个数据通道，复用指的是使用一个或多个固定线程来处理每一个 Socket。select、poll、epoll 都是 I/O 多路复用的具体实现，线程一次 select 调用可以获取内核态中多个数据通道的数据状态。多路复用解决了同步阻塞 I/O 和同步非阻塞 I/O 的问题，是一种非常高效的 I/O 模型。

4. 信号驱动 I/O

![4.png](https://s0.lgstatic.com/i/image/M00/61/05/CgqCHl-OnWqAddLWAAFUtZ6YHDA683.png)

信号驱动 I/O 并不常用，它是一种半异步的 I/O 模型。在使用信号驱动 I/O 时，当数据准备就绪后，内核通过发送一个 SIGIO 信号通知应用进程，应用进程就可以开始读取数据了。

5. 异步 I/O

![5.png](https://s0.lgstatic.com/i/image/M00/60/FA/Ciqc1F-OnXSAHOGVAACvxV3_3Mk188.png)

异步 I/O 最重要的一点是从内核缓冲区拷贝数据到用户态缓冲区的过程也是由系统异步完成，应用进程只需要在指定的数组中引用数据即可。异步 I/O 与信号驱动 I/O 这种半异步模式的主要区别：信号驱动 I/O 由内核通知何时可以开始一个 I/O 操作，而异步 I/O 由内核通知 I/O 操作何时已经完成。





了解了上述五种 I/O，我们再来看 Netty 如何实现自己的 I/O 模型。Netty 的 I/O 模型是基于**非阻塞 I/O** 实现的，底层依赖的是 JDK NIO 框架的多路复用器 Selector。一个多路复用器 Selector 可以同时轮询多个 Channel，采用 epoll 模式后，只需要一个线程负责 Selector 的轮询，就可以接入成千上万的客户端。



在 I/O 多路复用的场景下，当有数据处于就绪状态后，需要一个**事件分发器**（Event Dispather），它负责将读写事件分发给对应的**读写事件处理器**（Event Handler）。事件分发器有两种设计模式：Reactor 和 Proactor，Reactor 采用同步 I/O， Proactor 采用异步 I/O。

Reactor 实现相对简单，适合处理耗时短的场景，对于耗时长的 I/O 操作容易造成阻塞。Proactor 性能更高，但是实现逻辑非常复杂，目前主流的事件驱动模型还是依赖 select 或 epoll 来实现。

![6.png](https://s0.lgstatic.com/i/image/M00/60/47/Ciqc1F-NKE-AWqZfAARsOnKW3pg690.png)

上图所描述的便是 Netty 所采用的主从 Reactor 多线程模型，所有的 I/O 事件都注册到一个 I/O 多路复用器上，当有 I/O 事件准备就绪后，I/O 多路复用器会将该 I/O 事件通过事件分发器分发到对应的事件处理器中。该线程模型避免了同步问题以及多线程切换带来的资源开销，真正做到高性能、低延迟。



Netty 经过很多出名产品在线上的大规模验证，其健壮性和稳定性都被业界认可，其中典型的产品有一下几个。

服务治理：Apache Dubbo、gRPC。

大数据：Hbase、Spark、Flink、Storm。

搜索引擎：Elasticsearch。

消息队列：RocketMQ、ActiveMQ。