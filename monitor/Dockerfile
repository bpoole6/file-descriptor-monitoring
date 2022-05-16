FROM amazonlinux
RUN yum update -y;
RUN yum groupinstall 'Development Tools' -y;
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip";\
    unzip awscliv2.zip;\
    ./aws/install -i /usr/local/aws-cli -b /sbin;
RUN amazon-linux-extras install docker -y; #Install Docker
ADD fd_monitor.sh /
ENTRYPOINT exec ./fd_monitor.sh
