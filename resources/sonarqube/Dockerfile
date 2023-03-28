# SonarQube with Findbugs plugin included
FROM sonarqube:lts-community

USER root

# Install Findbugs plugin
ADD https://github.com/spotbugs/sonar-findbugs/releases/download/4.2.3/sonar-findbugs-plugin-4.2.3.jar \
    /opt/sonarqube/extensions/plugins/

# Configure SonarQube
RUN chown -R sonarqube:sonarqube bin/ conf/ extensions/ \
    && chmod u+x -R bin/ \
    # Disable SonarQube telemetry
    && sed -i 's/#sonar\.telemetry\.enable=true/sonar\.telemetry\.enable=false/' /opt/sonarqube/conf/sonar.properties \
    #### Set list of patterns matching Dockerfiles
    && echo 'sonar.lang.patterns.dockerfile=Dockerfile,Dockerfile.*' >> /opt/sonarqube/conf/sonar-scanner.properties

# Switch back to an unpriviledged user
USER sonarqube

ENTRYPOINT ["/opt/sonarqube/docker/entrypoint.sh"]