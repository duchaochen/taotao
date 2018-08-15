# taotao
淘淘商城项目所踩到的坑

#第一个问题：

    zookeeper的端口没有开放 
    我踩到这个坑了，由于linux系统的命令完全不会，就靠百度，百度之后出来的命令是错误的，结果导致我的端口一直没有开放，最后一直都是启动了tomcat之后访问什么都是404错误，最后百度了好久都是找的关键字是idea启动dubbo项目访问404，结果照做都无法解决问题，最后找了2天都没有找到问题，然后从头一步步的弄，最后还是把端口重新开放一下，测试结果成功了。
    问题结局步骤。
    
    1.查看端口是否开放命令：
    /etc/init.d/iptables status 
     
    2.开启zookeeper端口2181步骤：
    /sbin/iptables -I INPUT -p tcp --dport 2181 -j ACCEPT   写入修改
    /etc/init.d/iptables save   保存修改
    service iptables restart    重启防火墙，修改生效
    
#第二个问题：
    `就是把注册zookeeper的ip地址一定要输入正确，这个问题一般不会出现
由于我在家和公司的zookeeper的注册ip地址不同所以导致出现的这个问题。
`
#第三个问题：
        `当使用idea上的maven的install命令安装时，控制台出现中文乱码时，
        需要在settings -> maven -> Runner的 VM options设置一下字符编码 -Dfile.encoding=GBK`

#第四个问题：
    `文件服务器fsatDFS复制到虚拟机之后开启直接查看ip之后，代码链接的时候对应上ip就可以使用了。而不是视频中说的ip是133。`
    
    
#第五个问题：
##org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): 。。。。

     <resources>
        <!-- maven项目中src源代码下的xml等资源文件编译进classes文件夹，
            注意：如果没有这个，它会自动搜索resources下是否有mapper.xml文件，
            如果没有就会报org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): 。。。。-->
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
        </resource>
        <!--将resources目录下的配置文件编译进classes文件  -->
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>  

#第六个问题redis集群关闭防火墙

        查看防火墙状态：
        [root@centos6 ~]# service iptables status
        iptables：未运行防火墙。
        
        开启防火墙：
        [root@centos6 ~]# service iptables start
        
        关闭防火墙：
        [root@centos6 ~]# service iptables stop

#第七个问题Linux下如何查看tomcat是否启动

    在Linux系统下，重启Tomcat使用命令的操作！
    首先，进入Tomcat下的bin目录
    cd /usr/local/tomcat/bin
    
    使用Tomcat关闭命令
    ./shutdown.sh
    
    查看Tomcat是否以关闭
    ps -ef|grep java
    
    如果显示以下相似信息，说明Tomcat还没有关闭
    记住看清楚自己的linux的tomcat是不是root 7001
    
    root 7010 1 0 Apr19 ? 00:30:13 /usr/local/java/bin/java 
    -Djava.util.logging.config.file=/usr/local/tomcat/conf/logging.properties -Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1024m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+DisableExplicitGC -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=/usr/local/tomcat/endorsed -classpath /usr/local/tomcat/bin/bootstrap.jar -Dcatalina.base=/usr/local/tomcat -Dcatalina.home=/usr/local/tomcat -Djava.io.tmpdir=/usr/local/tomcat/temp org.apache.catalina.startup.Bootstrap start
    
   
    *如果你想直接干掉Tomcat，你可以使用kill命令，直接杀死Tomcat进程
    kill -9 7010
   
    然后继续查看Tomcat是否关闭
    ps -ef|grep java
    
    如果出现以下信息，则表示Tomcat已经关闭
    root 7010 1 0 Apr19 ? 00:30:30 [java]
    
    最后，启动Tomcat
    ./startup.sh
    
    查看tomcat启动日志
    tail -f logs/catalina.out
    
#第八个问题：solr
    
    HTTP Status 500 - {msg=SolrCore 'collection1' is not available due to init failure: Index locked for write 
    for core collection1,trace=org.apache.solr.common.SolrException: SolrCore 'collection1' is not available 
    due to init failure: Index locked for write for core collection1 at org.apache.solr.core.CoreContainer.
    getCore(CoreContainer.java:745) at org.apache.solr.servlet.SolrDispatchFilter.doFilter(SolrDispatchFilter.java:307) 
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243) 
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210) 
    at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:222) 
    ....
    
    
    解决办法
    1.释放tomcat下的/webapps/solr/WEB-INF/web.xml中的<env-entry>标签,并修改标签下的<env-entry-value>标签内容为solrhome的路径.我可以确保修改的路径没有问题,但是不管用.而且这个是我一开始就配置了的,.没启动tomcat之前
    2.修改solrhome下/collection1/conf/solrconfig.xml,释放标签:<unlockOnStartup>   并将值false改为true.不管用
    3.solrhome目录建立文件solr.xml，内容：
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <solr> 
    </solr>
  
