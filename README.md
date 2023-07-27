<div id="article_content" class="article_content clearfix">
        <link rel="stylesheet" href="https://csdnimg.cn/release/blogv2/dist/mdeditor/css/editerView/kdoc_html_views-1a98987dfd.css">
        <link rel="stylesheet" href="https://csdnimg.cn/release/blogv2/dist/mdeditor/css/editerView/ck_htmledit_views-25cebea3f9.css">
                <div id="content_views" class="markdown_views prism-atom-one-dark">
                    <svg xmlns="http://www.w3.org/2000/svg" style="display: none;">
                        <path stroke-linecap="round" d="M5,0 0,2.5 5,5z" id="raphael-marker-block" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0);"></path>
                    </svg>
                    <p>若该文为原创文章，转载请注明原文出处<br> 本文章博客地址：<a href="https://blog.csdn.net/qq21497936/article/details/108573195">https://blog.csdn.net/qq21497936/article/details/108573195</a><br> 各位读者，知识无穷而人力有穷，要么改需求，要么找专业人士，要么自己研究</p> 
<p><a href="https://blog.csdn.net/qq21497936/article/details/102478062">红胖子(红模仿)的博文大全：开发技术集合（包含Qt实用技术、树莓派、三维、OpenCV、OpenGL、ffmpeg、OSG、单片机、软硬结合等等）持续更新中…（点击传送门）</a></p> 
<h1><a name="t0"></a><a id="FFmpegSDLhttpsblogcsdnnetqq21497936articledetails102478062FFmpegE5BC80E58F91E4B893E6A08F_6"></a><a href="https://blog.csdn.net/qq21497936/article/details/102478062#FFmpeg%E5%BC%80%E5%8F%91%E4%B8%93%E6%A0%8F">FFmpeg和SDL开发专栏（点击传送门）</a></h1> 
<p>上一篇：《<a href="https://blog.csdn.net/qq21497936/article/details/108542400">FFmpeg开发笔记（三）：ffmpeg介绍、windows编译以及开发环境搭建</a>》<br> 下一篇：《<a href="https://blog.csdn.net/qq21497936/article/details/108639103">FFmpeg开发笔记（五）：ffmpeg解码的基本流程详解（ffmpeg3新解码api）</a>》</p> 
<br> 
<h1><a name="t1"></a><a id="_13"></a>前言</h1> 
<p>  ffmpeg涉及了很多，循序渐进，本篇描述基本的解码流程。</p> 
<br> 
<h1><a name="t2"></a><a id="Demo_18"></a>Demo</h1> 
<p>  <img src="https://img-blog.csdnimg.cn/20200914095418459.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxMjE0OTc5MzY=,size_16,color_FFFFFF,t_70#pic_center" alt="在这里插入图片描述"></p> 
<br> 
<h1><a name="t3"></a><a id="ffmpeg_23"></a>ffmpeg解码流程</h1> 
<p>  ffmpeg的解码和编码都遵循其基本的执行流程。<br>   基本流程如下：<br>   <img src="https://img-blog.csdnimg.cn/20200914102045703.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxMjE0OTc5MzY=,size_16,color_FFFFFF,t_70#pic_center" alt="在这里插入图片描述"></p> 
<h2><a name="t4"></a><a id="_28"></a>步骤一：注册：</h2> 
<p>  使用ffmpeg对应的库，都需要进行注册，可以注册子项也可以注册全部。</p> 
<h2><a name="t5"></a><a id="_30"></a>步骤二：打开文件：</h2> 
<p>  打开文件，根据文件名信息获取对应的ffmpeg全局上下文。</p> 
<h2><a name="t6"></a><a id="_32"></a>步骤三：探测流信息：</h2> 
<p>  一定要探测流信息，拿到流编码的编码格式，不探测流信息则其流编码器拿到的编码类型可能为空，后续进行数据转换的时候就无法知晓原始格式，导致错误。</p> 
<h2><a name="t7"></a><a id="_34"></a>步骤四：查找对应的解码器</h2> 
<p>  依据流的格式查找解码器，软解码还是硬解码是在此处决定的，但是特别注意是否支持硬件，需要自己查找本地的硬件解码器对应的标识，并查询其是否支持。普遍操作是，枚举支持文件后缀解码的所有解码器进行查找，查找到了就是可以硬解了（此处，不做过多的讨论，对应硬解码后续会有文章进行进一步研究）。<br>   （注意：解码时查找解码器，编码时查找编码器，两者函数不同，不要弄错了，否则后续能打开但是数据是错的）</p> 
<h2><a name="t8"></a><a id="_37"></a>步骤五：打开解码器</h2> 
<p>  打开获取到的解码器。</p> 
<h2><a name="t9"></a><a id="_39"></a>步骤六：申请缩放数据格式转换结构体</h2> 
<p>  此处特别注意，基本上解码的数据都是yuv系列格式，但是我们显示的数据是rgb等相关颜色空间的数据，所以此处转换结构体就是进行转换前到转换后的描述，给后续转换函数提供转码依据，是很关键并且非常常用的结构体。</p> 
<h2><a name="t10"></a><a id="_41"></a>步骤七：申请缓存区</h2> 
<p>  申请一个缓存区outBuffer，fill到我们目标帧数据的data上，比如rgb数据，QAVFrame的data上存是有指定格式的数据，且存储有规则，而fill到outBuffer（自己申请的目标格式一帧缓存区），则是我们需要的数据格式存储顺序。<br>   举个例子，解码转换后的数据为rgb888，实际直接用data数据是错误的，但是用outBuffer就是对的，所以此处应该是ffmpeg的fill函数做了一些转换。<br> 进入循环解码：</p> 
<h2><a name="t11"></a><a id="packet_45"></a>步骤八：获取一帧packet</h2> 
<p>  拿取封装的一个packet，判断packet数据的类型进行解码拿到存储的编码数据</p> 
<h2><a name="t12"></a><a id="_47"></a>步骤九：数据转换</h2> 
<p>  使用转换函数结合转换结构体对编码的数据进行转换，那拿到需要的目标宽度、高度和指定存储格式的原始数据。</p> 
<h2><a name="t13"></a><a id="_49"></a>步骤十：自行处理</h2> 
<p>  拿到了原始数据自行处理。<br>   不断循环，直到拿取pakcet函数成功，但是无法got一帧数据，则代表文件解码已经完成。<br>   帧率需要自己控制循环，此处只是循环拿取，可加延迟等。</p> 
<h2><a name="t14"></a><a id="QAVPacket_53"></a>步骤十一：释放QAVPacket</h2> 
<p>  此处要单独列出是因为，其实很多网上和开发者的代码：<br>   在进入循环解码前进行了av_new_packet，循环中未av_free_packet，造成内存溢出；<br>   在进入循环解码前进行了av_new_packet，循环中进行av_free_pakcet，那么一次new对应无数次free，在编码器上是不符合前后一一对应规范的。<br>   查看源代码，其实可以发现av_read_frame时，自动进行了av_new_packet()，那么其实对于packet，只需要进行一次av_packet_alloc()即可，解码完后av_free_packet。<br>   执行完后，返回执行“步骤八：获取一帧packet”，一次循环结束。</p> 
<h2><a name="t15"></a><a id="_59"></a>步骤十二：释放转换结构体</h2> 
<p>  全部解码完成后，安装申请顺序，进行对应资源的释放。</p> 
<h2><a name="t16"></a><a id="_61"></a>步骤十三：关闭解码/编码器</h2> 
<p>  关闭之前打开的解码/编码器。</p> 
<h2><a name="t17"></a><a id="_63"></a>步骤十四：关闭上下文</h2> 
<p>  关闭文件上下文后，要对之前申请的变量按照申请的顺序，依次释放。<br>   另附上完成的详细解码流程图：<br>   <img src="https://img-blog.csdnimg.cn/20200914130130701.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxMjE0OTc5MzY=,size_16,color_FFFFFF,t_70#pic_center" alt="在这里插入图片描述"></p> 
<br> 
<p>本文章博客地址：<a href="https://blog.csdn.net/qq21497936/article/details/108573195">https://blog.csdn.net/qq21497936/article/details/108573195</a></p> 
<h1><a name="t18"></a><a id="ffmpeg_72"></a>ffmpeg解码相关变量</h1> 
<h2><a name="t19"></a><a id="AVFormatContext_73"></a>AVFormatContext</h2> 
<p>  AVFormatContext描述了一个媒体文件或媒体流的构成和基本信息，位于avformat.h文件中。</p> 
<h2><a name="t20"></a><a id="AVInputFormat_75"></a>AVInputFormat</h2> 
<p>  AVInputFormat 是类似COM 接口的数据结构，表示输入文件容器格式，着重于功能函数，一种文件容器格式对应一个AVInputFormat 结构，在程序运行时有多个实例，位于avoformat.h文件中。</p> 
<h2><a name="t21"></a><a id="AVDictionary_77"></a>AVDictionary</h2> 
<p>  AVDictionary 是一个字典集合，键值对，用于配置相关信息。</p> 
<h2><a name="t22"></a><a id="AVCodecContext_79"></a>AVCodecContext</h2> 
<p>  AVCodecContext是一个描述编解码器上下文的数据结构，包含了众多编解码器需要的参数信息，位于avcodec.h文件中。</p> 
<h2><a name="t23"></a><a id="AVPacket_81"></a>AVPacket</h2> 
<p>  AVPacket是FFmpeg中很重要的一个数据结构，它保存了解复用（demuxer)之后，解码（decode）之前的数据（仍然是压缩后的数据）和关于这些数据的一些附加的信息，如显示时间戳（pts），解码时间戳（dts）,数据时长（duration），所在流媒体的索引（stream_index）等等。<br>   使用前，使用av_packet_alloc()分配，</p> 
<h2><a name="t24"></a><a id="AVCodec_84"></a>AVCodec</h2> 
<p>  AVCodec是存储编解码器信息的结构体，位于avcodec.h文件中。</p> 
<h2><a name="t25"></a><a id="AVFrame_86"></a>AVFrame</h2> 
<p>  AVFrame中存储的是经过解码后的原始数据。在解码中，AVFrame是解码器的输出；在编码中，AVFrame是编码器的输入。<br>   使用前，使用av_frame_alloc()进行分配。</p> 
<h2><a name="t26"></a><a id="struct_SwsContext_89"></a>struct SwsContext</h2> 
<p>  使用前，使用sws_getContext()进行获取，主要用于视频图像的转换。</p> 
<br> 
<h1><a name="t27"></a><a id="ffmpeg_94"></a>ffmpeg解码流程相关函数原型</h1> 
<h2><a name="t28"></a><a id="av_register_all_95"></a>av_register_all</h2> 
<pre data-index="0" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">void av_register_all(void);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  初始化libavformat并注册所有muxer、demuxer和协议。如果不调用此函数，则可以选择想要指定注册支持的哪种格式，通过av_register_input_format()、av_register_output_format()。</p> 
<h2><a name="t29"></a><a id="avformat_open_input_100"></a>avformat_open_input</h2> 
<pre data-index="1" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int avformat_open_input(AVFormatContext **ps,
                        const char *url,
                        AVInputFormat *fmt, 
                        AVDictionary **options);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li><li style="color: rgb(153, 153, 153);">4</li></ul></pre> 
<p>  打开输入流并读取标头。编解码器未打开。流必须使用avformat_close_input()关闭，返回0-成功，&lt;0-失败错误码。</p> 
<ul><li><strong>参数一</strong>：指向用户提供的AVFormatContext（由avformat_alloc_context分配）的指针。</li><li><strong>参数二</strong>：要打开的流的url</li><li><strong>参数三</strong>：fmt如果非空，则此参数强制使用特定的输入格式。否则将自动检测格式。</li><li><strong>参数四</strong>：包含AVFormatContext和demuxer私有选项的字典。返回时，此参数将被销毁并替换为包含找不到的选项。都有效则返回为空。</li></ul> 
<h2><a name="t30"></a><a id="avformat_find_stream_info_112"></a>avformat_find_stream_info</h2> 
<p>int avformat_find_stream_info(AVFormatContext *ic, AVDictionary **options);<br> 读取检查媒体文件的数据包以获取具体的流信息，如媒体存入的编码格式。</p> 
<ul><li><strong>参数一</strong>：媒体文件上下文。</li><li><strong>参数二</strong>：字典，一些配置选项。</li></ul> 
<h2><a name="t31"></a><a id="avcodec_find_decoder_117"></a>avcodec_find_decoder</h2> 
<pre data-index="2" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">AVCodec *avcodec_find_decoder(enum AVCodecID id);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  查找具有匹配编解码器ID的已注册解码器，解码时，已经获取到了，注册的解码器可以通过枚举查看，枚举太多，略。</p> 
<h2><a name="t32"></a><a id="avcodec_open2_122"></a>avcodec_open2</h2> 
<pre data-index="3" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int avcodec_open2(AVCodecContext *avctx, 
                  const AVCodec *codec, 
                  AVDictionary **options);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li></ul></pre> 
<p>  初始化AVCodeContext以使用给定的AVCodec。</p> 
<h2><a name="t33"></a><a id="sws_getContext_129"></a>sws_getContext</h2> 
<pre data-index="4" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">struct SwsContext *sws_getContext(int srcW, 
                                  int srcH, 
                                  enum AVPixelFormat srcFormat,
                                  int dstW,
                                  int dstH, 
                                  enum AVPixelFormat dstFormat,
                                  int flags, SwsFilter *srcFilter,
                                  SwsFilter *dstFilter,
                                  const double *param);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li><li style="color: rgb(153, 153, 153);">4</li><li style="color: rgb(153, 153, 153);">5</li><li style="color: rgb(153, 153, 153);">6</li><li style="color: rgb(153, 153, 153);">7</li><li style="color: rgb(153, 153, 153);">8</li><li style="color: rgb(153, 153, 153);">9</li></ul></pre> 
<p>  分配并返回一个SwsContext。需要它来执行sws_scale()进行缩放/转换操作。</p> 
<h2><a name="t34"></a><a id="avpicture_get_size_142"></a>avpicture_get_size</h2> 
<pre data-index="5" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int avpicture_get_size(enum AVPixelFormat pix_fmt, int width, int height);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  返回存储具有给定参数的图像的缓存区域大小。</p> 
<ul><li><strong>参数一</strong>：图像的像素格式</li><li><strong>参数二</strong>：图像的像素宽度</li><li><strong>参数三</strong>：图像的像素高度</li></ul> 
<h2><a name="t35"></a><a id="avpicture_fill_150"></a>avpicture_fill</h2> 
<pre data-index="6" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int avpicture_fill(AVPicture *picture,
              const uint8_t *ptr,
              enum AVPixelFormat pix_fmt,
              int width,
              int height);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li><li style="color: rgb(153, 153, 153);">4</li><li style="color: rgb(153, 153, 153);">5</li></ul></pre> 
<p>  根据指定的图像、提供的数组设置数据指针和线条大小参数。</p> 
<ul><li><strong>参数一</strong>：输入AVFrame指针，强制转换为AVPciture即可。</li><li><strong>参数二</strong>：映射到的缓存区，开发者自己申请的存放图像数据的缓存区。</li><li><strong>参数三</strong>：图像数据的编码格式。</li><li><strong>参数四</strong>：图像像素宽度。</li><li><strong>参数五</strong>：图像像素高度。</li></ul> 
<h2><a name="t36"></a><a id="av_read_frame_164"></a>av_read_frame</h2> 
<pre data-index="7" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int av_read_frame(AVFormatContext *s, AVPacket *pkt);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  返回流的下一帧。此函数返回存储在文件中的内容，不对有效的帧进行验证。获取存储在文件中的帧中，并为每个调用返回一个。不会的省略有效帧之间的无效数据，以便给解码器最大可用于解码的信息。<br>   返回0是成功，小于0则是错误，大于0则是文件末尾，所以大于等于0是返回成功。</p> 
<h2><a name="t37"></a><a id="avcodec_decode_video2_170"></a>avcodec_decode_video2</h2> 
<pre data-index="8" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int avcodec_decode_video2(AVCodecContext *avctx,
                          AVFrame *picture,
                          int *got_picture_ptr,
                          const AVPacket *avpkt);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li><li style="color: rgb(153, 153, 153);">4</li></ul></pre> 
<p>  将大小为avpkt-&gt;size from avpkt-&gt;data的视频帧解码为图片。一些解码器可以支持单个avpkg包中的多个帧，解码器将只解码第一帧。出错时返回负值，否则返回字节数，如果没有帧可以解压缩，则为0。</p> 
<ul><li><strong>参数一</strong>：编解码器上下文。</li><li><strong>参数二</strong>：将解码视频帧存储在AVFrame中。</li><li><strong>参数三</strong>：输入缓冲区的AVPacket。</li><li><strong>参数四</strong>：如果没有帧可以解压，那么得到的图片是0，否则，它是非零的。</li></ul> 
<h2><a name="t38"></a><a id="sws_scale_182"></a>sws_scale</h2> 
<pre data-index="9" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int sws_scale(struct SwsContext *c,
              const uint8_t *const srcSlice[],
              const int srcStride[],
              int srcSliceY,
              int srcSliceH,
              uint8_t *const dst[],
              const int dstStride[]);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li><li style="color: rgb(153, 153, 153);">4</li><li style="color: rgb(153, 153, 153);">5</li><li style="color: rgb(153, 153, 153);">6</li><li style="color: rgb(153, 153, 153);">7</li></ul></pre> 
<p>  在srcSlice中缩放图像切片并将结果缩放在dst中切片图像。切片是连续的序列图像中的行。</p> 
<ul><li><strong>参数一</strong>：以前用创建的缩放上下文*sws_getContext()。</li><li><strong>参数二</strong>：包含指向源片段，就是AVFrame的data。</li><li><strong>参数三</strong>：包含每个平面的跨步的数组，其实就是AVFrame的linesize。</li><li><strong>参数四</strong>：切片在源图像中的位置，从开始计数0对应切片第一行的图像，所以直接填0即可。</li><li><strong>参数五</strong>：源切片的像素高度。</li><li><strong>参数六</strong>：目标数据地址映像，是目标AVFrame的data。</li><li><strong>参数七</strong>：目标每个平面的跨步的数组，就是linesize。</li></ul> 
<h2><a name="t39"></a><a id="av_free_packet_200"></a>av_free_packet</h2> 
<pre data-index="10" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">void av_free_packet(AVPacket *pkt);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  释放一个包。</p> 
<h2><a name="t40"></a><a id="avcodec_close_205"></a>avcodec_close</h2> 
<pre data-index="11" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">int avcodec_close(AVCodecContext *avctx);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  关闭给定的avcodeContext并释放与之关联的所有数据（但不是AVCodecContext本身）。</p> 
<h2><a name="t41"></a><a id="avformat_close_input_210"></a>avformat_close_input</h2> 
<pre data-index="12" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">void avformat_close_input(AVFormatContext **s);
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li></ul></pre> 
<p>  关闭打开的输入AVFormatContext。释放它和它的所有内容并将*s设置为空。</p> 
<br> 
<h1><a name="t42"></a><a id="Demo_218"></a>Demo源码</h1> 
<pre data-index="13" class="prettyprint"><code class="has-numbering" onclick="mdcp.copyCode(event)" style="position: unset;">void FFmpegManager::testDecode()
{
//    QString fileName = "test/1.avi";
    QString fileName = "test/1.mp4";

    // ffmpeg相关变量预先定义与分配
    AVFormatContext *pAVFormatContext = 0;          // ffmpeg的全局上下文，所有ffmpeg操作都需要
    AVInputFormat *pAVInputFormat = 0;              // ffmpeg的输入格式结构体
    AVDictionary *pAVDictionary = 0;                // ffmpeg的字典option，各种参数给格式编解码配置参数的
    AVCodecContext *pAVCodecContext = 0;            // ffmpeg编码上下文
    AVCodec *pAVCodec = 0;                          // ffmpeg编码器
    AVPacket *pAVPacket = 0;                        // ffmpag单帧数据包
    AVFrame *pAVFrame = 0;                          // ffmpeg单帧缓存
    AVFrame *pAVFrameRGB32 = 0;                     // ffmpeg单帧缓存转换颜色空间后的缓存
    struct SwsContext *pSwsContext = 0;             // ffmpag编码数据格式转换

    int ret = 0;                                    // 函数执行结果
    int videoIndex = -1;                            // 音频流所在的序号
    int gotPicture = 0;                             // 解码时数据是否解码成功
    int numBytes = 0;                               // 解码后的数据长度
    uchar *outBuffer = 0;                           // 解码后的数据存放缓存区

    pAVFormatContext = avformat_alloc_context();     // 分配
    pAVPacket = av_packet_alloc();                  // 分配
    pAVFrame = av_frame_alloc();                   // 分配
    pAVFrameRGB32 = av_frame_alloc();             // 分配
    if(!pAVFormatContext || !pAVPacket || !pAVFrame || !pAVFrameRGB32)
    {
        LOG &lt;&lt; "Failed to alloc";
        goto END;
    }
    // 步骤一：注册所有容器和编解码器（也可以只注册一类，如注册容器、注册编码器等）
    av_register_all();

    // 步骤二：打开文件(ffmpeg成功则返回0)
    LOG &lt;&lt; "文件:" &lt;&lt; fileName &lt;&lt; "，是否存在：" &lt;&lt; QFile::exists(fileName);
    ret = avformat_open_input(&amp;pAVFormatContext, fileName.toUtf8().data(), pAVInputFormat, 0);
    if(ret)
    {
        LOG &lt;&lt; "Failed";
        goto END;
    }

    // 步骤三：探测流媒体信息
    // Assertion desc failed at libswscale/swscale_internal.h:668
    // 入坑：因为pix_fmt为空，需要对编码器上下文进一步探测
    ret = avformat_find_stream_info(pAVFormatContext, 0);
    if(ret &lt; 0)
    {
        LOG &lt;&lt; "Failed to avformat_find_stream_info(pAVCodecContext, 0)";
        goto END;
    }
    // 打印文件信息
    LOG &lt;&lt; "视频文件包含流信息的数量:" &lt;&lt; pAVFormatContext-&gt;nb_streams;
    // 在Qt中av_dump_format不会进行命令行输出
//    av_dump_format(pAVFormatContext, 1, fileName.toUtf8().data(), 0);

    // 步骤三：提取流信息,提取视频信息
    for(int index = 0; index &lt; pAVFormatContext-&gt;nb_streams; index++)
    {
        pAVCodecContext = pAVFormatContext-&gt;streams[index]-&gt;codec;
        switch (pAVCodecContext-&gt;codec_type)
        {
        case AVMEDIA_TYPE_UNKNOWN:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_UNKNOWN";
            break;
        case AVMEDIA_TYPE_VIDEO:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_VIDEO";
            videoIndex = index;
            LOG;
            break;
        case AVMEDIA_TYPE_AUDIO:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_AUDIO";
            break;
        case AVMEDIA_TYPE_DATA:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_DATA";
            break;
        case AVMEDIA_TYPE_SUBTITLE:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_SUBTITLE";
            break;
        case AVMEDIA_TYPE_ATTACHMENT:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_ATTACHMENT";
            break;
        case AVMEDIA_TYPE_NB:
            LOG &lt;&lt; "流序号:" &lt;&lt; index &lt;&lt; "类型为:" &lt;&lt; "AVMEDIA_TYPE_NB";
            break;
        default:
            break;
        }
        // 已经找打视频品流
        if(videoIndex != -1)
        {
            break;
        }
    }
    if(videoIndex == -1 || !pAVCodecContext)
    {
        LOG &lt;&lt; "Failed to find video stream";
        goto END;
    }
    // 步骤四：对找到的视频流寻解码器
    pAVCodec = avcodec_find_decoder(pAVCodecContext-&gt;codec_id);
    if(!pAVCodec)
    {
        LOG &lt;&lt; "Fialed to avcodec_find_decoder(pAVCodecContext-&gt;codec_id):"
            &lt;&lt; pAVCodecContext-&gt;codec_id;
        goto END;
    }

    // 步骤五：打开解码器
    ret = avcodec_open2(pAVCodecContext, pAVCodec, NULL);
    if(ret)
    {
        LOG &lt;&lt; "Failed to avcodec_open2(pAVCodecContext, pAVCodec, pAVDictionary)";
        goto END;
    }
    LOG &lt;&lt; pAVCodecContext-&gt;width &lt;&lt; "x" &lt;&lt; pAVCodecContext-&gt;height;
    // 步骤六：对拿到的原始数据格式进行缩放转换为指定的格式高宽大小
    // Assertion desc failed at libswscale/swscale_internal.h:668
    // 入坑：因为pix_fmt为空，需要对编码器上下文进一步探测
    pSwsContext = sws_getContext(pAVCodecContext-&gt;width,
                                 pAVCodecContext-&gt;height,
                                 pAVCodecContext-&gt;pix_fmt,
                                 pAVCodecContext-&gt;width,
                                 pAVCodecContext-&gt;height,
                                 AV_PIX_FMT_RGBA,
                                 SWS_FAST_BILINEAR,
                                 0,
                                 0,
                                 0);
    numBytes = avpicture_get_size(AV_PIX_FMT_RGBA,
                                  pAVCodecContext-&gt;width,
                                  pAVCodecContext-&gt;height);
    outBuffer = (uchar *)av_malloc(numBytes);
    // pAVFrame32的data指针指向了outBuffer
    avpicture_fill((AVPicture *)pAVFrameRGB32,
                   outBuffer,
                   AV_PIX_FMT_RGBA,
                   pAVCodecContext-&gt;width,
                   pAVCodecContext-&gt;height);
    // 此处无需分配
    // av_read_frame时他会分配，av_new_packet多此一举，正好解释了一次new和多次free的问题
//    av_new_packet(pAVPacket, pAVCodecContext-&gt;width * pAVCodecContext-&gt;height);
    // 步骤七：读取一帧数据的数据包
    while(av_read_frame(pAVFormatContext, pAVPacket) &gt;= 0)
    {
        if(pAVPacket-&gt;stream_index == videoIndex)
        {
            // 步骤八：对读取的数据包进行解码
            ret = avcodec_decode_video2(pAVCodecContext, pAVFrame, &amp;gotPicture, pAVPacket);
            if(ret &lt; 0)
            {
                LOG &lt;&lt; "Failed to avcodec_decode_video2(pAVFormatContext, pAVFrame, &amp;gotPicture, pAVPacket)";
                break;
            }
            // 等于0代表拿到了解码的帧数据
            if(!gotPicture)
            {
                LOG &lt;&lt; "no data";
                break;
            }else{
                sws_scale(pSwsContext,
                          (const uint8_t * const *)pAVFrame-&gt;data,
                          pAVFrame-&gt;linesize,
                          0,
                          pAVCodecContext-&gt;height,
                          pAVFrameRGB32-&gt;data,
                          pAVFrameRGB32-&gt;linesize);
                QImage imageTemp((uchar *)outBuffer,
                                 pAVCodecContext-&gt;width,
                                 pAVCodecContext-&gt;height,
                                 QImage::Format_RGBA8888);
                QImage image = imageTemp.copy();
                LOG &lt;&lt; image.save("1.jpg");
            }
            av_free_packet(pAVPacket);
        }
        QThread::msleep(100);
    }
END:
    LOG &lt;&lt; "释放回收资源";
    if(outBuffer)
    {
        av_free(outBuffer);
        outBuffer = 0;
    }
    if(pSwsContext)
    {
        sws_freeContext(pSwsContext);
        pSwsContext = 0;
        LOG &lt;&lt; "sws_freeContext(pSwsContext)";
    }
    if(pAVFrameRGB32)
    {
        av_frame_free(&amp;pAVFrameRGB32);
        pAVFrame = 0;
        LOG &lt;&lt; "av_frame_free(pAVFrameRGB888)";
    }
    if(pAVFrame)
    {
        av_frame_free(&amp;pAVFrame);
        pAVFrame = 0;
        LOG &lt;&lt; "av_frame_free(pAVFrame)";
    }
    if(pAVPacket)
    {
        av_free_packet(pAVPacket);
        pAVPacket = 0;
        LOG &lt;&lt; "av_free_packet(pAVPacket)";
    }
    if(pAVCodecContext)
    {
        avcodec_close(pAVCodecContext);
        pAVCodecContext = 0;
        LOG &lt;&lt; "avcodec_close(pAVCodecContext);";
    }
    if(pAVFormatContext)
    {
        avformat_free_context(pAVFormatContext);
        pAVFormatContext = 0;
        LOG &lt;&lt; "avformat_free_context(pAVFormatContext)";
    }
}
<div class="hljs-button {2}" data-title="复制"></div></code><ul class="pre-numbering" style=""><li style="color: rgb(153, 153, 153);">1</li><li style="color: rgb(153, 153, 153);">2</li><li style="color: rgb(153, 153, 153);">3</li><li style="color: rgb(153, 153, 153);">4</li><li style="color: rgb(153, 153, 153);">5</li><li style="color: rgb(153, 153, 153);">6</li><li style="color: rgb(153, 153, 153);">7</li><li style="color: rgb(153, 153, 153);">8</li><li style="color: rgb(153, 153, 153);">9</li><li style="color: rgb(153, 153, 153);">10</li><li style="color: rgb(153, 153, 153);">11</li><li style="color: rgb(153, 153, 153);">12</li><li style="color: rgb(153, 153, 153);">13</li><li style="color: rgb(153, 153, 153);">14</li><li style="color: rgb(153, 153, 153);">15</li><li style="color: rgb(153, 153, 153);">16</li><li style="color: rgb(153, 153, 153);">17</li><li style="color: rgb(153, 153, 153);">18</li><li style="color: rgb(153, 153, 153);">19</li><li style="color: rgb(153, 153, 153);">20</li><li style="color: rgb(153, 153, 153);">21</li><li style="color: rgb(153, 153, 153);">22</li><li style="color: rgb(153, 153, 153);">23</li><li style="color: rgb(153, 153, 153);">24</li><li style="color: rgb(153, 153, 153);">25</li><li style="color: rgb(153, 153, 153);">26</li><li style="color: rgb(153, 153, 153);">27</li><li style="color: rgb(153, 153, 153);">28</li><li style="color: rgb(153, 153, 153);">29</li><li style="color: rgb(153, 153, 153);">30</li><li style="color: rgb(153, 153, 153);">31</li><li style="color: rgb(153, 153, 153);">32</li><li style="color: rgb(153, 153, 153);">33</li><li style="color: rgb(153, 153, 153);">34</li><li style="color: rgb(153, 153, 153);">35</li><li style="color: rgb(153, 153, 153);">36</li><li style="color: rgb(153, 153, 153);">37</li><li style="color: rgb(153, 153, 153);">38</li><li style="color: rgb(153, 153, 153);">39</li><li style="color: rgb(153, 153, 153);">40</li><li style="color: rgb(153, 153, 153);">41</li><li style="color: rgb(153, 153, 153);">42</li><li style="color: rgb(153, 153, 153);">43</li><li style="color: rgb(153, 153, 153);">44</li><li style="color: rgb(153, 153, 153);">45</li><li style="color: rgb(153, 153, 153);">46</li><li style="color: rgb(153, 153, 153);">47</li><li style="color: rgb(153, 153, 153);">48</li><li style="color: rgb(153, 153, 153);">49</li><li style="color: rgb(153, 153, 153);">50</li><li style="color: rgb(153, 153, 153);">51</li><li style="color: rgb(153, 153, 153);">52</li><li style="color: rgb(153, 153, 153);">53</li><li style="color: rgb(153, 153, 153);">54</li><li style="color: rgb(153, 153, 153);">55</li><li style="color: rgb(153, 153, 153);">56</li><li style="color: rgb(153, 153, 153);">57</li><li style="color: rgb(153, 153, 153);">58</li><li style="color: rgb(153, 153, 153);">59</li><li style="color: rgb(153, 153, 153);">60</li><li style="color: rgb(153, 153, 153);">61</li><li style="color: rgb(153, 153, 153);">62</li><li style="color: rgb(153, 153, 153);">63</li><li style="color: rgb(153, 153, 153);">64</li><li style="color: rgb(153, 153, 153);">65</li><li style="color: rgb(153, 153, 153);">66</li><li style="color: rgb(153, 153, 153);">67</li><li style="color: rgb(153, 153, 153);">68</li><li style="color: rgb(153, 153, 153);">69</li><li style="color: rgb(153, 153, 153);">70</li><li style="color: rgb(153, 153, 153);">71</li><li style="color: rgb(153, 153, 153);">72</li><li style="color: rgb(153, 153, 153);">73</li><li style="color: rgb(153, 153, 153);">74</li><li style="color: rgb(153, 153, 153);">75</li><li style="color: rgb(153, 153, 153);">76</li><li style="color: rgb(153, 153, 153);">77</li><li style="color: rgb(153, 153, 153);">78</li><li style="color: rgb(153, 153, 153);">79</li><li style="color: rgb(153, 153, 153);">80</li><li style="color: rgb(153, 153, 153);">81</li><li style="color: rgb(153, 153, 153);">82</li><li style="color: rgb(153, 153, 153);">83</li><li style="color: rgb(153, 153, 153);">84</li><li style="color: rgb(153, 153, 153);">85</li><li style="color: rgb(153, 153, 153);">86</li><li style="color: rgb(153, 153, 153);">87</li><li style="color: rgb(153, 153, 153);">88</li><li style="color: rgb(153, 153, 153);">89</li><li style="color: rgb(153, 153, 153);">90</li><li style="color: rgb(153, 153, 153);">91</li><li style="color: rgb(153, 153, 153);">92</li><li style="color: rgb(153, 153, 153);">93</li><li style="color: rgb(153, 153, 153);">94</li><li style="color: rgb(153, 153, 153);">95</li><li style="color: rgb(153, 153, 153);">96</li><li style="color: rgb(153, 153, 153);">97</li><li style="color: rgb(153, 153, 153);">98</li><li style="color: rgb(153, 153, 153);">99</li><li style="color: rgb(153, 153, 153);">100</li><li style="color: rgb(153, 153, 153);">101</li><li style="color: rgb(153, 153, 153);">102</li><li style="color: rgb(153, 153, 153);">103</li><li style="color: rgb(153, 153, 153);">104</li><li style="color: rgb(153, 153, 153);">105</li><li style="color: rgb(153, 153, 153);">106</li><li style="color: rgb(153, 153, 153);">107</li><li style="color: rgb(153, 153, 153);">108</li><li style="color: rgb(153, 153, 153);">109</li><li style="color: rgb(153, 153, 153);">110</li><li style="color: rgb(153, 153, 153);">111</li><li style="color: rgb(153, 153, 153);">112</li><li style="color: rgb(153, 153, 153);">113</li><li style="color: rgb(153, 153, 153);">114</li><li style="color: rgb(153, 153, 153);">115</li><li style="color: rgb(153, 153, 153);">116</li><li style="color: rgb(153, 153, 153);">117</li><li style="color: rgb(153, 153, 153);">118</li><li style="color: rgb(153, 153, 153);">119</li><li style="color: rgb(153, 153, 153);">120</li><li style="color: rgb(153, 153, 153);">121</li><li style="color: rgb(153, 153, 153);">122</li><li style="color: rgb(153, 153, 153);">123</li><li style="color: rgb(153, 153, 153);">124</li><li style="color: rgb(153, 153, 153);">125</li><li style="color: rgb(153, 153, 153);">126</li><li style="color: rgb(153, 153, 153);">127</li><li style="color: rgb(153, 153, 153);">128</li><li style="color: rgb(153, 153, 153);">129</li><li style="color: rgb(153, 153, 153);">130</li><li style="color: rgb(153, 153, 153);">131</li><li style="color: rgb(153, 153, 153);">132</li><li style="color: rgb(153, 153, 153);">133</li><li style="color: rgb(153, 153, 153);">134</li><li style="color: rgb(153, 153, 153);">135</li><li style="color: rgb(153, 153, 153);">136</li><li style="color: rgb(153, 153, 153);">137</li><li style="color: rgb(153, 153, 153);">138</li><li style="color: rgb(153, 153, 153);">139</li><li style="color: rgb(153, 153, 153);">140</li><li style="color: rgb(153, 153, 153);">141</li><li style="color: rgb(153, 153, 153);">142</li><li style="color: rgb(153, 153, 153);">143</li><li style="color: rgb(153, 153, 153);">144</li><li style="color: rgb(153, 153, 153);">145</li><li style="color: rgb(153, 153, 153);">146</li><li style="color: rgb(153, 153, 153);">147</li><li style="color: rgb(153, 153, 153);">148</li><li style="color: rgb(153, 153, 153);">149</li><li style="color: rgb(153, 153, 153);">150</li><li style="color: rgb(153, 153, 153);">151</li><li style="color: rgb(153, 153, 153);">152</li><li style="color: rgb(153, 153, 153);">153</li><li style="color: rgb(153, 153, 153);">154</li><li style="color: rgb(153, 153, 153);">155</li><li style="color: rgb(153, 153, 153);">156</li><li style="color: rgb(153, 153, 153);">157</li><li style="color: rgb(153, 153, 153);">158</li><li style="color: rgb(153, 153, 153);">159</li><li style="color: rgb(153, 153, 153);">160</li><li style="color: rgb(153, 153, 153);">161</li><li style="color: rgb(153, 153, 153);">162</li><li style="color: rgb(153, 153, 153);">163</li><li style="color: rgb(153, 153, 153);">164</li><li style="color: rgb(153, 153, 153);">165</li><li style="color: rgb(153, 153, 153);">166</li><li style="color: rgb(153, 153, 153);">167</li><li style="color: rgb(153, 153, 153);">168</li><li style="color: rgb(153, 153, 153);">169</li><li style="color: rgb(153, 153, 153);">170</li><li style="color: rgb(153, 153, 153);">171</li><li style="color: rgb(153, 153, 153);">172</li><li style="color: rgb(153, 153, 153);">173</li><li style="color: rgb(153, 153, 153);">174</li><li style="color: rgb(153, 153, 153);">175</li><li style="color: rgb(153, 153, 153);">176</li><li style="color: rgb(153, 153, 153);">177</li><li style="color: rgb(153, 153, 153);">178</li><li style="color: rgb(153, 153, 153);">179</li><li style="color: rgb(153, 153, 153);">180</li><li style="color: rgb(153, 153, 153);">181</li><li style="color: rgb(153, 153, 153);">182</li><li style="color: rgb(153, 153, 153);">183</li><li style="color: rgb(153, 153, 153);">184</li><li style="color: rgb(153, 153, 153);">185</li><li style="color: rgb(153, 153, 153);">186</li><li style="color: rgb(153, 153, 153);">187</li><li style="color: rgb(153, 153, 153);">188</li><li style="color: rgb(153, 153, 153);">189</li><li style="color: rgb(153, 153, 153);">190</li><li style="color: rgb(153, 153, 153);">191</li><li style="color: rgb(153, 153, 153);">192</li><li style="color: rgb(153, 153, 153);">193</li><li style="color: rgb(153, 153, 153);">194</li><li style="color: rgb(153, 153, 153);">195</li><li style="color: rgb(153, 153, 153);">196</li><li style="color: rgb(153, 153, 153);">197</li><li style="color: rgb(153, 153, 153);">198</li><li style="color: rgb(153, 153, 153);">199</li><li style="color: rgb(153, 153, 153);">200</li><li style="color: rgb(153, 153, 153);">201</li><li style="color: rgb(153, 153, 153);">202</li><li style="color: rgb(153, 153, 153);">203</li><li style="color: rgb(153, 153, 153);">204</li><li style="color: rgb(153, 153, 153);">205</li><li style="color: rgb(153, 153, 153);">206</li><li style="color: rgb(153, 153, 153);">207</li><li style="color: rgb(153, 153, 153);">208</li><li style="color: rgb(153, 153, 153);">209</li><li style="color: rgb(153, 153, 153);">210</li><li style="color: rgb(153, 153, 153);">211</li><li style="color: rgb(153, 153, 153);">212</li><li style="color: rgb(153, 153, 153);">213</li><li style="color: rgb(153, 153, 153);">214</li><li style="color: rgb(153, 153, 153);">215</li><li style="color: rgb(153, 153, 153);">216</li><li style="color: rgb(153, 153, 153);">217</li><li style="color: rgb(153, 153, 153);">218</li><li style="color: rgb(153, 153, 153);">219</li><li style="color: rgb(153, 153, 153);">220</li><li style="color: rgb(153, 153, 153);">221</li><li style="color: rgb(153, 153, 153);">222</li><li style="color: rgb(153, 153, 153);">223</li></ul></pre> 
<br> 
<h1><a name="t43"></a><a id="v110_447"></a>工程模板v1.1.0</h1> 
<p>  对应工程模板v1.1.0</p> 
<br> 
<p>上一篇：《<a href="https://blog.csdn.net/qq21497936/article/details/108542400">FFmpeg开发笔记（三）：ffmpeg介绍、windows编译以及开发环境搭建</a>》<br> 下一篇：《<a href="https://blog.csdn.net/qq21497936/article/details/108639103">FFmpeg开发笔记（五）：ffmpeg解码的基本流程详解（ffmpeg3新解码api）</a>》</p> 
<br> 
<p>原博主博客地址：<a href="https://blog.csdn.net/qq21497936">https://blog.csdn.net/qq21497936</a><br> 原博主博客导航：<a href="https://blog.csdn.net/qq21497936/article/details/102478062">https://blog.csdn.net/qq21497936/article/details/102478062</a><br> 本文章博客地址：<a href="https://blog.csdn.net/qq21497936/article/details/108573195">https://blog.csdn.net/qq21497936/article/details/108573195</a></p>
                </div><div><div></div></div><div><div></div></div>
                <link href="https://csdnimg.cn/release/blogv2/dist/mdeditor/css/editerView/markdown_views-98b95bb57c.css" rel="stylesheet">
                <link href="https://csdnimg.cn/release/blogv2/dist/mdeditor/css/style-c216769e99.css" rel="stylesheet">
        </div>
