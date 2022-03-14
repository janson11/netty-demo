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

## 3 Netty 整体结构

Netty 是一个设计非常用心的网络基础组件，Netty 官网给出了有关 Netty 的整体功能模块结构，却没有其他更多的解释。从图中，我们可以清晰地看出 Netty 结构一共分为三个模块：

![Drawing 0.png](https://s0.lgstatic.com/i/image/M00/60/64/CgqCHl-NO7eATPMMAAH8t8KvehQ985.png)

1. Core 核心层
Core 核心层是 Netty 最精华的内容，它提供了底层网络通信的通用抽象和实现，包括可扩展的事件模型、通用的通信 API、支持零拷贝的 ByteBuf 等。

2. Protocol Support 协议支持层
协议支持层基本上覆盖了主流协议的编解码实现，如 HTTP、SSL、Protobuf、压缩、大文件传输、WebSocket、文本、二进制等主流协议，此外 Netty 还支持自定义应用层协议。Netty 丰富的协议支持降低了用户的开发成本，基于 Netty 我们可以快速开发 HTTP、WebSocket 等服务。

3. Transport Service 传输服务层
传输服务层提供了网络传输能力的定义和实现方法。它支持 Socket、HTTP 隧道、虚拟机管道等传输方式。Netty 对 TCP、UDP 等数据传输做了抽象和封装，用户可以更聚焦在业务逻辑实现上，而不必关系底层数据传输的细节。

## 4 Netty 逻辑架构

下图是 Netty 的逻辑处理架构。Netty 的逻辑处理架构为典型网络分层架构设计，共分为网络通信层、事件调度层、服务编排层，每一层各司其职。图中包含了 Netty 每一层所用到的核心组件。我将为你介绍 Netty 的每个逻辑分层中的各个核心组件以及组件之间是如何协调运作的。

![Drawing 1.png](https://s0.lgstatic.com/i/image/M00/60/58/Ciqc1F-NO9KAUOtaAAE1S5uRlDE275.png)

**网络通信层**
网络通信层的职责是执行网络 I/O 的操作。它支持多种网络协议和 I/O 模型的连接操作。当网络数据读取到内核缓冲区后，会触发各种网络事件，这些网络事件会分发给事件调度层进行处理。

网络通信层的核心组件包含BootStrap、ServerBootStrap、Channel三个组件。

BootStrap & ServerBootStrap

**事件调度层**
事件调度层的职责是通过 Reactor 线程模型对各类事件进行聚合处理，通过 Selector 主循环线程集成多种事件（ I/O 事件、信号事件、定时事件等），实际的业务处理逻辑是交由服务编排层中相关的 Handler 完成。

事件调度层的核心组件包括 EventLoopGroup、EventLoop。

EventLoopGroup & EventLoop

EventLoopGroup 本质是一个线程池，主要负责接收 I/O 请求，并分配线程执行处理请求。在下图中，我为你讲述了 EventLoopGroups、EventLoop 与 Channel 的关系。

![Drawing 4.png](https://s0.lgstatic.com/i/image/M00/60/64/CgqCHl-NPG6APzDfAAbX5ACAFh8001.png)

从上图中，我们可以总结出 EventLoopGroup、EventLoop、Channel 的几点关系。

一个 EventLoopGroup 往往包含一个或者多个 EventLoop。EventLoop 用于处理 Channel 生命周期内的所有 I/O 事件，如 accept、connect、read、write 等 I/O 事件。

EventLoop 同一时间会与一个线程绑定，每个 EventLoop 负责处理多个 Channel。

每新建一个 Channel，EventLoopGroup 会选择一个 EventLoop 与其绑定。该 Channel 在生命周期内都可以对 EventLoop 进行多次绑定和解绑



EventLoopGroup 是 Netty 的核心处理引擎，那么 EventLoopGroup 和之前课程所提到的 Reactor 线程模型到底是什么关系呢？其实 EventLoopGroup 是 Netty Reactor 线程模型的具体实现方式，Netty 通过创建不同的 EventLoopGroup 参数配置，就可以支持 Reactor 的三种线程模型：

1. 单线程模型：EventLoopGroup 只包含一个 EventLoop，Boss 和 Worker 使用同一个EventLoopGroup；
2. 多线程模型：EventLoopGroup 包含多个 EventLoop，Boss 和 Worker 使用同一个EventLoopGroup；
3. 主从多线程模型：EventLoopGroup 包含多个 EventLoop，Boss 是主 Reactor，Worker 是从 Reactor，它们分别使用不同的 EventLoopGroup，主 Reactor 负责新的网络连接 Channel 创建，然后把 Channel 注册到从 Reactor。

**服务编排层**
服务编排层的职责是负责组装各类服务，它是 Netty 的核心处理链，用以实现网络事件的动态编排和有序传播。

服务编排层的核心组件包括 ChannelPipeline、ChannelHandler、ChannelHandlerContext。

ChannelPipeline

ChannelPipeline 是 Netty 的核心编排组件，负责组装各种 ChannelHandler，实际数据的编解码以及加工处理操作都是由 ChannelHandler 完成的。ChannelPipeline 可以理解为ChannelHandler 的实例列表——内部通过双向链表将不同的 ChannelHandler 链接在一起。当 I/O 读写事件触发时，ChannelPipeline 会依次调用 ChannelHandler 列表对 Channel 的数据进行拦截和处理。

![Drawing 6.png](https://s0.lgstatic.com/i/image/M00/60/64/CgqCHl-NPJ-AHaUvAA7mpp9SCqY582.png)



**组件关系梳理**



![Drawing 9.png](https://s0.lgstatic.com/i/image/M00/60/59/Ciqc1F-NPLeAPdjRAADyud16HmQ759.png)

- 服务端启动初始化时有 Boss EventLoopGroup 和 Worker EventLoopGroup 两个组件，其中 Boss 负责监听网络连接事件。当有新的网络连接事件到达时，则将 Channel 注册到 Worker EventLoopGroup。


- Worker EventLoopGroup 会被分配一个 EventLoop 负责处理该 Channel 的读写事件。每个 EventLoop 都是单线程的，通过 Selector 进行事件循环。


- 当客户端发起 I/O 读写事件时，服务端 EventLoop 会进行数据的读取，然后通过 Pipeline 触发各种监听器进行数据的加工处理。


- 客户端数据会被传递到 ChannelPipeline 的第一个 ChannelInboundHandler 中，数据处理完成后，将加工完成的数据传递给下一个 ChannelInboundHandler。


- 当数据写回客户端时，会将处理结果在 ChannelPipeline 的 ChannelOutboundHandler 中传播，最后到达客户端。



### 轻量级对象回收站：Recycler 对象池技术解析

Recycler 的设计理念
对象池与内存池的都是为了提高 Netty 的并发处理能力，我们知道 Java 中频繁地创建和销毁对象的开销是很大的，所以很多人会将一些通用对象缓存起来，当需要某个对象时，优先从对象池中获取对象实例。通过重用对象，不仅避免频繁地创建和销毁所带来的性能损耗，而且对 JVM GC 是友好的，这就是对象池的作用





##  I/O 加速：与众不同的 Netty 零拷贝技术

 Linux 中系统调用 sendfile() 可以实现将数据从一个文件描述符传输到另一个文件描述符，从而实现了零拷贝技术。在 Java 中也使用了零拷贝技术，它就是 NIO FileChannel 类中的 transferTo() 方法，transferTo() 底层就依赖了操作系统零拷贝的机制，它可以将数据从 FileChannel 直接传输到另外一个 Channel。transferTo() 方法的定义如下：

![Drawing 0.png](https://s0.lgstatic.com/i/image/M00/80/0B/Ciqc1F_Qbz2AD4uMAARnlgeSFc4993.png)

从上图中可以看出，从数据读取到发送一共经历了四次数据拷贝，具体流程如下：

当用户进程发起 read() 调用后，上下文从用户态切换至内核态。DMA 引擎从文件中读取数据，并存储到内核态缓冲区，这里是第一次数据拷贝。

请求的数据从内核态缓冲区拷贝到用户态缓冲区，然后返回给用户进程。第二次数据拷贝的过程同时，会导致上下文从内核态再次切换到用户态。

用户进程调用 send() 方法期望将数据发送到网络中，此时会触发第三次线程切换，用户态会再次切换到内核态，请求的数据从用户态缓冲区被拷贝到 Socket 缓冲区。

最终 send() 系统调用结束返回给用户进程，发生了第四次上下文切换。第四次拷贝会异步执行，从 Socket 缓冲区拷贝到协议引擎中。

说明：DMA（Direct Memory Access，直接内存存取）是现代大部分硬盘都支持的特性，DMA 接管了数据读写的工作，不需要 CPU 再参与 I/O 中断的处理，从而减轻了 CPU 的负担。



Netty 的零拷贝技术
介绍完传统 Linux 的零拷贝技术之后，我们再来学习下 Netty 中的零拷贝如何实现。Netty 中的零拷贝和传统 Linux 的零拷贝不太一样。Netty 中的零拷贝技术除了操作系统级别的功能封装，更多的是面向用户态的数据操作优化，主要体现在以下 5 个方面：

- 堆外内存，避免 JVM 堆内存到堆外内存的数据拷贝。


- CompositeByteBuf 类，可以组合多个 Buffer 对象合并成一个逻辑上的对象，避免通过传统内存拷贝的方式将几个 Buffer 合并成一个大的 Buffer。


- 通过 Unpooled.wrappedBuffer 可以将 byte 数组包装成 ByteBuf 对象，包装过程中不会产生内存拷贝。

- ByteBuf.slice 操作与 Unpooled.wrappedBuffer 相反，slice 操作可以将一个 ByteBuf 对象切分成多个 ByteBuf 对象，切分过程中不会产生内存拷贝，底层共享一个 byte 数组的存储空间。

- Netty 使用 FileRegion 实现文件传输，FileRegion 底层封装了 FileChannel#transferTo() 方法，可以将文件缓冲区的数据直接传输到目标 Channel，避免内核缓冲区和用户态缓冲区之间的数据拷贝，这属于操作系统级别的零拷贝。




## 从 Linux 出发深入剖析服务端启动流程

开始深入分析 Netty 服务端的启动流程。在服务端启动之前，需要配置 ServerBootstrap 的相关参数，这一步大致可以分为以下几个步骤：

1. 配置 EventLoopGroup 线程组；

2. 配置 Channel 的类型；

3. 设置 ServerSocketChannel 对应的 Handler；

4. 设置网络监听的端口；

5. 设置 SocketChannel 对应的 Handler；

6. 配置 Channel 参数。



服务端 Channel 的过程我们已经讲完了，简单总结下其中几个重要的步骤：

1. ReflectiveChannelFactory 通过反射创建 NioServerSocketChannel 实例；

2. 创建 JDK 底层的 ServerSocketChannel；

3. 为 Channel 创建 id、unsafe、pipeline 三个重要的成员变量；

4. 设置 Channel 为非阻塞模式。



服务端启动的全流程

1. 创建服务端 Channel：本质是创建 JDK 底层原生的 Channel，并初始化几个重要的属性，包括 id、unsafe、pipeline 等。

2. 初始化服务端 Channel：设置 Socket 参数以及用户自定义属性，并添加两个特殊的处理器 ChannelInitializer 和 ServerBootstrapAcceptor。

3. 注册服务端 Channel：调用 JDK 底层将 Channel 注册到 Selector 上。

4. 端口绑定：调用 JDK 底层进行端口绑定，并触发 channelActive 事件，把 OP_ACCEPT 事件注册到 Channel 的事件集合中



Netty 服务端完全启动后，就可以对外工作了。接下来 Netty 服务端是如何处理客户端新建连接的呢？主要分为四步：

1. Boss NioEventLoop 线程轮询客户端新连接 OP_ACCEPT 事件；

2. 构造 Netty 客户端 NioSocketChannel；

3. 注册 Netty 客户端 NioSocketChannel 到 Worker 工作线程中；

4. 注册 OP_READ 事件到 NioSocketChannel 的事件集合。



Netty 服务端启动的相关源码层次比较深，推荐大家在读源码的时候，可以先把主体流程如下

![图片7.png](https://s0.lgstatic.com/i/image/M00/87/A2/CgqCHl_WABCAJA9VAAIG0Ncq3hs061.png)



NioEventLoop 的 run() 方法是一个无限循环，没有任何退出条件，在不间断循环执行以下三件事情，可以用下面这张图形象地表示。

![Lark20201216-164824.png](https://s0.lgstatic.com/i/image2/M01/02/1E/Cip5yF_ZyhCAM3GUAASrdEcuR2U593.png)

1. 轮询 I/O 事件（select）：轮询 Selector 选择器中已经注册的所有 Channel 的 I/O 事件。

2. 处理 I/O 事件（processSelectedKeys）：处理已经准备就绪的 I/O 事件。

3. 处理异步任务队列（runAllTasks）：Reactor 线程还有一个非常重要的职责，就是处理任务队列中的非 I/O 任务。Netty 提供了 ioRatio 参数用于调整 I/O 事件处理和任务处理的时间比例。



Netty 为了解决臭名昭著的 JDK epoll 空轮询 Bug

实际上 Netty 并没有从根源上解决该问题，而是巧妙地规避了这个问题。Netty 引入了计数变量 selectCnt，用于记录 select 操作的次数，如果事件轮询时间小于 timeoutMillis，并且在该时间周期内连续发生超过 SELECTOR_AUTO_REBUILD_THRESHOLD（默认512） 次空轮询，说明可能触发了 epoll 空轮询 Bug。Netty 通过重建新的 Selector 对象，将异常的 Selector 中所有的 SelectionKey 会重新注册到新建的 Selector，重建完成之后异常的 Selector 就可以废弃了



**一个网络请求在 Netty 中的旅程**

服务端接入客户端新连接为例

![Drawing 0.png](https://s0.lgstatic.com/i/image/M00/8A/4B/CgqCHl_Zyq-ALQoLAA9q2qihg8Q151.png)

Netty 服务端启动后，BossEventLoopGroup 会负责监听客户端的 Accept 事件。当有客户端新连接接入时，BossEventLoopGroup 中的 NioEventLoop 首先会新建客户端 Channel，然后在 NioServerSocketChannel 中触发 channelRead 事件传播，NioServerSocketChannel 中包含了一种特殊的处理器 ServerBootstrapAcceptor，最终通过 ServerBootstrapAcceptor 的 channelRead() 方法将新建的客户端 Channel 分配到 WorkerEventLoopGroup 中。WorkerEventLoopGroup 中包含多个 NioEventLoop，它会选择其中一个 NioEventLoop 与新建的客户端 Channel 绑定。

完成客户端连接注册之后，就可以接收客户端的请求数据了。当客户端向服务端发送数据时，NioEventLoop 会监听到 OP_READ 事件，然后分配 ByteBuf 并读取数据，读取完成后将数据传递给 Pipeline 进行处理。一般来说，数据会从 ChannelPipeline 的第一个 ChannelHandler 开始传播，将加工处理后的消息传递给下一个 ChannelHandler，整个过程是串行化执行。



ChannelPipeline 是双向链表结构，包含 ChannelInboundHandler 和 ChannelOutboundHandler 两种处理器。

Inbound 事件和 Outbound 事件的传播方向相反，Inbound 事件的传播方向为 Head -> Tail，而 Outbound 事件传播方向是 Tail -> Head。

异常事件的处理顺序与 ChannelHandler 的添加顺序相同，会依次向后传播，与 Inbound 事件和 Outbound 事件无关。

再整体回顾下 ChannelPipeline 中事件传播的实现原理：

Inbound 事件传播从 HeadContext 节点开始，Outbound 事件传播从 TailContext 节点开始。

AbstractChannelHandlerContext 抽象类中实现了一系列 fire 和 invoke 方法，如果想让事件想下传播，只需要调用 fire 系列的方法即可。fire 和 invoke 的系列方法结合 findContextInbound() 和 findContextOutbound() 可以控制 Inbound 和 Outbound 事件的传播方向，整个过程是一个递归调用。



ThreadLocal 和 FastThreadLocal，简单总结下 FastThreadLocal 的优势。

高效查找。FastThreadLocal 在定位数据的时候可以直接根据数组下标 index 获取，时间复杂度 O(1)。而 JDK 原生的 ThreadLocal 在数据较多时哈希表很容易发生 Hash 冲突，线性探测法在解决 Hash 冲突时需要不停地向下寻找，效率较低。此外，FastThreadLocal 相比 ThreadLocal 数据扩容更加简单高效，FastThreadLocal 以 index 为基准向上取整到 2 的次幂作为扩容后容量，然后把原数据拷贝到新数组。而 ThreadLocal 由于采用的哈希表，所以在扩容后需要再做一轮 rehash。

安全性更高。JDK 原生的 ThreadLocal 使用不当可能造成内存泄漏，只能等待线程销毁。在使用线程池的场景下，ThreadLocal 只能通过主动检测的方式防止内存泄漏，从而造成了一定的开销。然而 FastThreadLocal 不仅提供了 remove() 主动清除对象的方法，而且在线程池场景中 Netty 还封装了 FastThreadLocalRunnable，FastThreadLocalRunnable 最后会执行 FastThreadLocal.removeAll() 将 Set 集合中所有 FastThreadLocal 对象都清理掉，



延迟任务处理神器之时间轮 HashedWheelTimer

HashedWheelTimer 并不是十全十美的，使用的时候需要清楚它存在的问题：

如果长时间没有到期任务，那么会存在时间轮空推进的现象。

只适用于处理耗时较短的任务，由于 Worker 是单线程的，如果一个任务执行的时间过长，会造成 Worker 线程阻塞。

相比传统定时器的实现方式，内存占用较大。





阻塞队列
阻塞队列在队列为空或者队列满时，都会发生阻塞。阻塞队列自身是线程安全的，使用者无需关心线程安全问题，降低了多线程开发难度。阻塞队列主要分为以下几种：

ArrayBlockingQueue：最基础且开发中最常用的阻塞队列，底层采用数组实现的有界队列，初始化需要指定队列的容量。ArrayBlockingQueue 是如何保证线程安全的呢？它内部是使用了一个重入锁 ReentrantLock，并搭配 notEmpty、notFull 两个条件变量 Condition 来控制并发访问。从队列读取数据时，如果队列为空，那么会阻塞等待，直到队列有数据了才会被唤醒。如果队列已经满了，也同样会进入阻塞状态，直到队列有空闲才会被唤醒。

LinkedBlockingQueue：内部采用的数据结构是链表，队列的长度可以是有界或者无界的，初始化不需要指定队列长度，默认是 Integer.MAX_VALUE。LinkedBlockingQueue 内部使用了 takeLock、putLock两个重入锁 ReentrantLock，以及 notEmpty、notFull 两个条件变量 Condition 来控制并发访问。采用读锁和写锁的好处是可以避免读写时相互竞争锁的现象，所以相比于 ArrayBlockingQueue，LinkedBlockingQueue 的性能要更好。

PriorityBlockingQueue：采用最小堆实现的优先级队列，队列中的元素按照优先级进行排列，每次出队都是返回优先级最高的元素。PriorityBlockingQueue 内部是使用了一个 ReentrantLock 以及一个条件变量 Condition notEmpty 来控制并发访问，不需要 notFull 是因为 PriorityBlockingQueue 是无界队列，所以每次 put 都不会发生阻塞。PriorityBlockingQueue 底层的最小堆是采用数组实现的，当元素个数大于等于最大容量时会触发扩容，在扩容时会先释放锁，保证其他元素可以正常出队，然后使用 CAS 操作确保只有一个线程可以执行扩容逻辑。

DelayQueue，一种支持延迟获取元素的阻塞队列，常用于缓存、定时任务调度等场景。DelayQueue 内部是采用优先级队列 PriorityQueue 存储对象。DelayQueue 中的每个对象都必须实现 Delayed 接口，并重写 compareTo 和 getDelay 方法。向队列中存放元素的时候必须指定延迟时间，只有延迟时间已满的元素才能从队列中取出。

SynchronizedQueue，又称无缓冲队列。比较特别的是 SynchronizedQueue 内部不会存储元素。与 ArrayBlockingQueue、LinkedBlockingQueue 不同，SynchronizedQueue 直接使用 CAS 操作控制线程的安全访问。其中 put 和 take 操作都是阻塞的，每一个 put 操作都必须阻塞等待一个 take 操作，反之亦然。所以 SynchronizedQueue 可以理解为生产者和消费者配对的场景，双方必须互相等待，直至配对成功。在 JDK 的线程池 Executors.newCachedThreadPool 中就存在 SynchronousQueue 的运用，对于新提交的任务，如果有空闲线程，将重复利用空闲线程处理任务，否则将新建线程进行处理。

LinkedTransferQueue，一种特殊的无界阻塞队列，可以看作 LinkedBlockingQueues、SynchronousQueue（公平模式）、ConcurrentLinkedQueue 的合体。与 SynchronousQueue 不同的是，LinkedTransferQueue 内部可以存储实际的数据，当执行 put 操作时，如果有等待线程，那么直接将数据交给对方，否则放入队列中。与 LinkedBlockingQueues 相比，LinkedTransferQueue 使用 CAS 无锁操作进一步提升了性能。

非阻塞队列
说完阻塞队列，我们再来看下非阻塞队列。非阻塞队列不需要通过加锁的方式对线程阻塞，并发性能更好。JDK 中常用的非阻塞队列有以下几种：

ConcurrentLinkedQueue，它是一个采用双向链表实现的无界并发非阻塞队列，它属于 LinkedQueue 的安全版本。ConcurrentLinkedQueue 内部采用 CAS 操作保证线程安全，这是非阻塞队列实现的基础，相比 ArrayBlockingQueue、LinkedBlockingQueue 具备较高的性能。

ConcurrentLinkedDeque，也是一种采用双向链表结构的无界并发非阻塞队列。与 ConcurrentLinkedQueue 不同的是，ConcurrentLinkedDeque 属于双端队列，它同时支持 FIFO 和 FILO 两种模式，可以从队列的头部插入和删除数据，也可以从队列尾部插入和删除数据，适用于多生产者和多消费者的场景。



 BlockingQueue 接口的具体行为进行归类。

![图片3.png](https://s0.lgstatic.com/i/image2/M01/04/47/CgpVE1_sPeSAZ3hUAAESfy4p6LY171.png)

提供的高性能无锁队列已经可以满足我们的需求，其中非常出名的有 Disruptor 和 JCTools。

Disruptor 是 LMAX 公司开发的一款高性能无锁队列，我们平时常称它为 RingBuffer，其设计初衷是为了解决内存队列的延迟问题。Disruptor 内部采用环形数组和 CAS 操作实现，性能非常优越。为什么 Disruptor 的性能会比 JDK 原生的无锁队列要好呢？环形数组可以复用内存，减少分配内存和释放内存带来的性能损耗。而且数组可以设置长度为 2 的次幂，直接通过位运算加快数组下标的定位速度。此外，Disruptor 还解决了伪共享问题，对 CPU Cache 更加友好。Disruptor 已经开源，详细可查阅 Github 地址 https://github.com/LMAX-Exchange/disruptor。

JCTools 也是一个开源项目，Github 地址为 https://github.com/JCTools/JCTools。JCTools 是适用于 JVM 并发开发的工具，主要提供了一些 JDK 确实的并发数据结构，例如非阻塞 Map、非阻塞 Queue 等。其中非阻塞队列可以分为四种类型，可以根据不同的场景选择使用。

1. Spsc 单生产者单消费者；

2. Mpsc 多生产者单消费者；

3. Spmc 单生产者多消费者；

4. Mpmc 多生产者多消费者。



MpscArrayQueue 还只是 Jctools 中的冰山一角，其中蕴藏着丰富的技术细节，我们对 MpscArrayQueue 的知识点做一个简单的总结。

通过大量填充 long 类型变量解决伪共享问题。

环形数组的容量设置为 2 的次幂，可以通过位运算快速定位到数组对应下标。

入队 offer() 操作中 producerLimit 的巧妙设计，大幅度减少了主动获取消费者索引 consumerIndex 的次数，性能提升显著。

入队和出队操作中都大量使用了 UNSAFE 系列方法，针对生产者和消费者的场景不同，使用的 UNSAFE 方法也是不一样的。Jctools 在底层操作的运用上也是有的放矢，把性能发挥到极致。