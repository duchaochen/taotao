# taotao
淘淘商城

zookeeper的端口一定要开放 

我踩到这个坑了，由于linux系统的命令完全不会，就靠百度

查看端口是否开放命令：

       /etc/init.d/iptables status
       
1.开启zookeeper端口2181步骤：

    /sbin/iptables -I INPUT -p tcp --dport 80 -j ACCEPT   写入修改
    /etc/init.d/iptables save   保存修改
    service iptables restart    重启防火墙，修改生效