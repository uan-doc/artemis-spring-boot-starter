// properties([pipelineTriggers([githubPush()])])

pipeline {
    agent any
    
    stages {
        // Esse trecho de código foi tirado daqui: https://betterprogramming.pub/how-too-add-github-webhook-to-a-jenkins-pipeline-62b0be84e006
        // A intenção era de depender o mínimo possível da interface gráfica do Jenkins e ter tudo em código, porém eu não consegui fazer 
        // funcionar com o código abaixo, pois mesmo eu verificando que o GitHub alcançava o nosso Jenkins, o hook não chamava esse pipeline
        // stage('git pull...') {
        //     steps {
        //         checkout([
        //             $class: 'GitSCM',
        //             branches: [[name: 'main']],
        //             userRemoteConfigs: [[
        //                 url: 'git@github.com:ON3-Solutions/document-export.git',
        //                 credentialsId: 'GitHub',
        //             ]]
        //         ])
        //     }
        // }

        stage('Building...') {
            steps {
                sh "MAVEN_SKIP_RC=true JAVA_HOME=/Disk1/jdks/jdk-18.0.2.1 mvn clean -DskipTests install"
            }
        }
    }
}
