#!/bin/bash
/Users/Shared/ON3/git/devops/github/add_web_hook.sh uan-doc s3-spring-boot-starter
/Users/Shared/ON3/git/devops/jenkins/api/script_clone_default_build_job.sh 's3-spring-boot-starter' 'Infra'
