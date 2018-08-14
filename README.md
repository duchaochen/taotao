# taotao
淘淘商城项目所踩到的坑

碰到的第一个问题：zookeeper的端口没有开放 

我踩到这个坑了，由于linux系统的命令完全不会，就靠百度，百度之后出来的命令是错误的，结果导致我的端口一直没有开放，最后一直都是启动了tomcat之后访问什么都是404错误，最后百度了好久都是找的关键字是idea启动dubbo项目访问404，结果照做都无法解决问题，最后找了2天都没有找到问题，然后从头一步步的弄，最后还是把端口重新开放一下，测试结果成功了。
问题结局步骤。
1.查看端口是否开放命令：

       /etc/init.d/iptables status
       
2.开启zookeeper端口2181步骤：

    /sbin/iptables -I INPUT -p tcp --dport 2181 -j ACCEPT   写入修改
    /etc/init.d/iptables save   保存修改
    service iptables restart    重启防火墙，修改生效
    
第二个问题：
    就是把注册zookeeper的ip地址一定要输入正确，这个问题一般不会出现
由于我在家和公司的zookeeper的注册ip地址不同所以导致出现的这个问题。

第三个问题：
        当使用idea上的maven的install命令安装时，控制台出现中文乱码时，
        需要在settings -> maven -> Runner的 VM options设置一下字符编码 -Dfile.encoding=GBK
第四个问题：
        文件服务器fsatDFS复制到虚拟机之后开启直接查看ip之后，代码链接的时候对应上ip就可以使用了。而不是视频中说的ip是133。
        
第五个问题：
org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): 。。。。

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

redis集群关闭防火墙
查看防火墙状态：

        [root@centos6 ~]# service iptables status
        iptables：未运行防火墙。
        
        开启防火墙：
        [root@centos6 ~]# service iptables start
        
        关闭防火墙：
        [root@centos6 ~]# service iptables stop

     
