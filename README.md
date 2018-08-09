# taotao
淘淘商城

碰到的第一个问题：zookeeper的端口没有开放 

我踩到这个坑了，由于linux系统的命令完全不会，就靠百度，百度之后出来的命令是错误的，结果导致我的端口一直没有开放，最后一直都是启动了tomcat之后访问什么都是404错误，最后百度了好久都是找的关键字是idea启动dubbo项目访问404，结果照做都无法解决问题，最后找了2天都没有找到问题，然后从头一步步的弄，最后还是把端口重新开放一下，测试结果成功了。
问题结局步骤。
查看端口是否开放命令：

       /etc/init.d/iptables status
       
1.开启zookeeper端口2181步骤：

    /sbin/iptables -I INPUT -p tcp --dport 2181 -j ACCEPT   写入修改
    /etc/init.d/iptables save   保存修改
    service iptables restart    重启防火墙，修改生效
