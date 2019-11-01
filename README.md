这是系统的介绍部分，本系统是一个通过文本分类来进行股票涨跌的预测系统。
核心思想是爬取股吧中股民对股票的评论信息，通过这些评论构造训练集，构造舆情好坏分类模型。
再对测试集进行分类，通过股票下股民的舆情来进行股票涨跌的预测。
运用的核心算法是SVM构造训练集+LDA进行特征抽取，通过十折交叉验证，显示了较高的预测准确率。
欢迎各位同学的意见和指导。
***


## :moon: 核心算法流程图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101143712846.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)

## :bug: 数据爬取
在数据的爬取上选用网上的现有API可以方便的进行数据分页的爬取，我们选用Jsoup进行数据的爬取。获取的数据源是东方财富网。在数据的选取上，考虑到短文本的限制，我们只爬取标题，选取对应的标签，爬取大概80000条数据，核心代码如下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101143938601.png)

## :book: 文本分词
在进行特征选择之前，首先对训练集中的每篇文档进行分词，我们采用的分词工具是IKAnalyzer。IKAnalyzer是一个开源的，基于java语言开发的轻量级的中文分词工具包。我们选用此分词工具完成训练算法前的句子分词。分词后的结果如下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101144158407.png)
## :cloud: 特征选取
现有的特征抽取的方法很多，包括信息增益IG，局部文档频率和全局文档频率，对于文本分类选用局部DF或者IG都能达到良好的效果。传统的VSM向量空间都是根据这里的特征选取的特征词进行文本表示。

本系统核心算法第一步根据IG或者局部DF先完成普通的特征选取。然后通过训练得到基于LDA隐主题模型选取的，隐主题对应的高比重的单词进行特征加强。即对于原来特征选择中没有出现的单词，但在LDA主题模型中出现的单词将其加入。完成LDA拓展的文本特征选取。获得的部分特征词如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101144343761.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)

## :orange: LDA隐主题的抽取
为了完成LDA拓展的特征选取，我们要先训练LDA得到隐藏的主题。找到每个主题下的对应关键词项。LDA的模块使用的是GitHub上成熟的LdaGibbsSampler的API模块。但为了得到短文本的LDA主题模型，我们需要将这上千条数据划分为上千个单独的短文本文档。具体实现在本系统的LdaOfSingleDocument中：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101144454782.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019110114450220.png)

将数据处理成了lda可以处理的格式后，我们设定预期的主题是3个，分别对应股市的乐观，悲观，和中立三种。在进行LDA主题的处理后，我们得到下面的隐主题。

![在这里插入图片描述](https://img-blog.csdnimg.cn/201911011445351.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101144541407.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwODQzNjM5,size_16,color_FFFFFF,t_70)

   随着隐主题的划分对应的关键字信息，恰好对应的股市舆情为无关（topic0）,乐观（topic1）, 悲观（topic2）。即LDA隐主题的特征抽取给我们提供了关键的数据。
***

# :bulb:特征的文本表示和数据归一化
得到特征词后，选用tf*idf进行文本表示， tf*idf是常用的反应本文本中词汇出现个数和其他文本中没有出现个数的一种很好的表示方法。因为LDA计算的关键字的频率是处于0到1之间的，而原本tf*idf的文本表示结果是远远小的多的。在这里进行归一化，将原本小的多的数据放缩到0和1之间，从而方便应用LDA选取词项的权重。归一化过程如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101144743338.png)

***

## :floppy_disk: 训练SVM分类模型
在对应的TrainAndCategorization包下的两个JAVA文件中分别进行训练集的模型训练。和用模型对测试集进行测试：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101144913249.png)
***
## :wrench: 系统使用模块说明
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101145035660.png)

工具类的定义，和爬虫的编写，以及用IKAnlsaly进行文本分词的预处理

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101145107844.png)

两种不同的文本表示方式，用来优化和对比

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101145132687.png)

LDA隐主题抽取的必要部件，系统的先进性核心部分，包括对原有文本的分成单独文本，和对隐藏主题的抽取，以及设置LDA隐主题的个数和每个主题选取几个关键字。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101145205924.png)

SVM模型训练必要文件，没有进行优化。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101145246768.png)

LDA可拓展的核心部分，系统先进性的核心核心部分，包括数据的归一化和LDA拓展隐主题的特征词项目的加入。


### License

本笔记仓库的内容，除了再对SVM和LDA的核心实现上是调用了开源组件外，其他都是我的原创。在您引用本仓库内容或者对内容进行修改时，请署名并以相同方式共享，谢谢。
***
### 补充
有很多部分的知识点是以前学习的，现在课程较多没来的及补上，所以将这一部分知识点和未来的一些计划学习的内容列在目录中，之后会逐步更新






