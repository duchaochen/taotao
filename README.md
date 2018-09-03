# taotao
淘淘商城项目所踩到的坑

#taotao-search-web运行之后的路径
    搜索链接
    http://localhost:8085/search.html?q=%E6%89%8B%E6%9C%BA
    查询详细链接
    http://localhost:8086/item/153585278340714.html
#maven安装命令
    
    clean install -Dfile.encoding=UTF-8
    
    如果是腾讯云需要安装iptables（已安装忽略），要不然不能使用查看防火墙命令
    
    yum install iptables-services
    
    
#控制台日志乱码问题

    中文乱码需要修改四个部分：
    1.idea安装目录下的bin/idea64.exe.vmoptions和bin/idea.exe.vmoptions追加-Dfile.encoding=UTF-8
    2.log4j对应的properties文件中需要增加log4j.appender.F.Encoding=utf-8（其中F是自定义的）
    3.idea–》setting–》File encoding–》修改三处编码集为UTF-8
    4.发布服务器修改，edit configuration–》VM options=-Dfile.encoding=UTF-8 

#淘淘商城文件服务器问题

    将淘淘商城中的文件服务器用虚拟机启动之后查看ip地址，然后上传的ip修改为虚拟机文件服务器的ip即可。
    我是直接可以使用的，问题就是文件服务器的ip地址和视频中的ip地址不一致。而不是视频中说的ip是133。

#注册dubbo问题

    zookeeper的端口没有开放 
    我踩到这个坑了，由于linux系统的命令完全不会，就靠百度，百度之后出来的命令是错误的，结果导致我的端口一直没有开放，最后一直都是启动了tomcat之后访问什么都是404错误，最后百度了好久都是找的关键字是idea启动dubbo项目访问404，结果照做都无法解决问题，最后找了2天都没有找到问题，然后从头一步步的弄，最后还是把端口重新开放一下，测试结果成功了。
    问题结局步骤。
    
    1.查看端口是否开放命令：
    /etc/init.d/iptables status 
     
    2.开启zookeeper端口2181步骤：
    /sbin/iptables -I INPUT -p tcp --dport 2181 -j ACCEPT   写入修改
    /etc/init.d/iptables save   保存修改
    service iptables restart    重启防火墙，修改生效
    
    3.在启动dubbo注册中心出现java.net.UnknownHostException: dubbo:错误解决方案,我的机器名称为dubbo
    从报错的信息看是没有找到dubbo对应的名称和服务。于是在linux下用hostname命令查看hostname
    [root@zzyyb /]# hostname
    dubbo
    能够正确返回机器的hostname是dubbo说明主机名正确且没有别名。

    接着ping一下这个主机名
    [root@zzyyb /]# ping dubbo
    出现这个情况ping: unknown host dubbo
    就是ping不通，说明主机名没有绑定IP地址。

    vi /etc/hosts 添加正确的主机地址
    192.168.25.146 dubbo
    再ping主机可以正常ping通了。

    重启网络服务
    service network restart

    接着再启动dubbo服务，可以正常启动不报java.net.UnknownHostException 未知的名称或服务的错误了。
    
    
#zookeeper问题：

    就是把注册zookeeper的ip地址一定要输入正确，这个问题一般不会出现
    由于我在家和公司的zookeeper的注册ip地址不同所以导致出现的这个问题。

#maven编译时控制台出现中文乱码：

    当使用idea上的maven的install命令安装时，控制台出现中文乱码时，
    需要在settings -> maven -> Runner的 VM options设置一下字符编码 -Dfile.encoding=GBK

#org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): 。。。。

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

#redis集群关闭防火墙

        查看防火墙状态：
        [root@centos6 ~]# service iptables status
        iptables：未运行防火墙。
        
        开启防火墙：
        [root@centos6 ~]# service iptables start
        
        关闭防火墙：
        [root@centos6 ~]# service iptables stop
        
        
#redis存整张表方式案例：
        存储格式：
            表名:唯一键id:储存字段名称
        命令：
            set tb_user:7:username zhangsan

#tomcat启动停止以及查看tomcat进程-Linux下如何查看tomcat是否启动

    在Linux系统下，重启Tomcat使用命令的操作！
    首先，进入Tomcat下的bin目录
    cd /usr/local/tomcat/bin
    
    使用Tomcat关闭命令
    ./shutdown.sh
    
    查看Tomcat是否以关闭
    ps -ef|grep tomcat
    
    如果显示以下相似信息，说明Tomcat还没有关闭
    记住看清楚自己的linux的tomcat是不是root 7001
    
    root 7010 1 0 Apr19 ? 00:30:13 /usr/local/java/bin/java 
    -Djava.util.logging.config.file=/usr/local/tomcat/conf/logging.properties -Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1024m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+DisableExplicitGC -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=/usr/local/tomcat/endorsed -classpath /usr/local/tomcat/bin/bootstrap.jar -Dcatalina.base=/usr/local/tomcat -Dcatalina.home=/usr/local/tomcat -Djava.io.tmpdir=/usr/local/tomcat/temp org.apache.catalina.startup.Bootstrap start
    
   
    *如果你想直接干掉Tomcat，你可以使用kill命令，直接杀死Tomcat进程
    kill -9 7010
   
    然后继续查看Tomcat是否关闭
    ps -ef|grep tomcat
    
    如果出现以下信息，则表示Tomcat已经关闭
    root 7010 1 0 Apr19 ? 00:30:30 [java]
    
    最后，启动Tomcat
    ./startup.sh
    
    查看tomcat启动日志
    tail -f logs/catalina.out
    
    
#solr异常问题
    
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
    
    并且在linux上安装Solr,一切准备就绪的时候报出404错误,此时检查jar包是否导入到了tomcat/webapps/Solr/WEB-INF/lib中,从Solr客户端下面的example/lib/ext之下的所有jar包复制到tomcat根目录的lib文件夹中,也可以复制到tomcat/webapps/Solr/WEB-INF/lib中.特此记录一下
  
  
#activemq安装好之后以下操作
    
    activemq的默认端口号为8161
    开启activemqq防火墙端口
    1、如果使用了云服务器需要先开启8161(web管理页面端口）、61616（activemq服务监控端口） 两个端口
    2、打开linux防火墙端口
    /sbin/iptables -I INPUT -p tcp --dport 8161 -j ACCEPT&&/etc/init.d/iptables save&&service iptables restart&&/etc/init.d/iptables status
    /sbin/iptables -I INPUT -p tcp --dport 61616 -j ACCEPT&&/etc/init.d/iptables save&&service iptables restart&&/etc/init.d/iptables status

#activemq调用注意
    发送方的消息队列名称一定要和接收方的消息队列名称(itemAddTopic引用的name="item-add-topic")一致，并且tcp的地址要一致，要不然是接收不到的。
    以下是发送发的配置:
    <!-- JMS服务厂商提供的ConnectionFactory -->
        <bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
            <constructor-arg name="brokerURL" value="tcp://192.168.25.146:61616"/>
        </bean>
        <!-- spring对象ConnectionFactory的封装 -->
        <bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">
            <property name="targetConnectionFactory" ref="targetConnectionFactory"/>
        </bean>
    
        <!-- 配置JMSTemplate -->
        <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
            <property name="connectionFactory" ref="connectionFactory"/>
        </bean>
        <!-- 配置消息的目标Destination对象 -->
        <bean id="test-queue" class="org.apache.activemq.command.ActiveMQQueue">
            <constructor-arg name="name" value="test-queue"/>
        </bean>
    
        <bean id="itemAddTopic" class="org.apache.activemq.command.ActiveMQTopic">
            <constructor-arg name="name" value="item-add-topic"/>
        </bean>
        
        以下是接收方的配置:
        <bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
        		<constructor-arg name="brokerURL" value="tcp://192.168.25.146:61616"/>
        	</bean>
        
        	<bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">
        		<property name="targetConnectionFactory" ref="targetConnectionFactory"/>
        	</bean>
        	<!--使用queue形式发送消息,点对点形式-->
        	<bean id="test-queue" class="org.apache.activemq.command.ActiveMQQueue">
        		<constructor-arg name="name" value="test-queue"/>
        	</bean>
        
        	<bean id="myMessageListener" class="com.taotao.search.listener.MyMessageListener"/>
        
        	<bean class="org.springframework.jms.listener.DefaultMessageListenerContainer">
        		<property name="connectionFactory" ref="connectionFactory"/>
        		<property name="destination" ref="test-queue"/>
        		<property name="messageListener" ref="myMessageListener"/>
        	</bean>
        
        	<!--使用Topic形式发送消息,群发形式-->
        	<bean id="itemAddTopic" class="org.apache.activemq.command.ActiveMQTopic">
        		<constructor-arg name="name" value="item-add-topic"/>
        	</bean>
        
        	<bean id="itemAddTopicListener" class="com.taotao.search.listener.ItemAddTopicListener"/>
        
        	<bean class="org.springframework.jms.listener.DefaultMessageListenerContainer">
        		<property name="connectionFactory" ref="connectionFactory"/>
        		<property name="destination" ref="itemAddTopic"/>
        		<property name="messageListener" ref="itemAddTopicListener"/>
        	</bean>
        	
        	
#读取配置properties文件出错
    程序异常Could not resolve placeholder 'ITEM_INFO' in string value "${ITEM_INFO}"
    一般都是有2处spring的配置文件中都<context:property-placeholder location="classpath:properties/*.properties"/>标签
    
    主要从以下几个地方去解决：
    1. 两处都添加ignore-unresolvable="true"
    2.获取去掉一处，将另一处修改为<context:property-placeholder location="classpath:properties/*.properties"/>
    
#nginx安装
    
    1.安装：yum install gcc-c++ 
    2.安装第三方开发包pcre：yum install -y pcre pcre-devel
    3.安装zlib：yum install -y zlib zlib-devel
    4.安装openssl:yum install -y openssl openssl-devel
    5.创建makefile文件夹需要进入nginx文件夹中，
    命令：cd /usr/local/src/nginx-1.8.0
    然后执行以下命令：
      ./configure \
      --prefix=/usr/local/src/nginx \
      --pid-path=/var/run/nginx/nginx.pid \
      --lock-path=/var/lock/nginx.lock \
      --error-log-path=/var/log/nginx/error.log \
      --http-log-path=/var/log/nginx/access.log \
      --with-http_gzip_static_module \
      --http-client-body-temp-path=/var/temp/nginx/client \
      --http-proxy-temp-path=/var/temp/nginx/proxy \
      --http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
      --http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
      --http-scgi-temp-path=/var/temp/nginx/scgi
      
     6.这个时候就有了Makefile文件夹了，这个时候编译源代码命令：make
     7.安装nginx：make install
     
     8.首先查看是否存在/var/temp/nginx文件夹命令：cd /var/temp/nginx
       如果没有就创建一个，创建多级文件夹命令：mkdir /var/temp/nginx -p
     
     9.如果在没有创建就直接启动nginx的话会出现以下异常
     nginx: [emerg] mkdir() "/var/temp/nginx/client" failed (2: No such file or directory)
     是因为没有这个/var/temp/nginx的文件夹

     10.进入nginx的sbin文件夹：cd /usr/local/src/nginx/sbin,如果存在nginx文件夹存在local中在，
     则命令为：cd /usr/local/nginx/sbin
     11.启动nginx：./nginx
     12.查看nginx进程命令:ps aux|grep nginx
     13.关闭nginx命令：kill 进程号,或者使用./nginx -s stop
     14.修改nginx配置文件之后，刷新服务命令：./nginx -s reload
     
     现在就可以输入地址直接访问了，端口默认是80，如果访问不到表示端口没有开放，个人练习情况可以直接关闭防火墙
     关闭防火墙命令:service iptables stop
     
#本地nginx根据域名区分不同程序

    首先在hosts文件中绑定2个域名
    使用管理员权限打开C:\Windows\System32\drivers\etc\hosts文件,然后配置以下域名，这个就是配置本地郁闷绑定的ip
    192.168.25.146 www.aaa.com
    192.168.25.146 www.bbb.com
    
    然后配置nginx程序访问静态资源
    打开/usr/local/src/nginx/conf/nginx.conf文件，配置以下代码
    server {
            listen       80;
            server_name  www.aaa.com;
    
            #charset koi8-r;
    
            #access_log  logs/host.access.log  main;
    
            location / {
                root   html-aaa;
                index  index.html index.htm;
            }
         }
         
     1)listen端口：80，表示多个域名公用80端口
     2)server_name:刚刚添加的域名
     3)root表示访问的程序文件夹，
       该文件夹需要在创建一个出来 cp /usr/local/src/nginx/html /usr/local/src/nginx/html-aaa -r
    4)index表示默认访问的页面，这些页面需要在刚刚创建的html-aaa文件夹中存在
    5)以上就配置好了，注意下，刚刚创建了2个域名www.aaa.com和www.bbb.com,所以上面的server需要在复制一个，
    然后在配置一个www.bbb.com如下：
    server {
                listen       80;
                server_name  www.bbb.com;
        
                #charset koi8-r;
        
                #access_log  logs/host.access.log  main;
        
                location / {
                    root   html-bbb;
                    index  index.html index.htm;
                }
             }
     其它的操作同上面一样,主要是要创建一个html-bbb的文件夹
    6)重启nginx命令：/usr/local/src/nginx/sbin/nginx -s reload
    
#niginx反向代理

    安装3个tomcat，将将3个tomcat端口设置不一样即可.
    1.上传apache-tomcat-7.0.47.tar.gz文件
    2.进入上传文件的同级目录下命令：cd /usr/local/src/tomcat
    3.解压命令：tar -zxf apache-tomcat-7.0.47.tar.gz
      并且将解压的文件目录修改为tomcat01命令：mv apache-tomcat-7.0.47 tomcat01
    4.然后复制2个tomcat出来，命令：cp tomcat01 tomcat02 -r和cp tomcat01 tomcat03 -r
    5.修改各自端口
        tomcat01：
            1）<Server port="8006" shutdown="SHUTDOWN">
            
            2）<Connector port="8081" protocol="HTTP/1.1"
                           connectionTimeout="20000"
                           redirectPort="8443" />
            3)<Connector port="8011" protocol="AJP/1.3" redirectPort="8443" />
            
       tomcat02：
            1）<Server port="8007" shutdown="SHUTDOWN">
            2）<Connector port="8082" protocol="HTTP/1.1"
                                       connectionTimeout="20000"
                                       redirectPort="8443" />
            3)<Connector port="8012" protocol="AJP/1.3" redirectPort="8443" />   
                                     
        tomcat03：
           1）<Server port="8008" shutdown="SHUTDOWN">
           2）<Connector port="8083" protocol="HTTP/1.1"
                                      connectionTimeout="20000"
                                      redirectPort="8443" /> 
           3)<Connector port="8013" protocol="AJP/1.3" redirectPort="8443" /> 
    6.然后开启3个tomcat命令： 
            1)/usr/local/src/tomcat/tomcat01/bin/startup.sh
            2)/usr/local/src/tomcat/tomcat02/bin/startup.sh
            3)/usr/local/src/tomcat/tomcat03/bin/startup.sh
            
            
    7.配置nginx反向代理动态资源
        1)进入代理的配置nginx配置文件命令： cd /usr/local/src/nginx/conf
        2)添加upstream节点并且配置对应的ip地址和端口号
            
            upstream tomcat01{
            server 192.168.25.146:8081;
            }
        
            upstream tomcat02{
            server 192.168.25.146:8082;
            }
        
            upstream tomcat03{
            server 192.168.25.146:8083;
            }
        3)修改server中location的root节点名称为porxy_pass   http://tomcat01;
             server {
                    listen       80;
                    server_name  www.tomcat01.com;
            
                    #charset koi8-r;
            
                    #access_log  logs/host.access.log  main;
            
                    location / {
                        porxy_pass   http://tomcat01;
                        index  index.html index.htm;
                    }
                }
                
                server {
                    listen       80;
                    server_name  www.tomcat02.com;
            
                    #charset koi8-r;
            
                    #access_log  logs/host.access.log  main;
            
                    location / {
                        porxy_pass   http://tomcat02;
                        index  index.html index.htm;
                    }
                }
                
                server {
                    listen       80;
                    server_name  www.tomcat03.com;
            
                    #charset koi8-r;
            
                    #access_log  logs/host.access.log  main;
            
                    location / {
                        porxy_pass   http://tomcat03;
                        index  index.html index.htm;
                    }
                }
                
           最后重新加载配置文件命令：/usr/local/src/nginx/sbin/nginx -s reload

#nginx负载均衡

        只需要在上面配置好的upstream中多配置几个tomcat服务器即可,例如:
             upstream tomcat02{
                server 192.168.25.146:8083 weight=1;
                server 192.168.25.146:8084 weight=1;
                server 192.168.25.146:8085 weight=1;
                server 192.168.25.146:8086 weight=1;
            }
        weight的数值越大表示权重越高，表示被访问的越多,默认值都是1
        注意：如果在一台服务器中安装多个tomcat一定要修改端口,
        如果已经修改过的nginx和tomcat的配置文件一定要都重新加载一下。




            