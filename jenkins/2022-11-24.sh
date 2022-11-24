#!/bin/bash
cd /Users/Shared/ON3/git/devops/jenkins/api || exit
./script_clone_job.sh common-rest artemis-spring-boot-starter false
./add_job_to_view.sh 'artemis-spring-boot-starter' 'GedocFlex - MicroServices'
./build.sh 'artemis-spring-boot-starter'
