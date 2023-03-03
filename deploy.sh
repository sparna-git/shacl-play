scp shacl-play/target/shacl-play-0.6.2.war admin@calliope.sparna.fr:~

ssh -t admin@calliope.sparna.fr 'su -c "\
rm -rf /var/lib/tomcat8/tomcat8-instance1/webapps/play.war
rm -rf /var/lib/tomcat8/tomcat8-instance1/webapps/play
service tomcat8-instance1 stop
cp /home/admin/shacl-play-0.6.2.war /var/lib/tomcat8/tomcat8-instance1/webapps/play.war
service tomcat8-instance1 start"'
