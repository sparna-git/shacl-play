# Transfer
scp -i /home/thomas/.ssh/sparna-keypair-francfort.pem shacl-play/target/shacl-play-0.11.5.war ubuntu@92.243.27.145:~

# Redeploy
ssh -i /home/thomas/.ssh/sparna-keypair-francfort.pem ubuntu@92.243.27.145 'sudo su -c "\
service tomcat10 stop
rm -rf /var/lib/tomcat10/webapps/play.war
rm -rf /var/lib/tomcat10/webapps/play
rm -rf /var/lib/tomcat10/logs/*
cp /home/ubuntu/shacl-play-0.11.5.war /var/lib/tomcat10/webapps/play.war
service tomcat10 start"'