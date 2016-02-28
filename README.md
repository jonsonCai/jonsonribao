#jonson日报

---

###gif图片较大,加载时间可能会稍长,请耐心等候;

###1.网络接口的获取:
	使用Fiddler截取原版知乎日报的请求数据,分析整理出各个数据对应的网络API;
  	使用得到API在app上请求,并解析返回的Json数据,显示到应用中;

	//闪屏图片
    "http://news-at.zhihu.com/api/4/start-image/1080*1776";//闪屏页图片

    //新闻
    "http://news-at.zhihu.com/api/4/news/latest"  //主页面最新消息json
    "http://news-at.zhihu.com/api/4/news/"        //获取新闻页面json 后面+主页面消息条目的id
    "http://news-at.zhihu.com/api/4/story-extra/";//主页面详细信息json 后面+主页面消息条目的id
    "http://news-at.zhihu.com/api/4/story/'REPLACE_ID'/long-comments"   //新闻长评论 'REPLACE_ID替换为主页面消息条目的id
    "http://news-at.zhihu.com/api/4/story/'REPLACE_ID'/short-comments"   //新闻短评论 'REPLACE_ID替换为主页面消息条目的id

    //加载更多
    "http://news.at.zhihu.com/api/4/news/before/"   //历时消息(加载更多) 后面+日期(20130520)

    //侧滑菜单
    "http://news-at.zhihu.com/api/4/themes"    //主题分类(侧滑菜单)
    "http://news-at.zhihu.com/api/4/theme/"   //主题列表(显示到主页面),后面+id

	//获取更多评论
	评论默认加载20条,获取更多评论是拿到上一个评论的最后一条评论,然后取它的id,把他变成/before/'REPLACE_ID',把他加在原来获取评论的网址后面
	"news-at.zhihu.com/api/4/story/7740943/short-comments"  //评论网址
	"news-at.zhihu.com/api/4/story/7740943/short-comments/before/'REPLACE_ID'  //评论网址加载更多

	长评论也是使用一样的逻辑

###2.使用SlidingMenu完成侧滑功能;
	主页面和侧滑菜分别使用一个Fragment作为容器;
	侧滑菜单的列表设置点击监听,点击后触发主页面fragment中的setCurrentPage方法,把主页面的ListView移除,然后再添加对应页面的ListView;
	判断当前页面是否是主页面,不是则把铃铛和三个点图标设置为GONE,把加号设置为Visible;
		
![pic](http://i11.tietuku.com/ec094cd0df882044.gif)

###3.主页面实现下拉刷新和加载更多,另外在新闻列表头添加一个头条轮播;

	主页面下拉刷新和上拉加载更多使用自定义的listView,添加一个HeaderView,并且设置滑动监听,实现下拉时各种松开状态的转换
		上拉加载更多是判断页面显示的条目是否为listView的最后一个条目,如果结果为true,则触发加载更多的方法;
	
	图片轮播使用ViewPager实现,下方的几个小圆点使用开源框架"ViewPagerIndactor"实现

![pic](http://i12.tietuku.com/9144a2b125c29d61.gif)

###4.新闻阅读界面实现滚动时TitleBar变透明隐藏,页头图片实现和页面不同步滚动;
	新闻阅读界面使用ScrollView嵌套ImageView(页头图片) + WebView组成;
	阅读界面的TitleBar和页头图片的实现效果都是和手指的滑动实时互动的,这里使用了WebView的setOnScrollChangeListener来监听页面滚动,取得滚动的数据,
	通过这些数据的变化来改变titleBar的透明度,页头图片的padidng,来实现对应的效果.

	但是这个方法在Android 5.0以上是没有问题的,后期测试发现低于5.0的系统是不支持这个监听器的,所以通过自定义的ScrollView来解决这个问题
	ScrollView中有onScrollChanged这个方法,这个方法得到的数据和上边的数据是一样的,都是当前的XY坐标数据和上次记录到的XY坐标
	但是这个方法是Protect,外界无法调用,所以写了一个接口,把这个方法的到的数据通过这个接口传递到外部;

![pic](http://i11.tietuku.com/60148ffafa2c0366.gif)

###5.新闻评论界面,默认展开长评论 , 短评论默认折叠;
	新闻评论界面是是使用一个ScrollView,内嵌TextView(长评数量) , ListView(长评论列表) , TextView(短评数量) , 
	ListView(短评列表,默认隐藏,点击后展开)组成
	
	ScrollView内嵌ListView时,ListView还是以原来的大小展示的,更多内容通过滚动来显示,但是评论界面要实现的效果的长评论和短评论列表和页面都是一体滚动的,
	所以这里使用了自定义ListView,重写onMeasure方法,使得它测量出来的大小刚好的列表的长度,这样就可以实习那和ScrollView一体滚动;

![pic](http://i13.tietuku.com/4e6f87b756e51447.gif)

###5.分享
	分享使用SharedSDK实现,目前能实现QQ空间的分享,其他的后期改进;
	
![pic](http://i13.tietuku.com/459542683351244d.gif)



	
	

